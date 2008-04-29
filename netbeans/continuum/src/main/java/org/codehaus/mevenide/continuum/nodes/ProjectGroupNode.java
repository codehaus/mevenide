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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.Icon;
import javax.swing.UIManager;
import org.apache.maven.continuum.xmlrpc.project.Project;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroupSummary;
import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;
import org.codehaus.mevenide.continuum.ContinuumClient;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;

public class ProjectGroupNode extends AbstractNode {

    public ProjectGroupNode(ProjectGroupSummary projectGroup,
            ContinuumClient client) {
        super(new ProjectGroupChildren(projectGroup, client));
        setName(Integer.toString(projectGroup.getId()));
        setDisplayName(projectGroup.getName()); 
//        setIconBaseWithExtension("");
    }

    public Action[] getActions(boolean b) {
        Action[] retValue = {new RefreshAction()};
        return retValue;
    }

    private Image getIcon(boolean opened) {
        Icon baseIcon = UIManager.getIcon(opened ? "Tree.openIcon" : "Tree.closedIcon"); 
        Image badge = Utilities.loadImage("org/codehaus/mevenide/continuum/continuum-badge.png", true); //NOI18N
        return Utilities.mergeImages(Utilities.icon2Image(baseIcon), badge, 8, 8);
    }

    @Override
    public Image getIcon(int type) {
        return getIcon(false);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(true);
    }

    
    @SuppressWarnings("unchecked")
    private static class ProjectGroupChildren extends Children.Keys {

        private ProjectGroupSummary projectGroup;
        private ContinuumClient client;

        public ProjectGroupChildren(ProjectGroupSummary projectGroup,
                ContinuumClient client) {
            this.projectGroup = projectGroup;
            this.client = client;
        }

        protected Node[] createNodes(Object object) {
            if (object instanceof Project) {
                Project project = (Project) object;
                return new Node[]{new ProjectNode(project, client)};
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
            if (client != null && client.getXmlRpcClient() != null) {
                setKeys(Collections.singleton("Fetching projects ..."));
                client.getQueue().post(new Runnable() {

                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            Collection<Project> projects = new ArrayList<Project>();
                            for (ProjectSummary summary : client.getXmlRpcClient().getProjects(projectGroup.getId())) {
                                projects.add(client.getXmlRpcClient().getProjectWithAllDetails(summary.getId()));
                            }
                            setKeys(projects);
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
    private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            this.putValue(Action.NAME, "Refresh");
        }

        public void actionPerformed(ActionEvent e) {
            setChildren(new ProjectGroupChildren(
                    ((ProjectGroupChildren) getChildren()).projectGroup,
                    ((ProjectGroupChildren) getChildren()).client));
        }
    }
}
