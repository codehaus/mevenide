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
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.mevenide.reports.DefaultReportsFinder;
import org.mevenide.reports.IReportsFinder;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.editors.entries.DraggableTableEntry;

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
    
	public ReportsSection(ReportsPage page) {
		super(page);
		setHeaderText(Mevenide.getResourceString("ReportsSection.header"));
		setDescription(Mevenide.getResourceString("ReportsSection.description"));
	}

	public Composite createClient(Composite parent, PageWidgetFactory factory) {
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

	private TableViewer createAvailableReportsViewer(Composite container, PageWidgetFactory factory) {
		Composite availableContainer = factory.createComposite(container);
		GridLayout layout = new GridLayout();
		layout.numColumns = isInherited() ? 2 : 1;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		availableContainer.setLayout(layout);
		
		if (isInherited()) createSpacer(availableContainer, factory);
		factory.createHeadingLabel(availableContainer, Mevenide.getResourceString("ReportsSection.available.reports.label"));

		overrideToggle = createOverrideToggle(availableContainer, factory, 1, true);

		TableViewer viewer = new TableViewer(availableContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		viewer.setSorter(new ViewerSorter());
		GridData data = new GridData(GridData.FILL_BOTH);
		viewer.getTable().setLayoutData(data);
		
		return viewer;
	}

	private TableViewer createIncludedReportsViewer(Composite container, PageWidgetFactory factory) {
		Composite includedContainer = factory.createComposite(container);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 2;
		layout.verticalSpacing = 7;
		layout.horizontalSpacing = 5;
		includedContainer.setLayout(layout);
		
		factory.createHeadingLabel(includedContainer, Mevenide.getResourceString("ReportsSection.included.reports.label"));

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
		getPage().getEditor().getPom().setReports(reports);
		getPage().getEditor().setModelDirty(true);
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
		
		super.update(pom);
	}

	private void resetViewers() {
		availableReportsViewer.getTable().removeAll();
		availableReportsViewer.add(availableReports.toArray());
		includedReportsViewer.getTable().removeAll();
	}

}
