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

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.mevenide.reports.DefaultReportsFinder;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.pom.entries.DraggableTableEntry;

/**
 * @author Jeffrey Bonevich (jeff@bonevich.com)
 * @version $Id$
 */
public class ReportsSection extends PageSection {

	private static final Log log = LogFactory.getLog(ReportsSection.class);
	
	private TableViewer availableReportsViewer;
	private TableViewer includedReportsViewer;
	private Button overrideToggle;
	private DraggableTableEntry reportsEntry;
	private TreeSet availableReports = new TreeSet();
    
	public ReportsSection(
		ReportsPage page, 
		Composite parent, 
		FormToolkit toolkit) 
   	{
        super(page, parent, toolkit);
		setTitle(Mevenide.getResourceString("ReportsSection.header"));
		setDescription(Mevenide.getResourceString("ReportsSection.description"));
	}

    public Composite createSectionContent(Composite parent, FormToolkit factory) {
		Composite container = factory.createComposite(parent);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 2;
		layout.marginHeight = 2;
		layout.spacing = 5;
		container.setLayout(layout);
		
		try {
			IReportsFinder finder = new DefaultReportsFinder();
			String[] reports = finder.findReports();
			availableReports = new TreeSet(Arrays.asList(reports));
		} catch (Exception e) {
			log.error("Unable to find reports", e);
		}
		
		availableReportsViewer = createAvailableReportsViewer(container, factory);		
		includedReportsViewer = createIncludedReportsViewer(container, factory);
		
		reportsEntry = new DraggableTableEntry(availableReportsViewer, includedReportsViewer, overrideToggle);
		OverrideAdaptor adaptor = new OverrideAdaptor() {
			public void overrideParent(Object value) {
				setReports((List) value);
			}
			public Object acceptParent() {
				resetViewers();
				return getParentPom().getReports();
			}
		};
		reportsEntry.addEntryChangeListener(adaptor);
		reportsEntry.addOverrideAdaptor(adaptor);

		factory.paintBordersFor(container);
		return container;
	}

	private TableViewer createAvailableReportsViewer(Composite container, FormToolkit factory) {
		Composite availableContainer = factory.createComposite(container);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 2 : 1;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		availableContainer.setLayout(layout);
		
		if (isInherited()) createSpacer(availableContainer, factory);
		factory.createLabel(
		    availableContainer, 
		    Mevenide.getResourceString("ReportsSection.available.reports.label"),
		    SWT.BOLD
		);

		overrideToggle = createOverrideToggle(availableContainer, factory, 1, true);

		TableViewer viewer = new TableViewer(availableContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setSorter(new ViewerSorter());
		GridData data = new GridData(GridData.FILL_BOTH);
		viewer.getTable().setLayoutData(data);
		
		return viewer;
	}

	private TableViewer createIncludedReportsViewer(Composite container, FormToolkit factory) {
		Composite includedContainer = factory.createComposite(container);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		includedContainer.setLayout(layout);
		
		factory.createLabel(includedContainer, Mevenide.getResourceString("ReportsSection.included.reports.label"));

		TableViewer viewer = new TableViewer(includedContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		GridData data = new GridData(GridData.FILL_BOTH);
		viewer.getTable().setLayoutData(data);

		return viewer;
	}

	private void setReports(List reports) {
		if (log.isDebugEnabled()) {
			log.debug("setting reports on pom: " + reports);
		}
		getPage().getPomEditor().getPom().setReports(reports);
		getPage().getPomEditor().setModelDirty(true);
	}

	public void update(Project pom) {
		if (log.isDebugEnabled()) {
			log.debug("updating reports = " + pom.getReports());
		}
		resetViewers();

		List pomReports = pom.getReports();
		List parentReports = isInherited() ? getParentPom().getReports() : null;
		if (pomReports != null && !pomReports.isEmpty()) {
			reportsEntry.addEntries(pomReports);
			reportsEntry.setInherited(false);
		}
		else if (parentReports != null) {
			reportsEntry.addEntries(parentReports, true);
			reportsEntry.setInherited(true);
		}
		else {
			reportsEntry.setInherited(false);
		}
	}

	private void resetViewers() {
		availableReportsViewer.getTable().removeAll();
		availableReportsViewer.add(availableReports.toArray());
		includedReportsViewer.getTable().removeAll();
	}

}
