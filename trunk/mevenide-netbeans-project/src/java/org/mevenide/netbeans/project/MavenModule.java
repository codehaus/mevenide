/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.netbeans.project;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.util.FileUtils;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.netbeans.project.output.CompileAnnotation;
import org.mevenide.netbeans.project.output.PmdAnnotation;
import org.mevenide.plugins.PluginInfoFactory;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInstall;


/** Manages a module's lifecycle.
 * Remember that an installer is optional and often not needed at all.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenModule extends ModuleInstall {
    private static Log log = LogFactory.getLog(MavenModule.class);
    
    private static final long serialVersionUID = -485754848837354747L;
    
    public static final String CURRENT_VERSION = "maven-mevenide-plugin-0.2.jar"; //NOI18N
    
    public void restored() {
        // kind of duplicates the same call in mevenide-netbeans-grammar but these
        // can be used independently.
        SysEnvLocationFinder.setDefaultSysEnvProvider(new NbSysEnvProvider());
        PluginInfoFactory.getInstance().setCustomLoader(new NbCustomPluginLoaderImpl());
    }
    
   public void uninstalled () {
        PmdAnnotation.detachAllAnnotations();
        CompileAnnotation.detachAllAnnotations();
    }
   
    public void validate() throws java.lang.IllegalStateException {
        String maven_home = System.getProperty("Env-MAVEN_HOME");//NOI18N
        if (maven_home == null) {
            throw new IllegalStateException("Maven not installed or the MAVEN_HOME property not set.");
        }
        copyMevenidePlugin(new File(maven_home));
    }
    
    private void copyMevenidePlugin(File home) {
        if (home.exists()) {
            File plugins = new File(home, "plugins"); //NOI18N
            File[] files = plugins.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.startsWith("maven-mevenide-plugin")) {
                        return true;
                    }
                    return false;
                }
            });
            File current = InstalledFileLocator.getDefault().locate("maven-plugins/" + CURRENT_VERSION, null, false);
            // TODO refine here, to not overwrite everytime but only when changed.
            ILocationFinder finder = new LocationFinderAggregator(DefaultQueryContext.getNonProjectContextInstance());
            File cache = new File(finder.getMavenPluginsDir());
            boolean deleteCache = false;
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    String name = files[i].getName();
                    if (!name.equals(CURRENT_VERSION)) {
                        deleteCache = true;
                        name = name.substring(0, name.length() - ".jar".length());
                        File cached = new File(cache, name);
                        if (cached.exists()) {
                            FileUtilities.delete(cached);
                        }
                        files[i].delete();
                    }
                }
            }
            File newFile = new File(plugins, CURRENT_VERSION);
            try {
                FileUtils.newFileUtils().copyFile(current, newFile, null, true);
                if (deleteCache) {
                    File cFile = new File(cache, "plugins.cache");
                    if (cFile.exists()) {
                        FileUtilities.delete(cFile);
                    }
                }
            } catch (IOException exc) {
                log.error("cannot copy maven-mevenide-plugin", exc);
            }
            
        }
    }
}
