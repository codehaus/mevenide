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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.repository.Artifact;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.repository.RepoPathElement;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideColors;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.util.StringUtils;

/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryBrowser extends ViewPart implements RepositoryEventListener {
    
    private TreeViewer repositoryViewer;
    
    private List repositories = new ArrayList();
   
    private Action addRepositoryAction;
    private Action removeRepositoryAction;
    private Action downloadArtifactAction;
    private Action refreshAction;
    
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
        downloadArtifactAction = new Action() {
        	public void run() {
        	    IStructuredSelection selection = (IStructuredSelection) repositoryViewer.getSelection();
        	    if ( selection != null ) {
	        	    List downloadList = new ArrayList();
	        	    for ( Iterator it = selection.iterator(); it.hasNext(); ) {
                        RepoPathElement selectedItem = (RepoPathElement) it.next();
	                    if ( selectedItem.getLevel() == RepoPathElement.LEVEL_ARTIFACT ) {
	                        downloadList.add(selectedItem);
	                    }
	                }
	        	    if ( downloadList.size() > 0 ) {
	        	        DownloadJob downloadJob = new DownloadJob(downloadList);
	        	        downloadJob.schedule(Job.LONG);
	        	    }
	        	    else {
	        	        downloadAbortedMessage();
	        	    }
        	    }
        	    else {
        	        downloadAbortedMessage();
        	    }
        	    
            }
            private void downloadAbortedMessage() {
                MessageDialog.openWarning(repositoryViewer.getTree().getShell(), "Download aborted", "No artifact have been selected, and thus none will be downloaded.");
            }
        };
        downloadArtifactAction.setText("Get artifact");
        
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
                    String selectedRepo = ((RepoPathElement) it.next()).getURI().toString();
                    selectedRepositories.add(selectedRepo);
                }
                repositories.removeAll(selectedRepositories);
                saveRepositories();
                asyncUpdateUI();
            }
        };
        removeRepositoryAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.REMOVE_REPO_DEFINITION));
        removeRepositoryAction.setToolTipText("Remove repository");
        
        refreshAction = new Action(){
            public void run() { 
                StructuredSelection selection = (StructuredSelection) repositoryViewer.getSelection();
                final Object[] obj = ((StructuredSelection) repositoryViewer.getSelection()).toArray();
                for ( int i = 0; i < obj.length; i++ ) {
                    RepoPathElement selectedObject = (RepoPathElement) obj[i];
                    selectedObject.reset();
                }
                repositoryViewer.getControl().getDisplay().asyncExec(
        				new Runnable() {
        					public void run () {
        					    repositoryViewer.refresh(obj);
        					}
        				}
                );
            }
        };
        refreshAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.REFRESH_TOOL));
        refreshAction.setToolTipText("Refresh");
        refreshAction.setText("Refresh");
        
        createToolBarManager();
        createContextualMenu();
    }

    private void createContextualMenu() {
		MenuManager contextManager = new MenuManager();
		contextManager.setRemoveAllWhenShown(true);
		Menu menu = contextManager.createContextMenu(repositoryViewer.getControl());
		repositoryViewer.getControl().setMenu(menu);
		contextManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(downloadArtifactAction);
				manager.add(refreshAction);
			}
		});
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
        RepositoryList.saveUserDefinedRepositories(repositories);
    }
    
    private void loadRepositories() {
        repositories = RepositoryList.getUserDefinedRepositories();
    }
    
    private void createToolBarManager() {
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(addRepositoryAction);
        toolBarManager.add(removeRepositoryAction);
        toolBarManager.add(refreshAction);
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
        
        repositoryViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
	            List selection = ((StructuredSelection) repositoryViewer.getSelection()).toList();
			    
			    boolean enableDownload = false;
			    int refreshableItems = 0;
			    
			    for (int i = 0; i < selection.size(); i++) {
			        if ( selection.get(i) instanceof Artifact ) {
			            enableDownload = true;
			        }
			        else {
			            refreshableItems++;
			        }
	            }
			    downloadArtifactAction.setEnabled(enableDownload);
			    refreshAction.setEnabled(refreshableItems > 0);
            }
        });
    }
    
    

    public void setFocus() {
    }
}

