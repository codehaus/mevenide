/*
 * LocationTableRenderer.java
 *
 * Created on November 20, 2004, 11:17 PM
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
