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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.mevenide.reports.DefaultReportsFinder;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ReportsSection extends PageSection {

	private static final Log log = LogFactory.getLog(OrganizationSection.class);
	
	private TableViewer reportViewer, useViewer;
    
	public ReportsSection(ReportsPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("ReportsSection.header"));
		setDescription(Mevenide.getResourceString("ReportsSection.description"));
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
		
		// Available reports table
		Button toggle = createOverrideToggle(container, factory, 1, true);

		Table table = factory.createTable(container, SWT.MULTI);
		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);

		reportViewer = new TableViewer(table);
		reportViewer.setContentProvider(new WorkbenchContentProvider());
		reportViewer.setLabelProvider(new LabelProvider());
		reportViewer.setSorter(new ViewerSorter());
		
		try {
			IReportsFinder finder = new DefaultReportsFinder();
			String[] reports = finder.findReports();
			TreeSet reportSet = new TreeSet(Arrays.asList(reports));
			reportViewer.add(reportSet.toArray());
		} catch (Exception e) {
			log.error("Unable to find reports", e);
		}

		Composite buttonContainer = factory.createComposite(container);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		buttonContainer.setLayout(layout);

		final Button addButton = factory.createButton(buttonContainer, "Add Report >>", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		addButton.setLayoutData(data);
		
		final Button removeButton = factory.createButton(buttonContainer, "<< Remove Report", SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		removeButton.setLayoutData(data);

		table = factory.createTable(container, SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);

		useViewer = new TableViewer(table);
		useViewer.setContentProvider(new WorkbenchContentProvider());
		useViewer.setLabelProvider(new LabelProvider());
		useViewer.setSorter(new ViewerSorter());
		
		reportViewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					if (e.getSelection().isEmpty()) {
						addButton.setEnabled(false);
					} else {
						addButton.setEnabled(true);
					}
				}
			}
		);
		useViewer.addPostSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent e) {
					if (e.getSelection().isEmpty()) {
						removeButton.setEnabled(false);
					} else {
						removeButton.setEnabled(true);
					}
				}
			}
		);
		
		addButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selected = (IStructuredSelection) reportViewer.getSelection();
					Object[] reportsToAdd = selected.toArray();
					reportViewer.remove(reportsToAdd);
					useViewer.add(reportsToAdd);
					
					setReports(pom, useViewer);
				}
			}
		);
		
		removeButton.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					IStructuredSelection selected = (IStructuredSelection) useViewer.getSelection();
					Object[] reportsToRemove = selected.toArray();
					useViewer.remove(reportsToRemove);
					reportViewer.add(reportsToRemove);
					
					setReports(pom, useViewer);
				}
			}
		);
		
		addButton.setEnabled(false);
		removeButton.setEnabled(false);

		factory.paintBordersFor(container);
		return container;
	}

	private void setReports(Project pom, TableViewer viewer) {
		int reportCount = viewer.getTable().getItemCount();
		List reports = new ArrayList(reportCount);
		for (int i = 0; i < reportCount; i++) {
			String report = (String) viewer.getElementAt(i);
			reports.add(report);
		}
		pom.setReports(reports);
		
		getPage().getEditor().setModelDirty(true);
	}

	public void update(Project pom) {
		List pomReports = pom.getReports();
		if (pomReports != null) {
			Object[] reports = pomReports.toArray();
			useViewer.add(reports);
			reportViewer.remove(reports);
		}
		
		super.update(pom);
	}

}
