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
package org.mevenide.ui.eclipse.wizard;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.TemplateContentProvider;
import org.mevenide.ui.eclipse.template.model.Templates;
import org.mevenide.ui.eclipse.template.view.TemplateViewerFactory;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class NewPomWizardSecondPage extends WizardPage {
    
    public NewPomWizardSecondPage() {
        super("Pom Definition");
    }
    
    public void createControl(Composite parent) {
        Composite topLevelContainer = new Composite(parent, SWT.NULL);
        topLevelContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
        topLevelContainer.setLayout(new GridLayout());
        
        createPomDefinitionArea(topLevelContainer);
        
        createTemplateArea(topLevelContainer);
        
        setControl(topLevelContainer);
    }

    private void createTemplateArea(Composite topLevelContainer) {
        Composite templateArea = new Composite(topLevelContainer, SWT.NULL);
        templateArea.setLayout(new GridLayout());
        templateArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Button useTemplateButton = new Button(templateArea, SWT.CHECK);
        useTemplateButton.setText(Mevenide.getResourceString("NewPomWizardSecondPage.UseTemplate.Button"));
        TreeViewer templateViewer = TemplateViewerFactory.createTemplateViewer(templateArea);  
        
        Templates templates = Templates.newTemplates();
        templates.addObserver((TemplateContentProvider) templateViewer.getContentProvider());
        templateViewer.setInput(templates);
    }

    private void createPomDefinitionArea(Composite topLevelContainer) {
        Composite pomDefinitionArea = new Composite(topLevelContainer, SWT.NULL);
        
        GridLayout layout = new GridLayout();
        pomDefinitionArea.setLayout(layout);
        pomDefinitionArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Group group = new Group(pomDefinitionArea, SWT.NULL);
        group.setText(Mevenide.getResourceString("NewPomWizardSecondPage.DefinitionsGroup.Text"));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout groupLayout = new GridLayout();
        groupLayout.makeColumnsEqualWidth = false;
        groupLayout.numColumns = 2;
        group.setLayout(groupLayout);
        
        createPomNameTextField(group);
        
        createPomGroupIdTextField(group);
        
        createPomArtifactIdTextField(group);
    }

    private void createPomArtifactIdTextField(Group group) {
        Text artifactIdText = createTextField(group, "NewPomWizardSecondPage.ArtifactId.Label");
    }

    private void createPomGroupIdTextField(Group group) {
        Text groupIdText = createTextField(group, "NewPomWizardSecondPage.GroupId.Label");
    }

    private void createPomNameTextField(Group group) {
        Text nameText = createTextField(group, "NewPomWizardSecondPage.Name.Label");
    }

    private Text createTextField(Group group, String labelKey) {
        Label label = new Label(group, SWT.NULL);
        label.setText(Mevenide.getResourceString(labelKey));
        Text text = new Text(group, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return text;
    }

    
    
}
