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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.License;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class LicenseSection extends PageSection {
	
	private static final Log log = LogFactory.getLog(LicenseSection.class);

	private Table table;
	private TableViewer licenseViewer;
	private Button addButton, removeButton, upButton, downButton;
	
    public LicenseSection(OrganizationPage page) {
        super(page);
		setHeaderText(Mevenide.getResourceString("LicenseSection.header"));
		setDescription(Mevenide.getResourceString("LicenseSection.description"));
    }

    public Composite createClient(Composite parent, PageWidgetFactory factory) {
		Composite container = factory.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 3 : 2;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		container.setLayout(layout);
		
		final Project pom = getPage().getEditor().getPom();
		
		// POM license table
		Button toggle = createOverrideToggle(container, factory, 1, true);
		table = new Table(container, SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		
		licenseViewer = new TableViewer(table);
		licenseViewer.setContentProvider(new WorkbenchContentProvider());
		licenseViewer.setLabelProvider(new WorkbenchLabelProvider());
		
		Composite buttonContainer = factory.createComposite(container);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		buttonContainer.setLayoutData(data);
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonContainer.setLayout(layout);
		
		addButton = factory.createButton(buttonContainer, "Add", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		addButton.setLayoutData(data);
		addButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					log.debug("adding");
					License license = new License();
					pom.addLicense(license);
					LicensePropertySource source = new LicensePropertySource(license);
					licenseViewer.add(source);
				}
			}
		);

		removeButton = factory.createButton(buttonContainer, "Remove", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		removeButton.setLayoutData(data);
		removeButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					log.debug("removing");
					IStructuredSelection selected = (IStructuredSelection) licenseViewer.getSelection();
					licenseViewer.remove(selected.toArray());
				}
			}
		);

		upButton = factory.createButton(buttonContainer, "Up", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		upButton.setLayoutData(data);
		upButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					log.debug("moving up");
					IStructuredSelection selected = (IStructuredSelection) licenseViewer.getSelection();
					int index = licenseViewer.getTable().getSelectionIndex();
					if (index > 0) {
						Object item = licenseViewer.getElementAt(index);
						licenseViewer.remove(item);
						licenseViewer.insert(item,--index);
						licenseViewer.getTable().select(index);
					}
				}
			}
		);

		downButton = factory.createButton(buttonContainer, "Down", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		downButton.setLayoutData(data);
		downButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					log.debug("moving down");
					IStructuredSelection selected = (IStructuredSelection) licenseViewer.getSelection();
					int index = licenseViewer.getTable().getSelectionIndex();
					if (index >= 0 && index < licenseViewer.getTable().getItemCount() - 1) {
						Object item = licenseViewer.getElementAt(index);
						licenseViewer.remove(item);
						licenseViewer.insert(item, ++index);
						licenseViewer.getTable().select(index);
					}
				}
			}
		);

		removeButton.setEnabled(false);
		upButton.setEnabled(false);
		downButton.setEnabled(false);

		licenseViewer.addSelectionChangedListener(
		new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				getPage().getEditor().setPropertySourceSelection(e.getSelection());
			}
		}
		);
		
		licenseViewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					if (e.getSelection().isEmpty()) {
						removeButton.setEnabled(false);
						upButton.setEnabled(false);
						downButton.setEnabled(false);
					} else {
						removeButton.setEnabled(true);
						upButton.setEnabled(true);
						downButton.setEnabled(true);
					}
					
				}
			}
		);
		
		factory.paintBordersFor(container);
		return container;
    }
	
	public void update(Project pom) {
		Iterator itr = pom.getLicenses().iterator();
		while (itr.hasNext()) {
			licenseViewer.add(new LicensePropertySource((License) itr.next()));
		}
		
		super.update(pom);
	}

}
