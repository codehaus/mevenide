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

import java.io.File;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import org.mevenide.properties.IPropertyLocator;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class OriginChange {
    private LocationComboBox comboBox;
    private ChangeObserver observer;

    //# properties files actions..
    /*
     * property file action
     */
    public static final String ACTION_MOVE_TO_PROJECT = "MvProject";
    /*
     * property file action
     */
    public static final String ACTION_MOVE_TO_USER = "MvUser";
    /*
     * property file action
     */
    public static final String ACTION_MOVE_TO_BUILD = "MvBuild";
    /*
     * property file action
     */
    public static final String ACTION_DEFINE_IN_PROJECT = "DefProject";
    /*
     * property file action
     */
    public static final String ACTION_DEFINE_IN_USER = "DefUser";
    /*
     * property file action
     */
    public static final String ACTION_DEFINE_IN_BUILD = "DefBuild";
    /*
     * property file action
     */
    public static final String ACTION_RESET_TO_DEFAULT = "DefaultReset";
    /*
     * parent property file action
     */
    public static final String ACTION_MOVE_TO_PARENT_PROJECT = "MvParentProject";
    /*
     * property file action
     */
    public static final String ACTION_MOVE_TO_PARENTBUILD = "MvParentBuild";
    /*
     * parent build property file action
     */
    public static final String ACTION_DEFINE_IN_PARENT_BUILD = "DefParentBuild";    
    /*
     * parent property file action
     */
    public static final String ACTION_DEFINE_IN_PARENT_PROJECT = "DefParentProject";    
    
    
// pom files actions.
    /*
     * pom file action
     */
    public static final String ACTION_REMOVE_ENTRY = "RemoveEntry";
    /*
     * pom file action
     */
    public static final String ACTION_POM_MOVE_TO_PARENT = "MoveToParent";
    /*
     * pom file action
     */
    public static final String ACTION_POM_MOVE_TO_CHILD = "MoveToChild";
    
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
    
    public static final int LOCATION_POM_PARENT_PARENT = 2;
    
    OriginChange(LocationComboBox combo) {
        comboBox = combo;
    }

    public JComponent getComponent() {
        return comboBox;
    }
    
    public void setAction(String  action) {
        comboBox.invokePopupAction(action);
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
    
    public void setSelectedLocationID(int location) {
        comboBox.invokePopupAction(location);
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
