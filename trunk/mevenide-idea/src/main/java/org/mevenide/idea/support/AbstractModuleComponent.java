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
package org.mevenide.idea.support;

import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;

/**
 * @author Arik
 */
public abstract class AbstractModuleComponent extends AbstractIdeaComponent implements ModuleComponent {
    /**
     * The module this component belongs to.
     */
    protected final Module module;

    /**
     * Creates an instance for the given module.
     *
     * @param pModule the module this component belongs to.
     */
    protected AbstractModuleComponent(final Module pModule) {
        module = pModule;
    }

    /**
     * Returns the component module.
     *
     * @return idea module
     */
    public Module getModule() {
        return module;
    }

    /**
     * Returns the JDK associated with this module. If the module uses the project-designated JDK, that JDK is
     * returned. If the JDK is invalid, <code>null</code> is returned.
     *
     * @return the JDK, or <code>null</code> if not defined/invalid
     */
    public ProjectJdk getJdk() {
        final ProjectJdk jdk = ModuleRootManager.getInstance(module).getJdk();
        if (jdk == null)
            return ProjectRootManager.getInstance(module.getProject()).getProjectJdk();
        else
            return jdk;
    }

    public void moduleAdded() {
    }

    public void projectClosed() {
    }

    public void projectOpened() {
    }
}
