/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.nodes;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.spi.project.ui.support.NodeList;

/**
 *
 * @author mkleint
 */
public abstract class AbstractMavenNodeList<K> implements NodeList<K> {
    private List<ChangeListener> listeners;
    /** Creates a new instance of AbstractMavenNodeFactory */
    protected AbstractMavenNodeList() {
        listeners = new ArrayList<ChangeListener>();
    }
    
    
    public void addChangeListener(ChangeListener list) {
        listeners.add(list);
    }
    
    public void removeChangeListener(ChangeListener list) {
        listeners.remove(list);
    }
    
    protected void fireChange() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener list : listeners) {
            list.stateChanged(event);
        }
    }
    
    public void addNotify() {
    }
    
    public void removeNotify() {
    }
    
}
