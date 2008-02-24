/*
 * Copyright 2006 Mevenide Team
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.options;

import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;

/**
 * Preferences class for externalizing the hardwired goal executions to
 * allow minor (mostly version) changes by advanced users?)
 * @author mkleint
 */
public class MavenCommandSettings {
    private static final MavenCommandSettings INSTANCE = new MavenCommandSettings();
    
    public static final String COMMAND_CREATE_ARCHETYPE = "createArchetype"; //NOI18N
    public static final String COMMAND_CREATE_ARCHETYPENG = "createArchetypeNG"; //NOI18N
    public static final String COMMAND_INSTALL_FILE = "installFile"; //NOI18N
    public static final String COMMAND_SCM_CHECKOUT = "scmCheckout"; //NOI18N
    
    public static MavenCommandSettings getDefault() {
        return INSTANCE;
    }
    
    protected final Preferences getPreferences() {
        return NbPreferences.root().node("org/codehaus/mevenide/commands"); //NOI18N
    }
    
    protected final String putProperty(String key, String value) {
        String retval = getProperty(key);
        if (value != null) {
            getPreferences().put(key, value);
        } else {
            getPreferences().remove(key);
        }
        return retval;
    }

    protected final String getProperty(String key) {
        return getPreferences().get(key, null);
    }    
    
    private MavenCommandSettings() {
    }
    
    public String getCommand(String command) {
        String toRet = getProperty(command);
        if (toRet == null) {
            //TODO is there some other way to do this?
            if (COMMAND_CREATE_ARCHETYPE.equals(command)) {
                toRet = "org.apache.maven.plugins:maven-archetype-plugin:1.0-alpha-4:create";//NOI18N
            }
            else if (COMMAND_INSTALL_FILE.equals(command)) {
                toRet = "install:install-file";//NOI18N
            }
            else if (COMMAND_CREATE_ARCHETYPENG.equals(command)) {
                toRet = "org.apache.maven.plugins:maven-archetype-plugin:2.0-alpha-2:create";//NOI18N
            }
            else if (COMMAND_SCM_CHECKOUT.equals(command)) {
                toRet = "org.apache.maven.plugins:maven-scm-plugin:1.0:checkout";//NOI18N
            }
        }
        assert toRet != null : "Command " + command + " needs implementation."; //NOI18N
        return toRet;
    }
    
}
