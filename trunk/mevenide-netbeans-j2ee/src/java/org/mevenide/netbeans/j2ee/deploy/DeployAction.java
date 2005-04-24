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
import java.net.URL;
import javax.swing.AbstractAction;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.deployable.DeployableFactory;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.DeployableMonitorListener;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.deployer.DeployerFactory;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;
import org.codehaus.cargo.util.monitor.Monitor;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class DeployAction extends AbstractAction {
    
    private MavenProject project;
    
    public DeployAction(MavenProject proj) {
        putValue(NAME, "Deploy");
        project = proj;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        DeployPanel panel = new DeployPanel(project);
        DialogDescriptor dd = new DialogDescriptor(panel, "Deploy Web Application.");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            Container cont = panel.getSelectedContainer();
            WAR war = CargoServerRegistry.getInstance().getDeployableFactory().createWAR(panel.getDeployable());
            Deployer deployer = CargoServerRegistry.getInstance().getDeployerFactory().createDeployer(cont, DeployerFactory.DEFAULT);
//            DeployableMonitor mon = new URLDeployableMonitor(new URL(""));
            deployer.deploy(war);
        }
    }
    

}
