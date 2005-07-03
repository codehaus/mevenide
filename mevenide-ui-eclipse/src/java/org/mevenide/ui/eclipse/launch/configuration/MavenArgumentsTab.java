/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.launch.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.mevenide.runner.OptionsRegistry;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.view.GoalsPickerDialog;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenArgumentsTab extends AbstractLaunchConfigurationTab  {
	private static Log log = LogFactory.getLog(AbstractLaunchConfigurationTab.class);

	public static final String OPTIONS_MAP = "OPTIONS_MAP"; //$NON-NLS-1$
	public static final String GOALS_TO_RUN = "GOALS_TO_RUN";	 //$NON-NLS-1$
	public static final String SYS_PROPERTIES = "SYS_PROPERTIES"; //$NON-NLS-1$
	
	private final char[] options = new char[] { 'E', 'X', 'e', 'o' };

	private Map optionsMap = new TreeMap();
	private Map sysProperties = new TreeMap();
	private String selectedGoals = ""; //$NON-NLS-1$

	private Map optionsButtons = new HashMap();

	private Text goalsText;
	private Table table;
	
	
	public MavenArgumentsTab() {
		setDirty(false);
	}
	
	public Image getImage() {
        return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.ARGUMENTS_TAB_ICON);
    }
	
	public String getName() {
		return Mevenide.getResourceString("MavenArgumentsTab.name"); //$NON-NLS-1$
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GOALS_TO_RUN, Mevenide.getInstance().getDefaultGoals());
	}

	
	public void createControl(Composite parent) {
		try {
		
			Font font = parent.getFont();
		
			Composite composite = new Composite(parent, SWT.NONE);
			setControl(composite);
			
			GridLayout topLayout = new GridLayout();
			topLayout.numColumns = 2;
			topLayout.makeColumnsEqualWidth = false;
			composite.setLayout(topLayout);		
			GridData gd = new GridData(GridData.FILL_BOTH);
			composite.setLayoutData(gd);
			composite.setFont(font);
			
			createCheckBoxes(composite);
			log.debug("checkboxes initialized"); //$NON-NLS-1$

			createSysPropertiesTable(composite);
			log.debug("table initialized"); //$NON-NLS-1$

			createGoalsText(composite);
			log.debug("goals list initialized"); //$NON-NLS-1$

			//createMavenVersionLabel(composite);

			
		}
		catch (Exception e) {
			//e.printStackTrace();
			log.debug("Unable to LaunchWizardPage control due to : " + e); //$NON-NLS-1$
		}
	}	
	
	private void createCheckBoxes(Composite parent) throws Exception {
		
		Composite comp = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		layout.numColumns = 2;
		comp.setLayout(layout);
	
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
		comp.setFont(parent.getFont());
		
		for (int i = 0; i < options.length; i++) {
			char option = options[i];
			GridData buttonDataLayout = new GridData();
			buttonDataLayout.grabExcessHorizontalSpace = true;
			final Button optionButton = new Button(comp, SWT.CHECK);
			optionButton.setLayoutData(buttonDataLayout);
			
			
			optionButton.setText(OptionsRegistry.getRegistry().getDescription(option));
			optionButton.setToolTipText(new StringBuffer(" -").append(option).toString()); //$NON-NLS-1$
			
			optionsButtons.put(new Character(option), optionButton);			


			optionButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					char option = optionButton.getToolTipText().charAt(2);
					boolean oldValue = 
						optionsMap.get(new Character(option)) == null ? 
							false : ((Boolean)optionsMap.get(new Character(option))).booleanValue();
                  	optionsMap.remove(new Character(option));
                  	optionsMap.put(new Character(option), new Boolean(!oldValue));
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});    
		}
	}

	private void createSysPropertiesTable(Composite parent) {
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(gridData);
			
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText(Mevenide.getResourceString("MavenArgumentsTab.system.properties.key.column.name")); //$NON-NLS-1$
		column1.setWidth(200);
		
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText(Mevenide.getResourceString("MavenArgumentsTab.system.properties.value.column.name")); //$NON-NLS-1$
		column2.setWidth(200);
		
		createTableEditor();
		
		Composite buttonsArea = new Composite(parent, SWT.NULL);
		GridLayout bLayout = new GridLayout();
		buttonsArea.setLayout(bLayout);
		GridData topData = new GridData(GridData.FILL_BOTH);
		buttonsArea.setLayoutData(topData);
		
		Button addButton = new Button(buttonsArea, SWT.PUSH);
		addButton.setText(Mevenide.getResourceString("MavenArgumentsTab.system.properties.add")); //$NON-NLS-1$
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		addButton.setLayoutData(data1);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(table,SWT.NULL);
				item.setText(
					new String[] {
						Mevenide.getResourceString("MavenArgumentsTab.system.properties.key.new"),  //$NON-NLS-1$
						Mevenide.getResourceString("MavenArgumentsTab.system.properties.value.new") //$NON-NLS-1$
					}
				);
				setDirty(true);
				updateLaunchConfigurationDialog();
			} 
		});
		
		
		Button removeButton = new Button(buttonsArea, SWT.PUSH);
		removeButton.setText(Mevenide.getResourceString("MavenArgumentsTab.system.properties.remove")); //$NON-NLS-1$
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		removeButton.setLayoutData(data2);
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				table.remove(table.getSelectionIndices());
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
	}
	
	private void initTableItems(Table table) {
		table.removeAll();	
		Set keys = sysProperties.keySet();
		Iterator keyIterator = keys.iterator();
		while ( keyIterator.hasNext() ) {
			String nextKey = (String) keyIterator.next();
			String nextValue = (String) sysProperties.get(nextKey);
			
			TableItem item = new TableItem(table, SWT.NULL);
			item.setText(new String[] { nextKey, nextValue } );			
		}
	}


	private void createGoalsText(Composite parent) {
		
		goalsText = new Text(parent, SWT.BORDER);
		goalsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		goalsText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					log.debug("setting selectedGoals to=" + ((Text)event.getSource()).getText()); //$NON-NLS-1$
					selectedGoals = ((Text)event.getSource()).getText();
					setDirty(true);
					
					updateLaunchConfigurationDialog();
				}
			}
		);
		
		goalsText.setText(selectedGoals);

		Composite buttonsArea = new Composite(parent, SWT.NULL);
		GridLayout bLayout = new GridLayout();
		buttonsArea.setLayout(bLayout);
		GridData topData = new GridData(GridData.FILL_HORIZONTAL);
		buttonsArea.setLayoutData(topData);
		
		Button chooseButton = new Button(buttonsArea, SWT.PUSH);
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		chooseButton.setLayoutData(data1);
		chooseButton.setText(Mevenide.getResourceString("MavenArgumentsTab.system.properties.choose")); //$NON-NLS-1$
		chooseButton.setEnabled(true);
		
		chooseButton.addSelectionListener(
			new SelectionAdapter() {
                public void widgetSelected(SelectionEvent arg0) {
					GoalsPickerDialog goalsPickerDialog = new GoalsPickerDialog();
					goalsPickerDialog.setGoalsOrder(goalsText.getText());
					int ok = goalsPickerDialog.open();
					if ( ok == Window.OK ) {
						selectedGoals = goalsPickerDialog.getOrderedGoals();
						goalsText.setText(goalsPickerDialog.getOrderedGoals());
					}
                }                	 
			}
		);
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
											updateLaunchConfigurationDialog();
											text.dispose ();
											break;
										case SWT.Traverse:
											switch (e.detail) {
												case SWT.TRAVERSE_RETURN:
													item.setText (column, text.getText ());
													updateLaunchConfigurationDialog();
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
		
   


	public void initializeFrom(ILaunchConfiguration configuration) {
		initOptionsMap(configuration);
		initGoals(configuration);
		initSysProperties(configuration);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		storeGoals(configuration);
		storeSysProperties(configuration);
		storeOptionsMap(configuration);
	}

	private void initSysProperties(ILaunchConfiguration configuration) {
		try {
			sysProperties = configuration.getAttribute(SYS_PROPERTIES, new HashMap());
			initTableItems(table);
		} 
		catch (CoreException e) {
			//e.printStackTrace();
			log.debug("Unable to init goals due to : " + e); //$NON-NLS-1$
		}
	}

	private void storeSysProperties(ILaunchConfigurationWorkingCopy configuration) {
       Map systemProperties = new HashMap();
       TableItem[] items = table.getItems();
	   for (int i = 0; i < items.length; i++) {
		   String key = items[i].getText(0);
		   String value = items[i].getText(1);
		   systemProperties.put(key, value);
	   }
	   configuration.setAttribute(SYS_PROPERTIES, systemProperties);

   }
	
	private void initGoals(ILaunchConfiguration configuration) {
		try {
			selectedGoals = configuration.getAttribute(GOALS_TO_RUN, ""); //$NON-NLS-1$
			log.debug("goalsText == null ? " + (goalsText == null)); //$NON-NLS-1$
			goalsText.setText(selectedGoals);
		} 
		catch (CoreException e) {
			//e.printStackTrace();
			log.debug("Unable to init goals due to : " + e); //$NON-NLS-1$
		}
    }

	private void storeGoals(ILaunchConfigurationWorkingCopy configuration) {
    	configuration.setAttribute(GOALS_TO_RUN, selectedGoals);	
    }

	private void initOptionsMap(ILaunchConfiguration configuration) {
		try {
			Map storedMap = configuration.getAttribute(OPTIONS_MAP, new HashMap());
			log.debug("stored Options Map .size() = " + storedMap.size()); //$NON-NLS-1$
			
			Iterator iter = storedMap.keySet().iterator();
			
			log.debug("initializing optionsMap : "); //$NON-NLS-1$
			
			while (iter.hasNext()) {
				String opt = (String) iter.next();
				String storedOpt = (String) storedMap.get(opt);
				
				log.debug("Before manipulating options : " + opt + " => " + storedOpt);  //$NON-NLS-1$//$NON-NLS-2$
				
				storedOpt = storedOpt == null ? "false" : storedOpt; //$NON-NLS-1$
				boolean optValue = Boolean.valueOf(storedOpt).booleanValue();
				
				log.debug("\t" + opt + " => " + optValue);  //$NON-NLS-1$//$NON-NLS-2$
				
				optionsMap.put(new Character(opt.charAt(0)), new Boolean(optValue)) ;
				((Button)optionsButtons.get(new Character(opt.charAt(0)))).setSelection(optValue);
			}
			
		} 
		catch (CoreException e) {
			//e.printStackTrace();
			log.debug("Unable to init options map due to : " + e); //$NON-NLS-1$
		}
	}

    private void storeOptionsMap(ILaunchConfigurationWorkingCopy configuration) {
		
		Map storingMap = new HashMap();
		
		Iterator iter = optionsMap.keySet().iterator();
		log.debug("storing optionsMap : "); //$NON-NLS-1$
		while (iter.hasNext()) {
			Character opt = (Character) iter.next();
			boolean optValue = ((Boolean) optionsMap.get(opt)).booleanValue();
			
			log.debug("\t" + opt + " => " + optValue);  //$NON-NLS-1$ //$NON-NLS-2$
			
			storingMap.put(opt.toString(), Boolean.toString(optValue));
		}
        configuration.setAttribute(OPTIONS_MAP, storingMap);
    }

 
}
