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
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.mevenide.continuum.ContinuumSettings2;
import org.codehaus.mevenide.continuum.options.SingleServer;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author laurent.foret
 */
public class RootNode extends AbstractNode {

    private static RootNode instance;

    private RootNode() {
        super(new RootChildren());
        setName("continuumRoot");
        setDisplayName("Continuum servers");
        setIconBaseWithExtension("org/codehaus/mevenide/continuum/ContinuumServer.png");
    }

    public static RootNode getInstance() {
        if (instance == null) {
            instance = new RootNode();
        }
        return instance;
    }

    public Action[] getActions(boolean b) {
        Action[] retValue = {new AddAction(), new DeleteAllServersAction()};
        return retValue;
    }

    private static class RootChildren extends Children.Keys {

        private PreferenceChangeListener listener;

        RootChildren() {
            listener = new PreferenceChangeListener() {
                public void preferenceChange(PreferenceChangeEvent evt) {
                    reloadKeys();
                }
            };
        }

        protected Node[] createNodes(Object object) {
            return new Node[]{new ServerNode((String) object)};
        }

        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }

        protected void addNotify() {
            super.addNotify();
            reloadKeys();
            ContinuumSettings2.getDefault().getPreferences().addPreferenceChangeListener(listener);
        }

        void reloadKeys() {
            setKeys(ContinuumSettings2.getDefault().getServerArray());
        }
    }

    private class AddAction extends AbstractAction {

        public AddAction() {
            this.putValue(Action.NAME, "Add server ...");
        }

        public void actionPerformed(ActionEvent e) {
            SingleServer ss = new SingleServer();
            DialogDescriptor dd = new DialogDescriptor(ss, "Add Continuum server");
            dd.setOptions(new Object[]{
                NotifyDescriptor.OK_OPTION,
                NotifyDescriptor.CANCEL_OPTION
            });
            Object ret = DialogDisplayer.getDefault().notify(dd);
            if (ret == NotifyDescriptor.OK_OPTION) {
                try {
                    ContinuumSettings2.getDefault().addServer(ss.getServerInfo());
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private class DeleteAllServersAction extends AbstractAction {

        public DeleteAllServersAction() {
            this.putValue(Action.NAME, "Delete all servers");
        }

        public void actionPerformed(ActionEvent e) {
            ContinuumSettings2.getDefault().removeServers();
        }
    }
}
