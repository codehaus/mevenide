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

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class OriginChange {
    private LocationComboBox comboBox;
    OriginChange(LocationComboBox combo) {
        comboBox = combo;
    }

    public JComponent getComponent() {
        return comboBox;
    }
    
    public boolean hasChangedValue() {
        return comboBox.hasChangedSelection();
    }
    
    public void setSelectedLocationValue(int  id) {
        DefaultComboBoxModel model = (DefaultComboBoxModel)comboBox.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)model.getElementAt(i);
            if (wrapper.getID() == id) {
                comboBox.setSelectedItem(wrapper);
            }
        }
        comboBox.startLoggingChanges();
    }
    
    public int getSelectedLocationID() {
        LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)comboBox.getSelectedItem();
        return wrapper.getID();
    }
    
    public File getSelectedFile() {
        LocationComboBox.LocationWrapper wrapper = (LocationComboBox.LocationWrapper)comboBox.getSelectedItem();
        return wrapper.getFile();
    }
}
