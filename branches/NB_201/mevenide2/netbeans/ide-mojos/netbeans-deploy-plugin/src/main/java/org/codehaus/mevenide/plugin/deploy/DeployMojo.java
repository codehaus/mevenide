/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.plugin.deploy;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ServerManager;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.deployment.plugins.api.ServerDebugInfo;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;


/**
 * Deploy a maven artifact..
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal deploy
 * @requiresProject
 */
public class DeployMojo extends AbstractMojo {
    
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    
    /**
     * Holds value of property debugmode.
     * @parameter
     *
     */
    private boolean debugmode = false;
    
    /**
     * @parameter
     */
    private boolean forceRedeploy = true;
    
    /**
     *  URI of the web client or rich client in J2EE application to execute after deployment.
     * @parameter expression="${clientModuleUri}"
     */
    private String clientModuleUri;
    
    /**
     * Part to build returned property client.url
     * @parameter expression="${clientUrlPart}"
     */
    private String clientUrlPart;
    
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (clientUrlPart == null) {
            getLog().debug("Setting empty clientUrlPart");
            clientUrlPart = "";
        }
        Set allowedPackagings = new HashSet();
        allowedPackagings.add("war");
        allowedPackagings.add("ejb");
        allowedPackagings.add("ear");
        if (!allowedPackagings.contains(project.getPackaging())) {
            throw new MojoFailureException("Can only deploy war, ear or ejb packaged projects");
        }
        ClassLoader originalLoader = null;
        
        try {
            // see issue #62448
            ClassLoader current = (ClassLoader)Lookup.getDefault().lookup(ClassLoader.class);
            if (current == null) {
                getLog().debug("No classloader in lookup");
                current = ClassLoader.getSystemClassLoader();
            }
            if (current != null) {
                getLog().debug("setting classloader..");
                originalLoader = Thread.currentThread().getContextClassLoader();
                Thread.currentThread().setContextClassLoader(current);
            }
            
            FileObject fob = FileUtil.toFileObject(project.getBasedir());
            fob.refresh(); // without this the "target" directory is not found in filesystems
            
            J2eeModuleProvider jmp = (J2eeModuleProvider) FileOwnerQuery.getOwner(fob).getLookup().lookup(J2eeModuleProvider.class);
            
            getLog().info("Deploying on " + Deployment.getDefault().getServerInstanceDisplayName(jmp.getServerInstanceID()));
            try {
                getLog().info("    debugMode=" + debugmode);
                getLog().info("    clientModuleuri=" + clientModuleUri);
                getLog().info("    clientUrlPart=" + clientUrlPart);
                getLog().info("    forcedeploy=" + forceRedeploy);
                
                String clientUrl = Deployment.getDefault().deploy(jmp, debugmode, clientModuleUri, clientUrlPart, forceRedeploy/*, new DLogger(getLog())*/);
                if (clientUrl != null) {
                    getLog().info("Executing browser to show " + clientUrl);
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(clientUrl));
                }
                
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
                    project.getProperties().put("jpda-transport", transport);
                    project.getProperties().put("jpda-host", h);
                    project.getProperties().put("jpda-address", address);
                    
                }
            } catch (Exception ex) {
                throw new MojoFailureException("Failed Deployment:" + ex.getMessage());
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
