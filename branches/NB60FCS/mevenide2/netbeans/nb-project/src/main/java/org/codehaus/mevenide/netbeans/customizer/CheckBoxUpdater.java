/* ==========================================================================
 * Copyright 2007 Mevenide Team
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
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public abstract class CheckBoxUpdater implements ActionListener, AncestorListener {

    private JCheckBox component;
    
    private boolean inherited = false;
    
    public CheckBoxUpdater(JCheckBox comp) {
        component = comp;
        component.addAncestorListener(this);
    }
    
    public abstract Boolean getValue();
    public abstract boolean getDefaultValue();
    public abstract void setValue(Boolean value);

    private void setModelValue() {
        if (inherited) {
            inherited = false;
            component.setFont(component.getFont().deriveFont(Font.BOLD));
            
            component.setToolTipText(""); //NOI18N
        }
        boolean val = component.isSelected();
        setValue(val == getDefaultValue() ? null : val);
    }
    
    public void actionPerformed(ActionEvent e) {
        setModelValue();
    }

    public void ancestorAdded(AncestorEvent event) {
        setCheckBoxValue(getValue(), getDefaultValue(), component);
        component.addActionListener(this);
    }

    public void ancestorRemoved(AncestorEvent event) {
        component.removeActionListener(this);
    }

    public void ancestorMoved(AncestorEvent event) {
    }
    
    private void setCheckBoxValue(Boolean value, boolean defValue, JCheckBox component) {
        if (value != null) {
            component.setSelected(value.booleanValue());
            component.setToolTipText(""); //NOI18N
            inherited = false;
            component.setFont(component.getFont().deriveFont(Font.BOLD));
        } else {
            component.setSelected(defValue);
            component.setToolTipText(NbBundle.getMessage(CheckBoxUpdater.class, "MSG_Value_Inherited")); //NOI18N
            inherited = true;
            component.setFont(component.getFont().deriveFont(Font.PLAIN));
      }
    }

}
