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
package org.mevenide.netbeans.project.customizer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.properties.IPropertyLocator;


/**
 * changes tracker for checkboxes with an originchange instance attached.
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class CheckBoxPropertyChange implements MavenPropertyChange {
    private String key;
    private String value;
    private int location;
    private String newValue;
    private boolean defaultValue;
    private int newLocation;
    private JCheckBox check;
    private OriginChange origin;
    private DocListener listener;
    private boolean opposite;
    
    private boolean ignore = false;
    
    public CheckBoxPropertyChange(String keyParam, String oldValue, int oldLocation, 
                                   JCheckBox box, OriginChange oc, boolean defVal, boolean opposite) {
        this.opposite = opposite;
        key = keyParam;
        check = box;
        location = oldLocation;
        newLocation = oldLocation;
        defaultValue = defVal;
        origin = oc;
        value = oldValue;
        
        boolean boolValue = false;
        if (value != null) {
            boolValue = Boolean.valueOf(value).booleanValue();
        } else {
            boolValue = defaultValue;
        }
        setCheckBoxValue(boolValue, opposite);
        origin.setSelectedLocationID(oldLocation);
        listener = new DocListener();
        origin.setChangeObserver(listener);
        check.addActionListener(listener);
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
    
    private void setCheckBoxValue(boolean boolValue, boolean opposite) {
        newValue = boolValue ? "true" : "false";
        if (opposite) {
            check.setSelected(!boolValue);
        } else {
            check.setSelected(boolValue);
        }
    }
    
    
  private class DocListener implements ActionListener, OriginChange.ChangeObserver {
        private DocListener() {
        }
        
        public void actionPerformed(ActionEvent event) {
            if (ignore) {
                return;
            }
            if (opposite) {
                newValue = check.isSelected() ? "false" : "true";
            }
            else {
                newValue = check.isSelected() ? "true" : "false";
            }
            
            if (origin.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED ||
                origin.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
                // assume the default placement is build..
                // maybe have configurable or smartish later..
                origin.setAction(OriginChange.ACTION_DEFINE_IN_BUILD);
            }
        }
        
        public void actionSelected(String changeAction) {
            if (ignore) {
                return;
            }
            newLocation = origin.getSelectedLocationID();
            if (OriginChange.ACTION_RESET_TO_DEFAULT.equals(changeAction)) {
                ignore = true;
                setCheckBoxValue(defaultValue, opposite);
                ignore = false;
            }
        }
        
    }    
    
}