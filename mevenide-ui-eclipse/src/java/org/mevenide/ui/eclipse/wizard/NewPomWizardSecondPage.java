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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;
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
    
    private String name;
    private String artifactId;
    private String groupId;
    
    private boolean useTemplate;
    private Template selectedTemplate;
    private TreeViewer templateViewer;
    
    private boolean pageComplete;
    
    public NewPomWizardSecondPage() {
        super(Mevenide.getResourceString("NewPomWizardSecondPage.Name"), Mevenide.getResourceString("NewPomWizardSecondPage.Name"), null);
        setDescription(Mevenide.getResourceString("NewPomWizardSecondPage.Description"));
        setPageComplete(false);
    }
    
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        Composite topLevelContainer = new Composite(parent, SWT.NULL);
        topLevelContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
        topLevelContainer.setLayout(new GridLayout());
        topLevelContainer.setFont(parent.getFont());
        
        createPomDefinitionArea(topLevelContainer);
        createTemplateArea(topLevelContainer);
        
        setControl(topLevelContainer);
        update();
    }

    private void createTemplateArea(Composite topLevelContainer) {
        Composite templateArea = new Composite(topLevelContainer, SWT.NULL);
        templateArea.setLayout(new GridLayout());
        templateArea.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createUseTemplateButton(templateArea);
        createTemplateViewer(templateArea);
    }

    private void createTemplateViewer(Composite templateArea) {
        templateViewer = TemplateViewerFactory.createTemplateViewer(templateArea);
        Templates templates = Templates.newTemplates();
        templates.addObserver((TemplateContentProvider) templateViewer.getContentProvider());
        templateViewer.setInput(templates);
        templateViewer.getTree().setEnabled(false);
        
        templateViewer.addSelectionChangedListener(
                new ISelectionChangedListener() {
                    public void selectionChanged(SelectionChangedEvent event) {
                        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                        selectedTemplate = (Template) selection.getFirstElement();
                    }
                });
    }

    private void createUseTemplateButton(Composite templateArea) {
        Button useTemplateButton = new Button(templateArea, SWT.CHECK);
        useTemplateButton.setText(Mevenide.getResourceString("NewPomWizardSecondPage.UseTemplate.Button"));
        useTemplateButton.addSelectionListener(
                new SelectionAdapter() {
	                public void widgetSelected(SelectionEvent event) {
	                    useTemplate = ((Button) event.getSource()).getSelection();
	                    templateViewer.getTree().setEnabled(useTemplate);
	                    update();
	                } 
                });
    }

    private void createPomDefinitionArea(Composite topLevelContainer) {
        Composite pomDefinitionArea = new Composite(topLevelContainer, SWT.NULL);
        
        GridLayout layout = new GridLayout();
        pomDefinitionArea.setLayout(layout);
        pomDefinitionArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Group group = createPomDefinitionGroup(pomDefinitionArea);
        createPomNameTextField(group);
        createPomGroupIdTextField(group);
        createPomArtifactIdTextField(group);
    }

    private Group createPomDefinitionGroup(Composite pomDefinitionArea) {
        Group group = new Group(pomDefinitionArea, SWT.NULL);
        group.setText(Mevenide.getResourceString("NewPomWizardSecondPage.DefinitionsGroup.Text"));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout groupLayout = new GridLayout();
        groupLayout.makeColumnsEqualWidth = false;
        groupLayout.numColumns = 2;
        group.setLayout(groupLayout);
        return group;
    }

    private void createPomArtifactIdTextField(Group group) {
        final Text artifactIdText = createTextField(group, "NewPomWizardSecondPage.ArtifactId.Label");
        artifactIdText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        artifactId = artifactIdText.getText();
                    }
                });
    }

    private void createPomGroupIdTextField(Group group) {
        final Text groupIdText = createTextField(group, "NewPomWizardSecondPage.GroupId.Label");
        groupIdText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        groupId = groupIdText.getText();
                    }
                });
    }

    private void createPomNameTextField(Group group) {
        final Text nameText = createTextField(group, "NewPomWizardSecondPage.Name.Label");
        nameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        name = nameText.getText();
                    }
                });
    }

    private Text createTextField(Group group, String labelKey) {
        Label label = new Label(group, SWT.NULL);
        label.setText(Mevenide.getResourceString(labelKey));
        Text text = new Text(group, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        update();
                    }
                });
        return text;
    }

    private void update() {
        pageComplete = (artifactId != null &&
        			    groupId != null &&
        			    name != null) || 
        			   useTemplate;
        setPageComplete(pageComplete);
        setErrorMessage(pageComplete ? null : Mevenide.getResourceString("NewPomWizardSecondPage.Error.Message")); 
    }
    
    public boolean isPageComplete() {
        return pageComplete;
    }
    
    public String getArtifactId() {
        return artifactId;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getName() {
        return name;
    }
}
