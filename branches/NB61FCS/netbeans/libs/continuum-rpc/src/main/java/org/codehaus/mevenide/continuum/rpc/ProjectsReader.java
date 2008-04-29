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
package org.codehaus.mevenide.continuum.rpc;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.maven.continuum.model.project.BuildDefinition;
import org.apache.maven.continuum.model.project.BuildResult;
import org.apache.maven.continuum.model.project.Profile;
import org.apache.maven.continuum.model.project.Project;
import org.apache.maven.continuum.model.project.ProjectDependency;
import org.apache.maven.continuum.model.project.ProjectDeveloper;
import org.apache.maven.continuum.model.project.Schedule;
import org.apache.maven.continuum.model.scm.ScmResult;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author mkleint
 */
public class ProjectsReader {

    private URL server;
   
    /** Creates a new instance of ProjectsReader */
    public ProjectsReader(URL serverUrl) {
        server = serverUrl;
    }

    public URL getURL() {
        return server;
    }

    public Project[] readProjects() throws XmlRpcException, IOException {
        XmlRpcClient client = getClient();
        Object resultObj = client.execute("continuum.getProjects", new Vector());
        Collection projectList = new ArrayList();
        
        
        if (resultObj instanceof Map) {
            Map map = (Map) resultObj;
            Object[] projects = (Object[])map.get("projects");
           for (Object projectObj : projects) {
                projectList.add(populateProject((Map)projectObj, new Project()));
            }
            
            /*Vector projects = (Vector) map.get("projects");
            Iterator it = projects.iterator();
            while (it.hasNext()) {
                Hashtable proj = (Hashtable) it.next();
                set.add(populateProject(proj, new Project()));
                System.out.println("table=" + proj);
            }*/
        }
        return (Project[]) projectList.toArray(new Project[projectList.size()]);
    }

    public void updateProject(Project proj) throws XmlRpcException, IOException {
        XmlRpcClient client = getClient();
        Vector vect = new Vector();
        vect.add(new Integer(proj.getId()));
        Object obj = client.execute("continuum.getProject", vect);
        if (obj instanceof Map) {
            Map table = (Map) obj;
            populateProject((Map) table.get("project"), proj);
        }
    }

    public void buildProject(Project proj) throws XmlRpcException, IOException {
        XmlRpcClient client = getClient();
        Vector vect = new Vector();
        vect.add(new Integer(proj.getId()));
        //trigger
        vect.add(new Integer(1));
        Object obj = client.execute("continuum.buildProject", vect);
    }

