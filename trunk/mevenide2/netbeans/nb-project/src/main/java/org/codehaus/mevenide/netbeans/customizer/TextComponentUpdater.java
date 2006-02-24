/*
 * TextComponentUpdater.java
 *
 * Created on February 22, 2006, 5:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.customizer;

import java.awt.Color;
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
public abstract class TextComponentUpdater implements DocumentListener, AncestorListener {
    private static Color INHERITED = new Color(254, 255, 200);
    private static Color DEFAULT = UIManager.getColor("TextField.background");
    
    private JTextComponent component;
    
    private boolean inherited = false;
    
    /** Creates a new instance of TextComponentUpdater */
    public TextComponentUpdater(JTextComponent comp) {
        component = comp;
        component.addAncestorListener(this);
    }
    
    public abstract String getValue();
    public abstract String getDefaultValue();
    public abstract void setValue(String value);

    private void setModelValue() {
        if (inherited) {
            inherited = false;
            component.setBackground(DEFAULT);
            component.setToolTipText("");
        }
        setValue(component.getText().trim().length() == 0 ? null : component.getText());
        if (component.getText().trim().length() == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    component.getDocument().removeDocumentListener(TextComponentUpdater.this);
                    setTextFieldValue(getValue(), getDefaultValue(), component);
                    component.getDocument().addDocumentListener(TextComponentUpdater.this);
                }
            });
        }
    }
    
    public void insertUpdate(DocumentEvent e) {
        setModelValue();
    }

    public void removeUpdate(DocumentEvent e) {
        setModelValue();
    }

    public void changedUpdate(DocumentEvent e) {
        setModelValue();
    }
    

    public void ancestorAdded(AncestorEvent event) {
        setTextFieldValue(getValue(), getDefaultValue(), component);
        component.getDocument().addDocumentListener(this);
    }

    public void ancestorRemoved(AncestorEvent event) {
        component.getDocument().removeDocumentListener(this);
    }

    public void ancestorMoved(AncestorEvent event) {
    }
    
    private void setTextFieldValue(String value, String projectValue, JTextComponent field) {
        if (value != null) {
            field.setText(value);
            component.setToolTipText("");
            inherited = false;
        } else if (projectValue != null) {
            field.setText(projectValue);
            field.setBackground(INHERITED);
            component.setToolTipText("Value is inherited from parent POM.");
            inherited = true;
        } else {
            field.setText("");
            component.setToolTipText("");
            inherited = false;
        }
    }
    
}
