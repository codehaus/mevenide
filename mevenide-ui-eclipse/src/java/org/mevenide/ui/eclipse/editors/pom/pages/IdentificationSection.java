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
package org.mevenide.ui.eclipse.editors.pom.pages;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.MevenideResources;
import org.mevenide.ui.eclipse.editors.pom.entries.OverridableTextEntry;
import org.mevenide.ui.eclipse.editors.pom.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.pom.entries.TextEntry;
import org.mevenide.util.ProjectUtils;

/**
 * Section for identification info of the POM (i.e. name, ids, extended POM, etc.)
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class IdentificationSection extends PageSection {
	
	private static final Log log = LogFactory.getLog(IdentificationSection.class);
    
	private OverridableTextEntry pomNameText;
	private TextEntry pomVersionText;
	private TextEntry extendsText;
	private OverridableTextEntry artifactIdText;
	private OverridableTextEntry groupIdText;
	private OverridableTextEntry gumpRepoIdText;
	private Button extendButton;
	
   	public IdentificationSection(
   			OverviewPage page, 
   			Composite parent, 
   			FormToolkit toolkit) 
   	{
        super(page, parent, toolkit);
		setTitle(MevenideResources.IDENTIFIER_SECTION_HEADER);
		setDescription(MevenideResources.IDENTIFIER_SECTION_DESC);
    }

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 4 : 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getPomEditor().getPom();
		
		// POM name textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.pomNameText.label"), //$NON-NLS-1$
			Mevenide.getResourceString("IdentificationSection.pomNameText.tooltip"), //$NON-NLS-1$
			factory
		);
		pomNameText = new OverridableTextEntry(createText(container, factory, 2), toggle, null);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setName((String) value);
				((OverviewPage) getPage()).setHeading(pom);
			}
			public Object acceptParent() {
				return getParentPom().getName();
			}
		};
		pomNameText.addEntryChangeListener(adaptor);
		pomNameText.addOverrideAdaptor(adaptor);
		
		// POM artifactId textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.artifactIdText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("IdentificationSection.artifactIdText.tooltip"), //$NON-NLS-1$
			factory
		);
		artifactIdText = new OverridableTextEntry(createText(container, factory, 2), toggle, null);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setArtifactId((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getArtifactId();
			}
		};
		artifactIdText.addEntryChangeListener(adaptor);
		artifactIdText.addOverrideAdaptor(adaptor);
		
		// POM groupId textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.groupIdText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("IdentificationSection.groupIdText.tooltip"),  //$NON-NLS-1$
			factory
		);
		groupIdText = new OverridableTextEntry(createText(container, factory, 2), toggle, null);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setGroupId((String) value);
				//ProjectUtils.setGroupId(pom, (String) value);
			}
			public Object acceptParent() {
				//return getParentPom().getGroupId();
                pom.setGroupId(null);
				return ProjectUtils.getGroupId(getParentPom());
			}
		};
		groupIdText.addEntryChangeListener(adaptor);
		groupIdText.addOverrideAdaptor(adaptor);
		
		// POM gumpRepositoryId textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.gumpRepoIdText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("IdentificationSection.gumpRepoIdText.tooltip"),  //$NON-NLS-1$
			factory
		);
		gumpRepoIdText = new OverridableTextEntry(createText(container, factory, 2), toggle, null);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setGumpRepositoryId((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getGumpRepositoryId();
			}
		};
		gumpRepoIdText.addEntryChangeListener(adaptor);
		gumpRepoIdText.addOverrideAdaptor(adaptor);
		
		// POM version textbox 
		if (isInherited()) createSpacer(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.pomVersionText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("IdentificationSection.pomVersionText.tooltip"),  //$NON-NLS-1$
			factory
		);
		pomVersionText = new TextEntry(createText(container, factory, 2));
		pomVersionText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					pom.setPomVersion(pomVersionText.getText());
				}
			}
		);
		if (isInherited()) {
			pomVersionText.setEnabled(false);
		}
		
		// POM extend textbox and file browse button
		if (isInherited()) createSpacer(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.extendsText.label"),  //$NON-NLS-1$
			Mevenide.getResourceString("IdentificationSection.extendsText.tooltip"),  //$NON-NLS-1$
			factory
		);
		extendsText = new TextEntry(createText(container, factory));
		extendsText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
	                if (log.isDebugEnabled()) {
	                    log.debug("extend changed to " + extendsText.getText()); //$NON-NLS-1$
	                }
					pom.setExtend(extendsText.getText());
				}
			}
		);
		
		Composite buttonContainer = factory.createComposite(container);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		data.horizontalSpan = 1;
		buttonContainer.setLayoutData(data);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);

		String labelName = Mevenide.getResourceString("IdentificationSection.extendButton.label"); //$NON-NLS-1$
		String toolTip = Mevenide.getResourceString("IdentificationSection.extendButton.tooltip"); //$NON-NLS-1$
		extendButton = factory.createButton(buttonContainer, labelName, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		extendButton.setLayoutData(data);
		extendButton.setToolTipText(toolTip);

		final String title = Mevenide.getResourceString("IdentificationSection.extendButton.dialog.title"); //$NON-NLS-1$
		final String message = Mevenide.getResourceString("IdentificationSection.extendButton.dialog.message"); //$NON-NLS-1$
		extendButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						try { 
							IEditorInput input = getPage().getPomEditor().getEditorInput();
							IFile pomFile = ((IFileEditorInput) input).getFile();
							ViewerFilter filter = new PomResourceFilter(pomFile);
							ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
								getPage().getPomEditor().getSite().getShell(),
								new WorkbenchLabelProvider(),
								new WorkbenchContentProvider()
							);
							dialog.setTitle(title);
							dialog.setMessage(message);
							dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
							dialog.addFilter(filter);
							//dialog.setValidator();
							dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
							
							if (dialog.open() == Window.OK) {
								IResource pomResource = (IResource) dialog.getResult()[0];
								String pomProjectName = pomFile.getProject().getName();
								String extendProjectName = pomResource.getProject().getName();
								
								StringBuffer path = new StringBuffer();
								IContainer parentContainer;
								while ((parentContainer = pomFile.getParent()) != null) {
									String parentName = parentContainer.getName();
									if (parentName.equals(pomProjectName)) {
										if (!parentName.equals(extendProjectName)) {
											path.append("../"); //$NON-NLS-1$
											path.append(pomResource.getProject().getName());
											path.append("/"); //$NON-NLS-1$
										}
										break;
									}
									path.append("../"); //$NON-NLS-1$
								}
								path.append(pomResource.getProjectRelativePath());
								
								if ( path != null ) {
									extendsText.setFocus();
									extendsText.setText(path.toString());
								}
							}
						}
						catch ( Exception ex ) {
							log.error("Unable to browse for POM to extend", ex); //$NON-NLS-1$
						}
					}
				}
		);
		
		factory.paintBordersFor(container);
		return container;
    }

    public void update(Project pom) {
        if (log.isDebugEnabled()) {
            log.debug("updating id section content"); //$NON-NLS-1$
        }
		setIfDefined(pomNameText, pom.getName(), isInherited() ? getParentPom().getName() : null);
		if (isInherited()) {
			String parentVersion = getPage().getPomEditor().getParentPom().getPomVersion();
			setIfDefined(pomVersionText, parentVersion);
			// force local override with parent if inherited
			// Seems that Maven defaults it to 1 if it goes un-specified
			pom.setPomVersion(getPage().getPomEditor().getParentPom().getPomVersion());
		}
		else {
			setIfDefined(pomVersionText, pom.getPomVersion());
		}
		setIfDefined(extendsText, pom.getExtend());
		setIfDefined(artifactIdText, pom.getArtifactId(), isInherited() ? getParentPom().getArtifactId() : null);
		
		
        File pomFile = new File(((FileEditorInput) getPage().getPomEditor().getEditorInput()).getFile().getLocation().toOSString());


        IQueryContext queryContext = new DefaultQueryContext(pomFile.getParentFile());
        String inheritedGroupId = queryContext.getPOMContext().getFinalProject().getGroupId();
        
        setIfDefined(groupIdText, ProjectUtils.parseGroupId(getPomFile()), isInherited() ? inheritedGroupId : null);
		
		setIfDefined(gumpRepoIdText, pom.getGumpRepositoryId(), isInherited() ? getParentPom().getGumpRepositoryId() : null);
    }

}
