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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.dialogs.ListContentProvider;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.SourceDirectory;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroup;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroupLabelProvider;
import org.mevenide.ui.eclipse.util.SourceDirectoryTypeUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SourceDirectoryMappingViewControl {
	private static Log log = LogFactory.getLog(SourceDirectoryMappingViewControl.class);

	private static final String INHERIT = "isInherited";
    private static final String SOURCE_TYPE = "source.type";
    private static final String SOURCE_DIRECTORY = "source.directory";
    
	private SourceDirectoryMappingViewControl(){
	}
	
	public static TableViewer getViewer(Composite parent) {
		return getViewer(parent, SWT.NONE);	
	}
	
	public static TableViewer getViewer(Composite parent, int styles) {
		
		//configure viewer layout
		TableViewer tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | styles);
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
		
		column = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		column.setText("I");
		column.setWidth(16);

		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
	}
	private static void configureViewer(final TableViewer tableViewer) {
		tableViewer.setColumnProperties(new String[] {SourceDirectoryMappingViewControl.SOURCE_DIRECTORY, SourceDirectoryMappingViewControl.SOURCE_TYPE, SourceDirectoryMappingViewControl.INHERIT});
		
		tableViewer.setCellEditors(new CellEditor[] {
			new TextCellEditor(), 
			createComboBoxCellEditor(tableViewer),
			new CheckboxCellEditor(),
		});
		
		tableViewer.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return 
					SourceDirectoryMappingViewControl.SOURCE_TYPE.equals(property)
					|| SourceDirectoryMappingViewControl.INHERIT.equals(property);
			}
			
			public void modify(Object element, String property, Object value) {
				if ( SourceDirectoryMappingViewControl.SOURCE_TYPE.equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					((SourceDirectory) element).setDirectoryType(SourceDirectoryTypeUtil.sourceTypes[((Integer)value).intValue()]);
				}
				if ( SourceDirectoryMappingViewControl.INHERIT.equals(property) ) {
					if (element instanceof Item) {
						element = ((Item) element).getData();
					}
					log.debug("setting SourceDirectory isInherited property to : " + (((Boolean) value).booleanValue()));
					((SourceDirectory) element).setInherited(((Boolean) value).booleanValue());
				}
				tableViewer.update(element, null);
			}
			
			public Object getValue(Object element, String property) {
				if ( SourceDirectoryMappingViewControl.SOURCE_DIRECTORY.equals(property) ) {
					return ((SourceDirectory) element).getDisplayPath();
				}
				if ( SourceDirectoryMappingViewControl.INHERIT.equals(property) ) {
					return new Boolean(((SourceDirectory) element).isInherited());
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
