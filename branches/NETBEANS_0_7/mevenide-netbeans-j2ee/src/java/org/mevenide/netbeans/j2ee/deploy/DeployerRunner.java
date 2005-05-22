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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import org.codehaus.cargo.container.Container;
import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.configuration.StandaloneConfiguration;
import org.codehaus.cargo.container.deployable.Deployable;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.deployer.DeployableMonitor;
import org.codehaus.cargo.container.deployer.DeployableMonitorListener;
import org.codehaus.cargo.container.deployer.Deployer;
import org.codehaus.cargo.container.deployer.URLDeployableMonitor;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class DeployerRunner implements Runnable {
    private Container container;
    private String deployable;
    private String context;
    private String baseurl;
    private WAR war;
    /** Creates a new instance of Deployer */
    public DeployerRunner(Container cont, String depl, String url, String context) {
        container = cont;
        deployable = depl;
        baseurl = url;
        this.context = context;
    }
    
    public DeployerRunner(Container cont, WAR existingWar) {
        container = cont;
        war = existingWar;
    }
    
    public void run() {
        if (!CargoServerRegistry.getInstance().supportsDynamicDeployment(container)) {
            doStaticDeployment();
        } else {
            doDynamicDeployment();
        }
    }
    
    private void doStaticDeployment() {
        CargoServerRegistry reg = CargoServerRegistry.getInstance();
        if (container.getState() == State.STARTED) {
            StatusDisplayer.getDefault().setStatusText("Stopping Container " + container.getName());
            reg.stopContainer(container);
        }
        if (container.getConfiguration() instanceof StandaloneConfiguration) {
            StandaloneConfiguration config = (StandaloneConfiguration)container.getConfiguration();
            if (war == null) {
                // try to search for it anyway..
                Collection col = reg.getDeployables(container);
                Iterator it = col.iterator();
                File fil = new File(deployable);
                while (it.hasNext()) {
                    Deployable depl = (Deployable)it.next();
                    if (fil.equals(depl.getFile())) {
                        war = (WAR)depl;
                    }
                }
                if (war == null) {
                    war = reg.getDeployableFactory().createWAR(deployable);
                    war.setContext(context);
                    config.addDeployable(war);
                }
            }
        } else {
            throw new IllegalStateException("Can only deploy with standalone configuration");
        }
        StatusDisplayer.getDefault().setStatusText("Starting Container " + container.getName());
        reg.startContainer(container, false);
        if (baseurl != null) {
            try {
                final URL url = new URL(baseurl);
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            } catch (MalformedURLException exc) {
                NotifyDescriptor error = new NotifyDescriptor.Message("Is not a valid URL.", NotifyDescriptor.WARNING_MESSAGE);
                DialogDisplayer.getDefault().notify(error);
            }
        }
        StatusDisplayer.getDefault().setStatusText("Statically Deployed " + (war == null ? deployable : war.getFile().toString()));
    }
    
    private void doDynamicDeployment() {
        CargoServerRegistry reg = CargoServerRegistry.getInstance();
        if (container.getState() == State.STOPPED) {
            StatusDisplayer.getDefault().setStatusText("Starting Container " + container.getName());
            reg.startContainer(container, false);

        }
        if (container.getState() == State.STARTED) {
            StatusDisplayer.getDefault().setStatusText("Dynamically deploying " + deployable);
            if (war == null) {
                // try to search for it anyway..
                Collection col = reg.getDeployables(container);
                Iterator it = col.iterator();
                File fil = new File(deployable);
                while (it.hasNext()) {
                    Deployable depl = (Deployable)it.next();
                    if (fil.equals(depl.getFile())) {
                        war = (WAR)depl;
                    }
                }
                if (war == null) {
                    war = reg.getDeployableFactory().createWAR(deployable);
                    war.setContext(context);
                }
            }
            Deployer deployer = reg.getDeployer(container);
            if (baseurl != null) {
                try {
                    final URL url = new URL(baseurl);
                    DeployableMonitor mon = new URLDeployableMonitor(url);
                    mon.registerListener(new DeployableMonitorListener() {
                        public void deployed() {
                            StatusDisplayer.getDefault().setStatusText("Dynamically Deployed " + deployable);
                            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                        }
                    });
                    deployer.deploy(war, mon);
                    reg.registerDeployable(container, war);
                } catch (MalformedURLException exc) {
                    NotifyDescriptor error = new NotifyDescriptor.Message("Is not a valid URL.", NotifyDescriptor.WARNING_MESSAGE);
                    DialogDisplayer.getDefault().notify(error);
                } catch (ContainerException exc) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(exc.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }
            } else {
                try {
                    deployer.deploy(war);
                    reg.registerDeployable(container, war);
                    StatusDisplayer.getDefault().setStatusText("Dynamically Deployed at " + container.getName() + " ...");
                } catch (ContainerException exc) {
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    NotifyDescriptor nd = new NotifyDescriptor.Message(exc.getLocalizedMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                }
            }
        }
    }
        
    
}
