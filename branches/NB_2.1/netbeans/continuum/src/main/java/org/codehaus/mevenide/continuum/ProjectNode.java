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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.continuum.model.project.BuildResult;
import org.apache.maven.continuum.model.project.Project;
import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.mevenide.continuum.rpc.ProjectsReader;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author mkleint
 */
public class ProjectNode extends AbstractNode {
    
    private static final String DEPENDENCIES = "Dependencies";
    private static final String BUILD_RESULTS = "BuildResults";
    private static final String BUILD_DEFS = "BuildDefs";
    static final String PROPERTY_COMPLETE_RELOAD = "COMPLETE_RELOAD";
    
    static RequestProcessor QUEUE = new RequestProcessor("Continuum refresh", 1);
    
    private Project project;
    
    private ProjectsReader reader;
    private RequestProcessor.Task refreshTask;
    
    /** Creates a new instance of ProjectNode */
    public ProjectNode(Project proj, ProjectsReader read) {
        super(new ProjectChildren(proj), Lookups.singleton(proj));
        project = proj;
        reader = read;
        setName(project.getName());
        setDisplayName(project.getArtifactId());
        String executor = project.getExecutorId();
        if ("maven2".equals(executor)) {
            setIconBaseWithExtension("org/codehaus/mevenide/continuum/Maven2Icon.gif");
        } else if ("maven1".equals(executor)) {
            setIconBaseWithExtension("org/codehaus/mevenide/continuum/MavenIcon.gif");
        } else if ("ant".equals(executor)) {
            setIconBaseWithExtension("org/codehaus/mevenide/continuum/AntIcon.gif");
        }
        refreshTask = QUEUE.create(new RepeatingRefresher());
    }
    
