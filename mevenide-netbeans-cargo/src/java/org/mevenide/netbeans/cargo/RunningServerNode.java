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

package org.mevenide.netbeans.cargo;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
final class RunningServerNode extends AbstractNode implements RegistryListener {

    private Container container;
    private State lastState;

    RunningServerNode(Container cont) {
        super(Children.LEAF, Lookups.fixed(new Object[] {cont}));
        container = cont;
        setName(container.getName());
        String port = container.getConfiguration().getPropertyValue(ServletPropertySet.PORT);
        setDisplayName(getName() + " at port " + port);
        updateName(container.getState());
        CargoServerRegistry.getInstance().addRegistryListener(this);
    }


    
    public boolean canCopy() {
        return false;
    }

    public void containerAdded(RegistryEvent event) {
        //IGNORE
    }

    public void containerRemoved(RegistryEvent event) {
    }

    public void stateChanged(RegistryEvent event) {
        if (event.getContainer() == container) {
            lastState = event.getFutureState() != null ? event.getFutureState() : container.getState();
            updateName(lastState);
        }
    }
    
    private void updateName(State state) {
        String port = container.getConfiguration().getPropertyValue(ServletPropertySet.PORT);
        if (state == State.STARTED) {
            setShortDescription(getName() + " started at " + container.getHomeDir());
        } 
        else if (state == State.STARTING) {
            setShortDescription(getName() + " starting at " + container.getHomeDir());
        }
        else if (state == State.STOPPING) {
            setShortDescription(getName() + " stopping at " + container.getHomeDir());
        } else {
            setShortDescription(getName() + " at " + container.getHomeDir());
        }
        // do force fire display name so that gethtml can kick in.
        fireDisplayNameChange(null, getDisplayName());
    }

    public void destroy() throws java.io.IOException {
        super.destroy();
        CargoServerRegistry.getInstance().removeRegistryListener(this);
    }

    public String getHtmlDisplayName() {
        String partial = null;
        if (lastState == State.STARTED) {
            partial = "<b>" + getName() + "</b>";
        } 
        if (lastState == State.STARTING) {
            partial = "<b><i>" + getName() + "</i></b>";
        }
        if (lastState == State.STOPPING) {
            partial = "<i>" + getName() + "</i>";
        }
        if (partial != null) {
            String port = container.getConfiguration().getPropertyValue(ServletPropertySet.PORT);
            
            return "<html>" + partial + " at port " + port + "</html>";
        }
        return super.getHtmlDisplayName();
    }
    
    public Action[] getActions(boolean context) {

        Action[] retValue = new Action[5];
        retValue[0] = new StartStop();
        retValue[1] = null;
        retValue[2] = new Remove();
        retValue[3] = new ViewLogAction(container.getOutput(), 
                                       "View Log", "Log " + getName());
        retValue[4] = ((PropertiesAction)PropertiesAction.get(PropertiesAction.class)).createContextAwareInstance(this.getLookup());
        return retValue;
    }


    //-- ACTIONS
    
    private class StartStop extends AbstractAction {
        public StartStop() {
            putValue(Action.NAME, "Start/Stop Container");
            putValue(Action.SHORT_DESCRIPTION, "Start/Stop Cargo Container");
        }
        
        public void actionPerformed(ActionEvent actionEvent) {
            if (container.getState() == State.STOPPED) {
                CargoServerRegistry.getInstance().startContainer(container);
            } else if (container.getState() == State.STARTED) {
                CargoServerRegistry.getInstance().stopContainer(container);                
            } 
        }
    }
    
    private class Remove extends AbstractAction {
        public Remove() {
            putValue(Action.NAME, "Remove");
            putValue(Action.SHORT_DESCRIPTION, "Remove Cargo Container");
        }
        
        public void actionPerformed(ActionEvent actionEvent) {
            CargoServerRegistry.getInstance().removeContainer(container);
        }
    }

}
