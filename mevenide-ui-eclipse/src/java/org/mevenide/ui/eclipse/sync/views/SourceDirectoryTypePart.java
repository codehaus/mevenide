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
import org.eclipse.ui.internal.dialogs.ListContentProvider;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.source.*;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryTypePart extends ViewPart {
	/** 2 columns table viewer [source dir, source type] where source type is displayed in a CCombo */
	private TableViewer viewer;
	
	private IProject project;
	
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
		this.project = project;
		//@todo manage project swapping via the memento
		if ( viewer.getContentProvider() != null ) {
			SourceDirectoryGroup newInput = null ;
			try {
				
				newInput = getSavedInput(project);
			}
			catch (Exception e) {
				e.printStackTrace();
		
			}
			if ( newInput == null ) {
				newInput = new SourceDirectoryGroup(project);
			}
			
			viewer.setInput(newInput);
		}
	}

	private SourceDirectoryGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = MavenPlugin.getPlugin().getFile("sourceTypes.xml");
		
		return SourceDirectoryMarshaller.getSourceDirectoryGroup(project, savedStates);
		
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
					return ((SourceDirectory) element).getDisplayPath();
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
				if (directoriesList != null ) {
					return directoriesList.toArray();
				}
				else {
					return new Object[0]; 
				}
			}
		});
		
		viewer.setLabelProvider(new SourceDirectoryLabelProvider(SourceDirectoryUtil.sourceTypes));
		
//		viewer.setSorter(new ViewerSorter() {
//			
//		});

		viewer.setInput(new SourceDirectoryGroup(MavenPlugin.getPlugin().getProject()));
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
				
				try {
					saveState();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
			}
		};	
		synchronizeAction.setImageDescriptor(MavenPlugin.getImageDescriptor("maven-run.gif"));
		synchronizeAction.setToolTipText("Synchronize");
		
		Action refreshAction = new Action() {
			public void run() {
				
				try {
					setInput(project);
				} catch (Exception e) {
					e.printStackTrace();
				}
	
			}
		};
		refreshAction.setImageDescriptor(MavenPlugin.getImageDescriptor("refresh.gif"));
		refreshAction.setToolTipText("Refresh View");
		
		tbm.add(refreshAction);
		tbm.add(synchronizeAction);
		
		getViewSite().getActionBars().updateActionBars();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
	}
	
	public void init(IViewSite site) throws PartInitException {
		setSite(site);
	}
	
	/**
	 * not sure how to use memento efficiciently so doesnt use it for now. 
	 * have to figure out how it works. 
	 * 
	 * @see SourceDirectoryMarshaller 
	 */
	public void saveState(IMemento memento) {
	}

	public void saveState() throws Exception {
		SourceDirectoryMarshaller.saveSourceDirectoryGroup((SourceDirectoryGroup)viewer.getInput(), MavenPlugin.getPlugin().getFile("sourceTypes.xml"));
	}

 	
	public static void showView() throws Exception {
		IViewPart consoleView =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MavenPlugin.SYNCH_VIEW_ID); 
	}

}
