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
package org.mevenide.ui.eclipse.nature;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PatternsTab extends AbstractLaunchConfigurationTab {

    private static final Log log = LogFactory.getLog(PatternsTab.class);
    
    
    private Table table;
    
    private boolean autoBuild;


    private Button autoBuildButton;
    
    public PatternsTab() {
        setDirty(false);
    }

    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createDescriptionText(composite);
        
        Composite patternsComposite = new Composite(composite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.makeColumnsEqualWidth = false;
        patternsComposite.setLayout(layout);
        patternsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        createTable(patternsComposite);
        
        autoBuildButton = new Button(composite, SWT.CHECK);
        autoBuildButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        autoBuildButton.setText(Mevenide.getResourceString("PatternsTab.AutoBuildButton.Text")); //$NON-NLS-1$
        autoBuildButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent arg0) {
                autoBuild = !autoBuild;
                setDirty(true);
				updateLaunchConfigurationDialog();
            }
        });
        
        setControl(composite);
    }

    private void createDescriptionText(Composite composite) {
        Text descriptionText = new Text(composite, SWT.READ_ONLY);
        descriptionText.setText(Mevenide.getResourceString("PatternsTab.Description")); //$NON-NLS-1$
        GridData descriptionData = new GridData(GridData.FILL_HORIZONTAL);
        descriptionText.setLayoutData(descriptionData);
    }

    private void createTable(Composite parent) {
        GridData gridData = new GridData(GridData.FILL_BOTH);
        table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setLayoutData(gridData);
			
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn column1 = new TableColumn(table, SWT.NULL);
		column1.setText(Mevenide.getResourceString("PatternsTab.Table.Column.FilePattern.Text")); //$NON-NLS-1$
		column1.setWidth(200);
		
		TableColumn column2 = new TableColumn(table, SWT.NULL);
		column2.setText(Mevenide.getResourceString("PatternsTab.Table.Column.Description.Text")); //$NON-NLS-1$
		column2.setWidth(200);
		
		createTableEditor();
		
		Composite buttonsArea = new Composite(parent, SWT.NULL);
		GridLayout bLayout = new GridLayout();
		buttonsArea.setLayout(bLayout);
		GridData topData = new GridData(GridData.FILL_BOTH);
		buttonsArea.setLayoutData(topData);
		
		Button addButton = new Button(buttonsArea, SWT.PUSH);
		addButton.setText(Mevenide.getResourceString("PatternsTab.AddButton.Text")); //$NON-NLS-1$ 
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		addButton.setLayoutData(data1);
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = new TableItem(table,SWT.NULL);
				item.setText(
					new String[] {
						"**/*.sample",   //$NON-NLS-1$
						""  //$NON-NLS-1$
					}
				);
				setDirty(true);
				updateLaunchConfigurationDialog();
			} 
		});
		
		
		Button removeButton = new Button(buttonsArea, SWT.PUSH);
		removeButton.setText(Mevenide.getResourceString("PatternsTab.RemoveButton.Text"));  //$NON-NLS-1$
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

    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    }

    public void initializeFrom(ILaunchConfiguration configuration) {
        initPatterns(configuration);
        initAutoBuild(configuration);
    }

    private void initAutoBuild(ILaunchConfiguration configuration) {
        try {
            autoBuild = configuration.getAttribute(ActionDefinitionsManager.AUTO_BUILD, false);
        }
        catch (CoreException e) {
            String message = "Unable to retrieve autobuild value from configuration";  //$NON-NLS-1$
            log.error(message, e);
        }
        autoBuildButton.setSelection(autoBuild);
    }

    private void initPatterns(ILaunchConfiguration configuration) {
        table.removeAll();	
        Map patternList = new HashMap();
        try {
            patternList = configuration.getAttribute(ActionDefinitionsManager.PATTERNS_LIST, new HashMap());
        }
        catch (CoreException e) {
            String message = "Unable to retrieve pattern list from configuration";  //$NON-NLS-1$
            log.error(message, e);
        }
        for (Iterator i = patternList.keySet().iterator(); i.hasNext(); ) {
            String pattern = (String) i.next();
            String description = (String) patternList.get(pattern);
            TableItem item = new TableItem(table, SWT.NULL);
            item.setText(new String[] {
                  pattern,
                  description,
            });
        }
    }

    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        storePatterns(configuration);
        storeAutoBuild(configuration);
    }

    private void storeAutoBuild(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(ActionDefinitionsManager.AUTO_BUILD, autoBuild);
        
    }

    private void storePatterns(ILaunchConfigurationWorkingCopy configuration) {
       Map patterns = new HashMap();
       TableItem[] items = table.getItems();
 	   for (int i = 0; i < items.length; i++) {
 		   String key = items[i].getText(0);
 		   String value = items[i].getText(1);
 		   patterns.put(key, value);
 	   }
 	   configuration.setAttribute(ActionDefinitionsManager.PATTERNS_LIST, patterns);
    }

    public String getName() {
        return Mevenide.getResourceString("PatternsTab.Name"); //$NON-NLS-1$
    }
    
    
    public Image getImage() {
        return Mevenide.getInstance().getImageRegistry().get(IImageRegistry.PATTERN_SEARCH_ICON);
    }
    
    /**
     * disallow user to run the config
     */
    public boolean isValid(ILaunchConfiguration launchConfig) {
        return false;
    }
}
