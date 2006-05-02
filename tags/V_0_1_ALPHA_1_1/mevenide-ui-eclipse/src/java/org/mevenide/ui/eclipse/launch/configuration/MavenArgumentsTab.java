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
package org.mevenide.ui.eclipse.launch.configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.mevenide.ui.eclipse.launch.LaunchWizardPage;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class MavenArgumentsTab extends AbstractLaunchConfigurationTab  {
	private static Log log = LogFactory.getLog(LaunchWizardPage.class);

	public static final String OPTIONS_MAP = "OPTIONS_MAP";
	public static final String GOALS_TO_RUN = "GOALS_TO_RUN";	
	public static final String SYS_PROPERTIES = "SYS_PROPERTIES";
	
	private final char[] options = new char[] { 'E', 'X', 'e', 'o' };

	private Map optionsMap = new TreeMap();
	private Map sysProperties = new TreeMap();
	private String selectedGoals = "";

	private Map optionsButtons = new HashMap();

	private Text goalsText;
	private Table table;
	
	
	public MavenArgumentsTab() {
		setDirty(false);
	}
	
	
	
	public String getName() {
		return "Arguments";
	}
	
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(GOALS_TO_RUN, Mevenide.getPlugin().getDefaultGoals());
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
			log.debug("checkboxes initialized");

			createSysPropertiesTable(composite);
			log.debug("table initialized");

			createGoalsText(composite);
			log.debug("goals list initialized");

			//createMavenVersionLabel(composite);

			
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
			
			
			optionButton.setText(OptionsRegistry.getDescription(option));
			optionButton.setToolTipText(new StringBuffer(" -").append(option).toString());
			//optionButton.setSelection(((Boolean)optionsMap.get(new Character(option))).booleanValue());
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
		column1.setText("key");
		column1.setWidth(200);
		
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText("value");
		column2.setWidth(200);
		
		createTableEditor();
		
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
				setDirty(true);
				updateLaunchConfigurationDialog();
			} 
		});
		
		
		Button removeButton = new Button(buttonsArea, SWT.PUSH);
		removeButton.setText("Remove");
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
					log.debug("setting selectedGoals to=" + ((Text)event.getSource()).getText());
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
		chooseButton.setText("Choose...");
		chooseButton.setEnabled(false);
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
													setDirty(true);
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
			sysProperties = (Map) configuration.getAttribute(SYS_PROPERTIES, new HashMap());
			initTableItems(table);
		} 
		catch (CoreException e) {
			//e.printStackTrace();
			log.debug("Unable to init goals due to : " + e);
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
			selectedGoals = configuration.getAttribute(GOALS_TO_RUN, "");
			goalsText.setText(selectedGoals);
			
		} 
		catch (CoreException e) {
			//e.printStackTrace();
			log.debug("Unable to init goals due to : " + e);
		}
    }

	private void storeGoals(ILaunchConfigurationWorkingCopy configuration) {
    	configuration.setAttribute(GOALS_TO_RUN, selectedGoals);	
    }

	private void initOptionsMap(ILaunchConfiguration configuration) {
		try {
			Map storedMap = (Map)configuration.getAttribute(OPTIONS_MAP, new HashMap());
			log.debug("stored Options Map .size() = " + storedMap.size());
			
			Iterator iter = storedMap.keySet().iterator();
			
			log.debug("initializing optionsMap : ");
			
			while (iter.hasNext()) {
				String opt = (String) iter.next();
				String storedOpt = (String) storedMap.get(opt);
				
				log.debug("Before manipulating options : " + opt + " => " + storedOpt);
				
				storedOpt = storedOpt == null ? "false" : storedOpt;
				boolean optValue = Boolean.valueOf(storedOpt).booleanValue();
				
				log.debug("\t" + opt + " => " + optValue);
				
				optionsMap.put(new Character(opt.charAt(0)), new Boolean(optValue)) ;
				((Button)optionsButtons.get(new Character(opt.charAt(0)))).setSelection(optValue);
			}
			
		} 
		catch (CoreException e) {
			//e.printStackTrace();
			log.debug("Unable to init options map due to : " + e);
		}
	}

    private void storeOptionsMap(ILaunchConfigurationWorkingCopy configuration) {
		
		Map storingMap = new HashMap();
		
		Iterator iter = optionsMap.keySet().iterator();
		log.debug("storing optionsMap : ");
		while (iter.hasNext()) {
			Character opt = (Character) iter.next();
			boolean optValue = ((Boolean) optionsMap.get(opt)).booleanValue();
			
			log.debug("\t" + opt + " => " + optValue); 
			
			storingMap.put(opt.toString(), Boolean.toString(optValue));
		}
        configuration.setAttribute(OPTIONS_MAP, storingMap);
    }

 
}