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
package org.mevenide.ui.eclipse.launch;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenConstants;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.mevenide.OptionsRegistry;
import org.mevenide.ui.eclipse.Mevenide;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class LaunchWizardPage extends WizardPage {
	private static Log log = LogFactory.getLog(LaunchWizardPage.class);
	
	private final char[] options = new char[] { 'E', 'X', 'e', 'o' };
	
	private Map optionsMap = new TreeMap();
	
	private Map sysProperties = new TreeMap();
	
	private PreferenceStore store;
	
	private Collection goals = new TreeSet(); 
	private Combo goalsComboList;
	private String selectedGoals;
	
	private Table table;

    public LaunchWizardPage() {
		super(
			Mevenide.getResourceString("MavenLaunchPage.name"), 
			Mevenide.getResourceString("MavenLaunchPage.title"), 
			Mevenide.getImageDescriptor("maven-run-64.gif"));
	   
	   setDescription(Mevenide.getResourceString("MavenLaunchPage.description"));
	   
	   try {
           store = new PreferenceStore(Mevenide.getPlugin().getFile("prefs.ini"));
           store.load();
       }
       catch (IOException e) {
           e.printStackTrace();
       }
	}
	
	
	public void createControl(Composite parent) {
    	try {
    		
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.makeColumnsEqualWidth = false;

			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(layout);


			createCheckBoxes(composite);
			log.debug("checkboxes initialized");
			
			createSysPropertiesTable(composite);
			log.debug("table initialized");
			
			createGoalsList(composite);
			log.debug("goals list initialized");
			
			//createMavenVersionLabel(composite);
			
			
            setControl(composite);
        }
        catch (Exception e) {

			//e.printStackTrace();
			log.debug("Unable to LaunchWizardPage control due to : " + e);
        }
    }
	
	

	private void createMavenVersionLabel(Composite composite) {
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		
		Label label = new Label(composite, SWT.READ_ONLY);
		label.setText("Maven version: " + MavenConstants.POM_VERSION);
		
		label.setLayoutData(data);
	}

	private void createCheckBoxes(Composite parent) throws Exception {
		
		initOptionsMap();
		
		Composite comp = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		layout.numColumns = 2;
		comp.setLayout(layout);
	
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
		
		
	
		for (int i = 0; i < options.length; i++) {
			char option = options[i];
			GridData buttonDataLayout = new GridData();
			buttonDataLayout.grabExcessHorizontalSpace = true;
			final Button optionButton = new Button(comp, SWT.CHECK);
			optionButton.setLayoutData(buttonDataLayout);
			
			optionButton.setText(OptionsRegistry.getDescription(option));
			optionButton.setToolTipText(new StringBuffer(" -").append(option).toString());
			optionButton.setSelection(((Boolean)optionsMap.get(new Character(option))).booleanValue());
			
			optionButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					char option = optionButton.getToolTipText().charAt(2);
					boolean oldValue = 
						optionsMap.get(new Character(option)) == null ? 
							false : ((Boolean)optionsMap.get(new Character(option))).booleanValue();
                  	optionsMap.remove(new Character(option));
                  	optionsMap.put(new Character(option), new Boolean(!oldValue));
				}
			});    
		}
	}

    Map getOptionsMap() {
        return optionsMap;
    }

	private void initOptionsMap() {
		for (int i = 0; i < options.length; i++) {
        	boolean bool = store.getBoolean("launch.options.switch." + options[i]); 
        	optionsMap.put( new Character(options[i]), new Boolean(bool));
        }
	}

	private void createSysPropertiesTable(Composite parent) {
		
		initSysProperties();
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(gridData);
			
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText("key");
		column1.setWidth(200);
		
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText("value");
		column2.setWidth(200);
		
		createTableEditor();
		createTableValidator();
		
		initTableItems(table);
		
		Composite buttonsArea = new Composite(parent, SWT.NULL);
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
		
	}
	
	private void initSysProperties() {
		String keys = store.getString("launch.options.sysprops.keys");
		if ( keys != null && !keys.trim().equals("") ) {
			StringTokenizer tokenizer = new StringTokenizer(keys," ");
			while ( tokenizer.hasMoreTokens() ) {
				String nextKey = tokenizer.nextToken();
				String nextValue = store.getString("launch.options.sysprops.values." + nextKey);
				if ( nextValue != null && !nextValue.trim().equals("") ) {
					sysProperties.put(nextKey, nextValue);
				} 
			}
		}
	}
	
	private void initTableItems(Table table) {
				
		Set keys = sysProperties.keySet();
		Iterator keyIterator = keys.iterator();
		while ( keyIterator.hasNext() ) {
			String nextKey = (String) keyIterator.next();
			String nextValue = (String) sysProperties.get(nextKey);
			
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(new String[] { nextKey, nextValue } );			
		}
	}


	private void createGoalsList(Composite parent) {
		
		initGoals();
		
		goalsComboList = new Combo(parent, SWT.BORDER);
		goalsComboList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Object[] goalsObjects = goals.toArray();
		String[] goalsStrings = new String[goalsObjects.length]; 
		for (int i = 0; i < goalsObjects.length; i++) {
			goalsStrings[i] = (String) goalsObjects[i];
		}
		goalsComboList.setItems(goalsStrings);
		
		goalsComboList.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					log.debug("setting selectedGoals to=" + ((Combo)event.getSource()).getText());
					selectedGoals = ((Combo)event.getSource()).getText();
				}
			}
		);
		
		goalsComboList.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					log.debug("setting selectedGoals to=" + goalsComboList.getItem(goalsComboList.getSelectionIndex()));
					selectedGoals = goalsComboList.getItem(goalsComboList.getSelectionIndex());
				}
			}	
		);
		
		if ( selectedGoals != null ) {
			goalsComboList.setText(selectedGoals);
		}
		
		Composite buttonsArea = new Composite(parent, SWT.NULL);
		GridLayout bLayout = new GridLayout();
		buttonsArea.setLayout(bLayout);
		GridData topData = new GridData(GridData.FILL_HORIZONTAL);
		buttonsArea.setLayoutData(topData);
		
		Button chooseButton = new Button(buttonsArea, SWT.PUSH);
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		chooseButton.setLayoutData(data1);
		chooseButton.setText("Choose...");
		chooseButton.setEnabled(false);
	}

	private void initGoals() {
		String storedGoals = store.getString("launch.options.goals");
		int idx = 0;
		StringTokenizer tokenizer = new StringTokenizer(storedGoals, ",");
		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			if ( idx == 0 ) selectedGoals = token;
			goals.add(token);
			idx++;
		}
		
	}

	//from eclipse snippets :
	//http://dev.eclipse.org/viewcvs/index.cgi/~checkout~/platform-swt-home/snippits/snippet124.html
	private void createTableEditor() {
		
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
	private void createTableValidator() {
		final TextCellEditor cellEditor = new TextCellEditor(table);
		cellEditor.setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
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
	
    String getGoals() {
    	log.debug("selectedGoals=" + selectedGoals);
        return selectedGoals;
    }

    Map getSysProperties() {
        return sysProperties;
    }

	void performFinish() throws Exception {
		storeGoals();
		
		storeSysProperties();
		
		storeOptions();
        
        store.save();
	}


    private void storeOptions() {
        for (int i = 0; i < options.length; i++) {
        	store.setValue("launch.options.switch." + options[i], ((Boolean) optionsMap.get(new Character(options[i]))).booleanValue());
        }
    }


    private void storeGoals() {
    	String allGoals = selectedGoals;
    	Iterator goalsIterator = goals.iterator(); 
    	while ( goalsIterator.hasNext() ) {
    		String nextGoal = (String) goalsIterator.next();
    		if ( !nextGoal.equals(selectedGoals) ) {
				log.debug("storing nextGoal=" + nextGoal);
	    		allGoals += "," + nextGoal;
    		} 
		}
        store.setValue("launch.options.goals", allGoals);
    }


    private void storeSysProperties() {
    
    	TableItem[] items = table.getItems();
    	for (int i = 0; i < items.length; i++) {
        	String key = items[i].getText(0);
        	String value = items[i].getText(1);
        	if ( sysProperties.containsKey(key) ) {
        		sysProperties.remove(key);
        	}
        	sysProperties.put(key, value);
        }
    	
        Set keySet = sysProperties.keySet();
		Iterator keyIterator = keySet.iterator();
		String keys = "";
		while ( keyIterator.hasNext() ) {
			String key = (String) keyIterator.next();
			keys += key + " ";
			String value = (String) sysProperties.get(key);
			store.setValue("launch.options.sysprops.values." + key, value); 
		}
		store.setValue("launch.options.sysprops.keys", keys);
    }
}