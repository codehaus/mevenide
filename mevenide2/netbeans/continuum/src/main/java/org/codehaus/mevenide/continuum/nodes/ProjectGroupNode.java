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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.Project;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroup;
import org.apache.maven.continuum.xmlrpc.project.ProjectGroupSummary;
import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;
import org.codehaus.mevenide.continuum.ServerInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.RequestProcessor;

public class ProjectGroupNode extends AbstractNode {

    // private ServerInfo serverInfo;
    // private ProjectGroup projectGroup;
    // private ContinuumXmlRpcClient reader;
    // private RequestProcessor queue;
    public ProjectGroupNode(ProjectGroupSummary projectGroup,
            ContinuumXmlRpcClient reader, ServerInfo serverInfo,
            RequestProcessor queue) {
        super(new ProjectGroupChildren(projectGroup, reader, serverInfo, queue));
        setName(Integer.toString(projectGroup.getId()));
        setDisplayName(projectGroup.getName());
        setIconBaseWithExtension("org/codehaus/mevenide/continuum/threeBrands.gif");
    // this.projectGroup = projectGroup;
    // this.serverInfo= serverInfo;
    // this.reader = reader;
    // this.queue = queue;
    }

    public Action[] getActions(boolean b) {
        Action[] retValue = {new RefreshAction()};
        return retValue;
    }

    @SuppressWarnings("unchecked")
    private static class ProjectGroupChildren extends Children.Keys {

        private ServerInfo serverInfo;
        private ProjectGroupSummary projectGroup;
        private ContinuumXmlRpcClient reader;
        private RequestProcessor queue;

        public ProjectGroupChildren(ProjectGroupSummary projectGroup,
                ContinuumXmlRpcClient reader, ServerInfo serverInfo,
                RequestProcessor queue) {
            this.projectGroup = projectGroup;
            this.serverInfo = serverInfo;
            this.reader = reader;
            this.queue = queue;
        }

        protected Node[] createNodes(Object object) {
            if (object instanceof Project) {
                Project project = (Project) object;
                return new Node[]{new ProjectNode(project, reader,
                    serverInfo, queue)
                };
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
                setKeys(Collections.singleton("Fetching projects ..."));
                queue.post(new Runnable() {

                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            Collection<Project> projects = new ArrayList<Project>();
                            for (ProjectSummary summary : reader.getProjects(projectGroup.getId())) {
                                projects.add(reader.getProjectWithAllDetails(summary.getId()));
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
                    ((ProjectGroupChildren) getChildren()).reader,
                    ((ProjectGroupChildren) getChildren()).serverInfo,
                    ((ProjectGroupChildren) getChildren()).queue));
        }
    }
}
