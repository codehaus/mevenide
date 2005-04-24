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
package org.mevenide.netbeans.project.customizer;

import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.properties.IPropertyLocator;


/**
 * changes tracker for textfields with an originchange instance attached.
 * @author  Milos Kleint (ca206216@tiscali.cz)
df */
public class TextComponentPOMChange implements MavenPOMChange {
    private String key;
    private String value;
    private int location;
    private String newValue;
    private int newLocation;
    private JTextComponent field;
    private OriginChange origin;
    private DocListener listener;
    
    private boolean ignore = false;
    
    public TextComponentPOMChange(String keyParam, String oldValue, int oldLocation, 
                                   JTextComponent textfield, OriginChange oc) {
        key = keyParam;
        value = oldValue != null ? oldValue : "";
        location = oldLocation;
        newValue= value;
        newLocation = oldLocation;
        field = textfield;
        origin = oc;
        origin.setInitialLocationID(oldLocation);
        field.setText(value);
        listener = new DocListener();
        origin.setChangeObserver(listener);
        field.getDocument().addDocumentListener(listener);
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
        field.setText(newValue);
        origin.setInitialLocationID(newLocation);
        ignore = false;
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
        field.setEditable(false);
        origin.getComponent().setEnabled(false);
        field.setText(resvalue);
        ignore = false;
    }
    
    public void resetToNonResolvedValue() {
        ignore = true;
        field.setEditable(true);
        origin.getComponent().setEnabled(true);
        field.setText(newValue);
        ignore = false;
    }

    public String getPath() {
        return key;
    }
    
    
  private class DocListener implements DocumentListener, OriginChange.ChangeObserver {
        private DocListener() {
        }
        private void update() {
            if (ignore) {
                return;
            }
            newValue = field.getText();
            if (origin.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED ||
                origin.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
                // assume the default placement is build..
                // maybe have configurable or smartish later..
                origin.setAction(OriginChange.LOCATION_POM);
            }
        }
        
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        public void locationChanged() {
            if (ignore) {
                return;
            }
            newLocation = origin.getSelectedLocationID();
            if (newLocation < 0) {
                // assuming the correct default value is not-override..
                ignore = true;
                newValue = "";
                field.setText(newValue);
                ignore = false;
            }
        }
        
    }    
    
}