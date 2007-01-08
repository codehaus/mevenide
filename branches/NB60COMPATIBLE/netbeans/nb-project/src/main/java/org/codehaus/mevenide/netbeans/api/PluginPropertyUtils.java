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

import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
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
        String toRet = null;
        if (project.getOriginalMavenProject().getBuildPlugins() == null) {
            return toRet;
        }
        for (Object obj : project.getOriginalMavenProject().getBuildPlugins()) {
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
    
}
