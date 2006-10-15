/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.continuum;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import org.apache.maven.continuum.model.project.Project;
import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.mevenide.continuum.rpc.ProjectsReader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author mkleint
 */
public class ServerNode extends AbstractNode {
    
    /** Creates a new instance of ContinuumServerNode */
    public ServerNode(String url) {
        super(new ServerChildren(url));
        setName(url);
        setDisplayName(url);
        setIconBaseWithExtension("org/codehaus/mevenide/continuum/ContinuumServer.png");
    }
    
    
    private static class ServerChildren extends Children.Keys {
        private String url;
        private ProjectsReader reader;
        public ServerChildren(String url) {
            this.url = url;
            try {
                reader = new ProjectsReader(new URL(url));
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        
        protected Node[] createNodes(Object object) {
            if (object instanceof Project) {
                Project proj = (Project)object;
                return new Node[] { new ProjectNode(proj, reader) };
            }
            if (object instanceof String) {
                AbstractNode nd = new AbstractNode(Children.LEAF);
                nd.setDisplayName((String)object);
                return new Node[] { nd };
            }
            return new Node[0];
        }
        
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected void addNotify() {
            super.addNotify();
            if (reader != null) {
                setKeys(Collections.singleton("Loading..."));
                ProjectNode.QUEUE.post(new Runnable() {
                    public void run() {
                        try {
                            setKeys(Arrays.asList(reader.readProjects()));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            setKeys(Collections.EMPTY_LIST);
                        } catch (XmlRpcException ex) {
                            ex.printStackTrace();
                            setKeys(Collections.EMPTY_LIST);
                        }
                    }
                });
            }
        }
        
    }
}
