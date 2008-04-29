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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.continuum.xmlrpc.client.ContinuumXmlRpcClient;
import org.apache.maven.continuum.xmlrpc.project.BuildDefinition;
import org.apache.maven.continuum.xmlrpc.project.Project;
import org.apache.maven.continuum.xmlrpc.project.ProjectDependency;
import org.apache.maven.continuum.xmlrpc.project.ProjectDeveloper;
import org.codehaus.mevenide.continuum.ContinuumClient;
import org.codehaus.mevenide.continuum.ServerInfo;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

/**
 *
 * @author mkleint
 */
public class ProjectNode extends AbstractNode {

    private Project project;
    private ContinuumClient client;
    private RequestProcessor.Task refreshTask;

    /** Creates a new instance of ProjectNode */
    public ProjectNode(Project proj, ContinuumClient client) {
        super(Children.LEAF);
        this.client = client;
        project = proj;
        setName(project.getName());
        setDisplayName(project.getName());
        String executor = project.getExecutorId();
        if ("maven2".equals(executor)) {
            setIconBaseWithExtension("org/codehaus/mevenide/continuum/Maven2Icon.gif");
        } else if ("maven1".equals(executor)) {
            setIconBaseWithExtension("org/codehaus/mevenide/continuum/MavenIcon.gif");
        } else if ("ant".equals(executor)) {
            setIconBaseWithExtension("org/codehaus/mevenide/continuum/ant-freeform.png");
        }
        refreshTask = client.getQueue().create(new RepeatingRefresher());
    }

