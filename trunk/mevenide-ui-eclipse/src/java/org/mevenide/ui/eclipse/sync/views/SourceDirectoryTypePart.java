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
package org.mevenide.ui.eclipse.sync.views;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.internal.dialogs.ListContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryTypePart extends ViewPart {

	private TableViewer viewer;
	private IMemento memento;
	
	private static SourceDirectoryTypePart partInstance; 
	
	//public constructor : it should be instantiated by Eclipse 
	public SourceDirectoryTypePart() {
		partInstance = this;
	}
	
	public static SourceDirectoryTypePart getInstance() {
		return partInstance;
	}
	
	public void createPartControl(Composite parent) {
		Composite composite = parent;
		GridLayout layout = new GridLayout();
		layout.marginHeight=0;
		layout.marginWidth=0;
		parent.setLayout(layout);
		
		//configure viewer layout
		viewer = new TableViewer(parent, SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		viewer.getTable().setLayoutData(gridData);
		
		configureViewer();
		
		//configure table layout
		createTableColumns();

		getSite().setSelectionProvider(viewer);
		
		//add action buttons
		addContributions();
		
		
	}

	public void setInput(IProject project) {
		//@todo manage project swapping via the memento
		 viewer.setInput(new SourceDirectoryGroup(project));
	}

	private void createTableColumns() {
		TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Source Directory");
		column.setWidth(200);
		
		column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText("Source Type");
		column.setWidth(300);
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
	}

	private void configureViewer() {
		viewer.setColumnProperties(new String[] {"source.directory", "source.type"});
		
		viewer.setCellEditors(new CellEditor[] {
			new TextCellEditor(), 
			createComboBoxCellEditor()
		});
		
		viewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return "source.type".equals(property);
			}
			
			public void modify(Object element, String property, Object value) {
				if ( "source.type".equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					((SourceDirectory) element).setDirectoryType(SourceDirectoryUtil.sourceTypes[((Integer)value).intValue()]);
					viewer.update(element, new String[] {"source.type"});
				}
			}
			
			public Object getValue(Object element, String property) {
				if ( "source.directory".equals(property) ) {
					return ((SourceDirectory) element).getDirectoryPath();
				}
				else {
					return SourceDirectoryUtil.getSourceTypeIndex(((SourceDirectory) element).getDirectoryType());
				} 
			}
		});
		
		viewer.setContentProvider(new ListContentProvider(){
			public Object[] getElements(Object input) {
				Assert.isTrue(input instanceof SourceDirectoryGroup);
				List directoriesList = ((SourceDirectoryGroup) input).getSourceDirectories();
				return directoriesList.toArray();
			}
		});
		
		viewer.setLabelProvider(new SourceDirectoryLabelProvider(SourceDirectoryUtil.sourceTypes));
		
//		viewer.setSorter(new ViewerSorter() {
//			
//		});
	}

	private ComboBoxCellEditor createComboBoxCellEditor() {
		ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(viewer.getTable(), SourceDirectoryUtil.sourceTypes, SWT.READ_ONLY);
		comboBoxCellEditor.activate();
		return comboBoxCellEditor;
	}

	public void setFocus() {
	}

	private void addContributions() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		Action synchronizeAction = new Action() {
			public void run() {
				saveState(memento);
				SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
			}
		};	
		synchronizeAction.setImageDescriptor(MavenPlugin.getImageDescriptor("maven-run.gif"));
		tbm.add(synchronizeAction);
		getViewSite().getActionBars().updateActionBars();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		if ( memento == null ) {
			memento = XMLMemento.createWriteRoot("sourceDirectory");
		}
		super.init(site, memento);
	}
	
	public void init(IViewSite site) throws PartInitException {
		setSite(site);
	}
	
	public void saveState(IMemento memento) {
		IMemento[] mementos = memento.getChildren("projects");
		for (int i = 0; i < mementos.length; i++) {
			
		}
	}

	public static void showView() throws Exception {
		IViewPart consoleView =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MavenPlugin.SYNCH_VIEW_ID); 
	}

}
