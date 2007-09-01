/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.api;

import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.project.Project;

/**
 *
 * @author mkleint
 */
public class PluginPropertyUtils {

    
    /** Creates a new instance of PluginPropertyUtils */
    private PluginPropertyUtils() {
    }
    
    
    /**
     * tried to figure out if the property of the given plugin is customized in the
     * current project and returns it's value if so, otherwise null
     */
    public static String getPluginProperty(Project prj, String groupId, String artifactId, String property, String goal) {
        NbMavenProject project = prj.getLookup().lookup(NbMavenProject.class);
        assert project != null : "Requires a maven project instance"; //NOI18N
        return getPluginProperty(project.getOriginalMavenProject(), groupId, artifactId, property, goal);
    }
    /**
     * tried to figure out if the property of the given plugin is customized in the
     * current project and returns it's value if so, otherwise null
     */
    public static String getPluginProperty(MavenProject prj, String groupId, String artifactId, String property, String goal) {
        String toRet = null;
        if (prj.getBuildPlugins() == null) {
            return toRet;
        }
        for (Object obj : prj.getBuildPlugins()) {
            Plugin plug = (Plugin)obj;
            if (artifactId.equals(plug.getArtifactId()) &&
                   groupId.equals(plug.getGroupId())) {
                if (plug.getExecutions() != null) {
                    for (Object obj2 : plug.getExecutions()) {
                        PluginExecution exe = (PluginExecution)obj2;
                        if (exe.getGoals().contains(goal)) {
                            toRet = checkConfiguration(exe.getConfiguration(), property);
                            if (toRet != null) {
                                break;
                            }
                        }
                    }
                }
                if (toRet == null) {
                    toRet = checkConfiguration(plug.getConfiguration(), property);
                }
            }
        }
        if (toRet == null && 
                //TODO - the plugin configuration probably applies to 
                //lifecycle plugins only. always checking is wrong, how to get a list of lifecycle plugins though?
                (Constants.PLUGIN_COMPILER.equals(artifactId) || //NOI18N
                 Constants.PLUGIN_SUREFIRE.equals(artifactId) || //NOI18N
                 Constants.PLUGIN_RESOURCES.equals(artifactId))) {  //NOI18N
            if (prj.getPluginManagement() != null) {
                for (Object obj : prj.getPluginManagement().getPlugins()) {
                    Plugin plug = (Plugin)obj;
                    if (artifactId.equals(plug.getArtifactId()) &&
                        groupId.equals(plug.getGroupId())) {
                        toRet = checkConfiguration(plug.getConfiguration(), property);
                        break;
                    }
                }
            }
        }
        return toRet;
    }
    
    private static String checkConfiguration(Object conf, String property) {
        if (conf != null && conf instanceof Xpp3Dom) {
            Xpp3Dom dom = (Xpp3Dom)conf;
            Xpp3Dom source = dom.getChild(property);
            if (source != null) {
                return source.getValue().trim();
            }
        }
        return null;
    }
    
    
    /**
     * 
     * @param mdl 
     * @param groupId 
     * @param artifactId 
     * @param add true == add to model, always returns a non-null value then.
     * @return 
     */
    public static Dependency checkModelDependency(Model mdl, String groupId, String artifactId, boolean add) {
        List deps = mdl.getDependencies();
        Dependency ret = null;
        Dependency managed = null;
        if (deps != null) {
            Iterator it = deps.iterator();
            while (it.hasNext()) {
                Dependency d = (Dependency)it.next();
                if (groupId.equalsIgnoreCase(d.getGroupId()) && artifactId.equalsIgnoreCase(d.getArtifactId())) {
                    ret = d;
                    break;
                }
            }
        }
        if (ret == null || ret.getVersion() == null) {
            //check dependency management section as well..
            DependencyManagement mng = mdl.getDependencyManagement();
            if (mng != null) {
                deps = mng.getDependencies();
                if (deps != null) {
                    Iterator it = deps.iterator();
                    while (it.hasNext()) {
                        Dependency d = (Dependency)it.next();
                        if (groupId.equalsIgnoreCase(d.getGroupId()) && artifactId.equalsIgnoreCase(d.getArtifactId())) {
                            managed = d;
                            break;
                        }
                    }
                }
            }
        }
        if (add && ret == null) {
            ret = new Dependency();
            ret.setGroupId(groupId);
            ret.setArtifactId(artifactId);
            mdl.addDependency(ret);
        }
        // if managed dependency section is present, return that one for editing..
        return managed == null ? ret : managed;
    }
    
