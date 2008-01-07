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
package org.codehaus.mevenide.continuum.nodes;

import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroupSummary;
import org.codehaus.mevenide.continuum.ContinuumSettings2;
import org.codehaus.mevenide.continuum.ServerInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class ServerNode extends AbstractNode {

    /** Creates a new instance of ContinuumServerNode */
    public ServerNode(String serverRawInfo) {
        super(new ServerChildren(serverRawInfo));
        ServerInfo serverInfo = new ServerInfo(serverRawInfo);
        setName(serverRawInfo);
        setDisplayName(serverInfo.getXmlRpcUrl().toString());
        setIconBaseWithExtension("org/codehaus/mevenide/continuum/threeBrands.gif");
    }

    public Action[] getActions(boolean b) {
        Action[] retValue = {new RemoveAction(), new RefreshAction()};
        return retValue;
    }

    protected static class ServerChildren extends Children.Keys {

        private ServerInfo serverInfo;
        private ContinuumXmlRpcClient reader;
        private RequestProcessor queue;

        public ServerChildren(String serverRawInfo) {
            this.serverInfo = new ServerInfo(serverRawInfo);
            this.queue = new RequestProcessor("Continuum server processor", 1);
            reader = new ContinuumXmlRpcClient(serverInfo.getXmlRpcUrl(), serverInfo.getUser(), serverInfo.getPassword());

        }

        protected Node[] createNodes(Object object) {
            if (object instanceof ProjectGroupSummary) {
                ProjectGroupSummary projectGroup = (ProjectGroupSummary) object;
                return new Node[]{new ProjectGroupNode(projectGroup, reader, serverInfo, queue)};
            }
            if (object instanceof String) {
                AbstractNode nd = new AbstractNode(Children.LEAF);
                nd.setDisplayName((String) object);
                return new Node[]{nd};
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
                setKeys(Collections.singleton("Connecting to "+serverInfo.getXmlRpcUrl()+" ..."));
                queue.post(new Runnable() {

                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            setKeys(reader.getAllProjectGroups());
                        } catch (Exception ex) {
                            setKeys(Collections.singletonList(getDisplayableMessageFrom(ex)));
                            throw new RuntimeException(ex);
                        }
                    }

                    private String getDisplayableMessageFrom(Exception ex) {
                        return (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getName();
                    }
                });
            }
        }
    }

    @SuppressWarnings("serial")
    private class RemoveAction extends AbstractAction {

        public RemoveAction() {
            this.putValue(Action.NAME, "Remove");
        }

        public void actionPerformed(ActionEvent e) {
            ContinuumSettings2.getDefault().removeServer(getName());
        }
    }

    @SuppressWarnings("serial")
    private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            this.putValue(Action.NAME, "Refresh");
        }

        public void actionPerformed(ActionEvent e) {
            setChildren(new ServerChildren(getName()));
        }
    }
}
