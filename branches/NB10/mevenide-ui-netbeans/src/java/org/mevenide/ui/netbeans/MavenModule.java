/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.ui.netbeans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.environment.SysEnvLocationFinder;
import org.openide.modules.ModuleInfo;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;

/** Manages a module's lifecycle.
 * Remember that an installer is optional and often not needed at all.
 *
 * @author Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenModule extends ModuleInstall
{
    private static Log log = LogFactory.getLog(MavenModule.class);
 
    private static final long serialVersionUID = -485754848837354747L;
    
    public void restored()
    {
        // kind of duplicates the same call in mevenide-netbeans-grammar but these
        // can be used independently.
        SysEnvLocationFinder.setDefaultSysEnvProvider(new NbSysEnvProvider());
    }
    
    public void validate() throws java.lang.IllegalStateException
    {
        String maven_home = System.getProperty("Env-MAVEN_HOME");//NOI18N
        if (maven_home == null)
        {
            throw new IllegalStateException("Maven not installed or the MAVEN_HOME property not set.");
        }
        
//        //DEBUG
//        Lookup.Template template = new Lookup.Template(ModuleInfo.class, "Module[org.mevenide.ui.netbeans", null); //NOI18N
//        Lookup.Item item = Lookup.getDefault().lookupItem(template);
//        if (item != null)
//        {
//            ModuleInfo info = (ModuleInfo)item.getInstance();
//            log.debug("classpath = " + info.getAttribute("Class-Path"));
//        } else {
//            log.debug("module not found :(");
//        }
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
    
    // Generally the methods below should be avoided in favor of restored():
    /*
    // By default, do nothing but call restored().
    public void installed() {
        restored();
    }
     
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
