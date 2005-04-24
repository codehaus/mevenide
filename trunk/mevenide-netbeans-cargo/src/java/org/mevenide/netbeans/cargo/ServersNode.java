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

import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.codehaus.cargo.container.Container;
import org.openide.util.HelpCtx;
import org.openide.nodes.*;
import org.openide.util.NbBundle;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class ServersNode extends AbstractNode {


    /** Reference to an instance of this class */
    private static Node processNode;
    
    /**
     * 
     */
    private ServersNode () {
        super(new ServChildren());
        System.out.println("server node..");
//        setIconBase("org/mevenide/netbeans/cargo/"); // NOI18N
        setName("Cargo Containers");
        setShortDescription("Cargo Containers");
    }


    static final class RootHandle implements Node.Handle {
        static final long serialVersionUID =-6979883764640743928L;

        public Node getNode () {
            return getRootNode();
        }
    }

    /**
     * 
     * @return 
     */
    public static Node getRootNode() {
        System.out.println("root node getting !!!!!");
        if (processNode == null) {
            processNode = new ServersNode();
        }
        return processNode;
    }

    public Action[] getActions(boolean context) {

        Action[] retValue = new Action[2];
        retValue[0] = new AddContainerAction();
        retValue[1] = new ViewLogAction(CargoServerRegistry.getInstance().getMonitorFile(), 
                                       "View Cargo Log", "Cargo Monitor");
        return retValue;
    }

    private static final class ServChildren extends Children.Keys 
                                            implements RegistryListener {
        
        public ServChildren() {}
        
        /** key representing no processes running */
        private static final Object KEY_NONE = "NONE"; // NOI18N
        
        private void updateKeys() {
            Collection c = CargoServerRegistry.getInstance().getContainers();
            setKeys(c.isEmpty() ? Collections.singleton(KEY_NONE) : c);
        }
        
        protected void addNotify() {
            super.addNotify();
            updateKeys();
            CargoServerRegistry.getInstance().addRegistryListener(this);
        }
        
        protected void removeNotify() {
            CargoServerRegistry.getInstance().removeRegistryListener(this);
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }

        protected Node[] createNodes(Object key) {
            if (key == KEY_NONE) {
                AbstractNode n = new AbstractNode(Children.LEAF);
                n.setName("No Containers Installed");
                n.setShortDescription("Please add a cargo container");
//                n.setIconBase("org/netbeans/core/resources/noProcesses"); // NOI18N
                return new Node[] {n};
            } else {
                return new Node[] {new RunningServerNode((Container)key)};
            }
        }

        public void containerAdded(RegistryEvent event) {
            updateKeys();
        }

        public void containerRemoved(RegistryEvent event) {
            updateKeys();
        }

        public void stateChanged(RegistryEvent event) {
        }
        

        
    }
    
}
