/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
package org.mevenide.ui.eclipse.template.view;

import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;
import org.mevenide.ui.eclipse.template.model.Templates;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ChooseTemplateDialog extends TitleAreaDialog {
    private Template selectedTemplate;
    private boolean useTemplate;
    private Combo templatesCombo;
    
    public ChooseTemplateDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.setBlockOnOpen(true);
	}
	
	protected Control createDialogArea(Composite parent) {
	    getShell().setText(Mevenide.getResourceString("ChoosePomTemplateDialog.Shell.Text")); //$NON-NLS-1$
	    setTitle(Mevenide.getResourceString("ChoosePomTemplateDialog.Title")); //$NON-NLS-1$
		setMessage(Mevenide.getResourceString("ChoosePomTemplateDialog.Message")); //$NON-NLS-1$
	    setShellStyle(SWT.RESIZE | SWT.APPLICATION_MODAL);
	    Composite composite = new Composite(parent, SWT.RESIZE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 5;
        layout.marginWidth = 5;
        layout.numColumns = 1;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createUseTemplateButton(composite);
        createTemplatesCombo(composite);
		
        return composite;
	}
	
	
    private void createTemplatesCombo(Composite composite) {
        final Templates templates = Templates.newTemplates();
        
		templatesCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SINGLE);
        templatesCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		templatesCombo.add(Mevenide.getResourceString("ChooseTemplateDialog.DefaultTemplate.Label")); //$NON-NLS-1$
		for (int i = 0; i < templates.getTemplates().length; i++) {
			templatesCombo.add(((Template) templates.getTemplates()[i]).getTemplateName());
		}
		templatesCombo.select(0);
		templatesCombo.setEnabled(false);
        
		SelectionListener selectionProvider = new SelectionListener() {
            	public void widgetDefaultSelected(SelectionEvent arg0) { }
            	public void widgetSelected(SelectionEvent event) {
            	    int index = templatesCombo.getSelectionIndex();
            	    if ( index > 0 ) {
            	        selectedTemplate = (Template) templates.getTemplates()[index - 1];
            	    }
            	    else {
            	        selectedTemplate = null;
            	    }
            	}
        };
		templatesCombo.addSelectionListener(selectionProvider);
    }

    private void createUseTemplateButton(Composite composite) {
        Button useTemplateButton = new Button(composite, SWT.CHECK);
		useTemplateButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useTemplateButton.setText(Mevenide.getResourceString("CreatePomAction.template.UseTemplate.Button.Text")); //$NON-NLS-1$
		useTemplateButton.setSelection(false);
		useTemplateButton.addSelectionListener(
				new SelectionListener() {
				   public void widgetDefaultSelected(SelectionEvent arg0) { }
			       public void widgetSelected(SelectionEvent event) {
			           useTemplate = ((Button) event.getSource()).getSelection();
			           templatesCombo.setEnabled(useTemplate);
			       }
				});
    }

    public Template getTemplate() {
        return useTemplate ? selectedTemplate : null;
    }
    
}
