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

/**
 * Section for identification info of the POM (i.e. name, ids, extended POM, etc.)
 * 
 * @author Jeff Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class PomIdentificationSection extends PageSection {
	
	private static final Log log = LogFactory.getLog(PomIdentificationSection.class);
    
	private boolean isInherited;
	private OverridableTextEntry pomNameText;
	private TextEntry pomVersionText;
	private TextEntry extendsText;
	private OverridableTextEntry artifactIdText;
	private OverridableTextEntry groupIdText;
	private Button extendButton;
	
	private Project parentPom;

    public PomIdentificationSection(OverviewPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("PomIdentificationSection.header"));
		setDescription(Mevenide.getResourceString("PomIdentificationSection.description"));
		
		this.parentPom = page.getEditor().getParentPom();
		if (parentPom != null) isInherited = true;
    }

    public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getEditor().getPom();
		
		// POM name textbox
		String labelName = Mevenide.getResourceString("PomIdentificationSection.pomNameText.label");
		pomNameText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited)
		);
		pomNameText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					if (log.isDebugEnabled()) {
						log.debug("pom updated from changed entry; name = " + pomNameText.getText());
					}
					String pomName = pomNameText.getText();
					pom.setName(pomName);
					((OverviewPage) getPage()).setHeading(pom);
				}
			}
		);
		pomNameText.addSelectionListener(
			pomNameText.new OverridableSelectionAdapter() {
				public void updateProject(String value) {
					pom.setName(value);
				}
				public String getParentProjectAttribute() {
					return parentPom.getName();
				}
				public void refreshUI() {
					PomIdentificationSection.this.redrawSection();
				}
			}
		);
		
		// POM version textbox 
		labelName = Mevenide.getResourceString("PomIdentificationSection.pomVersionText.label");
		pomVersionText = new TextEntry(createText(container, labelName, factory));
		factory.createSpacer(container);
		pomVersionText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					pom.setPomVersion(pomVersionText.getText());
				}
			}
		);
		if (isInherited) {
			pomVersionText.setEnabled(false);
		}
		
		// POM extend textbox and file browse button
		labelName = Mevenide.getResourceString("PomIdentificationSection.extendsText.label");
		extendsText = new TextEntry(createText(container, labelName, factory));
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
		GridData data = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_END);
		data.horizontalSpan = 1;
		buttonContainer.setLayoutData(data);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);

		labelName = Mevenide.getResourceString("PomIdentificationSection.extendButton.label");
		String toolTip = Mevenide.getResourceString("PomIdentificationSection.extendButton.tooltip");
		extendButton = factory.createButton(buttonContainer, labelName, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		extendButton.setLayoutData(data);
		extendButton.setToolTipText(toolTip);

		final String title = Mevenide.getResourceString("PomIdentificationSection.extendButton.dialog.title");
		final String message = Mevenide.getResourceString("PomIdentificationSection.extendButton.dialog.message");
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
		
		// POM artifactId textbox
		labelName = Mevenide.getResourceString("PomIdentificationSection.artifactIdText.label");
		artifactIdText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited)
		);
		artifactIdText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					pom.setArtifactId(artifactIdText.getText());
				}
			}
		);
		artifactIdText.addSelectionListener(
			artifactIdText.new OverridableSelectionAdapter() {
				public void updateProject(String value) {
					pom.setArtifactId(value);
				}
				public String getParentProjectAttribute() {
					return parentPom.getArtifactId();
				}
				public void refreshUI() {
					PomIdentificationSection.this.redrawSection();
				}
			}
		);
		
		// POM groupId textbox
		labelName = Mevenide.getResourceString("PomIdentificationSection.groupIdText.label");
		groupIdText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited)
		);
		groupIdText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					pom.setGroupId(groupIdText.getText());
				}
			}
		);
		groupIdText.addSelectionListener(
			groupIdText.new OverridableSelectionAdapter() {
				public void updateProject(String value) {
					pom.setGroupId(value);
				}
				public String getParentProjectAttribute() {
					return parentPom.getGroupId();
				}
				public void refreshUI() {
					PomIdentificationSection.this.redrawSection();
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
		setIfDefined(pomNameText, pom.getName(), isInherited ? parentPom.getName() : null);
		if (isInherited) {
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
		setIfDefined(artifactIdText, pom.getArtifactId(), isInherited ? parentPom.getArtifactId() : null);
		setIfDefined(groupIdText, pom.getGroupId(), isInherited ? parentPom.getGroupId() : null);
		redrawSection();
    }

}
