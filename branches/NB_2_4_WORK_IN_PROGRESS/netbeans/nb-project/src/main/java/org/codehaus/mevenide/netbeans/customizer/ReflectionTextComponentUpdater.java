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
