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

package org.codehaus.mevenide.plugin.debugger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.debugger.MavenDebugger;
import org.openide.util.Lookup;

/**
 * Connect the JPDA debugger
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal jpdaconnect
 * @requiresProject
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class JPDAConnectMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * @parameter expression="${jpda.host}"
     */
    private String host = "localhost";
    
    /**
     * @parameter expression="${jpda.address}"
     * @required
     */
    private String address;
    
    /**
     * Name which will represent this debugging session in debugger UI.
     * @parameter expression="${project.artifactId}"
     */
    private String name;
    
    /**
     * @parameter expression="${jpda.transport}"
     */
    private String transport = "dt_socket";
    
    /** Creates a new instance of JPDAConnectMojo */
    public JPDAConnectMojo() {
    }
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Attaching JPDA Debugger...");
        getLog().info("    Transport=" + getTransport());
        getLog().info("    Address=" + getAddress());
        getLog().info("    Host=" + getHost());
        MavenDebugger debugger = (MavenDebugger)Lookup.getDefault().lookup(MavenDebugger.class);
        debugger.attachDebugger(getProject(), getLog(), name, getTransport(), getHost(), getAddress());
    }

    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getTransport() {
        return transport;
    }
    
    public void setTransport(String transport) {
        this.transport = transport;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
    
}
