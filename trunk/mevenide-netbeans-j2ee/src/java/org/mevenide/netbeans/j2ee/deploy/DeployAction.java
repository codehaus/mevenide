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
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.AbstractAction;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.DeployableMonitorListener;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.deployer.DeployerFactory;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.mevenide.netbeans.project.MavenProject;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;
import org.openide.util.RequestProcessor;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class DeployAction extends AbstractAction {
    
    private MavenProject project;
    private DeployPanel panel;
    public DeployAction(MavenProject proj) {
        putValue(NAME, "Deploy");
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
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    if (container.getState() == State.STOPPED) {
                        StatusDisplayer.getDefault().setStatusText("Starting Container " + container.getName());
                        CargoServerRegistry.getInstance().startContainer(container, false);
                    }
                    if (container.getState() == State.STARTED) {
                        StatusDisplayer.getDefault().setStatusText("Dynamically deploying " + panel.getDeployable());
                        WAR war = CargoServerRegistry.getInstance().getDeployableFactory().createWAR(panel.getDeployable());
                        war.setContext(panel.getContext());
                        Deployer deployer = CargoServerRegistry.getInstance().getDeployerFactory().createDeployer(container, DeployerFactory.DEFAULT);
                        URL url = null;
                        try {
                            url = new URL(panel.getBaseUrl());
                        } catch (MalformedURLException exc) {
                            NotifyDescriptor error = new NotifyDescriptor.Message("Is not a valid URL.", NotifyDescriptor.WARNING_MESSAGE);
                            DialogDisplayer.getDefault().notify(error);
                        }
                        if (url != null) {
                            final URL fUrl = url;
                            DeployableMonitor mon = new URLDeployableMonitor(url);
                            mon.registerListener(new DeployableMonitorListener() {
                                public void deployed() {
                                    System.out.println("monitored deplloyed");
                                    StatusDisplayer.getDefault().setStatusText("Deployed " + panel.getDeployable());
                                    HtmlBrowser.URLDisplayer.getDefault().showURL(fUrl);
                                }
                            });
                            try {
                                deployer.deploy(war, mon);
                            } catch (ContainerException exc) {
                                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                                NotifyDescriptor nd = new NotifyDescriptor.Message(exc.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                                DialogDisplayer.getDefault().notify(nd);
                            }                            
                        } else {
                            try {
                                deployer.deploy(war);
                            } catch (ContainerException exc) {
                                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                                NotifyDescriptor nd = new NotifyDescriptor.Message(exc.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                                DialogDisplayer.getDefault().notify(nd);
                            }
                        }
                    }
                    
                }
            });
        }
    }
    
    
}
