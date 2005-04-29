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
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.configuration.Configuration;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
final class RunningServerNode extends AbstractNode implements RegistryListener {

    private Container container;
    private State lastState;
    private Sheet.Set basicProps;
    private Sheet.Set configProps;
    private Action viewLog;
    RunningServerNode(Container cont) {
        super(Children.LEAF, Lookups.fixed(new Object[] {cont}));
        container = cont;
        setName(container.getName());
        String port = container.getConfiguration().getPropertyValue(ServletPropertySet.PORT);
        setDisplayName(getName() + " at port " + port);
        updateName(container.getState());
        CargoServerRegistry.getInstance().addRegistryListener(this);
        viewLog = new ViewLogAction(container.getOutput(), 
                                       "View Log", "Log " + getName());
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
            firePropertyChange(null, null, null);
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

        Action[] retValue = new Action[7];
        retValue[0] = new StartStop();
        retValue[1] = null;
        retValue[2] = new Remove();
        retValue[3] = null;
        retValue[4] = viewLog;
        retValue[5] = null;
        retValue[6] = ((PropertiesAction)PropertiesAction.get(PropertiesAction.class)).createContextAwareInstance(Lookups.singleton(this));
        return retValue;
    }

 
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        basicProps = sheet.get(Sheet.PROPERTIES);
        try {
            ContainerReflection home = new ContainerReflection(container, File.class, "homeDir");
            home.setName("home");
            home.setDisplayName("Home Directory");
            home.setShortDescription("The directory where the container is installed.");
            ContainerReflection id = new ContainerReflection(container, String.class, "getId", null);
            id.setName("id");
            id.setDisplayName("Container Type ID");
            ContainerReflection name = new ContainerReflection(container, String.class, "getName", null);
            name.setName("name");
            name.setDisplayName("Container Type Name");
            ContainerReflection timeout = new ContainerReflection(container, Long.TYPE, "timeout");
            timeout.setDisplayName("Timeout");
            timeout.setShortDescription("Timeout (in ms) after which we consider the container cannot be started or stopped.");
            timeout.setName("timeout");
            ContainerReflection war = new ContainerReflection(container.getCapability(), Boolean.TYPE, "supportsWar", null);
            war.setDisplayName("Support WARs");
            war.setName("war");
            ContainerReflection ear = new ContainerReflection(container.getCapability(), Boolean.TYPE, "supportsEar", null);
            ear.setDisplayName("Support EARs");
            ear.setName("ear");
            ContainerReflection output = new ContainerReflection(container, File.class, "getOutput", "setOutput");
            output.setDisplayName("Output File");
            output.setShortDescription("The file to which the container's output will be logged to");
            output.setName("output");
            ContainerReflection append = new ContainerReflection(container, Boolean.TYPE, "isAppend", "setAppend");
            append.setDisplayName("Append to Output File");
            append.setShortDescription("Sets whether output of the container should be appended to an existing file, or the existing file should be truncated.");
            append.setName("append");
            ContainerReflection classpath = new ContainerReflection(container, String[].class, "extraClasspath");
            classpath.setDisplayName("Extra Classpath");
            classpath.setShortDescription("Additional Container Classpath.");
            classpath.setName("classpath");
            
            basicProps.put(new Node.Property[] {
                 id, name, war, ear, home, output, append, timeout, classpath
            });
        } catch (NoSuchMethodException exc) {
            exc.printStackTrace();
        }
        configProps = new Sheet.Set();
        configProps.setName("configuration");
        configProps.setDisplayName("Configuration");
        try {
            Configuration config = container.getConfiguration();
            ContainerReflection dir = new ContainerReflection(config, File.class, "getDir", null);
            dir.setName("dir");
            dir.setDisplayName("Directory");
            dir.setShortDescription("Configuration Directory");
            Map map = config.getProperties();
            Iterator it = map.entrySet().iterator();
            Node.Property[] prps = new Node.Property[map.entrySet().size() + 1];
            prps[0] = dir;
            int index = 1;
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                prps[index] = new ConfigProperty((String)entry.getKey());
                index = index + 1;
            }
            configProps.put(prps);
        } catch (NoSuchMethodException exc) {
            exc.printStackTrace();
        }
        
        sheet.put(configProps);
        return sheet;
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
    
    private class ContainerReflection extends PropertySupport.Reflection {
        
        ContainerReflection(Object inst, Class clazz, String prop) throws NoSuchMethodException {
            super(inst, clazz, prop);
        }
        
        ContainerReflection(Object inst, Class clazz, String getter, String setter) throws NoSuchMethodException {
            super(inst, clazz, getter, setter);
        }
        
        
        public boolean canWrite() {
            if (container.getState() == State.STOPPED) {
                return super.canWrite();
            }
            return false;
        }
    }
    
    private class ConfigProperty extends PropertySupport {
        private String key;
        ConfigProperty(String propkey) {
            super(propkey, String.class, propkey, propkey, true, true);
            key = propkey;
        }
        
        public void setValue(Object obj) throws IllegalAccessException, IllegalArgumentException, java.lang.reflect.InvocationTargetException {
            container.getConfiguration().setProperty(key, (String)obj);
        }

        public Object getValue() throws IllegalAccessException, java.lang.reflect.InvocationTargetException {
            return container.getConfiguration().getPropertyValue(key);
        }

         public boolean canWrite() {
            if (container.getState() == State.STOPPED) {
                return super.canWrite();
            }
            return false;
        }
        
        
    }

}
