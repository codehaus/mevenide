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
package org.mevenide.netbeans.project.customizer.ui;

import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class OriginChange {
    private LocationComboBox comboBox;
    private ChangeObserver observer;
    
    public static final String ACTION_MOVE_TO_PROJECT = "MvProject";
    public static final String ACTION_MOVE_TO_USER = "MvUser";
    public static final String ACTION_MOVE_TO_BUILD = "MvBuild";
    public static final String ACTION_DEFINE_IN_PROJECT = "DefProject";
    public static final String ACTION_DEFINE_IN_USER = "DefUser";
    public static final String ACTION_DEFINE_IN_BUILD = "DefBuild";
    public static final String ACTION_RESET_TO_DEFAULT = "DefaultReset";
    
    OriginChange(LocationComboBox combo) {
        comboBox = combo;
    }

    public JComponent getComponent() {
        return comboBox;
    }
    
    public boolean hasChangedValue() {
        return comboBox.hasChangedSelection();
    }
    
    public void setAction(String  action) {
        comboBox.invokePopupAction(action);
    }
    
    public int getSelectedLocationID() {
        LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)comboBox.getSelectedItem();
        return wrapper.getID();
    }
    
    public void setSelectedLocationID(int location) {
        comboBox.setInitialItem(location);
    }
    
    public File getSelectedFile() {
        LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)comboBox.getSelectedItem();
        return wrapper.getFile();
    }
    
    public void setChangeObserver(ChangeObserver obs) {
        observer = obs;
        comboBox.setChangeObserver(observer);
    }
    /** callback to get notified when user selects an action from the popup.
     */
    public interface ChangeObserver {
        void actionSelected(String action);
    }
}
