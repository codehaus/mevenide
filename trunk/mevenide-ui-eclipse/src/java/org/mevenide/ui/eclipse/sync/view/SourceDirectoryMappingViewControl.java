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
 * 
 */
package org.mevenide.ui.eclipse.sync.view;

import java.util.List;

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
import org.eclipse.ui.internal.dialogs.ListContentProvider;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.*;
import org.mevenide.ui.eclipse.util.*;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SourceDirectoryMappingViewControl {
	
	private SourceDirectoryMappingViewControl(){
	}
	public static TableViewer getViewer(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginHeight=0;
		layout.marginWidth=0;
		parent.setLayout(layout);
		
		//configure viewer layout
		TableViewer tableViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		tableViewer.getTable().setLayoutData(gridData);
		
		configureViewer(tableViewer);
		
		//configure table layout
		createTableColumns(tableViewer);
		
		return tableViewer;
	}
	private static void createTableColumns(TableViewer tableViewer) {
		TableColumn column = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		column.setText("Source Directory");
		column.setWidth(200);
		
		column = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		column.setText("Source Type");
		column.setWidth(300);
		
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
	}
	private static void configureViewer(final TableViewer tableViewer) {
		tableViewer.setColumnProperties(new String[] {"source.directory", "source.type"});
		
		tableViewer.setCellEditors(new CellEditor[] {
			new TextCellEditor(), 
			createComboBoxCellEditor(tableViewer)
		});
		
		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return "source.type".equals(property);
			}
			
			public void modify(Object element, String property, Object value) {
				if ( "source.type".equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					((SourceDirectory) element).setDirectoryType(SourceDirectoryTypeUtil.sourceTypes[((Integer)value).intValue()]);
					tableViewer.update(element, new String[] {"source.type"});
				}
			}
			
			public Object getValue(Object element, String property) {
				if ( "source.directory".equals(property) ) {
					return ((SourceDirectory) element).getDisplayPath();
				}
				else {
					return SourceDirectoryTypeUtil.getSourceTypeIndex(((SourceDirectory) element).getDirectoryType());
				} 
			}
		});
		
		tableViewer.setContentProvider(new ListContentProvider(){
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
		
		tableViewer.setLabelProvider(new SourceDirectoryGroupLabelProvider(SourceDirectoryTypeUtil.sourceTypes));
		
//		viewer.setSorter(new ViewerSorter() {
//			
//		});

		tableViewer.setInput(new SourceDirectoryGroup(Mevenide.getPlugin().getProject()));
	}
	
	
	private static ComboBoxCellEditor createComboBoxCellEditor(TableViewer tableViewer) {
		ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(tableViewer.getTable(), SourceDirectoryTypeUtil.sourceTypes, SWT.READ_ONLY);
		comboBoxCellEditor.activate();
		return comboBoxCellEditor;
	}
}
