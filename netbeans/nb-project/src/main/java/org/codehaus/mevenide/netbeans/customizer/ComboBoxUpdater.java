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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mkleint
 */
public abstract class ComboBoxUpdater implements ActionListener, AncestorListener {
    private static Color INHERITED = new Color(254, 255, 200);
    private static Color DEFAULT = UIManager.getColor("TextField.background");
    
    private JComboBox component;
    
    private boolean inherited = false;
    
    /** Creates a new instance of TextComponentUpdater */
    public ComboBoxUpdater(JComboBox comp) {
        component = comp;
        component.addAncestorListener(this);
    }
    
    public abstract Object getValue();
    public abstract Object getDefaultValue();
    public abstract void setValue(Object value);

    private void setModelValue() {
        if (inherited) {
            inherited = false;
            component.setBackground(DEFAULT);
            component.setToolTipText("");
        }
        Object val = component.getSelectedItem();
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
    
    private void setComboValue(Object value, Object projectValue, JComboBox field) {
        if (value != null) {
            field.setSelectedItem(value);
            component.setToolTipText("");
            inherited = false;
        } else if (projectValue != null) {
            field.setSelectedItem(projectValue);
            field.setBackground(INHERITED);
            component.setToolTipText("Value is inherited from parent POM.");
            inherited = true;
        } else {
            field.setSelectedItem(field.getModel().getElementAt(0));
            component.setToolTipText("");
            inherited = false;
        }
    }
    
}
