/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.netbeans.project.customizer.ui;

import java.awt.Component;
import javax.swing.JTable;
import org.mevenide.netbeans.project.customizer.MavenPropertyChange;
import org.mevenide.properties.IPropertyLocator;


/**
 * changes tracker for textfields with an originchange instance attached.
 * @author  Milos Kleint (mkleint@codehaus.org)
df */
public class TableRowPropertyChange implements MavenPropertyChange {
    private String key;
    private String value;
    private int location;
    private String newValue;
    private int newLocation;
    private String defaultValue;
    private JTable table;
    private OriginChange origin;
    
    private boolean ignore = false;
    
    public TableRowPropertyChange(String keyParam, String oldValue, int oldLocation, 
                                  JTable table, OriginChange oc, String defVal) {
        key = keyParam;
        value = oldValue != null ? oldValue : "";
        location = oldLocation;
        newValue= value;
        newLocation = oldLocation;
        this.table = table;
        origin = oc;
        defaultValue = defVal;
        origin.setInitialLocationID(oldLocation);
//        field.setText(value);
//        origin.setChangeObserver(listener);
//        field.getDocument().addDocumentListener(listener);
    }
    
    Component getOriginComponent() {
        return origin.getComponent();
    }
    
    /**
     * changes in the field or location combo are not prpagated into the value
     */
    public void startIgnoringChanges() {
        ignore = true;
    }
    
    /**
     * changes in the field or location combo are not prpagated into the value
     * assigns the textfield and loc combo with current values.
     */
    public void stopIgnoringChanges() {
//        field.setText(newValue);
        origin.setInitialLocationID(newLocation);
        ignore = false;
    }
    
    public String getKey() {
        return key;
    }

    public int getNewLocation() {
        return newLocation;
    }
    
    public String getNewValue() {
        return newValue;
    }

    public int getOldLocation() {
        return location;
    }

    public String getOldValue() {
        return value;
    }
    
    public boolean hasChanged() {
        return newLocation != location || !getOldValue().equals(getNewValue());
    }
    
    
    public void setResolvedValue(String resvalue) {
        ignore = true;
//        field.setEditable(false);
        origin.getComponent().setEnabled(false);
//        field.setText(resvalue);
        ignore = false;
    }
    
    public void resetToNonResolvedValue() {
        ignore = true;
//        field.setEditable(true);
        origin.getComponent().setEnabled(true);
//        field.setText(newValue);
        ignore = false;
    }
    
    public void setNewValue(String value) {
        newValue = value;
        if (origin.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED ||
                origin.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
            // assume the default placement is build..
            // maybe have configurable or smartish later..
            newLocation = IPropertyLocator.LOCATION_PROJECT_BUILD;
            origin.setSelectedLocationID(IPropertyLocator.LOCATION_PROJECT_BUILD);
        }
    }
    
    public void setNewLocation(int loc) {
        newLocation = loc;
        if (newLocation == IPropertyLocator.LOCATION_DEFAULTS || 
            newLocation == IPropertyLocator.LOCATION_NOT_DEFINED) 
        {
            newValue = defaultValue;
        }
        origin.setSelectedLocationID(loc);
    }

//        public void actionSelected(String changeAction) {
//            if (ignore) {
//                return;
//            }
//            newLocation = origin.getSelectedLocationID();
//            if (OriginChange.ACTION_RESET_TO_DEFAULT.equals(changeAction)) {
//                // assuming the correct default value is not-override..
//                ignore = true;
//                newValue = (defaultValue == null ? "" : defaultValue);
////                field.setText(newValue);
//                ignore = false;
//            }
//        }
    
}