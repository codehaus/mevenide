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
        //container.setBackground(MevenideColors.WHITE);
        
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
        composite.setLayoutData(layoutData);
        
        repositoryViewer = new TreeViewer(composite, SWT.NULL);
        RepositoryContentProvider contentProvider = new RepositoryContentProvider();
        contentProvider.addRepositoryEventListener(this);
        repositoryViewer.setContentProvider(contentProvider);
        repositoryViewer.setLabelProvider(new RepositoryObjectLabelProvider());
        repositoryViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        repositoryViewer.setInput(repositoryUrl);
    }

    private void createRepositoryUrlComposite(Composite container) {
        Composite composite = new Composite(container, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(layoutData);
        
        repositoryUrlText = new Text(composite, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        repositoryUrlText.setData(data);
        
        Button browseRepoButton = new Button(composite, SWT.FLAT);
        browseRepoButton.setLayoutData(new GridData());
        browseRepoButton.setText("Browse");
        browseRepoButton.setData(new GridData());
    }

    public void setFocus() {
    }
}

