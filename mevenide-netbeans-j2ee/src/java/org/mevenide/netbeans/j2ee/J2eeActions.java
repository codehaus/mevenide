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
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.WeakHashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.codehaus.cargo.container.deployable.Deployable;
import org.mevenide.netbeans.api.project.AdditionalActionsProvider;
import org.mevenide.netbeans.cargo.CargoServerRegistry;
import org.mevenide.netbeans.j2ee.deploy.DeployAction;
import org.mevenide.netbeans.j2ee.deploy.RedeployAction;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.ErrorManager;
import org.openide.awt.HtmlBrowser;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

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
        if (provider.getJ2eeModule() == null) {
//        if (war == null || !war.exists()) {
            return new Action[0];
        }
        Collection toRet = new ArrayList();
        Action deploy = (Action)cache.get(project);
        if (deploy == null) {
            deploy = new DeployAction(project);
            cache.put(project, deploy);
        }
        toRet.add(deploy);
        File war = project.getWar();
        if (war != null && war.exists()) {
            Deployable[] depls = CargoServerRegistry.getInstance().findDeployables(war.toString());
            if (depls.length > 0) {
                toRet.add(new RedeployAction(project));
            }
        }
        toRet.add(new NbDeploy(project));
        return (Action[])toRet.toArray(new Action[toRet.size()]);
    }
    
    private static class NbDeploy extends AbstractAction implements Logger, Runnable {
        private MavenProject project;
        private InputOutput io;
        public NbDeploy(MavenProject prj) {
            putValue(Action.NAME, "Netbeans Deploy");
            project = prj;
        }
        
        public void actionPerformed(ActionEvent event) {
            RequestProcessor.getDefault().post(this);
        }
        public void run() {
            io = IOProvider.getDefault().getIO(project.getName(), true);
            io.select();
            try {
                J2eeModuleProvider jmp = null;
                jmp = (J2eeModuleProvider) project.getLookup().lookup(J2eeModuleProvider.class);
                WebModuleImplementation impl = (WebModuleImplementation)project.getLookup().lookup(WebModuleImplementation.class);
                jmp.getConfigSupport().setWebContextRoot(impl.getContextPath());
                jmp.getConfigSupport().ensureConfigurationReady();
                String clientUrl = Deployment.getDefault().deploy(jmp, true,
                        "",
                        "sample.jsp", true, this);
                URL url = new URL(clientUrl);
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            } catch (Exception e) {
                ErrorManager.getDefault().log("e message=" + e.getMessage());
                ErrorManager.getDefault().notify(e);
            } finally {
                io.getOut().close();
            }
            
        }
        
        public void log(String str) {
            io.getOut().println(str);
        }
        
    }
    
    
    private static class NbDeployDebug extends AbstractAction implements Logger {
        private MavenProject project;
        public NbDeployDebug(MavenProject prj) {
            putValue(Action.NAME, "Netbeans Deploy Debug");
            project = prj;
        }
        
        public void actionPerformed(ActionEvent event) {
            
            try {
                J2eeModuleProvider jmp = null;
                jmp = (J2eeModuleProvider) project.getLookup().lookup(J2eeModuleProvider.class);
                WebModuleImplementation impl = (WebModuleImplementation)project.getLookup().lookup(WebModuleImplementation.class);
                jmp.getConfigSupport().setWebContextRoot(impl.getContextPath());
                jmp.getConfigSupport().ensureConfigurationReady();
                String clientUrl = Deployment.getDefault().deploy(jmp, true,
                        "",
                        "sample.jsp", true, this);
                ServerDebugInfo sdi = jmp.getServerDebugInfo();
                
                if (sdi != null) { //fix for bug 57854, this can be null
                    String h = sdi.getHost();
                    String transport = sdi.getTransport();
                    String address = "";   //NOI18N
                    
                    if (transport.equals(ServerDebugInfo.TRANSPORT_SHMEM)) {
                        address = sdi.getShmemName();
                    } else {
                        address = Integer.toString(sdi.getPort());
                    }
//                    try {
//                        JPDADebugger debug = JPDADebugger.attach(host, port, new Object[0]);
//                    } catch (DebuggerStartException exc) {
//                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Cannot attach debugger.", NotifyDescriptor.ERROR_MESSAGE));
//                    }
                    
                }
                URL url = new URL(clientUrl);
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            } catch (Exception e) {
                ErrorManager.getDefault().log("e message=" + e.getMessage());
                ErrorManager.getDefault().notify(e);
            }
            
        }
        
        public void log(String str) {
            System.out.println("logged=" + str);
            if (str.startsWith("FAIL")) {
                Thread.dumpStack();
            }
        }
        
    }
    
    
    
    
}
