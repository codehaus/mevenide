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

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
class LocationComboBox extends JComboBox {
    private LocationWrapper initial;
    public LocationComboBox() {
        super();
        setRenderer(new ListRenderer());
    }
    
    public void startLoggingChanges() {
        initial = (LocationWrapper)getSelectedItem();
    }
    
    /**
     * true if the currently selected value is different from the initial one.
     */
    public boolean hasChangedSelection() {
        Object selected = getSelectedItem();
        return (selected != initial);
    }
    
    static class LocationWrapper {
        private String name;
        private Icon icon;
        private File file;
        private int ID;
        
        public LocationWrapper(String name, Icon icon, File file, int id) {
            this.name = name;
            this.icon = icon;
            this.file = file;
            ID = id;
        }
        public Icon getIcon() {
            return icon;
        }
        public String getName() {
            return name;
        }
        public File getFile() {
            return file;
        }
        
        public String toString() {
            return getName();
        }
        
        public int getID() {
            return ID;
        }
    }
    
    private class ListRenderer extends DefaultListCellRenderer {
        
        /**
         * Return a component that has been configured to display the specified
         * value. That component's <code>paint</code> method is then called to
         * "render" the cell.  If it is necessary to compute the dimensions
         * of a list because the list cells do not have a fixed size, this method
         * is called to generate a component on which <code>getPreferredSize</code>
         * can be invoked.
         *
         * @param list The JList we're painting.
         * @param value The value returned by list.getModel().getElementAt(index).
         * @param index The cells index.
         * @param isSelected True if the specified cell was selected.
         * @param cellHasFocus True if the specified cell has the focus.
         * @return A component whose paint() method will render the specified value.
         *
         * @see JList
         * @see ListSelectionModel
         * @see ListModel
         */
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel retValue;
            
            retValue = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof LocationWrapper) {
                LocationWrapper wrapper = (LocationWrapper)value;
//                retValue.setText("");
                retValue.setIcon(wrapper.getIcon());
            } else {
                throw new IllegalStateException("Wrong usage");
            }
            return retValue;
        }
        
    }
}
