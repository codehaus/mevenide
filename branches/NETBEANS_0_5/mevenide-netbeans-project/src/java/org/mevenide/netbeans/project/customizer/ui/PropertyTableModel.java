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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.mevenide.properties.IPropertyResolver;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class PropertyTableModel implements TableModel, TableCellRenderer {
    
    private static final int COLUMN_KEY = 0;
    private static final int COLUMN_VALUE = 1;
    private static final int COLUMN_ORIGIN = 2;
    
    private ArrayList lst;
    
    private DefaultTableCellRenderer keyDelegate;
    private DefaultTableCellRenderer valueDelegate;
    
    private boolean resolve;
    private IPropertyResolver resolver;
    private List listeners;
    
    public PropertyTableModel(IPropertyResolver res, Collection valuelist) {
        Set set = new TreeSet(new KeyComparator());
        set.addAll(valuelist);
        lst = new ArrayList(set);
        keyDelegate = new DefaultTableCellRenderer();
        valueDelegate = new DefaultTableCellRenderer();
        resolve = false;
        resolver = res;
        listeners = new ArrayList();
    }
    
    public void setResolve(boolean res) {
        resolve = res;
        fireTableModelChange();
        
    }
    
    private void fireTableModelChange() {
        TableModelEvent event = new TableModelEvent(this);
        List toFire = null;
        synchronized (listeners) {
            toFire = new ArrayList(listeners);
        }
        Iterator it = toFire.iterator();
        while (it.hasNext()) {
            TableModelListener list = (TableModelListener)it.next();
            list.tableChanged(event);
        }
    }
    
    public Object getValueAt(int row, int column) {
        TableRowPropertyChange change = (TableRowPropertyChange)lst.get(row);
        if (column == COLUMN_KEY) {
            return change.getKey();
        }
        if (column == COLUMN_VALUE) {
            return change.getNewValue();
        }
        if (column == COLUMN_ORIGIN) {
            return new Integer(change.getNewLocation());
        }
        return "";
    }
    
    public void addTableModelListener(TableModelListener tableModelListener) {
        synchronized (listeners) {
            listeners.add(tableModelListener);
        }
    }
    
    public Class getColumnClass(int param) {
        if (param == COLUMN_ORIGIN) {
            return Integer.class;
        }
        return String.class;
    }
    
    public int getColumnCount() {
        return 3;
    }
    
    public String getColumnName(int column) {
        if (column == COLUMN_KEY) {
            return "Property Key";
        }
        if (column == COLUMN_VALUE) {
            return "Property Value";
        }
        if (column == COLUMN_ORIGIN) {
            return "Location";
        }
        return "";
    }
    
    public int getRowCount() {
        return lst.size();
    }
    
    public boolean isCellEditable(int row, int column) {
        if (resolve) {
            return false;
        }
        if (column == COLUMN_VALUE || column == COLUMN_ORIGIN) {
            return true;
        }
        return false;
    }
    
    public void removeTableModelListener(TableModelListener tableModelListener) {
        synchronized (listeners) {
            listeners.remove(tableModelListener);
        }
    }
    
    public void setValueAt(Object obj, int row, int column) {
        if (column == COLUMN_VALUE) {
            TableRowPropertyChange change = (TableRowPropertyChange)lst.get(row);
            if (!obj.toString().equals(change.getNewValue())) {
                change.setNewValue(obj.toString());
                fireTableModelChange();
            }
        }
        if (column == COLUMN_ORIGIN) {
            TableRowPropertyChange change = (TableRowPropertyChange)lst.get(row);
            Integer integ = (Integer)obj;
            change.setNewLocation(integ.intValue());
            fireTableModelChange();
        }
    }

    public Component getTableCellRendererComponent(JTable jTable, Object obj, 
                                                   boolean isSelected, boolean hasFocus, 
                                                   int row, int column) {
        TableRowPropertyChange change = (TableRowPropertyChange)lst.get(row);
        if (column == COLUMN_KEY) {
            //key
            Component com = keyDelegate.getTableCellRendererComponent(jTable,  change.getKey(), isSelected, hasFocus, row, column);
            if (com instanceof JLabel) {
                JLabel lbl = (JLabel)com;
                lbl.setToolTipText(change.getKey());
                lbl.setBackground(UIManager.getColor("Label.background"));
            }
            return com;
        }
        if (column == COLUMN_VALUE) {
            //value
            String val = resolve ? resolver.resolveString(change.getNewValue()) : change.getNewValue();
            TableCellRenderer render = resolve ? keyDelegate : valueDelegate;
            Component com = render.getTableCellRendererComponent(jTable,  val, isSelected, hasFocus, row, column);
            if (com instanceof JLabel) {
                JLabel lbl = (JLabel)com;
                lbl.setToolTipText(val);
            }
            return com;
        }
        if (column == COLUMN_ORIGIN) {
            return change.getOriginComponent();
        }
        
        return keyDelegate.getTableCellRendererComponent(jTable, "", isSelected, hasFocus, row, column);
        
    }
    


    
    
    private static class KeyComparator implements Comparator {
        public int compare(Object obj, Object obj1) {
            TableRowPropertyChange change1 = (TableRowPropertyChange)obj;
            TableRowPropertyChange change2 = (TableRowPropertyChange)obj1;
            return change1.getKey().compareTo(change2.getKey());
        }
        
    }
}

