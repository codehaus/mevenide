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
package org.mevenide.ui.eclipse.preferences.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DynamicPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    
    private class ClipboardFieldEditor extends StringButtonFieldEditor {

        private Button changeButton;

        ClipboardFieldEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        protected Button getChangeControl(Composite parent) {
            if (changeButton == null) {
                changeButton = new Button(parent, SWT.FLAT);
                changeButton.setImage(Mevenide.getInstance().getImageRegistry().get(IImageRegistry.COPY_TOOL));
        		changeButton.addSelectionListener(new SelectionAdapter() {
        			public void widgetSelected(SelectionEvent evt) {
        				Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench().getDisplay());

        				String[] keyParts = org.apache.commons.lang.StringUtils.split(getPreferenceName(), "|");
        				String key = keyParts.length == 1 ? keyParts[0] : keyParts[1];
        				
        				clipboard.setContents(
        				        		new String[]{key + "=" + getTextControl().getText()}, //$NON-NLS-1$
        								new Transfer[]{TextTransfer.getInstance()});        
        				
        			}
        		});
        		changeButton.addDisposeListener(new DisposeListener() {
        			public void widgetDisposed(DisposeEvent event) {
        				changeButton = null;
        			}
        		});
        	} 
            else {
        		checkParent(changeButton, parent);
        	}
        	changeButton.setToolTipText(Mevenide.getResourceString("DynamicPreferencePage.ClipboardFieldEditor.Tooltip")); //$NON-NLS-1$
            return changeButton;
        }

        protected void doFillIntoGrid(Composite parent, int numColumns) {
            super.doFillIntoGrid(parent, numColumns);
            GridData data = new GridData();
            data.grabExcessHorizontalSpace = false;
            data.horizontalAlignment = GridData.CENTER;
        	changeButton.setLayoutData(data);
        }

        protected String changePressed() {
            return null;
        }
    }

    private List categories;
    private String pluginDescription;
    private String pluginName;
    
    private Map editors = new HashMap();
    
    private PreferencesManager preferencesManager;
    
    private List dirtyProperties = new ArrayList();
    
    public DynamicPreferencePage() {
        super();
        preferencesManager = DynamicPreferencesManager.getDynamicManager();
		preferencesManager.loadPreferences();
    }
    
    public void setVisible(boolean visible) {
		for (Iterator it = editors.keySet().iterator(); it.hasNext();) {
            PluginProperty pluginProperty = (PluginProperty) it.next();
            StringFieldEditor editor = (StringFieldEditor) editors.get(pluginProperty);
            String[] keyParts = org.apache.commons.lang.StringUtils.split(editor.getPreferenceName(), "|");
			String key = keyParts.length == 1 ? keyParts[0] : keyParts[1];
			String importedKeyValue = DynamicPreferencesManager.getDynamicManager().getValue(key);
			if ( !StringUtils.isNull(importedKeyValue) ) {
				String fullyQualifiedKeyValue = preferencesManager.getValue(editor.getPreferenceName());
				editor.setStringValue(importedKeyValue);
				
				//store fully qualified property  
				preferencesManager.remove(key);
				editor.store();
			}
        }
        super.setVisible(visible);
    }
    
    protected Control createContents(Composite parent) {
        return getContentsControl(parent); 
	}
  
	private Control getContentsControl(Composite parent) {
	    DynamicPreferencePageFactory.getFactory().initialize(this);
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        
        TabFolder tabFolder = new TabFolder(composite, SWT.TOP);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createDescriptionComposite(tabFolder);
        
        for (int i = 0; i < categories.size(); i++) {
	        createCategoryComposite(tabFolder, (PluginCategory) categories.get(i));
        }
        
        return composite;
    }

    private void createDescriptionComposite(TabFolder tabFolder) {
	    TabItem tabItem = new TabItem(tabFolder, SWT.NULL); 
	    tabItem.setText(Mevenide.getResourceString("DynamicPreferencePage.Description")); //$NON-NLS-1$
	    
	    Composite area = new Composite(tabFolder, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        area.setLayout(layout);
        
        Group descriptionGroup = new Group(area, SWT.NULL);
        descriptionGroup.setText(pluginName + " " + Mevenide.getResourceString("DynamicPreferencePage.Description")); //$NON-NLS-1$ //$NON-NLS-2$
        descriptionGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        descriptionGroup.setLayout(new GridLayout());

        Text textDescription = new Text(descriptionGroup, SWT.READ_ONLY | SWT.MULTI);
        textDescription.setText(pluginDescription != null ? pluginDescription : Mevenide.getResourceString("DynamicPreferencePage.nodescription")); //$NON-NLS-1$
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        textDescription.setLayoutData(data);
        
        tabItem.setControl(area);
    }

    private void createCategoryComposite(TabFolder tabFolder, PluginCategory category) {
        TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
        tabItem.setText(category.getName());
        

        Composite area = new Composite(tabFolder, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.makeColumnsEqualWidth = false;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        area.setLayout(layout);
        
        Group propertiesGroup = createPropertyGroup(area, category.getName());
        GridLayout groupLayout = new GridLayout();
        groupLayout.numColumns = 1;
        groupLayout.makeColumnsEqualWidth = false;
        propertiesGroup.setLayout(groupLayout);
        
        Composite groupIndirectionComposite = new Composite(propertiesGroup, SWT.NULL);
        GridLayout groupIndirectionLayout = new GridLayout();
        groupIndirectionLayout.numColumns = 3;
        groupIndirectionLayout.makeColumnsEqualWidth = false;
        groupIndirectionComposite.setLayout(groupIndirectionLayout);
        GridData groupIndirectionData = new GridData(GridData.FILL_BOTH);
        groupIndirectionComposite.setLayoutData(groupIndirectionData);
        
        List properties = category.getProperties();
        
        if( properties != null && properties.size() > 0 ) {
            for ( Iterator it = properties.iterator(); it.hasNext(); ) {
		        PluginProperty pluginProperty = (PluginProperty) it.next();
                StringButtonFieldEditor editor = createPluginPropertyEditor(groupIndirectionComposite, pluginProperty);
                editors.put(pluginProperty, editor);
            }
        }
        else {
            Text noPropertyWarningText = new Text(propertiesGroup, SWT.READ_ONLY);
            noPropertyWarningText.setText(Mevenide.getResourceString("DynamicPreferencePage.noproperty")); //$NON-NLS-1$
            GridData warningData = new GridData(GridData.FILL_BOTH);
            noPropertyWarningText.setLayoutData(warningData);
        }
        
        tabItem.setControl(area);
    }

    private Group createPropertyGroup(Composite composite, String categoryName) {
        Group propertiesGroup = new Group(composite, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        propertiesGroup.setLayout(layout);
        GridData groupData = new GridData(GridData.FILL_HORIZONTAL);
        propertiesGroup.setLayoutData(groupData);
        propertiesGroup.setText(Mevenide.getResourceString("DynamicPreferencePage.Configuration", categoryName)); //$NON-NLS-1$
        return propertiesGroup;
    }

    private StringButtonFieldEditor createPluginPropertyEditor(Composite composite, final PluginProperty pluginProperty) {
        
        String propertyName = pluginProperty.getName(); 
        String propertyDefault = pluginProperty.getDefault();
        String propertyLabel = pluginProperty.getLabel();
        String propertyDescription = pluginProperty.getDescription();
        String propertyType = pluginProperty.getType();
        String pageId = pluginProperty.getPageId();
        
        StringButtonFieldEditor editor = new ClipboardFieldEditor(pageId + DynamicPreferencesManager.SEPARATOR + propertyName, propertyLabel, composite);
        
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
        editor.getLabelControl(composite).setToolTipText(toolTip);
        
        if ( StringUtils.isNull(editor.getStringValue()) && 
                !StringUtils.isNull(propertyDefault) ) {
            editor.setStringValue(propertyDefault);
        }
        
        editor.getTextControl(composite).addModifyListener(editorListener);
        
        return editor;
    }

    public void init(IWorkbench workbench) {
    }
	
    public List getCategories() {
        return categories;
    }
    
    public void setCategories(List properties) {
        this.categories = properties;
    }
    
    protected void performApply() {
        performOk();
    }
    
    protected void performDefaults() {
        for (Iterator it = editors.keySet().iterator(); it.hasNext(); ) {
            PluginProperty pluginProperty = (PluginProperty) it.next();
            StringFieldEditor editor = (StringFieldEditor) editors.get(pluginProperty);
            editor.setStringValue(pluginProperty.getDefault());
        }
        super.performDefaults();
    }
    
    public boolean isValid() {
        boolean valid = true;
        setErrorMessage(null);
        for (Iterator it = editors.keySet().iterator(); it.hasNext(); ) {
            PluginProperty pluginProperty = (PluginProperty) it.next();
            StringFieldEditor editor = (StringFieldEditor) editors.get(pluginProperty);
            if ( StringUtils.isNull(editor.getStringValue()) && pluginProperty.isRequired() && !StringUtils.isNull(pluginProperty.getDefault())) {
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
    
    public String getPluginName() {
        return pluginName;
    }
    
    public void setPluginName(String pageName) {
        this.pluginName = pageName;
    }
}
