/*
 * PluginPropertyUtils.java
 *
 * Created on December 22, 2005, 4:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans;

import java.util.Iterator;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 *
 * @author mkleint
 */
public class PluginPropertyUtils {
    
    /** Creates a new instance of PluginPropertyUtils */
    public PluginPropertyUtils() {
    }
    
    
    /**
     * tried to figure out if the property of the given plugin is customized in the
     * current project and returns it's value if so, otherwise null
     */
    public static String getPluginProperty(NbMavenProject project, String groupId, String artifactId, String property, String goal) {
        String toRet = null;
        if (project.getOriginalMavenProject().getBuildPlugins() == null) {
            return toRet;
        }
        Iterator it = project.getOriginalMavenProject().getBuildPlugins().iterator();
        while (it.hasNext()) {
            Plugin plug = (Plugin)it.next();
            if (artifactId.equals(plug.getArtifactId()) &&
                   groupId.equals(plug.getGroupId())) {
                if (plug.getExecutions() != null) {
                    Iterator it2 = plug.getExecutions().iterator();
                    while (it2.hasNext()) {
                        PluginExecution exe = (PluginExecution)it2.next();
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
