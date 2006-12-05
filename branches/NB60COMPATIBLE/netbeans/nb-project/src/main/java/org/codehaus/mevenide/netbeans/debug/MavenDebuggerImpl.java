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
            properties.put("sourcepath", sourcePath); //NOI18N
            properties.put("name", name); //NOI18N
            properties.put("jdksources", jdkSourcePath); //NOI18N
            
            
            synchronized(lock) {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        synchronized(lock) {
                            try {
                                // VirtualMachineManagerImpl can be initialized
                                // here, so needs to be inside RP thread.
                                if (transport.equals("dt_socket")) //NOI18N
                                    try {
                                        JPDADebugger.attach(
                                                host,                                             
                                                Integer.parseInt(address),
                                                new Object[] {properties}
                                        );
                                    } catch (NumberFormatException e) {
                                        throw new MojoFailureException(
                                                "address attribute must specify port " + //NOI18N
                                                "number for dt_socket connection"); //NOI18N
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
