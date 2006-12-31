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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.mevenide.netbeans.api.customizer.OriginChange;
import org.mevenide.netbeans.project.writer.AbstractContentProvider;
import org.mevenide.project.io.IContentProvider;
import org.mevenide.properties.IPropertyLocator;


/**
 * changes tracker for lists with an originchange instance attached.
 * useful for pom.mailingLists etc.
 * @author  Milos Kleint (mkleint@codehaus.org)
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
    private boolean hasPomChanges;
    
    /**
     * assumes the items in the list are single strings..
     */
    public ListModelPOMChange(String keyParam, List oldValues, int oldLocation, 
                              DefaultListModel lst, OriginChange oc) 
    {
        this(keyParam, oldValues, oldLocation, lst, oc, false);
    }
    
    /**
     * @param itemsPomChanges - if true, items in model are MultiTextComponentPOMChanges representing subcontent, otherwise it's
     *                  just plain strings..
     */
    public ListModelPOMChange(String keyParam, List oldValues, int oldLocation, 
                              DefaultListModel lst, OriginChange oc, boolean itemsPomChanges) 
    {
        key = keyParam;
        values = oldValues;
        location = oldLocation;
        newValues = new ArrayList(values);
        newLocation = oldLocation;
        origin = oc;
        origin.setInitialLocationID(oldLocation);
        model = lst;
        Iterator it = newValues.iterator();
        while (it.hasNext()) {
            model.addElement(it.next());
        }
        listener = new DocListener();
        origin.setChangeObserver(listener);
        model.addListDataListener(listener);
        hasPomChanges = itemsPomChanges;
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
        if (!hasPomChanges) {
            return new ValueListContentProvider(newValues);
        } else {
            return new SubProviderContainerProvider(newValues);
        }
    }
    
    
  private final class DocListener implements ListDataListener, OriginChange.ChangeObserver {
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
            if (origin.getSelectedLocationID() == IPropertyLocator.LOCATION_NOT_DEFINED 
             || origin.getSelectedLocationID() == IPropertyLocator.LOCATION_DEFAULTS) {
                // assume the default placement is pom file..
                // maybe have configurable or smartish later..
                origin.setAction(OriginChange.LOCATION_POM);
            }
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
    
    //TODO needs refactoring maybe
    protected static class ValueListContentProvider extends AbstractContentProvider {
        private List vals;
        public ValueListContentProvider(List vls) {
            vals = vls;
        }
        public List getValueList(String parentKey, String childKey) {
            return vals;
        }
    }  
    //TODO needs refactoring maybe
    protected static class SubProviderContainerProvider extends AbstractContentProvider {
        private List vals;
        public SubProviderContainerProvider(List vls) {
            vals = vls;
        }
        public List getSubContentProviderList(String parentKey, String childKey) {
            List toReturn = new ArrayList();
            Iterator it = vals.iterator();
            while (it.hasNext()) {
                MultiTextComponentPOMChange chng = (MultiTextComponentPOMChange)it.next();
                toReturn.add(chng.getChangedContent());
            }
            return toReturn;
        }
    }  
    
    
}
