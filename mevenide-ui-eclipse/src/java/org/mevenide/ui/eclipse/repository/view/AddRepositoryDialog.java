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
import java.util.List;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class AddRepositoryDialog extends TitleAreaDialog {
    
    private String repository;
    
    private StringFieldEditor fieldEditor;

    private boolean addMirror;

    private Combo mirrorCombo;
    
    public AddRepositoryDialog() {
        super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
        super.setBlockOnOpen(true);
    }
    
    public String getRepository() {
        return repository;
    }
    
    protected void okPressed() {
        if ( !addMirror ) {
            repository = fieldEditor.getStringValue();
        }
        else {
            repository = mirrorCombo.getText();
        }
        super.okPressed();
    }
    
    protected Control createDialogArea(Composite composite) {
        getShell().setText("Add Repository");
		setShellStyle(SWT.RESIZE | SWT.APPLICATION_MODAL);
		setTitle("Add a new repository");
		setTitleImage(Mevenide.getInstance().getImageRegistry().get(IImageRegistry.NEW_MAVEN_REPO_WIZ));
		
		GridLayout topLayout = new GridLayout();
        topLayout.marginHeight = 5;
        topLayout.marginWidth = 5;
        composite.setLayout(topLayout);
        
        final Composite container = new Composite(composite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 7;
        layout.marginWidth = 7;
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        container.setLayout(layout);

        GridData topData = new GridData(GridData.FILL_BOTH);
        topData.grabExcessHorizontalSpace = true;
        topData.grabExcessVerticalSpace = true;
        container.setLayoutData(topData);
        topData.horizontalIndent = 15;
        
        fieldEditor = new StringFieldEditor("fake", "Repository", container); 
        
        Button button = new Button(container, SWT.CHECK);
        button.setText("Add Mirror");
        button.addSelectionListener(new SelectionListener() {
	        public void widgetDefaultSelected(SelectionEvent arg0) { }
	        public void widgetSelected(SelectionEvent event) {
	            addMirror = ((Button) event.getSource()).getSelection();
	            mirrorCombo.setEnabled(addMirror);
	            fieldEditor.setEnabled(!addMirror, container);
	        }
        });
        
        mirrorCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
        mirrorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        List repos = new ArrayList();
        repos.addAll(RepositoryList.MIRRORS);
        repos.removeAll(RepositoryList.getUserDefinedRepositories());
        mirrorCombo.setItems((String[]) repos.toArray(new String[repos.size()]));
        mirrorCombo.setEnabled(false);
        
        return container;
    }
    
}
