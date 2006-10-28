/*
 * MavenDebuggerImpl.java
 *
 * Created on April 22, 2006, 11:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.debugger.MavenDebugger;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class MavenDebuggerImpl implements MavenDebugger {
    
    /** Creates a new instance of MavenDebuggerImpl */
    public MavenDebuggerImpl() {
    }

    public void attachDebugger(MavenProject project, Log log, String name, 
            final String transport,
            final String host, 
            final String address) throws MojoFailureException, MojoExecutionException {
//        JPDAStart.verifyPaths(getProject(), classpath);
//        JPDAStart.verifyPaths(getProject(), sourcepath);
        
        final Object[] lock = new Object [1];
        try {
            
            Project nbproject = ProjectManager.getDefault().findProject(FileUtil.toFileObject(project.getBasedir()));
            ClassPath sourcePath = Utils.createSourcePath(nbproject, project);
            ClassPath jdkSourcePath = Utils.createJDKSourcePath(nbproject);
            
            final Map properties = new HashMap();
            properties.put("sourcepath", sourcePath);
            properties.put("name", name);
            properties.put("jdksources", jdkSourcePath);
            
            
            synchronized(lock) {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        synchronized(lock) {
                            try {
                                // VirtualMachineManagerImpl can be initialized
                                // here, so needs to be inside RP thread.
                                if (transport.equals("dt_socket"))
                                    try {
                                        JPDADebugger.attach(
                                                host,                                             
                                                Integer.parseInt(address),
                                                new Object[] {properties}
                                        );
                                    } catch (NumberFormatException e) {
                                        throw new MojoFailureException(
                                                "address attribute must specify port " +
                                                "number for dt_socket connection");
                                    } else
                                        JPDADebugger.attach(
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
    }
    
}
