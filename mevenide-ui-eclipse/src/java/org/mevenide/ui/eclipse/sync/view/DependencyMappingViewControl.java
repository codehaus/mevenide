/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
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
package org.mevenide.ui.eclipse.sync.view;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
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
import org.mevenide.ui.eclipse.sync.model.DependencyWrapper;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingViewControl {
	
	private static final String INHERITED = "inherited";
    private static final String VALUE = "value";
    private static final String ATTRIBUTE = "attribute";
    
    
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
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText("I");
		column.setWidth(16);
		
	}
	private static void configureViewer(final TableTreeViewer tableTreeViewer) {
		TextCellEditor editor = new TextCellEditor(tableTreeViewer.getTableTree().getTable());
		editor.activate();
		
		CheckboxCellEditor cbEditor =  new CheckboxCellEditor(tableTreeViewer.getTableTree().getTable());
		cbEditor.activate();
		
		
		tableTreeViewer.setCellEditors(new CellEditor[] {
			new TextCellEditor(),
			editor,
			cbEditor,
		});
		
		tableTreeViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return 
					DependencyMappingViewControl.VALUE.equals(property) && element instanceof DependencyGroupContentProvider.DependencyInfo
					|| DependencyMappingViewControl.INHERITED.equals(property) && element instanceof DependencyWrapper;
					
			}
			
			public void modify(Object element, String property, Object value) {
				if ( DependencyMappingViewControl.VALUE.equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					((DependencyGroupContentProvider.DependencyInfo) element).setInfo((String)value);
				}
				if ( DependencyMappingViewControl.INHERITED.equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}	
					((DependencyWrapper) element).setInherited(((Boolean)value).booleanValue());
				}
				tableTreeViewer.update(element, null);
			}
			
			public Object getValue(Object element, String property) {
				if ( element instanceof DependencyWrapper && DependencyMappingViewControl.INHERITED.equals(property) ) {
					return new Boolean(((DependencyWrapper) element).isInherited());
				}
				if ( element instanceof DependencyGroupContentProvider.DependencyInfo && DependencyMappingViewControl.ATTRIBUTE.equals(property) ) {
					return ((DependencyGroupContentProvider.DependencyInfo) element).getDependency().getArtifact();
				}
				if ( element instanceof DependencyGroupContentProvider.DependencyInfo && DependencyMappingViewControl.VALUE.equals(property) ) {
					return ((DependencyGroupContentProvider.DependencyInfo) element).getInfo();
				}
				else {
					return "";
				} 
			}
		});
		
		tableTreeViewer.setColumnProperties(new String[] {DependencyMappingViewControl.ATTRIBUTE, DependencyMappingViewControl.VALUE, DependencyMappingViewControl.INHERITED});
		
		tableTreeViewer.setContentProvider(new DependencyGroupContentProvider());
		
//		viewer.setSorter(new ViewerSorter() {
//			
//		});

		tableTreeViewer.setInput(new DependencyGroup(Mevenide.getPlugin().getProject()));
		
		tableTreeViewer.setLabelProvider(new DependencyGroupLabelProvider());
	}
	
}
