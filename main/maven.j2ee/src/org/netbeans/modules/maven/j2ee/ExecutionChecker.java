/* ==========================================================================
 * Copyright 2008 Mevenide Team
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
package org.netbeans.modules.maven.j2ee;

import java.net.URL;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.project.Project;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.execute.ExecutionResult;
import org.netbeans.modules.maven.api.execute.ExecutionResultChecker;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.modules.maven.bridges.debugger.MavenDebugger;
import org.netbeans.modules.maven.j2ee.web.WebRunCustomizerPanel;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.OutputWriter;

/**
 *
 * @author mkleint
 */
public class ExecutionChecker implements ExecutionResultChecker {

    private Project project;
    public static final String DEV_NULL = "WTF-NULL"; //NOI18N
    public static final String DEPLOY = "netbeans.deploy"; //NOI18N
    public static final String DEBUG_MODE = "netbeans.deploy.debugmode"; //NOI18N
    public static final String REDEPLOY = "netbeans.deploy.forceRedeploy"; //NOI18N
    public static final String MODULEURI = "netbeans.deploy.clientModuleUri"; //NOI18N
    public static final String CLIENTURLPART = "netbeans.deploy.clientUrlPart"; //NOI18N
    
    
    ExecutionChecker(Project prj) {
        project = prj;
    }

    public void executionResult(RunConfig config, ExecutionResult res) {
        boolean depl = Boolean.parseBoolean(config.getProperties().getProperty(DEPLOY));
        if (depl && res.getExitCode() == 0) {
            String moduleUri = config.getProperties().getProperty(MODULEURI);
            String clientUrl = config.getProperties().getProperty(CLIENTURLPART, "");
            boolean redeploy = Boolean.parseBoolean(config.getProperties().getProperty(REDEPLOY, "true")); //NOI18N
            boolean debugmode = Boolean.parseBoolean(config.getProperties().getProperty(DEBUG_MODE)); //NOI18N
            performDeploy(res, debugmode, clientUrl, moduleUri, redeploy);
        }
    }

    private void performDeploy(ExecutionResult res, boolean debugmode, String clientModuleUri, String clientUrlPart, boolean forceRedeploy) {
        FileUtil.refreshFor(FileUtil.toFile(project.getProjectDirectory()));
        OutputWriter err = res.getInputOutput().getErr();
        OutputWriter out = res.getInputOutput().getOut();
        J2eeModuleProvider jmp = project.getLookup().lookup(J2eeModuleProvider.class);
        String serverInstanceID = jmp.getServerInstanceID();
        if (DEV_NULL.equals(serverInstanceID)) {
            err.println();
            err.println();
            err.println("NetBeans: No suitable Deployment Server is defined for the project or globally.");//NOI18N - no localization in maven build now.
            //TODO - click here to setup..
            return;
        }
        out.println("NetBeans: Deploying on " + Deployment.getDefault().getServerInstanceDisplayName(serverInstanceID));//NOI18N - no localization in maven build now.
        try {
                out.println("    debug mode: " + debugmode);//NOI18N - no localization in maven build now.
//                log.info("    clientModuleUri: " + clientModuleUri);//NOI18N - no localization in maven build now.
//                log.info("    clientUrlPart: " + clientUrlPart);//NOI18N - no localization in maven build now.
                out.println("    force redeploy: " + forceRedeploy);//NOI18N - no localization in maven build now.

            String clientUrl = Deployment.getDefault().deploy(jmp, debugmode, clientModuleUri, clientUrlPart, forceRedeploy, new DLogger(out));
            if (clientUrl != null) {
                FileObject fo = project.getProjectDirectory();
                boolean show = true;
                if (fo != null) {
                    String browser = (String) fo.getAttribute(WebRunCustomizerPanel.PROP_SHOW_IN_BROWSER);
                    show = browser != null ? Boolean.parseBoolean(browser) : true;
                }
                if (show) {
//                        log.info("Executing browser to show " + clientUrl);//NOI18N - no localization in maven build now.
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(clientUrl));
                }
            }
            if (debugmode) { 
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
                    MavenDebugger deb = Lookup.getDefault().lookup(MavenDebugger.class);
                    MavenProject prj = project.getLookup().lookup(NbMavenProject.class).getMavenProject();
                    //TODO get rid of MavenProject here..
                    deb.attachDebugger(prj, null, "Debug Deployed app", transport, h, address);//NOI18N - no localization in maven build now.
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static class DLogger implements Deployment.Logger {

        private OutputWriter logger;

        public DLogger(OutputWriter log) {
            logger = log;
        }

        public void log(String string) {
            logger.println(string);
        }
    }
}
