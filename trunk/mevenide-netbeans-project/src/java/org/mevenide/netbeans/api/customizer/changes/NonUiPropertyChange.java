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

/**
 * a MavenPropertyChange implementation that is not tied to any UI component
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class NonUiPropertyChange implements MavenPropertyChange {
    private String key;
    private boolean hasChanged;
    private String oldValue;
    private String newValue;
    private int oldLocation;
    private int newLocation;
    /** Creates a new instance of NonUiPropertyChange */
    public NonUiPropertyChange(String propkey, String oldVal, int oldLoc) {
        key = propkey;
        oldLocation = oldLoc;
        oldValue = oldVal;
        hasChanged = false;
        newLocation = oldLoc;
        newValue = oldVal;
    }

    public String getKey() {
        return key;
    }
    
    public void setNewLocation(int location) {
        newLocation = location;
        hasChanged = true;
    }

    public int getNewLocation() {
        return newLocation;
    }

    public void setNewValue(String value) {
        newValue = value;
        hasChanged = true;
    }
    
    public String getNewValue() {
        return newValue;
    }

    public int getOldLocation() {
        return oldLocation;
    }

    public String getOldValue() {
        return oldValue;
    }

    public boolean hasChanged() {
        return hasChanged;
    }
    
}
