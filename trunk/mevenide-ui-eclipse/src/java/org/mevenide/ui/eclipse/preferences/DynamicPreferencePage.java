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
package org.mevenide.ui.eclipse.preferences;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    private List properties;
    
    private PreferencesManager preferencesManager;
    
    public DynamicPreferencePage() {
        super();
        preferencesManager = PreferencesManager.getManager();
		preferencesManager.loadPreferences();
    }
    
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        if( properties != null ) {
            GridLayout layout = new GridLayout();
            layout.numColumns = 2;
            for ( Iterator it = properties.iterator(); it.hasNext(); ) {
                PluginProperty pluginProperty = (PluginProperty) it.next();
                createPluginPropertyEditor(composite, pluginProperty);
            }
        }
        return composite; 
	}
  
	private void createPluginPropertyEditor(Composite parent, PluginProperty pluginProperty) {
        String propertyName = pluginProperty.getName(); 
        String propertyDefault = pluginProperty.getDefault();
        String propertyType = pluginProperty.getType();
        String propertyLabel = pluginProperty.getLabel();
        String propertyDescription = pluginProperty.getDescription();
        String pageId = pluginProperty.getPageId();
        StringFieldEditor editor = new StringFieldEditor(pageId + "." + propertyName, propertyLabel, parent);
        editor.fillIntoGrid(parent, 2);
        editor.setPreferenceStore(preferencesManager.getPreferenceStore());
        editor.setEmptyStringAllowed(!pluginProperty.isRequired());
        editor.load();
        String toolTip = propertyName + " : " + 
        				(!StringUtils.isNull(propertyDescription) ? propertyDescription : "No available description");
        editor.getLabelControl(parent).setToolTipText(toolTip);
    }

    public void init(IWorkbench workbench) {
    }
	
    public List getProperties() {
        return properties;
    }
    
    public void setProperties(List properties) {
        this.properties = properties;
    }
}
