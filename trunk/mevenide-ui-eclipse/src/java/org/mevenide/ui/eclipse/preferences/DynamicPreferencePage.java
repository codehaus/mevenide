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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    private List properties;
    private String pluginDescription;
    
    private Map editors = new HashMap();
    
    private PreferencesManager preferencesManager;
    
    private List dirtyProperties = new ArrayList();
    
    public DynamicPreferencePage() {
        super();
        preferencesManager = DynamicPreferencesManager.getDynamicManager();
		preferencesManager.loadPreferences();
    }
    
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        
        createDescriptionComposite(composite);
        
        createPropertyComposite(composite);
        
        return composite; 
	}
  
	private void createDescriptionComposite(Composite composite) {
	    Text title = new Text(composite, SWT.READ_ONLY);
	    title.setText(Mevenide.getResourceString("DynamicPreferencePage.Description")); //$NON-NLS-1$
	    title.setFont(new Font(PlatformUI.getWorkbench().getDisplay(), new FontData("bold", composite.getFont().getFontData()[0].getHeight(), SWT.BOLD))); //$NON-NLS-1$
	    title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    
        Text textDescription = new Text(composite, SWT.READ_ONLY | SWT.MULTI);
        textDescription.setText(pluginDescription != null ? pluginDescription : Mevenide.getResourceString("DynamicPreferencePage.nodescription")); //$NON-NLS-1$
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        textDescription.setLayoutData(data);
        
        new Label(composite, SWT.NULL);
    }

    private void createPropertyComposite(Composite composite) {
        Text title = new Text(composite, SWT.READ_ONLY | SWT.MULTI);
	    title.setText(Mevenide.getResourceString("DynamicPreferencePage.Configuration")); //$NON-NLS-1$
	    title.setFont(new Font(PlatformUI.getWorkbench().getDisplay(), new FontData("bold", composite.getFont().getFontData()[0].getHeight(), SWT.BOLD))); //$NON-NLS-1$
	    title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Group propertiesGroup = createPropertyGroup(composite);
        
        if( properties != null && properties.size() > 0 ) {
            for ( Iterator it = properties.iterator(); it.hasNext(); ) {
		        PluginProperty pluginProperty = (PluginProperty) it.next();
                StringFieldEditor editor = createPluginPropertyEditor(propertiesGroup, pluginProperty);
                editors.put(pluginProperty, editor);
            }
        }
        else {
            Text noPropertyWarningText = new Text(propertiesGroup, SWT.READ_ONLY);
            noPropertyWarningText.setText(Mevenide.getResourceString("DynamicPreferencePage.noproperty")); //$NON-NLS-1$
            GridData warningData = new GridData(GridData.FILL_BOTH);
            noPropertyWarningText.setLayoutData(warningData);
        }
    }

    private Group createPropertyGroup(Composite composite) {
        Group propertiesGroup = new Group(composite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.verticalSpacing = 30;
        layout.horizontalSpacing = 30;
        propertiesGroup.setLayout(layout);
        GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
        propertiesGroup.setLayoutData(groupData);
        propertiesGroup.setText(Mevenide.getResourceString("DynamicPreferencePage.propertygroup.text")); //$NON-NLS-1$
        return propertiesGroup;
    }

    private StringFieldEditor createPluginPropertyEditor(Composite parent, final PluginProperty pluginProperty) {
        String propertyName = pluginProperty.getName(); 
        String propertyDefault = pluginProperty.getDefault();
        String propertyLabel = pluginProperty.getLabel();
        String propertyDescription = pluginProperty.getDescription();
        String propertyType = pluginProperty.getType();
        String pageId = pluginProperty.getPageId();
        
        StringFieldEditor editor = new StringFieldEditor(pageId + DynamicPreferencesManager.SEPARATOR + propertyName, propertyLabel, parent);
        editor.fillIntoGrid(parent, 2);
        
        editor.setPreferenceStore(preferencesManager.getPreferenceStore());
        editor.load();

        editor.setEmptyStringAllowed(!pluginProperty.isRequired());
        editor.setErrorMessage(Mevenide.getResourceString("DynamicPreferencePage.Empty.NotAllowed", pluginProperty.getLabel())); //$NON-NLS-1$
        
        ModifyListener editorListener = new ModifyListener() {
            public void modifyText(ModifyEvent arg0) {
                dirtyProperties.add(pluginProperty);
                updateApplyButton();
                getContainer().updateButtons();
            }
        };

        String toolTip = propertyName + " : " +  //$NON-NLS-1$
        				(!StringUtils.isNull(propertyDescription) ? 
        				        					propertyDescription : 
        				        					Mevenide.getResourceString("DynamicPreferencePage.property.nodescription")); //$NON-NLS-1$
        editor.getLabelControl(parent).setToolTipText(toolTip);
        
        if ( StringUtils.isNull(editor.getStringValue()) && 
                !StringUtils.isNull(propertyDefault) ) {
            editor.setStringValue(propertyDefault);
        }
        
        editor.getTextControl(parent).addModifyListener(editorListener);
        
        return editor;
    }

    public void init(IWorkbench workbench) {
    }
	
    public List getProperties() {
        return properties;
    }
    
    public void setProperties(List properties) {
        this.properties = properties;
    }
    
    protected void performApply() {
        performOk();
    }
    
    protected void performDefaults() {
        super.performDefaults();
    }
    
    public boolean isValid() {
        boolean valid = true;
        setErrorMessage(null);
        for (Iterator it = editors.keySet().iterator(); it.hasNext(); ) {
            PluginProperty pluginProperty = (PluginProperty) it.next();
            StringFieldEditor editor = (StringFieldEditor) editors.get(pluginProperty);
            if ( StringUtils.isNull(editor.getStringValue()) && pluginProperty.isRequired() ) {
                setErrorMessage(editor.getErrorMessage());
                valid = false;
                break;
            }
        }
        return valid;
    }
    
    public boolean performOk() {
        for (Iterator it = editors.keySet().iterator(); it.hasNext(); ) {
            PluginProperty pluginProperty = (PluginProperty) it.next();
            if ( dirtyProperties.contains(pluginProperty) ) {
                StringFieldEditor editor = (StringFieldEditor) editors.get(pluginProperty);
	            String pageId = pluginProperty.getPageId();
	            String propertyName = pluginProperty.getName();
	            if ( !StringUtils.relaxEqual(pluginProperty.getDefault(), editor.getStringValue()) ) {
	                preferencesManager.setValue(pageId + DynamicPreferencesManager.SEPARATOR + propertyName, editor.getStringValue());
	            }
	            else {
	                preferencesManager.remove(pageId + DynamicPreferencesManager.SEPARATOR + propertyName);
	            }
            }
        }
        return preferencesManager.store();
    }
    
    public String getPluginDescription() {
        return pluginDescription;
    }
    
    public void setPluginDescription(String pluginDescription) {
        this.pluginDescription = pluginDescription;
    }
}
