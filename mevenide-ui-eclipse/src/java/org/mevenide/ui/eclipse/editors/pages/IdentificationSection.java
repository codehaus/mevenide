/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Jeffrey Bonevich (jeff@bonevich.com).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse.editors.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.OverridableTextEntry;
import org.mevenide.ui.eclipse.editors.entries.PageEntry;
import org.mevenide.ui.eclipse.editors.entries.TextEntry;

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
	
    public IdentificationSection(OverviewPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("IdentificationSection.header"));
		setDescription(Mevenide.getResourceString("IdentificationSection.description"));
    }

    public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 4 : 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getEditor().getPom();
		
		// POM name textbox
		Button toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.pomNameText.label"),
			Mevenide.getResourceString("IdentificationSection.pomNameText.tooltip"),
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
			Mevenide.getResourceString("IdentificationSection.artifactIdText.label"), 
			Mevenide.getResourceString("IdentificationSection.artifactIdText.tooltip"),
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
			Mevenide.getResourceString("IdentificationSection.groupIdText.label"), 
			Mevenide.getResourceString("IdentificationSection.groupIdText.tooltip"), 
			factory
		);
		groupIdText = new OverridableTextEntry(createText(container, factory, 2), toggle, null);
		adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				pom.setGroupId((String) value);
			}
			public Object acceptParent() {
				return getParentPom().getGroupId();
			}
		};
		groupIdText.addEntryChangeListener(adaptor);
		groupIdText.addOverrideAdaptor(adaptor);
		
		// POM gumpRepositoryId textbox
		toggle = createOverrideToggle(container, factory);
		createLabel(
			container, 
			Mevenide.getResourceString("IdentificationSection.gumpRepoIdText.label"), 
			Mevenide.getResourceString("IdentificationSection.gumpRepoIdText.tooltip"), 
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
			Mevenide.getResourceString("IdentificationSection.pomVersionText.label"), 
			Mevenide.getResourceString("IdentificationSection.pomVersionText.tooltip"), 
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
			Mevenide.getResourceString("IdentificationSection.extendsText.label"), 
			Mevenide.getResourceString("IdentificationSection.extendsText.tooltip"), 
			factory
		);
		extendsText = new TextEntry(createText(container, factory));
		extendsText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
	                if (log.isDebugEnabled()) {
	                    log.debug("extend changed to " + extendsText.getText());
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

		String labelName = Mevenide.getResourceString("IdentificationSection.extendButton.label");
		String toolTip = Mevenide.getResourceString("IdentificationSection.extendButton.tooltip");
		extendButton = factory.createButton(buttonContainer, labelName, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		extendButton.setLayoutData(data);
		extendButton.setToolTipText(toolTip);

		final String title = Mevenide.getResourceString("IdentificationSection.extendButton.dialog.title");
		final String message = Mevenide.getResourceString("IdentificationSection.extendButton.dialog.message");
		extendButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						try { 
							IEditorInput input = getPage().getEditor().getEditorInput();
							IFile pomFile = ((IFileEditorInput) input).getFile();
							ViewerFilter filter = new PomResourceFilter(pomFile);
							ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
								getPage().getEditor().getSite().getShell(),
								new WorkbenchLabelProvider(),
								new WorkbenchContentProvider()
							);
							dialog.setTitle(title);
							dialog.setMessage(message);
							dialog.setInput(Mevenide.getWorkspace().getRoot());
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
											path.append("../");
											path.append(pomResource.getProject().getName());
											path.append("/");
										}
										break;
									}
									path.append("../");
								}
								path.append(pomResource.getProjectRelativePath());
								
								if ( path != null ) {
									extendsText.setFocus();
									extendsText.setText(path.toString());
								}
							}
						}
						catch ( Exception ex ) {
							log.error("Unable to browse for POM to extend", ex);
						}
					}
				}
		);
		
		factory.paintBordersFor(container);
        return container;
    }

    public void update(Project pom) {
        if (log.isDebugEnabled()) {
            log.debug("updating id section content");
        }
		setIfDefined(pomNameText, pom.getName(), isInherited() ? getParentPom().getName() : null);
		if (isInherited()) {
			String parentVersion = getPage().getEditor().getParentPom().getPomVersion();
			setIfDefined(pomVersionText, parentVersion);
			// force local override with parent if inherited
			// Seems that Maven defaults it to 1 if it goes un-specified
			pom.setPomVersion(getPage().getEditor().getParentPom().getPomVersion());
		}
		else {
			setIfDefined(pomVersionText, pom.getPomVersion());
		}
		setIfDefined(extendsText, pom.getExtend());
		setIfDefined(artifactIdText, pom.getArtifactId(), isInherited() ? getParentPom().getArtifactId() : null);
		setIfDefined(groupIdText, pom.getGroupId(), isInherited() ? getParentPom().getGroupId() : null);
		setIfDefined(gumpRepoIdText, pom.getGumpRepositoryId(), isInherited() ? getParentPom().getGumpRepositoryId() : null);

		super.update(pom);
    }

}
