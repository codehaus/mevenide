/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

import java.io.InputStream;
import org.apache.maven.util.StringInputStream;
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
import org.mevenide.project.io.PomSkeletonBuilder;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.model.Template;
import org.mevenide.ui.eclipse.template.model.TemplateContentProvider;
import org.mevenide.ui.eclipse.template.model.Templates;
import org.mevenide.ui.eclipse.template.view.TemplateViewerFactory;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class NewPomWizardSecondPage extends WizardPage {
    
    private String pomName;
    private String artifactId;
    private String groupId;
    private String version;
    private String shortDescription;
    
    private boolean useTemplate;
    private Template selectedTemplate;
    private TreeViewer templateViewer;
    
    private boolean pageComplete;
    
    public NewPomWizardSecondPage() {
        super(Mevenide.getResourceString("NewPomWizardSecondPage.Name"), Mevenide.getResourceString("NewPomWizardSecondPage.Name"), null);  //$NON-NLS-1$//$NON-NLS-2$
        setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_POM_WIZ));
        setDescription(Mevenide.getResourceString("NewPomWizardSecondPage.Description")); //$NON-NLS-1$
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
                        update();
                    }
                });
    }

    private void createUseTemplateButton(Composite templateArea) {
        Button useTemplateButton = new Button(templateArea, SWT.CHECK);
        useTemplateButton.setText(Mevenide.getResourceString("NewPomWizardSecondPage.UseTemplate.Button")); //$NON-NLS-1$
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
        createPomVersionTextField(group);
        createPomShortDescriptionTextField(group);
    }

    private Group createPomDefinitionGroup(Composite pomDefinitionArea) {
        Group group = new Group(pomDefinitionArea, SWT.NULL);
        group.setText(Mevenide.getResourceString("NewPomWizardSecondPage.DefinitionsGroup.Text")); //$NON-NLS-1$
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout groupLayout = new GridLayout();
        groupLayout.makeColumnsEqualWidth = false;
        groupLayout.numColumns = 2;
        group.setLayout(groupLayout);
        return group;
    }
    
    private void createPomShortDescriptionTextField(Group group) {
        final Text shortDescriptionText = createTextField(group, "NewPomWizardSecondPage.ShortDescription.Label"); //$NON-NLS-1$
        shortDescriptionText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        shortDescription = shortDescriptionText.getText();
                        update();
                    }
                });
    }

    private void createPomVersionTextField(Group group) {
        final Text versionText = createTextField(group, "NewPomWizardSecondPage.Version.Label"); //$NON-NLS-1$
        versionText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        version = versionText.getText();
                        update();
                    }
                });
    }
    
    private void createPomArtifactIdTextField(Group group) {
        final Text artifactIdText = createTextField(group, "NewPomWizardSecondPage.ArtifactId.Label"); //$NON-NLS-1$
        artifactIdText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        artifactId = artifactIdText.getText();
                        update();
                    }
                });
    }

    private void createPomGroupIdTextField(Group group) {
        final Text groupIdText = createTextField(group, "NewPomWizardSecondPage.GroupId.Label"); //$NON-NLS-1$
        groupIdText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        groupId = groupIdText.getText();
                        update();
                    }
                });
    }

    private void createPomNameTextField(Group group) {
        final Text nameText = createTextField(group, "NewPomWizardSecondPage.Name.Label"); //$NON-NLS-1$
        nameText.addModifyListener(
                new ModifyListener() {
                    public void modifyText(ModifyEvent event) {
                        pomName = nameText.getText();
                        update();
                    }
                });
    }

    private Text createTextField(Group group, String labelKey) {
        Label label = new Label(group, SWT.NULL);
        label.setText(Mevenide.getResourceString(labelKey));
        Text text = new Text(group, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return text;
    }

    protected InputStream getInitialContents() throws Exception {
        PomSkeletonBuilder builder = null;
        if ( !useTemplate ) {
            builder = PomSkeletonBuilder.getSkeletonBuilder();
        }
        else {
            builder = PomSkeletonBuilder.getSkeletonBuilder(selectedTemplate.getProject().getFile().getAbsolutePath());
        }
        String skeleton = builder.getPomSkeleton(pomName != null ? pomName : ((NewPomWizard) getWizard()).getContainerName(), 
                                                 groupId, artifactId, version, shortDescription);
        return new StringInputStream(skeleton);
    }

    
    private void update() {
        pageComplete = (!StringUtils.isNull(artifactId) &&
                		!StringUtils.isNull(groupId) &&
                		!StringUtils.isNull(pomName) &&
                		!StringUtils.isNull(version) &&
                		!StringUtils.isNull(shortDescription)) || 
        			   (useTemplate && 
        			    selectedTemplate != null );
        setPageComplete(pageComplete);
        setErrorMessage(pageComplete ? null : Mevenide.getResourceString("NewPomWizardSecondPage.Error.Message"));  //$NON-NLS-1$
    }
    
    public boolean isPageComplete() {
        return pageComplete;
    }
}
