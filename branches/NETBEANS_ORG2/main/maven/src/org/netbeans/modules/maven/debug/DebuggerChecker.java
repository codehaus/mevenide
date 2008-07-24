/* ==========================================================================
 * Copyright 2008 Mevenide Team
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
package org.netbeans.modules.maven.debug;

import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.execute.LateBoundPrerequisitesChecker;
import org.netbeans.modules.maven.api.execute.RunConfig;

/**
 *
 * @author mkleint
 */
public class DebuggerChecker implements LateBoundPrerequisitesChecker {
    private Logger LOGGER = Logger.getLogger(DebuggerChecker.class.getName());

    public boolean checkRunConfig(RunConfig config) {
        if (config.getProject() == null) {
            //cannot act on execution without a project instance..
            return true;
        }
        if ("true".equals(config.getProperties().getProperty("jpda.listen"))) {//NOI18N
            try {
                //NOI18N
                JPDAStart start = new JPDAStart();
                NbMavenProject prj = config.getProject().getLookup().lookup(NbMavenProject.class);
                start.setName(prj.getMavenProject().getArtifactId());
                start.setStopClassName(config.getProperties().getProperty("jpda.stopclass")); //NOI18N
                start.setLog(new MavenEmbedderConsoleLogger());
                String val = start.execute(config.getProject());
                Enumeration en = config.getProperties().propertyNames();
                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    String value = config.getProperties().getProperty(key);
                    StringBuffer buf = new StringBuffer(value);
                    String replaceItem = "${jpda.address}"; //NOI18N
                    int index = buf.indexOf(replaceItem);
                    while (index > -1) {
                        String newItem = val;
                        newItem = newItem == null ? "" : newItem; //NOI18N
                        buf.replace(index, index + replaceItem.length(), newItem);
                        index = buf.indexOf(replaceItem);
                    }
                    //                System.out.println("setting property=" + key + "=" + buf.toString());
                    config.setProperty(key, buf.toString());
                }
                config.setProperty("jpda.address", val); //NOI18N
            } catch (MojoExecutionException ex) {
                LOGGER.log(Level.FINE, ex.getMessage(), ex);
                return false;
            } catch (MojoFailureException ex) {
                LOGGER.log(Level.FINE, ex.getMessage(), ex);
                return false;
            }
        }
        return true;
    }
}
