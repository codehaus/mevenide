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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.WeakHashMap;
import javax.swing.Action;
import org.codehaus.cargo.container.deployable.Deployable;
import org.mevenide.netbeans.api.project.AdditionalActionsProvider;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.mevenide.netbeans.j2ee.deploy.DeployAction;
import org.mevenide.netbeans.j2ee.deploy.NbDeployAction;
import org.mevenide.netbeans.j2ee.deploy.RedeployAction;
import org.mevenide.netbeans.api.project.MavenProject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;


/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class J2eeActions implements AdditionalActionsProvider {
    
    private WeakHashMap cache = new WeakHashMap();
    /** Creates a new instance of J2EEActions */
    public J2eeActions() {
    }
    
    public Action[] createPopupActions(MavenProject project) {
        J2eeModuleProvider provider = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
        J2eeModule module = provider.getJ2eeModule();
            if (module == null || module.getModuleType() == null /*|| module.getArchive() == null*/) {
                return new Action[0];
            }
        Collection toRet = new ArrayList();
        toRet.add(new NbDeployAction(project));
        try {
        if (module.getArchive() != null) {
            Action deploy = (Action)cache.get(project);
            if (deploy == null) {
                deploy = new DeployAction(project);
                cache.put(project, deploy);
            }
            toRet.add(deploy);
        }
        } catch (IOException exc) {
            return new Action[0];
        }
        File war = project.getWar();
        if (war != null && war.exists()) {
            Deployable[] depls = CargoServerRegistry.getInstance().findDeployables(war.toString());
            if (depls.length > 0) {
                toRet.add(new RedeployAction(project));
            }
        }
        return (Action[])toRet.toArray(new Action[toRet.size()]);
    }
    
    
}
