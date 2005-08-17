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
import javax.swing.AbstractAction;
import org.codehaus.cargo.container.Container;
import org.mevenide.netbeans.api.project.MavenProject;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class DeployAction extends AbstractAction {
    
    private MavenProject project;
    private DeployPanel panel;
    public DeployAction(MavenProject proj) {
        putValue(NAME, "Deploy using Cargo");
        project = proj;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        if (panel == null) {
            panel = new DeployPanel(project);
        }
        DialogDescriptor dd = new DialogDescriptor(panel, "Deploy Web Application.");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            final Container container = panel.getSelectedContainer();
            if (container == null) {
                // no container defined..
                return;
            }
            DeployerRunner runner = new DeployerRunner(container, panel.getDeployable(), panel.getBaseUrl(), panel.getContext());
            RequestProcessor.getDefault().post(runner);
        }
    }
}
