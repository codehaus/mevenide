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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author cenda
 */
public class LocationTableRenderer implements TableCellRenderer {
    
    private DefaultTableCellRenderer delegate;
    /** Creates a new instance of LocationTableRenderer */
    public LocationTableRenderer() {
        delegate = new DefaultTableCellRenderer();
    }

    public Component getTableCellRendererComponent(JTable jTable, Object obj, 
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column) {
        TableRowPropertyChange change = (TableRowPropertyChange)obj;
        if (row == 0) {
            //key
            return delegate.getTableCellRendererComponent(jTable,  change.getKey(), isSelected, hasFocus, row, column);
        }
        if (row == 1) {
            //value
            return delegate.getTableCellRendererComponent(jTable,  change.getNewValue(), isSelected, hasFocus, row, column);
        }
        if (row == 2) {
            return change.getOriginComponent();
        }
        
        return delegate.getTableCellRendererComponent(jTable, "", isSelected, hasFocus, row, column);
    }
    
}
