/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans.j2ee;

import java.net.URL;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.debugger.MavenDebugger;
import org.codehaus.mevenide.bridges.deployment.MavenDeployment;
import org.codehaus.mevenide.netbeans.j2ee.web.WebRunCustomizerPanel;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class MavenDeploymentImpl implements MavenDeployment {

    public static final String DEV_NULL = "WTF"; //NOI18N
    
    /** Creates a new instance of MavenDeploymentImpl */
    public MavenDeploymentImpl() {
    }
    
    public void doDeployment(MavenProject project, 
                              Log log,
                              boolean debugmode,
                              String clientUrlPart,
                              String clientModuleUri,
                              boolean forceRedeploy) throws MojoExecutionException, MojoFailureException {
        ClassLoader originalLoader = null;
        log.info("Deployment started"); //NOI18N - no localization in maven build now.
        try {
            // see issue #62448
            ClassLoader current = Lookup.getDefault().lookup(ClassLoader.class);
            if (current == null) {
                current = ClassLoader.getSystemClassLoader();
            }
            if (current != null) {
                originalLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(current);
            }
            
            FileObject fob = FileUtil.toFileObject(project.getBasedir());
            fob.refresh(); // without this the "target" directory is not found in filesystems
            
            J2eeModuleProvider jmp = FileOwnerQuery.getOwner(fob).getLookup().lookup(J2eeModuleProvider.class);
            String serverInstanceID = jmp.getServerInstanceID();
            if (DEV_NULL.equals(serverInstanceID)) {
                log.error("No suitable Deployment Server is defined for the project or globally.");//NOI18N - no localization in maven build now.
                throw new MojoFailureException("No suitable Deployment Server is defined for the project or globally.");//NOI18N - no localization in maven build now.
            }
            log.info("Deploying on " + Deployment.getDefault().getServerInstanceDisplayName(serverInstanceID));//NOI18N - no localization in maven build now.
            try {
                log.info("    debugMode: " + debugmode);//NOI18N - no localization in maven build now.
                log.info("    clientModuleUri: " + clientModuleUri);//NOI18N - no localization in maven build now.
                log.info("    clientUrlPart: " + clientUrlPart);//NOI18N - no localization in maven build now.
                log.info("    forcedeploy: " + forceRedeploy);//NOI18N - no localization in maven build now.
                
                String clientUrl = Deployment.getDefault().deploy(jmp, debugmode, clientModuleUri, clientUrlPart, forceRedeploy, new DLogger(log));
                if (clientUrl != null) {
                    FileObject fo = FileUtil.toFileObject(project.getBasedir());
                    boolean show = true;
                    if (fo != null) {
                        String browser = (String)fo.getAttribute(WebRunCustomizerPanel.PROP_SHOW_IN_BROWSER);
                        show = browser != null ? Boolean.parseBoolean(browser) : true;
                    }
                    if (show) {
                        log.info("Executing browser to show " + clientUrl);//NOI18N - no localization in maven build now.
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
                        deb.attachDebugger(project, log, "Debug Deployed app", transport, h, address);//NOI18N - no localization in maven build now.
                    }
                }
            } catch (Exception ex) {
                throw new MojoExecutionException("Failed Deployment:" + ex.getMessage(), ex);//NOI18N - no localization in maven build now.
            }
        } finally {
            if (originalLoader != null) {
                Thread.currentThread().setContextClassLoader(originalLoader);
            }
        }
    }

    private static class DLogger implements Deployment.Logger {

        private Log logger;
        
        public DLogger(Log log) {
            logger = log;
        }
        public void log(String string) {
            logger.info(string);
        }
        
    }
    
}
