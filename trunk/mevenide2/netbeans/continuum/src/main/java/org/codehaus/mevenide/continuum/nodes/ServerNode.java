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

import org.apache.maven.continuum.xmlrpc.project.ProjectGroupSummary;
import org.codehaus.mevenide.continuum.ContinuumClient;
import org.codehaus.mevenide.continuum.ContinuumSettings2;
import org.codehaus.mevenide.continuum.forms.MavenProjectForm;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author mkleint
 */
public class ServerNode extends AbstractNode {

    private ContinuumClient client;

    /** Creates a new instance of ContinuumServerNode */
    public ServerNode(String serverRawInfo) {
        super(new ServerChildren(new ContinuumClient(serverRawInfo)));
        client = ((ServerChildren)getChildren()).getClient();
        
        setName(client.getServerInfo().toString());
        setDisplayName(client.getServerInfo().getXmlRpcUrl().toString());
        setIconBaseWithExtension("org/codehaus/mevenide/continuum/threeBrands.gif");
    }

    public Action[] getActions(boolean b) {
        Action[] retValue = {new RemoveAction(), new RefreshAction(), new AddMaven2Action(client)};
        return retValue;
    }

    protected static class ServerChildren extends Children.Keys {

        private ContinuumClient client;

        public ServerChildren(ContinuumClient client) {
            this.client = client;
        }

        protected Node[] createNodes(Object object) {
            if (object instanceof ProjectGroupSummary) {
                ProjectGroupSummary projectGroup = (ProjectGroupSummary) object;
                return new Node[]{new ProjectGroupNode(projectGroup, client)};
            }
            if (object instanceof String) {
                AbstractNode nd = new AbstractNode(Children.LEAF);
                nd.setDisplayName((String) object);
                return new Node[]{nd};
            }
            return new Node[0];
        }

        @Override
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }

        @Override
        protected void addNotify() {
            super.addNotify();
            if (client != null) {
                setKeys(Collections.singleton("Connecting to " + client.getServerInfo().getXmlRpcUrl() + " ..."));
                client.getQueue().post(new Runnable() {

                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            setKeys(client.getXmlRpcClient().getAllProjectGroups());
                        } catch (Exception ex) {
                            setKeys(Collections.singletonList(getDisplayableMessageFrom(ex)));
                            throw new RuntimeException(ex);
                        }
                    }
                });
            }
        }

        private String getDisplayableMessageFrom(Exception ex) {
            return (ex.getMessage() != null) ? ex.getMessage() : ex.getClass().getName();
        }

        public ContinuumClient getClient() {
            return client;
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
            setChildren(new ServerChildren(new ContinuumClient(getName())));
        }
    }

    private class AddMaven2Action extends AbstractAction {

        private ContinuumClient client;

        public AddMaven2Action(ContinuumClient client) {
            this.putValue(Action.NAME, NbBundle.getMessage(ServerNode.class, "ServerNode.addMaven2.action"));
            this.client = client;
        }

        public void actionPerformed(ActionEvent e) {
            MavenProjectForm form = new MavenProjectForm();
            DialogDescriptor dd = new DialogDescriptor(form, NbBundle.getMessage(ServerNode.class, "ServerNode.addMaven2.action") );
            dd.setOptions(new Object[]{
                        NotifyDescriptor.OK_OPTION,
                        NotifyDescriptor.CANCEL_OPTION
                    });
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (ret == NotifyDescriptor.OK_OPTION) {
                try {
                    // TODO : enqueue + refresh + exception handling 
                    client.getXmlRpcClient().addMavenTwoProject(form.getPomUrl());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }       
    }
}
