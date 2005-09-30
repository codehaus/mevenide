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
package org.mevenide.netbeans.api.customizer.changes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.mevenide.netbeans.api.customizer.OriginChange;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.properties.IPropertyLocator;


/**
 * POM values changes tracker for multiple textfields with an originchange instance attached.
 * Useful for stuff line pom.organization values, pom.repository etc.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MultiTextComponentPOMChange implements MavenPOMTreeChange {
    private String key;
    private Map values;
    private int location;
    private Map newValues;
    private int newLocation;
    private Map fields;
    private OriginChange origin;
    private Map listeners;
    private OrigListener orListener;
    
    private boolean ignore = false;
    
    /**
     * @param oldValues - key:id, value:String
     * @param textfields - key:id, value:JTextComponent
     */
    public MultiTextComponentPOMChange(String keyParam, Map oldValues, int oldLocation, 
                                       Map textfields, OriginChange oc) {
        this(keyParam, oldValues, oldLocation, textfields, oc, true);
    }
    
    /**
     * @param oldValues - key:id, value:String
     * @param textfields - key:id, value:JTextComponent
     * @param attachListeners attach listeners from the begining.
     */
    public MultiTextComponentPOMChange(String keyParam, Map oldValues, int oldLocation, 
                                       Map textfields, OriginChange oc, boolean attachListeners) 
    {
        key = keyParam;
        values = oldValues;
        location = oldLocation;
        newValues = new HashMap(values);
        newLocation = oldLocation;
        fields = textfields;
        origin = oc;
        origin.setInitialLocationID(oldLocation);
        orListener = new OrigListener();
        listeners = new HashMap();
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            JTextComponent field = (JTextComponent)entry.getValue();
            String val = (String)values.get(entry.getKey());
            if (attachListeners) {
                field.setText(val == null ? "" : val);
            }
            listeners.put(entry.getKey(), new DocListener((String)entry.getKey(), field));
        }
        if (attachListeners) {
            attachListeners();
        }
    }    
    
    
    public void attachListeners() {
        origin.setChangeObserver(orListener);
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            JTextComponent field = (JTextComponent)entry.getValue();
            DocListener list = (DocListener)listeners.get(entry.getKey());
            field.getDocument().addDocumentListener(list);
        }
    }
    
    public void detachListeners() {
        origin.setChangeObserver(null);
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            JTextComponent field = (JTextComponent)entry.getValue();
            DocListener list = (DocListener)listeners.get(entry.getKey());
            field.getDocument().removeDocumentListener(list);
        }
    }
    
    /**
     * changes in the field or location combo are not prpagated into the value
     */
    public void startIgnoringChanges() {
        ignore = true;
    }
    
    /**
     * changes in the field or location combo are not prpagated into the value
     * assigns the textfield and loc combo with current values.
     */
    public void stopIgnoringChanges() {
        origin.getComponent().setEnabled(true);
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            JTextComponent field = (JTextComponent)entry.getValue();
            String val = (String)newValues.get(entry.getKey());
            field.setText(val == null ? "" : val);
            field.setEditable(true);
        }
        origin.setInitialLocationID(newLocation);
        ignore = false;
    }
    
    public int getNewLocation() {
        return newLocation;
    }
    
    public int getOldLocation() {
        return location;
    }

    public boolean hasChanged() {
        boolean toReturn =  newLocation != location || !valuesEqual();
        return toReturn;
        
    }
    
    private boolean valuesEqual() {
        boolean equal = values.keySet().containsAll(newValues.keySet()) 
                     && newValues.keySet().containsAll(values.keySet());
        if (equal) {
            Iterator it = values.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String oldval = (String)entry.getValue();
                String newval = (String)newValues.get(entry.getKey());
                if (oldval != null && newval != null) {
                    equal = oldval.equals(newval);
                } else {
                    equal= false;
                }
                if (!equal) {
                    return false;
                }
            }
        }
        return equal;
    }
    
    public void setResolvedValues(HashMap resvalues) {
        ignore = true;
        origin.getComponent().setEnabled(false);
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry ent = (Map.Entry)it.next();
            JTextComponent field = (JTextComponent)ent.getValue();
            field.setEditable(false);
            String val = (String)resvalues.get(ent.getKey());
            field.setText(val != null ? val : "");
        }
        ignore = false;
    }
    
    public void resetToNonResolvedValue() {
        startIgnoringChanges();
        stopIgnoringChanges();
    }

    public String getPath() {
        return key;
    }

    public IContentProvider getChangedContent() {
        return new SimpleContentProvider(new HashMap(newValues));
    }
    
    /**
     * helper methods for cell renderers inlists. (eg MailingLists' list)
     */
    public String getValueFor(String keyx) {
        return (String)newValues.get(keyx);
    }
    
    
  private final class DocListener implements DocumentListener {
        private String id;
        private JTextComponent textField;
        private DocListener(String keyid, JTextComponent component) {
            id = keyid;
            textField = component;
        }
        private void update() {
            if (ignore) {
                return;
            }
            newValues.put(id, textField.getText());
            if (origin.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED 
             || origin.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
                // maybe have configurable or smartish later..
                origin.setAction(OriginChange.LOCATION_POM);
            }
        }
        
        public void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        public void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
    }    
  
  private final class OrigListener implements  OriginChange.ChangeObserver {
      
        private OrigListener() {
        }

        public void locationChanged() {
            if (ignore) {
                return;
            }
            newLocation = origin.getSelectedLocationID();
            if (newLocation < 0) {
                // assuming the correct default value is not-override..
                ignore = true;
                newValues.clear();
                Iterator it = fields.values().iterator();
                while (it.hasNext()) {
                    JTextComponent field = (JTextComponent)it.next();
                    field.setText("");
                }
                ignore = false;
            }
        }
        
    }    
    
  
    private static class SimpleContentProvider implements IContentProvider {
        private Map vals;
        public SimpleContentProvider(Map vls) {
            vals = vls;
        }
        
        
        public java.util.List getProperties() {
            return Collections.EMPTY_LIST;
        }

        public IContentProvider getSubContentProvider(String keyx) {
            return null;
        }

        public java.util.List getSubContentProviderList(String parentKey, String childKey) {
            return null;
        }

        public String getValue(String keyx) {
            return (String)vals.get(keyx);
        }

        public java.util.List getValueList(String parentKey, String childKey) {
            return (java.util.List)vals.get(parentKey);
        }
        
    }
}
