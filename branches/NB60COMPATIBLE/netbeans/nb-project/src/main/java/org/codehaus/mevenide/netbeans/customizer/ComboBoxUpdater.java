/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.customizer;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author mkleint
 */
public abstract class ComboBoxUpdater<T> implements ActionListener, AncestorListener {

    private JComboBox component;
    private JLabel label;
    
    private boolean inherited = false;
    
    /** Creates a new instance of TextComponentUpdater */
    public ComboBoxUpdater(JComboBox comp, JLabel label) {
        component = comp;
        component.addAncestorListener(this);
        this.label = label;
    }
    
    public abstract T getValue();
    public abstract T getDefaultValue();
    public abstract void setValue(T value);

    private void setModelValue() {
        if (inherited) {
            inherited = false;
//            component.setBackground(DEFAULT);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            
            component.setToolTipText(""); //NOI18N
        }
        T val = (T)component.getSelectedItem();
        setValue(val == getDefaultValue() ? null : val);
        if (val == getDefaultValue()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    component.removeActionListener(ComboBoxUpdater.this);
                    setComboValue(getValue(), getDefaultValue(), component);
                    component.addActionListener(ComboBoxUpdater.this);
                }
            });
        }
    }
    
    public void actionPerformed(ActionEvent event) {
        setModelValue();
    }

    public void ancestorAdded(AncestorEvent event) {
        setComboValue(getValue(), getDefaultValue(), component);
        component.addActionListener(this);
    }

    public void ancestorRemoved(AncestorEvent event) {
        component.removeActionListener(this);
    }

    public void ancestorMoved(AncestorEvent event) {
    }
    
    private void setComboValue(T value, T projectValue, JComboBox field) {
        if (value != null) {
            field.setSelectedItem(value);
            component.setToolTipText(""); //NOI18N
            inherited = false;
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        } else if (projectValue != null) {
            field.setSelectedItem(projectValue);
//            field.setBackground(INHERITED);
            label.setFont(label.getFont().deriveFont(Font.PLAIN));
            component.setToolTipText(org.openide.util.NbBundle.getMessage(ComboBoxUpdater.class, "HINT_inherited"));
            inherited = true;
        } else {
            field.setSelectedItem(field.getModel().getElementAt(0));
            component.setToolTipText(""); //NOI18N
            inherited = false;
            label.setFont(label.getFont().deriveFont(Font.BOLD));
      }
    }

}
