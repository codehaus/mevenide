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

import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.util.ResourceSorter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class DescriptionSection extends PageSection {

	private static final Log log = LogFactory.getLog(DescriptionSection.class);

	private OverridableTextEntry shortDescText;
	private OverridableTextEntry inceptionYearText;
	private OverridableTextEntry urlText;
	private OverridableTextEntry logoText;
	private OverridableTextEntry packageText;
	private Button logoButton;
	private Button packageButton;

    public DescriptionSection(AbstractPomEditorPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("DescriptionSection.header"));
		setDescription(Mevenide.getResourceString("DescriptionSection.description"));
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
		String labelName = Mevenide.getResourceString("DescriptionSection.shortDescText.label");
		shortDescText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited())
		);
		shortDescText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					String shortDesc = shortDescText.getText();
					pom.setShortDescription(shortDesc);
				}
			}
		);
		shortDescText.addOverrideAdaptor(
			new OverrideAdaptor() {
				public void updateProject(String value) {
					pom.setShortDescription(value);
				}
				public String getParentProjectAttribute() {
					return getParentPom().getShortDescription();
				}
			}
		);
		
		// POM project inception year textbox
		labelName = Mevenide.getResourceString("DescriptionSection.inceptionYearText.label");
		inceptionYearText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited())
		);
		inceptionYearText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					String year = inceptionYearText.getText();
					pom.setShortDescription(year);
				}
			}
		);
		inceptionYearText.addOverrideAdaptor(
			new OverrideAdaptor() {
				public void updateProject(String value) {
					pom.setInceptionYear(value);
				}
				public String getParentProjectAttribute() {
					return getParentPom().getInceptionYear();
				}
			}
		);
		
		// POM project url textbox
		labelName = Mevenide.getResourceString("DescriptionSection.urlText.label");
		urlText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited())
		);
		urlText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					String url = urlText.getText();
					pom.setUrl(url);
				}
			}
		);
		urlText.addOverrideAdaptor(
			new OverrideAdaptor() {
				public void updateProject(String value) {
					pom.setUrl(value);
				}
				public String getParentProjectAttribute() {
					return getParentPom().getUrl();
				}
			}
		);
		
		// POM extend textbox and file browse button
		labelName = Mevenide.getResourceString("DescriptionSection.logoText.label");
		logoText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited())
		);
		logoText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					String logo = logoText.getText();
					pom.setLogo(logo);
				}
			}
		);
		logoText.addOverrideAdaptor(
			new OverrideAdaptor() {
				public void updateProject(String value) {
					pom.setLogo(value);
				}
				public String getParentProjectAttribute() {
					return getParentPom().getLogo();
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

		labelName = Mevenide.getResourceString("DescriptionSection.logoButton.label");
		String toolTip = Mevenide.getResourceString("DescriptionSection.logoButton.tooltip");
		logoButton = factory.createButton(buttonContainer, labelName, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		logoButton.setLayoutData(data);
		logoButton.setToolTipText(toolTip);

		final String title = Mevenide.getResourceString("DescriptionSection.logoButton.dialog.title");
		logoButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						FileDialog dialog = new FileDialog(
							getPage().getEditor().getSite().getShell(),
							SWT.NULL
						);
						dialog.setText(title);
						
						String imageFile = dialog.open();
						if (imageFile != null) {
							logoText.setFocus();
							logoText.setText(imageFile);
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for logo images", ex);
					}
				}
			}
		);
		
		// POM package textbox and package browse button
		labelName = Mevenide.getResourceString("DescriptionSection.packageText.label");
		packageText = new OverridableTextEntry(
			createText(container, labelName, factory), 
			createOverrideToggle(container, factory, isInherited())
		);
		packageText.addEntryChangeListener(
			new EntryChangeListenerAdaptor() {
				public void entryChanged(PageEntry entry) {
					String packageName = packageText.getText();
					pom.setPackage(packageName);
				}
			}
		);
		packageText.addOverrideAdaptor(
			new OverrideAdaptor() {
				public void updateProject(String value) {
					pom.setPackage(value);
				}
				public String getParentProjectAttribute() {
					return getParentPom().getPackage();
				}
			}
		);
		
		buttonContainer = factory.createComposite(container);
		data = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_END);
		data.horizontalSpan = 1;
		buttonContainer.setLayoutData(data);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);

		labelName = Mevenide.getResourceString("DescriptionSection.packageButton.label");
		toolTip = Mevenide.getResourceString("DescriptionSection.packageButton.tooltip");
		packageButton = factory.createButton(buttonContainer, labelName, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		packageButton.setLayoutData(data);
		packageButton.setToolTipText(toolTip);

		final String title1 = Mevenide.getResourceString("DescriptionSection.packageButton.dialog.title");
		final String message1 = Mevenide.getResourceString("DescriptionSection.packageButton.dialog.message");
		packageButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						IProject project = Mevenide.getWorkspace().getRoot().getProject();
						IJavaProject javaProject = JavaCore.create(project);
						SelectionDialog dialog = JavaUI.createPackageDialog(
							getPage().getEditor().getSite().getShell(),
							javaProject,
							IJavaElementSearchConstants.CONSIDER_REQUIRED_PROJECTS
						);
						dialog.setTitle(title1);
						dialog.setMessage(message1);
						
						if (dialog.open() == Window.OK) {
							IResource packageResource = (IResource) dialog.getResult()[0];
							packageButton.setFocus();
							packageButton.setText(packageResource.toString());
						}
					}
					catch ( Exception ex ) {
						log.error("Unable to browse for packages", ex);
					}
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
    }

	public void update(Project pom) {
		setIfDefined(shortDescText, pom.getShortDescription(), isInherited() ? getParentPom().getShortDescription() : null);
		setIfDefined(inceptionYearText, pom.getInceptionYear(), isInherited() ? getParentPom().getInceptionYear() : null);
		setIfDefined(urlText, pom.getUrl(), isInherited() ? getParentPom().getUrl() : null);
		setIfDefined(logoText, pom.getLogo(), isInherited() ? getParentPom().getLogo() : null);
		setIfDefined(packageText, pom.getPackage(), isInherited() ? getParentPom().getPackage() : null);

		super.update(pom);
	}
	
}
