/*
 *  Copyright 2008 Mevenide Team.
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

package org.codehaus.mevenide.hints;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    /**
     * screw friend dependency.
     */
    @Override
    public void validate() throws IllegalStateException {
        try {
            java.lang.Class main = java.lang.Class.forName("org.netbeans.core.startup.Main", false, //NOI18N
                    Thread.currentThread().getContextClassLoader());
            Method meth = main.getMethod("getModuleSystem", new Class[0]); //NOI18N

            Object moduleSystem = meth.invoke(null, new Object[0]);
            meth = moduleSystem.getClass().getMethod("getManager", new Class[0]); //NOI18N

            Object mm = meth.invoke(moduleSystem, new Object[0]);
            Method moduleMeth = mm.getClass().getMethod("get", new Class[]{String.class}); //NOI18N

            Object moduleInstance = moduleMeth.invoke(mm, "org.netbeans.modules.java.hints"); //NOI18N

            if (moduleInstance != null) {
                Field frField = moduleInstance.getClass().getSuperclass().getDeclaredField("friendNames"); //NOI18N

                frField.setAccessible(true);
                Set friends = (Set) frField.get(moduleInstance);
                if (friends == null) {
                    friends = new HashSet();
                    frField.set(moduleInstance, friends);
                }
                friends.add("org.codehaus.mevenide.hints"); //NOI18N

            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            new IllegalStateException("Cannot fix dependencies for org.codehaus.mevenide.hints. " + //NOI18N
                    "Please log a report at http://jira.codehaus.org/browse/MEVENIDE"); //NOI18N

        }
    }

    @Override
    public void restored() {
        super.restored();
    }
}
