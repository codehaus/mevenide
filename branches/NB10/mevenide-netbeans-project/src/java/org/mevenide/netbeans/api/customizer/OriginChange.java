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
package org.mevenide.netbeans.api.customizer;

import java.io.File;
import javax.swing.JComponent;
import org.mevenide.properties.IPropertyLocator;

/**
 * a wrapper dealing with changes in the location of property/pom element
 * value definitions.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class OriginChange {
    private LocationComboBox comboBox;
    private ChangeObserver observer;

    /**
     * The item is located in the project's POM file. 
     * similar to constants in IPropertyLocator, but not belonging there.
     */
    public static final int LOCATION_POM = 0;
    /**
     * The item is located in the POM file parent. 
     * similar to constants in IPropertyLocator, but not belonging there.
     */
    public static final int LOCATION_POM_PARENT = 1;
    
    /**
     * The item is located in the POM file parent's parent. 
     * similar to constants in IPropertyLocator, but not belonging there.
     */
    public static final int LOCATION_POM_PARENT_PARENT = 2;
    
    
    OriginChange(LocationComboBox combo) {
        comboBox = combo;
    }

    /**
     * UI component handling the change of origin. Can be safely casted to
     * JButton if required.
     */
    public JComponent getComponent() {
        return comboBox;
    }
    /**
     * @param location - new location to be set, 
     *  for property values, check IpropertyLocator constants
     *  for pom values, use constants in this class.
     */
    public void setAction(int location) {
        comboBox.invokePopupAction(location);
    }
    
    public int getSelectedLocationID() {
        LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)comboBox.getSelectedItem();
        if (wrapper == null) {
            return IPropertyLocator.LOCATION_NOT_DEFINED;
        }
        return wrapper.getID();
    }
    
    public void setInitialLocationID(int location) {
        comboBox.setInitialItem(location);
    }
    
    public File getSelectedFile() {
        LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)comboBox.getSelectedItem();
        return wrapper.getFile();
    }
    
    /**
     * setter for use by MavenChange implemetations.
     */
    public void setChangeObserver(ChangeObserver obs) {
        observer = obs;
        comboBox.setChangeObserver(observer);
    }
    /** callback to get notified when user selects an action from the popup.
     * Generally useful just for MavenChange implementations.
     */
    public interface ChangeObserver {
        void locationChanged();
    }
    

}
