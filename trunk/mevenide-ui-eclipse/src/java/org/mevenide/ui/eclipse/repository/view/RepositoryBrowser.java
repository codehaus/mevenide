/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.repository.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.ui.eclipse.repository.model.BaseRepositoryObject;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryBrowser extends ViewPart implements RepositoryEventListener {
    
    
    
    private static final String MAVEN_REPOSITORIES = "MAVEN_REPOSITORIES";
    private static final List DEFAULT_REPOSITORIES = new ArrayList();
    private static final List MIRRORS = new ArrayList();
    
    static {
        DEFAULT_REPOSITORIES.add("http://www.ibiblio.org/maven/");
        DEFAULT_REPOSITORIES.add("http://cvs.apache.org/repository/");
        DEFAULT_REPOSITORIES.add("http://maven-plugins.sourceforge.net/maven/");
        DEFAULT_REPOSITORIES.add("http://seasar.sourceforge.jp/maven/");
        DEFAULT_REPOSITORIES.add("http://spring-ext.sourceforge.jp/maven/");
        DEFAULT_REPOSITORIES.add("http://ibiblio.org/geotools");
        DEFAULT_REPOSITORIES.add("http://www.codeczar.com/maven/");
        
        MIRRORS.add("http://mirrors.sunsite.dk/maven/");
        MIRRORS.add("http://ftp.up.ac.za/pub/linux/maven/");
        MIRRORS.add("http://download.au.kde.org/pub/maven/");
        MIRRORS.add("http://public.planetmirror.com/pub/maven/");
        MIRRORS.add("http://public.www.planetmirror.com/pub/maven/");
        MIRRORS.add("http://smokeping.planetmirror.com/pub/maven/");
        MIRRORS.add("http://horde.planetmirror.com/pub/maven/");
        MIRRORS.add("http://curl.planetmirror.com/pub/maven/");
        MIRRORS.add("http://python.planetmirror.com/pub/maven/");
    }
    
    private TreeViewer repositoryViewer;
    
    private List repositories = new ArrayList();
   
    private Action addRepositoryAction;
    private Action removeRepositoryAction;

    private PreferencesManager preferenceManager;
    
    public void dataLoaded(final RepositoryEvent event) {
        if ( repositories.contains(event.getRepositoryUrl()) ) {
            repositoryViewer.getControl().getDisplay().asyncExec(
    				new Runnable() {
    					public void run () {
				            repositoryViewer.refresh(event.getElement());
    					}
    				}
    		);
        }   
    }
    
    public void createPartControl(Composite parent) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setBackground(MevenideColors.WHITE);
        
        createRepositoryBrowsingArea(container);
        
        createActions();
        
    }
    
    private void createActions() {
        addRepositoryAction = new Action() {
            public void run() {
                AddRepositoryDialog dialog = new AddRepositoryDialog();
                int result = dialog.open();
                String repo = dialog.getRepository();
                if ( result == Window.OK && !StringUtils.isNull(repo) ) {
                    repositories.add(repo);
                    saveRepositories();
                    asyncUpdateUI();
                }
            }
        };
        addRepositoryAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.NEW_REPO_DEFINITION));
        addRepositoryAction.setToolTipText("Add repository");
        
        removeRepositoryAction = new Action() {
            public void run() { 
                StructuredSelection selection = (StructuredSelection) repositoryViewer.getSelection();
                List selectedRepositories = new ArrayList();
                for ( Iterator it = selection.iterator(); it.hasNext(); ) {
                    String selectedRepo = ((BaseRepositoryObject) it.next()).getRepositoryUrl();
                    selectedRepositories.add(selectedRepo);
                }
                repositories.removeAll(selectedRepositories);
                saveRepositories();
                asyncUpdateUI();
            }
        };
        removeRepositoryAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.REMOVE_REPO_DEFINITION));
        removeRepositoryAction.setToolTipText("Remove repository");
        
        createToolBarManager();
    }

    private void asyncUpdateUI() {
        repositoryViewer.getControl().getDisplay().asyncExec(
				new Runnable() {
					public void run () {
			            repositoryViewer.setInput(repositories);
					}
				}
        );
    }
    
    private void saveRepositories() {
        preferenceManager = PreferencesManager.getManager();
        String serializedRepos = "";
        for (Iterator it = repositories.iterator(); it.hasNext();) {
            serializedRepos += it.next() + ",";
        }
        preferenceManager.setValue(MAVEN_REPOSITORIES, serializedRepos);
        preferenceManager.store();
    }
    
    private void loadRepositories() {
        preferenceManager = PreferencesManager.getManager();
        String repos = preferenceManager.getValue(MAVEN_REPOSITORIES);
        if ( !StringUtils.isNull(repos) ) {
            repositories = new ArrayList(Arrays.asList(org.apache.commons.lang.StringUtils.split(repos, ",")));
        }
        else {
            repositories = DEFAULT_REPOSITORIES;
        }
    }
    
    private void createToolBarManager() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(addRepositoryAction);
        toolBarManager.add(removeRepositoryAction);
	}
    
    private void createRepositoryBrowsingArea(Composite container) {

        repositoryViewer = new TreeViewer(container, SWT.MULTI);
        RepositoryContentProvider contentProvider = new RepositoryContentProvider();
        contentProvider.addRepositoryEventListener(this);
        repositoryViewer.setContentProvider(contentProvider);
        repositoryViewer.setLabelProvider(new RepositoryObjectLabelProvider());
        
        GridData treeViewerLayoutData = new GridData(GridData.FILL_BOTH);
        treeViewerLayoutData.grabExcessHorizontalSpace = true;
        treeViewerLayoutData.grabExcessVerticalSpace = true;
        repositoryViewer.getTree().setLayoutData(treeViewerLayoutData);
        
        loadRepositories();
        repositoryViewer.setInput(repositories);
    }
    
    

    public void setFocus() {
    }
}

