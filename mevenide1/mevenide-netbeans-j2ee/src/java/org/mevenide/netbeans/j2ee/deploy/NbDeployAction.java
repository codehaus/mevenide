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
import javax.swing.Action;
import org.mevenide.netbeans.j2ee.MavenJ2eeModule;
import org.mevenide.netbeans.j2ee.web.WebModuleImpl;
import org.mevenide.netbeans.j2ee.web.WebModuleProviderImpl;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.ActionProviderImpl;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment.Logger;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Message;
import org.openide.awt.HtmlBrowser;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.URLMapper;
import org.openide.util.RequestProcessor;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class NbDeployAction extends AbstractAction implements Logger, Runnable {
    private MavenProject project;
    private InputOutput io;
    private String serverInstanceid;
    private String path;
    private boolean debug;
    private String goal;
    public NbDeployAction(MavenProject prj) {
        putValue(Action.NAME, "Netbeans Deploy");
        project = prj;
    }
    
    public void actionPerformed(ActionEvent event) {
        J2eeModuleProvider prov = (J2eeModuleProvider)project.getLookup().lookup(J2eeModuleProvider.class);
        final NbDeployPanel panel = ((MavenJ2eeModule)prov.getJ2eeModule()).getPanel();
        DialogDescriptor dd = new DialogDescriptor(panel, "Deploy Web Application.");
        Object ret = DialogDisplayer.getDefault().notify(dd);
        if (ret == NotifyDescriptor.OK_OPTION) {
            goal = panel.getGoalToRun();
            serverInstanceid = panel.getSelectedServer();
            path = panel.getPath();
            debug = panel.isDebugging();
            prov.setServerInstanceID(serverInstanceid);
            MavenJ2eeModule mod = (MavenJ2eeModule)prov.getJ2eeModule();
            mod.setInplace(panel.isInplace());
            
            RequestProcessor.getDefault().post(this);
        }
        
    }
    
    public void run() {
        if (goal != null) {
            ActionProviderImpl actionProvider = (ActionProviderImpl)project.getLookup().lookup(ActionProviderImpl.class);
            ExecutorTask task = actionProvider.runGoal(goal, project.getLookup());
            task.waitFinished();
            project.getProjectDirectory().refresh();
        }
        io = IOProvider.getDefault().getIO(project.getName(), true);
        io.select();
        try {
            J2eeModuleProvider jmp = null;
            jmp = (J2eeModuleProvider) project.getLookup().lookup(J2eeModuleProvider.class);
            WebModuleImpl impl = ((WebModuleProviderImpl)project.getLookup().lookup(WebModuleProviderImpl.class)).getWebImpl();
            if (impl != null && impl.isValid()) {
                FileObject check1 = impl.getDocumentBase();
                FileObject check2 = impl.getDeploymentDescriptor();
                FileObject check3 = impl.getWebInf();
                if (check1 == null || check2 == null || check3 == null) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message("Your project is not completely setup for deployment. Please check that your war plugin properties 'maven.war.src' and 'maven.war.webxml' are pointing to the correct, existing places.", NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    return;
                }
                jmp.getConfigSupport().setWebContextRoot(impl.getContextPath());
                
            }
            jmp.getConfigSupport().ensureConfigurationReady();
            String clientUrl = Deployment.getDefault().deploy(jmp, debug,
                    impl == null ? "" : impl.getContextPath(),
                    path, true, this);
            URL url = new URL(clientUrl);
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
            if (debug) {
                //TODO
            }
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
