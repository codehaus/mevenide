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
package org.mevenide.ui.eclipse.sync.wizard;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyPropertiesDialog extends Dialog {
	
	public DependencyPropertiesDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}

	public Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		final Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(gridData);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	
		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText("key");
		column1.setWidth(200);
	
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText("value");
		column2.setWidth(200);
	
		createTableEditor(table);
		createTableValidator(table);
	
		//initTableItems(table);
	
		Composite buttonsArea = new Composite(composite, SWT.NULL);
		GridLayout bLayout = new GridLayout();
		buttonsArea.setLayout(bLayout);
		GridData topData = new GridData(GridData.FILL_BOTH);
		buttonsArea.setLayoutData(topData);
	
		Button addButton = new Button(buttonsArea, SWT.PUSH);
		addButton.setText("Add");
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		addButton.setLayoutData(data1);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(table,SWT.NULL);
				item.setText(new String[] {"<custom key>", "<custom value>"});
			} 
		});
	
	
		Button removeButton = new Button(buttonsArea, SWT.PUSH);
		removeButton.setText("Remove");
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		removeButton.setLayoutData(data2);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.remove(table.getSelectionIndices());
			}
		});
		
		return composite;
	}

	//from eclipse snippets :
	//http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/platform-swt-home/snippits/snippet124.html
	private void createTableEditor(final Table table) {
		
		final TableEditor editor = new TableEditor (table);
		
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener (SWT.MouseDoubleClick, new Listener () {
			public void handleEvent (Event event) {
				
				Rectangle clientArea = table.getClientArea ();
				Point pt = new Point (event.x, event.y);
				int index = table.getTopIndex ();
				while (index < table.getItemCount ()) {
					boolean visible = false;
					final TableItem item = table.getItem (index);
					for (int i=0; i<table.getColumnCount (); i++) {
						Rectangle rect = item.getBounds (i);
						if (rect.contains (pt)) {
							final int column = i;
							final Text text = new Text (table, SWT.NONE);
							Listener textListener = new Listener () {
								public void handleEvent (final Event e) {
									switch (e.type) {
										case SWT.FocusOut:
											item.setText (column, text.getText ());
											text.dispose ();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													item.setText (column, text.getText ());
													//FALL THROUGH
												case SWT.TRAVERSE_ESCAPE:
													text.dispose ();
													e.doit = false;
											}
											break;
									}
								}
							};
							text.addListener (SWT.FocusOut, textListener);
							text.addListener (SWT.Traverse, textListener);
							editor.setEditor (text, item, i);
							text.setText (item.getText (i));
							text.selectAll ();
							text.setFocus ();
							return;
						}
						if (!visible && rect.intersects (clientArea)) {
							visible = true;
						}
					}
					if (!visible) return;
					index++;
				}
			}
		});
	}
	

	/**
	 * 
	 *
	 * thats obviously not the correct way to use it 
	 * 
	 * @author g.dodinet
	 */
	private void createTableValidator(Table table) {
		final TextCellEditor cellEditor = new TextCellEditor(table);
		cellEditor.setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				System.out.println("VALIDATING");
				if ( value == null ) {
					return "null value not allowed";	
				}
				if ( !(value instanceof String) ) {
					return "non String values not allowed";
				}
				if ( ((String) value).trim().equals("") ) {
					return "empty values not allowed";
				}
				return null;
			}
		});
	}
	





		
}
