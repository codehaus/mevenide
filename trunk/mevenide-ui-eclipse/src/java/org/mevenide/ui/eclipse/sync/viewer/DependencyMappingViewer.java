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
package org.mevenide.ui.eclipse.sync.viewer;

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
import org.mevenide.ui.eclipse.sync.model.dependency.DependencyGroupContentProvider;
import org.mevenide.ui.eclipse.sync.model.dependency.DependencyInfo;
import org.mevenide.ui.eclipse.sync.model.dependency.DependencyWrapper;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingViewer {
	
	static final String INHERITED = "inherited";
    static final String VALUE = "value";
    static final String ATTRIBUTE = "attribute";
    
    
    private DependencyMappingViewer(){
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
		gridData.heightHint = 450;
		
		tableTreeViewer.getTableTree().setLayoutData(gridData);
		
		configureViewer(tableTreeViewer);
		
		//configure table layout
		createTableColumns(table);
		
		return tableTreeViewer;
	}
	private static void createTableColumns(Table table) {
		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setText(Mevenide.getResourceString("DependencyMappingViewer.table.columns.attribute"));
		column.setWidth(200);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText(Mevenide.getResourceString("DependencyMappingViewer.table.columns.value"));
		column.setWidth(300);
		
		column = new TableColumn(table, SWT.LEFT);
		column.setText(Mevenide.getResourceString("DependencyMappingViewer.table.columns.inherited"));
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
				return 	( DependencyMappingViewer.VALUE.equals(property) && element instanceof DependencyInfo && !((DependencyInfo) element).isReadOnly())
					 || ( DependencyMappingViewer.INHERITED.equals(property) && element instanceof DependencyWrapper && !((DependencyWrapper) element).isReadOnly() );
					
			}
			
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item) {
					element = ((Item) element).getData();
				}
				if ( DependencyMappingViewer.VALUE.equals(property) ) {
					((DependencyInfo) element).setInfo((String)value);
				}
				if ( DependencyMappingViewer.INHERITED.equals(property) 
						&& ((DependencyWrapper) element).getDependencyGroup().isInherited() ) { 
					((DependencyWrapper) element).setInherited(((Boolean)value).booleanValue());
				}
				tableTreeViewer.refresh();
			}
			
			public Object getValue(Object element, String property) {
				if ( element instanceof DependencyWrapper && DependencyMappingViewer.INHERITED.equals(property) ) {
					return new Boolean(((DependencyWrapper) element).isInherited());
				}
				if ( element instanceof DependencyInfo && DependencyMappingViewer.ATTRIBUTE.equals(property) ) {
					return ((DependencyInfo) element).getDependency().getArtifact();
				}
				if ( element instanceof DependencyInfo && DependencyMappingViewer.VALUE.equals(property) ) {
					return ((DependencyInfo) element).getInfo();
				}
				else {
					return "";
				} 
			}
		});
		
		tableTreeViewer.setColumnProperties(new String[] {DependencyMappingViewer.ATTRIBUTE, DependencyMappingViewer.VALUE, DependencyMappingViewer.INHERITED});
		
		tableTreeViewer.setContentProvider(new DependencyGroupContentProvider());
		
		tableTreeViewer.setLabelProvider(new DependencyGroupLabelProvider());
	}
	
}
