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
package org.mevenide.ui.eclipse.sync.dependency;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyViewUtil {
	
	private DependencyViewUtil(){
	}
	
	
	public static TableTreeViewer getViewer(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginHeight=0;
		layout.marginWidth=0;
		parent.setLayout(layout);
		

		
		
		
		//configure viewer layout
		TableTreeViewer tableTreeViewer = new TableTreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		
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
				return "value".equals(property) && element instanceof DependencyContentProvider.DependencyInfo;
			}
			
			public void modify(Object element, String property, Object value) {
				if ( "value".equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					((DependencyContentProvider.DependencyInfo) element).setInfo((String)value);
					tableTreeViewer.update(element, null);
				}
			}
			
			public Object getValue(Object element, String property) {
				if ( element instanceof DependencyContentProvider.DependencyInfo && "attribute".equals("property") ) {
					return ((DependencyContentProvider.DependencyInfo) element).getDependency().getArtifact();
				}
				if ( element instanceof DependencyContentProvider.DependencyInfo && "value".equals(property) ) {
					return ((DependencyContentProvider.DependencyInfo) element).getInfo();
				}
				else {
					return "";
				} 
			}
		});

		tableTreeViewer.setContentProvider(new DependencyContentProvider());
		
		//tableTreeViewer.setLabelProvider(new );
		
//		viewer.setSorter(new ViewerSorter() {
//			
//		});

		tableTreeViewer.setLabelProvider(new DependencyLabelProvider());

		tableTreeViewer.setInput(new DependencyGroup(Mevenide.getPlugin().getProject()));
	}
	
}