    public static boolean hasModelDependency(Model mdl, String groupid, String artifactid) {
        return checkModelDependency(mdl, groupid, artifactid, false) != null;
    }

    /**
     * 
     * @param mdl 
     * @param url of the repository 
     * @param add true == add to model, will not add if the repo is in project but not in model (eg. central repo)
     * @return 
     */
    public static Repository checkModelRepository(MavenProject project, Model mdl, String url, boolean add) {
        if (url.contains("http://repo1.maven.org/maven2")) { //NOI18N
            return null;
        }
        for (Object rr : mdl.getRepositories()) {
            Repository r = (Repository)rr;
            if (url.equals(r.getUrl())) {
                //already in model..either in pom.xml or added in this session.
                return null;
            }
        }
        List reps = project.getRepositories();
        Repository prjret = null;
        Repository ret = null;
        if (reps != null) {
            Iterator it = reps.iterator();
            while (it.hasNext()) {
                Repository re = (Repository)it.next();
                if (url.equals(re.getUrl())) {
                    prjret = re;
                    break;
                }
            }
        }
        //now find the correct instance in model
        if (prjret != null) {
            reps = mdl.getRepositories();
            if (reps != null) {
                Iterator it = reps.iterator();
                while (it.hasNext()) {
                    Repository re = (Repository)it.next();
                    if (re.getId().equals(prjret.getId())) {
                        ret = re;
                        break;
                    }
                }
            }
        }
        if (add && ret == null && prjret == null) {
            ret = new Repository();
            ret.setUrl(url);
            ret.setId(url);
            mdl.addRepository(ret);
        }
        return ret;
    }

    public static boolean hasModelRepository(MavenProject project, Model mdl, String url) {
        return checkModelRepository(project, mdl, url, false) != null;
    }
    
//    /**
//     * 
//     * @param mdl 
//     * @param groupId 
//     * @param artifactId 
//     * @param profileId 
//     * @param add true == add to model, always returns a non-null value then.
//     * @return 
//     */
//    public static Plugin checkModelPlugin(Model mdl, String groupId, String artifactId, String profileId, boolean add) {
//        Plugin ret = null;
//        Profile prof = null;
//        if (profileId != null) {
//            List lst = mdl.getProfiles();
//            if (lst != null) {
//                Iterator it = lst.iterator();
//                while (it.hasNext()) {
//                    Profile p = (Profile)it.next();
//                    if (profileId.equalsIgnoreCase(p.getId())) {
//                        prof = p;
//                        break;
//                    }
//                }
//            }
//            //TODO
//        }
//        
//        //TODO
//        
//        if (add && ret == null) {
//            ret = new Plugin();
//            ret.setGroupId(groupId);
//            ret.setArtifactId(artifactId);
//            if (profileId == null) {
//                Build bld = mdl.getBuild();
//                if (bld == null) {
//                    bld = new Build();
//                    mdl.setBuild(bld);
//                }
//                bld.addPlugin(ret);
//            } else {
//                if (prof == null) {
//                    prof = new Profile();
//                    prof.setId(profileId);
//                    mdl.addProfile(prof);
//                }
//                BuildBase bld = prof.getBuild();
//                if (bld == null) {
//                    bld = new BuildBase();
//                    prof.setBuild(bld);
//                }
//                bld.addPlugin(ret);
//            }
//        }
//        return ret;
//    }
    
}
