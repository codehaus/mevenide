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
package org.mevenide.netbeans.grammar;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.SysEnvLocationFinder;
import org.openide.modules.ModuleInstall;



/** Manages a module's lifecycle.
 * Remember that an installer is optional and often not needed at all.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenGrammarModule extends ModuleInstall
{
    private static Log log = LogFactory.getLog(MavenGrammarModule.class);
 
    private static final long serialVersionUID = -485754848837354747L;
    
    private static transient ClassLoader mavenClassLoader;
    public void restored()
    {
        // By default, do nothing.
        // Put your startup code here.
        
        SysEnvLocationFinder.setDefaultSysEnvProvider(new NbSysEnvProvider());
    }
    
    public void validate() throws java.lang.IllegalStateException
    {
//        System.out.println("#########################################validating");
        String maven_home = System.getProperty("Env-MAVEN_HOME");//NOI18N
        if (maven_home == null)
        {
            throw new IllegalStateException("Maven not installed or the MAVEN_HOME property not set. Cannot Install.");
        }
        File pluginDir = new File(maven_home, "lib"); //NOI18N
        File[] jars = pluginDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("commons-jelly") || name.equals("maven.jar"); //NOI18N
            }
        });
        try {
            URL[] urls = new URL[jars.length];
            for (int i = 0; i < jars.length; i++) {
                urls[i] = jars[i].toURL();
            }
            mavenClassLoader = new URLClassLoader(urls);
        } catch (MalformedURLException exc)
        {
            System.out.println("error" + exc);
        }
    }    

    public static ClassLoader getMavenClassLoader() {
        return mavenClassLoader;
    }
    
    // Less commonly needed:
    /*
    public boolean closing() {
        // return false if cannot close now
        return true;
    }
    public void close() {
        // shut down stuff
    }
     */
    
/*     
    // By default, do nothing.
    public void uninstalled() {
    }
     
    // By default, call restored().
    public void updated(int release, String specVersion) {
    }
     */
    
    // It is no longer recommended to override Externalizable methods
    // (readExternal and writeExternal). See the Modules API section on
    // "installation-clean" modules for an explanation.
    
}