    public Image getIcon(int param) {
        Image img = super.getIcon(param);
        int state = project.getState();
        if (state == 2) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-ok.png"),
                    16, 8);
        }
        // fail or error
        if (state == 3 || state == 4) {
            return Utilities.mergeImages(
                    img,
                    Utilities.loadImage("org/codehaus/mevenide/continuum/state-error.png"),
                    16, 8);
        }
        // many running states
        if (state == 5 || state == 6 || state == 7 || state == 8) {
            return Utilities.mergeImages(
                    img,
                    Utilities.loadImage("org/codehaus/mevenide/continuum/state-running.png"),
                    16, 8);
        }
        return img;
    }

    public Image getOpenedIcon(int param) {
        Image img = super.getOpenedIcon(param);
        int state = project.getState();
        if (state == 2) {
            return Utilities.mergeImages(img, Utilities.loadImage("org/codehaus/mevenide/continuum/state-ok.png"),
                    16, 8);
        }
        // fail or error
        if (state == 3 || state == 4) {
            return Utilities.mergeImages(
                    img,
                    Utilities.loadImage("org/codehaus/mevenide/continuum/state-error.png"),
                    16, 8);
        }
        // many running states
        if (state == 6 || state == 7 || state == 8) {
            return Utilities.mergeImages(
                    img,
                    Utilities.loadImage("org/codehaus/mevenide/continuum/state-running.png"),
                    16, 8);
        }
        return img;
    }

    /**
     * @LFO find a more elegant manner to crate the sheet .
     */
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();

        Sheet.Set basicProps = new Sheet.Set();
        basicProps.setName("General");
        basicProps.setDisplayName("General");

        sheet.put(basicProps);
        try {
            // Node.Property buildState = new MyStringPropertySupport("state",
            // "State",
            // ProjectHelper.getInstance().getDisplayableStateFrom(project));
            Node.Property buildNumber = new MyStringPropertySupport(
                    "buildNumber", "Build Number", Integer.toString(project.getBuildNumber()));

            PropertySupport.Reflection artifactId = new PropertySupport.Reflection(
                    project, String.class, "getArtifactId", null);
            artifactId.setName("artifactId");
            artifactId.setDisplayName("Artifact Id");
            artifactId.setShortDescription("");
            PropertySupport.Reflection groupId = new PropertySupport.Reflection(
                    project, String.class, "getGroupId", null);
            groupId.setName("groupId");
            groupId.setDisplayName("Group Id");
            groupId.setShortDescription("");
            PropertySupport.Reflection version = new PropertySupport.Reflection(
                    project, String.class, "getVersion", null);
            version.setName("version");
            version.setDisplayName("Version");
            version.setShortDescription("Version of the current artifact");
            PropertySupport.Reflection name = new PropertySupport.Reflection(
                    project, String.class, "getName", null);
            name.setName("name");
            name.setDisplayName("Name");
            PropertySupport.Reflection scmUrl = new PropertySupport.Reflection(
                    project, String.class, "getScmUrl", null);
            scmUrl.setName("scmURL");
            scmUrl.setDisplayName("Scm url");
            PropertySupport.Reflection desc = new PropertySupport.Reflection(
                    project, String.class, "getDescription", null);
            desc.setName("description");
            desc.setDisplayName("Description");

            // basicProps.put(new Node.Property[] {
            // buildState, buildNumber, artifactId, groupId, version, name,
            // desc, scmUrl
            // });

            Sheet.Set developerSet = new Sheet.Set();
            developerSet.setName("developers");
            developerSet.setDisplayName("Developers");
            for (Object developerObj : project.getDevelopers()) {
                ProjectDeveloper dev = (ProjectDeveloper) developerObj;
                Node.Property developer = new MyStringPropertySupport(Integer.toString(dev.getContinuumId()), dev.getName(), dev.getEmail());
                developerSet.put(developer);
            }
            sheet.put(developerSet);

            Sheet.Set dependencySet = new Sheet.Set();
            dependencySet.setName("dependencies");
            dependencySet.setDisplayName("Dependencies");
            for (Object obj : project.getDependencies()) {
                ProjectDependency dep = (ProjectDependency) obj;
                String depStr = dep.getArtifactId() + ":" + dep.getVersion();
                Node.Property dependency = new MyStringPropertySupport(depStr,
                        dep.getGroupId(), depStr);
                dependencySet.put(dependency);
            }
            sheet.put(dependencySet);

            Sheet.Set buildDefinitionSet = new Sheet.Set();
            buildDefinitionSet.setName("buildDefinition");
            buildDefinitionSet.setDisplayName("Build definitions");
            int i = 0;
            for (Object obj : project.getBuildDefinitions()) {
                BuildDefinition def = (BuildDefinition) obj;
                String defStr = def.getArguments() + ", " + def.getGoals() + ", " + def.getModelEncoding();
                Node.Property definitionSet = new MyStringPropertySupport("def" + (i++), "", defStr);
                buildDefinitionSet.put(definitionSet);
            }
            sheet.put(buildDefinitionSet);
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

    public Object getValue(String attributeName) {
        Object retValue;

        retValue = super.getValue(attributeName);
        return retValue;
    }

    @SuppressWarnings("serial")
    private class RefreshAction extends AbstractAction {

        public RefreshAction() {
            this.putValue(Action.NAME, "Refresh");
        }

        public void actionPerformed(ActionEvent e) {
            try {
                client.getXmlRpcClient().updateProject(project);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            fireIconChange();
            fireOpenedIconChange();
        // ProjectNode.this.firePropertyChange(PROPERTY_COMPLETE_RELOAD,
        // null, Boolean.TRUE);
        }
    }

    @SuppressWarnings("serial")
    private class ForceBuildAction extends AbstractAction {

        public ForceBuildAction() {
            this.putValue(Action.NAME, "Force Build");
        }

        public void actionPerformed(ActionEvent e) {
            try {
                client.getXmlRpcClient().buildProject(project.getId());
                refreshTask.schedule(1000);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * @LFO Make it works on my environment.
     */
    @SuppressWarnings("serial")
    private class ShowLastOutputAction extends AbstractAction {

        public ShowLastOutputAction() {
            this.putValue(Action.NAME, "Show Last Build Output");
        }

        public void actionPerformed(ActionEvent e) {
            RequestProcessor.getDefault().post(new Runnable() {

                public void run() {
                    String pathRoot = client.getServerInfo().getWebUrl().toExternalForm();
                    InputOutput io = IOProvider.getDefault().getIO(
                            "Continuum-" + project.getName(), true);
                    io.select();
                    OutputWriter out = io.getOut();
                    try {
                        BufferedReader read;
                        if (pathRoot != null) {	
                            String loginPath = pathRoot + "/security/login.action?username="+ client.getServerInfo().getUser()+"&password="+client.getServerInfo().getPassword()+"&method:login=login";
                            String path = pathRoot + "/buildOutputText.action?buildId=" + project.getLatestBuildId() + "&projectId=" + project.getId() + "&projectGroupId=" + project.getProjectGroup().getId();
                            HttpClient client = new HttpClient();
                            out.println("Connect to " + loginPath.substring(0, loginPath.indexOf("&password")));
                            HttpMethod method = new GetMethod(loginPath);
                            int ret = client.executeMethod(method);
                            
                            out.println("Connect to " + path);
                            method = new GetMethod(path);
                            client.executeMethod(method);
                            if (ret == HttpStatus.SC_OK) {
                                read = new BufferedReader(
                                        new InputStreamReader(method.getResponseBodyAsStream()));
                            } else if (ret == org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND) {
                                read = new BufferedReader(
                                        new StringReader(
                                        "The output page for build " + project.getLatestBuildId() + " was not found.\n" + "Tried accessing the build output under " + path + "\n"));
                            } else {
                                read = new BufferedReader(
                                        new InputStreamReader(method.getResponseBodyAsStream()));
                            }
                        } else {
                            read = new BufferedReader(
                                    new StringReader(
                                    "You don't defined the URL for locating the build outputs" + " for server '" + client.getServerInfo().getWebUrl().toString() + "'. Please go to Options dialog and under Miscellaneous update " + "your server settings."));
                        }
                        String line = read.readLine();
                        while (line != null) {
                            out.println(line);
                            line = read.readLine();
                        }
                        out.close();
                        read.close();
                    } catch (IOException ex) {
                        out.println(ex.getMessage());
                    }
                }
            });

        }
    }

    private class RepeatingRefresher implements Runnable {

        public void run() {
            try {
                client.getXmlRpcClient().updateProject(project);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            fireIconChange();
            fireOpenedIconChange();
            // ProjectNode.this.firePropertyChange(PROPERTY_COMPLETE_RELOAD,
            // null, Boolean.TRUE);
            createSheet();

            if (project.getState() == 6 || project.getState() == 7 || project.getState() == 8) {
                refreshTask.schedule(10 * 1000);
            }
        }
    }

    private static class MyStringPropertySupport extends PropertySupport.ReadOnly {

        private String value;

        MyStringPropertySupport(String name, String displayName, String value) {
            super(name, String.class, displayName, "");
            this.value = value;
        }

        public Object getValue() throws IllegalAccessException,
                InvocationTargetException {
            return value;
        }
    }
}
