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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.DependencyGroup;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupContentProvider;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupLabelProvider;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingViewControl {
	
	private DependencyMappingViewControl(){
	}
	
	public static TableTreeViewer getViewer(Composite parent) {
		return getViewer(parent, SWT.NONE);	
	}
	
	public static TableTreeViewer getViewer(Composite parent, int styles) {
			
		//configure viewer layout
		TableTreeViewer tableTreeViewer = new TableTreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | styles);
		
		Table table = tableTreeViewer.getTableTree().getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
				
		tableTreeViewer.getTableTree().setLayoutData(gridData);
		
		configureViewer(tableTreeViewer);
		
		//configure table layout
		createTableColumns(table);
		
		return tableTreeViewer;
	}
	private static void createTableColumns(Table table) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText("Attribute");
		column.setWidth(200);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("Value");
		column.setWidth(300);
		
	}
	private static void configureViewer(final TableTreeViewer tableTreeViewer) {
		tableTreeViewer.setColumnProperties(new String[] {"attribute", "value"});
		
		TextCellEditor editor = new TextCellEditor(tableTreeViewer.getTableTree().getTable());
		editor.activate();
		
		tableTreeViewer.setCellEditors(new CellEditor[] {
			new TextCellEditor(),
			editor
			
		});
		
		tableTreeViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return "value".equals(property) && element instanceof DependencyGroupContentProvider.DependencyInfo;
			}
			
			public void modify(Object element, String property, Object value) {
				if ( "value".equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					((DependencyGroupContentProvider.DependencyInfo) element).setInfo((String)value);
					tableTreeViewer.update(element, null);
				}
			}
			
			public Object getValue(Object element, String property) {
				if ( element instanceof DependencyGroupContentProvider.DependencyInfo && "attribute".equals("property") ) {
					return ((DependencyGroupContentProvider.DependencyInfo) element).getDependency().getArtifact();
				}
				if ( element instanceof DependencyGroupContentProvider.DependencyInfo && "value".equals(property) ) {
					return ((DependencyGroupContentProvider.DependencyInfo) element).getInfo();
				}
				else {
					return "";
				} 
			}
		});

		tableTreeViewer.setContentProvider(new DependencyGroupContentProvider());
		
		//tableTreeViewer.setLabelProvider(new );
		
//		viewer.setSorter(new ViewerSorter() {
//			
//		});

		tableTreeViewer.setLabelProvider(new DependencyGroupLabelProvider());

		tableTreeViewer.setInput(new DependencyGroup(Mevenide.getPlugin().getProject()));
	}
	
}
