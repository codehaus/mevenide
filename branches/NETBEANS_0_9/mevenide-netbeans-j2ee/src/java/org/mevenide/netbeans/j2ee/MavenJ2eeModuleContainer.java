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

package org.mevenide.netbeans.j2ee;

import java.util.ArrayList;
import java.util.List;
import org.mevenide.netbeans.api.project.MavenProject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModuleContainer;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleListener;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJ2eeModuleContainer extends MavenJ2eeModule implements J2eeModuleContainer {
    private MavenProject project;
    private List listeners;
    /** Creates a new instance of MavenJ2eeModuleContainer */
    public MavenJ2eeModuleContainer(MavenProject proj) {
        super(proj);
        project = proj;
        listeners = new ArrayList();
    }

    public void addModuleListener(ModuleListener ml) {
        listeners.add(ml);
    }

    public J2eeModule[] getModules(ModuleListener ml) {
        return new J2eeModule[0];
    }

    public void removeModuleListener(ModuleListener ml) {
        listeners.remove(ml);
    }
    
}
