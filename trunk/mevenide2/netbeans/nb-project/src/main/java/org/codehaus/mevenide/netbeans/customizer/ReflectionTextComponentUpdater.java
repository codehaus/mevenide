/*
 * ReflectionTextComponentUpdater.java
 *
 * Created on February 22, 2006, 6:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.customizer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mkleint
 */
public class ReflectionTextComponentUpdater extends TextComponentUpdater {
    private Object model;
    private Object defaults;
    private Method modelgetter;
    private Method defgetter;
    private Method modelsetter;
    /** Creates a new instance of ReflectionTextComponentUpdater */
    public ReflectionTextComponentUpdater(String getter, String setter, Object model, Object defaults, JTextComponent field) 
                        throws NoSuchMethodException {
        super(field);
        this.model = model;
        this.defaults = defaults;
        modelgetter = model.getClass().getMethod(getter, new Class[0]);
        modelsetter = model.getClass().getMethod(setter, new Class[] {String.class});
        if (defaults != null) {
            defgetter = defaults.getClass().getMethod(getter, new Class[0]);
        }
        
    }
    
    public String getValue() {
        try {
            return (String)modelgetter.invoke(model, new Object[0]);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public String getDefaultValue() {
        if (defgetter == null) {
            return null;
        }
        try {
            return (String)defgetter.invoke(defaults, new Object[0]);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void setValue(String value) {
        try {
            modelsetter.invoke(model, new Object[] { value });
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
    
}
