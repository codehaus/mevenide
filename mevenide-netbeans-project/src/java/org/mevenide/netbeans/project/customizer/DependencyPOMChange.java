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

package org.mevenide.netbeans.project.customizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.text.JTextComponent;
import org.apache.maven.project.Dependency;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.project.io.IContentProvider;


/**
 * pom change instance for dependencies
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DependencyPOMChange implements MavenPOMTreeChange {
    
    private String key;
    private Map values;
    private Map propValues;
    
    private int location;
    private Map newValues;
    private Map newPropValues;
    private int newLocation;
    private Map fields;
//    private JComboBox box;
//    private JTextComponent propField;
    private OriginChange origin;
//    private Map listeners;
    private OrigListener orListener;
    private DependencyContentProvider contentProvider;
    
    private boolean ignore = false;
    
    /**      
     *
     * @param oldValues - key:id, value:String
     * @param textfields - key:id, value:JTextComponent
     * @param propOldValues - key:id value:String
     * @param attachListeners attach listeners from the begining.
     */
    private DependencyPOMChange(String keyParam, Map oldValues, int oldLocation, 
                               Map textfields, OriginChange oc, 
                               Map propOldValues, 
                               boolean attachListeners) 
    {
        key = keyParam;
        values = oldValues;
        propValues = propOldValues;
        location = oldLocation;
        newValues = new HashMap(values);
        newPropValues = new HashMap(propValues);
        newLocation = oldLocation;
        fields = textfields;
        origin = oc;
        origin.setInitialLocationID(oldLocation);
        orListener = new OrigListener();
        if (attachListeners) {
            attachListeners();
        }
        contentProvider = new DependencyContentProvider();
    } 
    
    
    public static DependencyPOMChange createChangeInstance(Dependency dep,
            int location, Map fieldMap,
            OriginChange oc,
            boolean attachListeners) {
        HashMap vals = new HashMap();
        HashMap props = new HashMap();
        if (dep != null) {
            vals.put("artifactId", dep.getArtifactId());
            vals.put("groupId", dep.getGroupId());
            vals.put("version", dep.getVersion());
            vals.put("type", dep.getType());
            vals.put("jar", dep.getJar());
            vals.put("url", dep.getUrl());  
            Map map = dep.resolvedProperties();
            if (map != null) {
                Iterator it2 = map.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry ent = (Map.Entry)it2.next();
                if (ent.getValue() != null && ent.getValue().toString().trim().length() > 0) {
                    props.put(ent.getKey(), ent.getValue());
                }
            }
            }
        }
        DependencyPOMChange change = new DependencyPOMChange(
                "pom.dependencies.dependency",
                vals, location, fieldMap,
                oc,
                props, attachListeners);
        return change;
    }
    
    
    public void attachListeners() {
        origin.setChangeObserver(orListener);
    }
    
    public void detachListeners() {
        origin.setChangeObserver(null);
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
        boolean equal = values.keySet().containsAll(newValues.keySet()) &&
                           newValues.keySet().containsAll(values.keySet());
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
    
    public void setNewValues(HashMap newOnes, HashMap newProps) {
        newValues = new HashMap(newOnes);
        newPropValues = new HashMap(newProps);
        //TODO
    }
    
    public HashMap getOldValues() {
        return new HashMap(values);
    }
    
    public HashMap getOldProperties() {
        return new HashMap(propValues);
    }

    public IContentProvider getChangedContent() {
        return contentProvider;
    }
    
  
  private class OrigListener implements  OriginChange.ChangeObserver {
      
        private OrigListener() {
        }

        public void locationChanged() {
            if (ignore) {
                return;
            }
            newLocation = origin.getSelectedLocationID();
            if (newLocation  < 0) {
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
    
  
    private class DependencyContentProvider implements IContentProvider {
        public DependencyContentProvider() {
        }
        
        
        public java.util.List getProperties() {
            List toReturn = new ArrayList();
            Iterator it = newPropValues.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry ent = (Map.Entry)it.next();
                toReturn.add("" + ent.getKey() + ":" + ent.getValue());
            }
            return toReturn;
        }

        public IContentProvider getSubContentProvider(String key) {
            return null;
        }

        public java.util.List getSubContentProviderList(String parentKey, String childKey) {
            return null;
        }

        public String getValue(String key) {
            return (String)newValues.get(key);
        }

        public java.util.List getValueList(String parentKey, String childKey) {
            return null;
        }
        
    }    
}
