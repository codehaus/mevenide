/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package org.mevenide.ui.eclipse.sync.pom.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryPropertyView extends ViewPart {
	
	//doesnt work for now.. committing to keep a trace
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		StructuredViewer viewer = new TableViewer(composite, SWT.NULL);
		
		String[] sourceTypes = {
			"source",
			"aspect",
			"test",
			"integration",
			"resource",
			"test resource"
		};
		ComboBoxPropertyDescriptor propertyDescriptor = new ComboBoxPropertyDescriptor("source.directory", "Source Directory", sourceTypes);
		
		propertyDescriptor.setLabelProvider(new LabelProvider() {
			public Image getImage(Object element) {
				return null;
			}
			public String getText(Object element) {
				return "yosh";
			}
		});
		
		propertyDescriptor.createPropertyEditor((Composite)viewer.getControl());
		
		
		
		addContributions();
		
	}

	public void setFocus() {
	}
	
	private void addContributions() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		tbm.add(new Action() {
			public void run() {
				SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
			}	
		});
		getViewSite().getActionBars().updateActionBars();
	}

	public void init(IViewSite site, IMemento memento)
		throws PartInitException {
		// @todo Auto-generated method stub
		super.init(site, memento);
	}

	public void init(IViewSite site) throws PartInitException {
		// @todo Auto-generated method stub
		super.init(site);
	}

	public void saveState(IMemento memento) {
		// @todo Auto-generated method stub
		super.saveState(memento);
	}

	public static void showView() throws Exception {
		IViewPart consoleView =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MavenPlugin.SYNCH_VIEW_ID); 
	}
}