    private Project populateProject(Map map, Project instance) {
        instance.setArtifactId((String) map.get("artifactId"));
        instance.setGroupId((String) map.get("groupId"));
        instance.setName((String) map.get("name"));
        instance.setDescription((String) map.get("description"));
        instance.setVersion((String) map.get("version"));
        instance.setUrl((String) map.get("url"));
        instance.setExecutorId((String) map.get("executorId"));
        instance.setWorkingDirectory((String) map.get("workingDirectory"));
        instance.setScmUsername((String) map.get("scmUsername"));
        instance.setScmPassword((String) map.get("scmPassword"));
        instance.setScmTag((String) map.get("scmTag"));
        instance.setScmUrl((String) map.get("scmUrl"));
        instance.setId(Integer.parseInt((String) map.get("id")));
        instance.setLatestBuildId(Integer.parseInt((String) map.get("latestBuildId")));
        instance.setState(Integer.parseInt((String) map.get("state")));
        instance.setOldState(Integer.parseInt((String) map.get("oldState")));
        instance.setBuildNumber(Integer.parseInt((String) map.get("buildNumber")));
        List buildDefinitionList = new ArrayList();
        Object[] buildDefinitionObjs = (Object[])map.get("buildDefinitions");
        for (Object buildDefinitionObj : buildDefinitionObjs) {
            Map buildDefinitionMap = ((Map) buildDefinitionObj);
            BuildDefinition def = new BuildDefinition();
            def.setId(Integer.parseInt((String)buildDefinitionMap.get("id")));
            def.setArguments((String)buildDefinitionMap.get("arguments"));
            def.setBuildFile((String)buildDefinitionMap.get("buildFile"));
            def.setDefaultForProject(Boolean.getBoolean((String)buildDefinitionMap.get("defaultForProject")));
            def.setGoals((String)buildDefinitionMap.get("goals"));
            
            Object[] profileObjs = (Object[]) buildDefinitionMap.get("profile");
            if (profileObjs != null) {
                Profile profile = new Profile();
                //TODO
                def.setProfile(profile);
            }
            Map scheduleMap = (Map) map.get("schedule");
            if (scheduleMap != null) {
                Schedule schedule = new Schedule();
                schedule.setActive(Boolean.getBoolean((String) scheduleMap.get("active")));
                schedule.setCronExpression((String) scheduleMap.get("cronExpression"));
                schedule.setDelay(Integer.parseInt((String) scheduleMap.get("delay")));
                schedule.setDescription((String) scheduleMap.get("description"));
                schedule.setId(Integer.parseInt((String) scheduleMap.get("id")));
                schedule.setName((String) scheduleMap.get("name"));
                def.setSchedule(schedule);
            }
            buildDefinitionList.add(def);
        
            instance.setBuildDefinitions(buildDefinitionList);
        }
        Object[] buildResultObjs = (Object[]) map.get("buildResults");
        if (buildResultObjs != null) {
            List resultList = new ArrayList();
            for (Object buildResultObj : buildResultObjs) {
                Map buildResultMap = (Map) buildResultObj;
                BuildResult result = new BuildResult();
                result.setBuildNumber(Integer.parseInt((String) buildResultMap.get("buildNumber")));
                result.setEndTime(Long.parseLong((String) buildResultMap.get("endTime")));
                result.setError((String) buildResultMap.get("error"));
                result.setExitCode(Integer.parseInt((String) buildResultMap.get("exitCode")));
                result.setId(Integer.parseInt((String) buildResultMap.get("id")));
                //TODO                result.setScmResult();
                result.setStartTime(Long.parseLong((String) buildResultMap.get("startTime")));
                result.setState(Integer.parseInt((String) buildResultMap.get("state")));
                result.setSuccess(Boolean.getBoolean((String) buildResultMap.get("success")));
                result.setTrigger(Integer.parseInt((String) buildResultMap.get("trigger")));
                resultList.add(result);
            }
            instance.setBuildResults(resultList);
        }
        Object[] dependencyObjs = (Object[]) map.get("dependencies");
        if (dependencyObjs != null) {
            List dependencyList = new ArrayList();
            for (Object dependencyObj : dependencyObjs) {
                Map dependencyMap = (Map) dependencyObj;
                ProjectDependency dependency = new ProjectDependency();
                dependency.setArtifactId((String) dependencyMap.get("artifactId"));
                dependency.setGroupId((String) dependencyMap.get("groupId"));
                dependency.setVersion((String) dependencyMap.get("version"));
                dependencyList.add(dependency);
            }
            instance.setDependencies(dependencyList);
        }
        Map parentMap = (Map) map.get("parent");
        if (parentMap != null) {
            ProjectDependency parent = new ProjectDependency();
            parent.setArtifactId((String) parentMap.get("artifactId"));
            parent.setGroupId((String) parentMap.get("groupId"));
            parent.setVersion((String) parentMap.get("version"));
            instance.setParent(parent);
        }
        Object[] developerObjs = (Object[]) map.get("developers");
        if (developerObjs != null) {
            List developerList = new ArrayList();
            for (Object developerObj : developerObjs) {
                Map developerMap = (Map) developerObj;
                ProjectDeveloper developer = new ProjectDeveloper();
                developer.setContinuumId(Integer.parseInt((String) developerMap.get("continuumId")));
                developer.setName((String) developerMap.get("name"));
                developer.setEmail((String) developerMap.get("email"));
                developer.setScmId((String) developerMap.get("scmId"));
                developerList.add(developer);
            }
            instance.setDevelopers(developerList);
        }
       
        Map checkout = (Map) map.get("checkoutResult");
        if (checkout != null) {
            ScmResult scmResult = new ScmResult();
            scmResult.setSuccess(Boolean.getBoolean((String) checkout.get("success")));
            scmResult.setCommandLine((String) checkout.get("commandLine"));
            //TODO            res.setChanges();
            scmResult.setCommandOutput((String) checkout.get("commandOutput"));
            scmResult.setException((String) checkout.get("exception"));
            scmResult.setProviderMessage((String) checkout.get("providerMessage"));
            instance.setCheckoutResult(scmResult);

        }
        return instance;
    }

    private XmlRpcClient getClient() {
        //if (client == null) {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(server);
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
        //}
        return client;
    }   
}
