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
package org.mevenide.ui.eclipse.sync.wizard;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.project.Dependency;
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
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyPropertiesDialog extends Dialog {
	//private static Log log = LogFactory.getLog(DependencyPropertiesDialog.class);
	
	private Table table;
	private Map properties;
	
	private Dependency dependency;
	

	public DependencyPropertiesDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}

	public Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(gridData);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	
		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText(Mevenide.getResourceString("DependencyPropertiesDialog.properties.key.column.name"));
		column1.setWidth(200);
	
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText(Mevenide.getResourceString("DependencyPropertiesDialog.properties.value.column.name"));
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
		addButton.setText(Mevenide.getResourceString("DependencyPropertiesDialog.properties.add"));
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		addButton.setLayoutData(data1);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(table,SWT.NULL);
				item.setText(
					new String[] {
						Mevenide.getResourceString("DependencyPropertiesDialog.properties.key.new"), 
						Mevenide.getResourceString("DependencyPropertiesDialog.properties.value.new")
					}
				);
			} 
		});
	
	
		Button removeButton = new Button(buttonsArea, SWT.PUSH);
		removeButton.setText(Mevenide.getResourceString("DependencyPropertiesDialog.properties.remove"));
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		removeButton.setLayoutData(data2);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selectedItemIndex = table.getSelectionIndices()[0];
				table.remove(selectedItemIndex);
				
				properties.remove(table.getItem(selectedItemIndex).getText(0));
			}
		});
		
		initInput();
		
		return composite;
	}
	
	public void setInput(Dependency dep) {
		dependency = dep;
	}

	private void initInput() {
		Map dependencyProperties = dependency.resolvedProperties();
		//Map dependencyProperties = dependency.getProperties();
		if ( dependencyProperties != null ) {
			Iterator it = dependencyProperties.keySet().iterator();
			while ( it.hasNext() ) {
				String key = (String) it.next();
				String value = (String) dependencyProperties.get(key);
				TableItem item = new TableItem(table,SWT.NULL);
				item.setText(new String[] {key, value});
			}
		}
	}
	
	private void updateProperties() {
		properties = new Properties();
		for (int i = 0; i < table.getItems().length; i++) {
			TableItem item = table.getItems()[i];
			properties.put(item.getText(0), item.getText(1));
		}
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
												default: 
													//do nothing
													break;
											}
											break;
										default: 
											//do nothing
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
				if ( value == null ) {
					return Mevenide.getResourceString("DependencyPropertiesDialog.validator.null.error");	
				}
				if ( !(value instanceof String) ) {
					return Mevenide.getResourceString("DependencyPropertiesDialog.validator.non.string.error");
				}
				if ( ((String) value).trim().equals("") ) {
					return Mevenide.getResourceString("DependencyPropertiesDialog.validator.empty.value.error");
				}
				return null;
			}
		});
	}
	
	public Map getProperties() {
		return properties;
	}

	protected void okPressed() {
		updateProperties();
		super.okPressed();
	}

}
