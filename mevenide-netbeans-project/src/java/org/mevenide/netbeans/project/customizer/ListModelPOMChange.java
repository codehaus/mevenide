/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.mevenide.netbeans.project.customizer.ui.OriginChange;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.properties.IPropertyLocator;


/**
 * changes tracker for lists with an originchange instance attached.
 * @author  Milos Kleint (ca206216@tiscali.cz)
df */
public class ListModelPOMChange implements MavenPOMTreeChange {
    
    private String key;
    private List values;
    private int location;
    private List newValues;
    private int newLocation;
    private DefaultListModel model;
    private OriginChange origin;
    private DocListener listener;
    
    private boolean ignore = false;
    
    public ListModelPOMChange(String keyParam, List oldValues, int oldLocation, 
                              DefaultListModel lst, OriginChange oc) {
        key = keyParam;
        values = oldValues;
        location = oldLocation;
        newValues = new ArrayList(values);
        newLocation = oldLocation;
        origin = oc;
        origin.setSelectedLocationID(oldLocation);
        model = lst;
        Iterator it = newValues.iterator();
        while (it.hasNext()) {
            model.addElement(it.next());
        }
        listener = new DocListener();
        origin.setChangeObserver(listener);
        model.addListDataListener(listener);
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
        model.removeAllElements();
        Iterator it = newValues.iterator();
        while (it.hasNext()) {
            model.addElement(it.next());
        }
        origin.setSelectedLocationID(newLocation);
        ignore = false;
    }
    
    public int getNewLocation() {
        return newLocation;
    }
    
    public int getOldLocation() {
        return location;
    }

    public boolean hasChanged() {
        return newLocation != location || !values.equals(newValues);
    }
    
    public void setResolvedValues(List resvalues) {
        ignore = true;
        origin.getComponent().setEnabled(false);
        model.removeAllElements();
        Iterator it = resvalues.iterator();
        while (it.hasNext()) {
            model.addElement(it.next());
        }
        ignore = false;
    }
    
    public void resetToNonResolvedValue() {
        ignore = true;
        origin.getComponent().setEnabled(true);
        model.removeAllElements();
        Iterator it = newValues.iterator();
        while (it.hasNext()) {
            model.addElement(it.next());
        }
        ignore = false;
    }

    public String getPath() {
        return key;
    }

    public IContentProvider getChangedContent() {
        return new ValueListContentProvider(newValues);
    }
    
    
  private class DocListener implements ListDataListener, OriginChange.ChangeObserver {
        private DocListener() {
        }
        private void update() {
            if (ignore) {
                return;
            }
            Enumeration en = model.elements();
            newValues.clear();
            while (en.hasMoreElements()) {
                newValues.add(en.nextElement());
            }
            if (origin.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED ||
                origin.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
                // assume the default placement is pom file..
                // maybe have configurable or smartish later..
                origin.setAction(OriginChange.ACTION_POM_MOVE_TO_CHILD);
            }
        }
        
        public void actionSelected(String changeAction) {
            if (ignore) {
                return;
            }
            newLocation = origin.getSelectedLocationID();
            if (OriginChange.ACTION_REMOVE_ENTRY.equals(changeAction)) {
                // assuming the correct default value is not-override..
                ignore = true;
                newValues.clear();
                model.removeAllElements();
                ignore = false;
            }
        }

      public void contentsChanged(ListDataEvent listDataEvent) {
          update();
      }

      public void intervalAdded(ListDataEvent listDataEvent) {
          update();
      }

      public void intervalRemoved(ListDataEvent listDataEvent) {
          update();
      }
        
    }    
    
    protected static class ValueListContentProvider implements IContentProvider {
        private List vals;
        public ValueListContentProvider(List values) {
            vals = values;
        }
        
        public List getProperties() {
            return Collections.EMPTY_LIST;
        }

        public IContentProvider getSubContentProvider(String key) {
            return null;
        }

        public List getSubContentProviderList(String parentKey, String childKey) {
            return null;
        }

        public String getValue(String key) {
            return null;
        }

        public List getValueList(String parentKey, String childKey) {
            return vals;
        }
        
    }  
}