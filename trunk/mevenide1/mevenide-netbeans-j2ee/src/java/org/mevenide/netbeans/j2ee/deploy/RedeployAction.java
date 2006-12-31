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

package org.mevenide.netbeans.j2ee.deploy;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.mevenide.netbeans.api.project.MavenProject;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class RedeployAction extends AbstractAction {
    
    private MavenProject project;
    private DeployPanel panel;
    public RedeployAction(MavenProject proj) {
        putValue(NAME, "Redeploy using Cargo");
        project = proj;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        final File war = project.getWar();
        if (war != null) {
            if (!war.exists()) {
                //TODO rebuild??
                NotifyDescriptor desc = new NotifyDescriptor.Message("War file not built. Please build first.");
                DialogDisplayer.getDefault().notify(desc);
            } else {
                CargoServerRegistry reg = CargoServerRegistry.getInstance();
                Deployable[] depls = reg.findDeployables(war.getAbsolutePath());
                if (depls != null && depls.length > 0) {
                    for (int i = 0; i < depls.length; i++) {
                        Container cont = reg.findContainerForDeployable(depls[i]);
                        DeployerRunner runner = new DeployerRunner(cont, (WAR)depls[i]);
                        RequestProcessor.getDefault().post(runner);
                    }
                }
                
            }
        }
    }
    
    
}
