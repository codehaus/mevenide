/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.openide.modules.ModuleInstall;

/**
 * hack!! remove when http://www.netbeans.org/issues/show_bug.cgi?id=89019
 * is fixed.
 * @author mkleint
 */
public class ModInstall extends ModuleInstall {
    
    /** Creates a new instance of ModInstall */
    public ModInstall() {
    }
    
    /**
     * screw friend dependency.
     */ 
    public void validate() throws IllegalStateException {
        try {
            java.lang.Class main = java.lang.Class.forName("org.netbeans.core.startup.Main", false, 
                    Thread.currentThread().getContextClassLoader());
            Method meth = main.getMethod("getModuleSystem", new Class[0]);
            Object moduleSystem = meth.invoke(null, new Object[0]);
            meth = moduleSystem.getClass().getMethod("getManager", new Class[0]);
            Object mm = meth.invoke(moduleSystem, new Object[0]);
            Method moduleMeth = mm.getClass().getMethod("get", new Class[] {String.class});
            Object persistence = moduleMeth.invoke(mm, "org.netbeans.modules.j2ee.persistenceapi");
            if (persistence != null) {
                Field frField = persistence.getClass().getSuperclass().getDeclaredField("friendNames");
                frField.setAccessible(true);
                Set friends = (Set)frField.get(persistence);
                friends.add("org.codehaus.mevenide.netbeans.persistence");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new IllegalStateException("Cannot fix dependencies for org.codehaus.mevenide.netbeans.persistence. " +
                    "Please log a report at http://jira.codehaus.org/browse/MEVENIDE");
        }
    }
    
    
    
}
