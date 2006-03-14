/*
 * JPDAConnectMojo.java
 *
 * Created on January 13, 2006, 10:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.plugin.debugger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

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
        getLog().info("Transport=" + transport);
        getLog().info("Address=" + address);
        getLog().info("Host=" + host);
//        JPDAStart.verifyPaths(getProject(), classpath);
//        JPDAStart.verifyPaths(getProject(), sourcepath);
        
        final Object[] lock = new Object [1];
        try {

            Project nbproject = ProjectManager.getDefault().findProject(FileUtil.toFileObject(project.getBasedir()));
            ClassPath sourcePath = JPDAStartMojo.createSourcePath(nbproject, project);
            ClassPath jdkSourcePath = JPDAStartMojo.createJDKSourcePath(nbproject);
            
            final Map properties = new HashMap ();
            properties.put ("sourcepath", sourcePath);
            properties.put ("name", name);
            properties.put ("jdksources", jdkSourcePath);
            

            synchronized(lock) {
                RequestProcessor.getDefault ().post (new Runnable () {
                    public void run() {
                        synchronized(lock) {
                            try {
                                // VirtualMachineManagerImpl can be initialized 
                                // here, so needs to be inside RP thread.
                                if (transport.equals ("dt_socket"))
                                    try {
                                        JPDADebugger.attach (
                                            host, 
                                            Integer.parseInt (address), 
                                            new Object[] {properties}
                                        );
                                    } catch (NumberFormatException e) {
                                        throw new MojoFailureException(
                                            "address attribute must specify port " +
                                            "number for dt_socket connection");
                                    }
                                else
                                    JPDADebugger.attach (
                                        address, 
                                        new Object[] {properties}
                                    );
                            } catch (Throwable e) {
                                lock[0] = e;
                            } finally {
                                lock.notify();
                            }
                        }
                    }
                });
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new MojoExecutionException("", e);
                }
                if (lock[0] != null)  {
                    throw new MojoExecutionException("", (Throwable) lock[0]);
                }

            }
        } catch (IOException ex) {
            throw new MojoExecutionException("", ex);
        }
//        if (host == null)
//            log ("Attached JPDA debugger to " + address);
//        else
//            log ("Attached JPDA debugger to " + host + ":" + address);
//        if (startVerbose)
//            System.out.println(
//                "\nS JPDAConnect.execute () " +
//                "end: success "
//            );
        
    }
    
}