    public Image getIcon(int param) {
        Image img = super.getIcon(param);
        int state = project.getState();
        if (state == 2) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-ok.png"), 16, 8);
        }
        // fail or error
        if (state == 3 || state == 4) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-error.png"), 16, 8);
        }
        // many running states
        if (state == 5 || state == 6 || state == 7 || state == 8) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-running.png"), 16, 8);
        }
        return img;
    }
    
    
    public Image getOpenedIcon(int param) {
        Image img = super.getOpenedIcon(param);
        int state = project.getState();
        if (state == 2) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-ok.png"), 16, 8);
        }
        // fail or error
        if (state == 3 || state == 4) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-error.png"), 16, 8);
        }
        // many running states
        if (state == 6 || state == 7 || state == 8) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-running.png"), 16, 8);
        }
        return img;
    }
    
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set basicProps = sheet.get(Sheet.PROPERTIES);
        try {
            PropertySupport.Reflection artifactId = new PropertySupport.Reflection(project, String.class, "getArtifactId", null);
            artifactId.setName("artifactId");
            artifactId.setDisplayName("Artifact Id");
            artifactId.setShortDescription("");
            PropertySupport.Reflection groupId = new PropertySupport.Reflection(project, String.class, "getGroupId", null);
            groupId.setName("groupId");
            groupId.setDisplayName("Group Id");
            groupId.setShortDescription("");
            PropertySupport.Reflection version = new PropertySupport.Reflection(project, String.class, "getVersion", null);
            version.setName("version");
            version.setDisplayName("Version");
            version.setShortDescription("Version of the current artifact");
            PropertySupport.Reflection name = new PropertySupport.Reflection(project, String.class, "getName", null);
            name.setName("name");
            name.setDisplayName("Name");
            PropertySupport.Reflection desc = new PropertySupport.Reflection(project, String.class, "getDescription", null);
            desc.setName("description");
            desc.setDisplayName("Description");
            
            basicProps.put(new Node.Property[] {
                artifactId, groupId, version, name, desc
            });
        } catch (NoSuchMethodException exc) {
            exc.printStackTrace();
        }
        return sheet;
    }
    
    public Action[] getActions(boolean context) {
        Action[] retValue = new Action[3];
        retValue[0] = new RefreshAction();
        retValue[1] = new ForceBuildAction();
        retValue[2] = new ShowLastOutputAction();
        return retValue;
    }
    
    
    private static class ProjectChildren extends Children.Keys {
        private Project project;
        
        public ProjectChildren(Project proj) {
            project = proj;
        }
        protected Node[] createNodes(Object object) {
            if (DEPENDENCIES.equals(object)) {
                
            }
            if (BUILD_DEFS.equals(object)) {
                
            }
            if (BUILD_RESULTS.equals(object)) {
                
            }
            if (object instanceof BuildResult) {
                return new Node[] { new ResultNode((BuildResult)object) };
            }
            return new Node[0];
        }
        
        void doRefresh() {
            List lst = new ArrayList();
            if (project.getBuildDefinitions() != null) {
                lst.add(BUILD_DEFS);
                lst.addAll(project.getBuildDefinitions());
            }
            if (project.getBuildResults() != null) {
                lst.add(BUILD_RESULTS);
                lst.addAll(project.getBuildResults());
            }
            if (project.getDependencies() != null) {
                lst.add(DEPENDENCIES);
            }
            setKeys(lst);
        }
        protected void removeNotify() {
            super.removeNotify();
            setKeys(Collections.EMPTY_LIST);
        }
        
        protected void addNotify() {
            super.addNotify();
            doRefresh();
        }
    }
    
    private class RefreshAction extends AbstractAction {
        public RefreshAction() {
            this.putValue(Action.NAME, "Refresh");
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                reader.updateProject(project);
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            fireIconChange();
            fireOpenedIconChange();
            ProjectNode.this.firePropertyChange(PROPERTY_COMPLETE_RELOAD, null, Boolean.TRUE);
            ((ProjectChildren)getChildren()).doRefresh();
        }
        
    }
    
    private class ForceBuildAction extends AbstractAction {
        
        public ForceBuildAction() {
            this.putValue(Action.NAME, "Force Build");
        }
        
        public void actionPerformed(ActionEvent e) {
            try {
                reader.buildProject(project);
                refreshTask.schedule(1000);
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class ShowLastOutputAction extends AbstractAction {
        public ShowLastOutputAction() {
            this.putValue(Action.NAME, "Show Last Build Output");
        }
        
        public void actionPerformed(ActionEvent e) {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    int projectId = project.getId();
                    int lastBuildId = project.getLatestBuildId();
                    
                    String pathRoot = ContinuumSettings.getDefault().getOutputForServer(reader.getURL().toString());
                    try {
                        BufferedReader read;
                        if (pathRoot != null) {
                            if (!pathRoot.endsWith("/")) {
                                pathRoot = pathRoot + "/";
                            }
                            String path = pathRoot + projectId + "/" + lastBuildId + ".log.txt";
                            HttpClient client = new HttpClient();
                            HttpMethod method = new GetMethod(path);
                            int ret = client.executeMethod(method);
                            if (ret == HttpStatus.SC_OK) {
                                read = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                            } else if (ret == org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND) {
                                read = new BufferedReader(new StringReader("The output page for build " + lastBuildId + " was not found.\n" +
                                        "Tried accessing the build output under " + path + "\n" +
                                        "Please make sure that the Continuum server is setup correctly and allows the build outputs to be accessible through the web interface.\n" +
                                        "It's configurable through the web interface, under Configuration submenu.\n\n" +
                                        "Please also check that the settings in Netbeans Options match those set on the server."));
                            } else {
                                read = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                            }
                        } else {
                            read = new BufferedReader(new StringReader("You don't defined the URL for locating the build outputs" +
                                    " for server '" + reader.getURL() + "'. Please go to Options dialog and under Miscellaneous update " +
                                    "your server settings."));
                        }
                        InputOutput io = IOProvider.getDefault().getIO("Continuum-" + project.getName(), true);
                        io.select();
                        OutputWriter out = io.getOut();
                        String line = read.readLine();
                        while (line != null) {
                            out.println(line);
                            line = read.readLine();
                        }
                        out.close();
                        read.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            
        }
    }
    
    private class RepeatingRefresher implements Runnable {
        public void run() {
            try {
                reader.updateProject(project);
            } catch (XmlRpcException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            fireIconChange();
            fireOpenedIconChange();
            ProjectNode.this.firePropertyChange(PROPERTY_COMPLETE_RELOAD, null, Boolean.TRUE);
            ((ProjectChildren)getChildren()).doRefresh();
            if (project.getState() == 6 || project.getState() == 7 || project.getState() == 8) {
                refreshTask.schedule(10 * 1000);
                
            }
        }
        
    }
}
