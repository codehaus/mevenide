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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.MevenideColors;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class RepositoryBrowser extends ViewPart implements RepositoryEventListener {
    
    
    private String repositoryUrl = "http://www.ibiblio.org/maven/";
    
    private Text repositoryUrlText;
    
    private TreeViewer repositoryViewer;
    
    
    public void dataLoaded(final RepositoryEvent event) {
        if ( repositoryUrl != null && repositoryUrl.equals(event.getRepositoryUrl()) ) {
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
        
        createRepositoryUrlComposite(container);
        
        createRepositoryBrowsingArea(container);
    }
    
    private void createRepositoryBrowsingArea(Composite container) {
        Composite composite = new Composite(container, SWT.NULL);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.grabExcessVerticalSpace = true;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        composite.setLayoutData(layoutData);
        composite.setBackground(MevenideColors.BLUE_GRAY);
        
        repositoryViewer = new TreeViewer(composite, SWT.NULL);
        RepositoryContentProvider contentProvider = new RepositoryContentProvider();
        contentProvider.addRepositoryEventListener(this);
        repositoryViewer.setContentProvider(contentProvider);
        repositoryViewer.setLabelProvider(new RepositoryObjectLabelProvider());
        
        GridData treeViewerLayoutData = new GridData(GridData.FILL_BOTH);
        treeViewerLayoutData.grabExcessHorizontalSpace = true;
        treeViewerLayoutData.grabExcessVerticalSpace = true;
        repositoryViewer.getTree().setLayoutData(treeViewerLayoutData);

        repositoryViewer.setInput(repositoryUrl);
    }

    private void createRepositoryUrlComposite(Composite container) {
        Composite composite = new Composite(container, SWT.NULL);
        composite.setBackground(MevenideColors.BLUE_GRAY);
        GridLayout topLayout = new GridLayout();
        topLayout.marginWidth = 2;
        topLayout.marginHeight = 2;
        composite.setLayout(topLayout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        composite.setLayoutData(layoutData);
        
        Composite textComposite = new Composite(composite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = false;
        textComposite.setLayout(layout);
        textComposite.setBackground(MevenideColors.WHITE);
        textComposite.setLayout(layout);
        GridLayout textLayout = new GridLayout();
        GridData topData = new GridData(GridData.FILL_HORIZONTAL);
        topData.grabExcessHorizontalSpace = true;
        textComposite.setLayoutData(topData);
        
        Text repositoryLabel = new Text(textComposite, SWT.READ_ONLY | SWT.BOLD);
        repositoryLabel.setText("Repository");
        repositoryLabel.setBackground(MevenideColors.WHITE);
        repositoryLabel.setForeground(MevenideColors.BLUE_GRAY);
        
        repositoryUrlText = new Text(textComposite, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        repositoryUrlText.setLayoutData(data);
        
        Button browseRepoButton = new Button(textComposite, SWT.FLAT);
        browseRepoButton.setText("Browse");
        GridData browseButtonLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_END);
        browseButtonLayoutData.grabExcessHorizontalSpace = false;
        browseRepoButton.setLayoutData(browseButtonLayoutData);
    }

    public void setFocus() {
    }
}

