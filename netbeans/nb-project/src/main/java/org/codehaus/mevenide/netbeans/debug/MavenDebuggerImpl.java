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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.bridges.debugger.MavenDebugger2;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 */
public class MavenDebuggerImpl implements MavenDebugger2 {
    
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
            ClassPath sourcePath = Utils.createSourcePath(nbproject);
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

    public void reload(MavenProject project, Log logger, String classname) throws MojoFailureException, MojoExecutionException {
        // check debugger state
        DebuggerEngine debuggerEngine = DebuggerManager.getDebuggerManager ().
            getCurrentEngine ();
        if (debuggerEngine == null) {
            throw new MojoFailureException ("No debugging sessions was found.");
        }
        JPDADebugger debugger = (JPDADebugger) debuggerEngine.lookupFirst 
            (null, JPDADebugger.class);
        if (debugger == null) {
            throw new MojoFailureException("Current debugger is not JPDA one.");
        }
        if (!debugger.canFixClasses ()) {
            throw new MojoFailureException("The debugger does not support Fix action.");
        }
        if (debugger.getState () == JPDADebugger.STATE_DISCONNECTED) {
            throw new MojoFailureException ("The debugger is not running");
        }
        
        System.out.println ("Classes to be reloaded:");
        
        Map map = new HashMap ();
        EditorContext editorContext = (EditorContext) DebuggerManager.
            getDebuggerManager ().lookupFirst (null, EditorContext.class);

        String clazz = classname.replace('.', File.separatorChar) + ".class";
        String source = classname.replace('.', File.separatorChar) + ".java";
        File srcfile = new File(project.getBuild().getSourceDirectory(), source);
        File clazzfile = new File(project.getBuild().getOutputDirectory(), clazz);
        FileObject fo1 = FileUtil.toFileObject(FileUtil.normalizeFile(srcfile));
        FileObject fo2 = FileUtil.toFileObject(FileUtil.normalizeFile(clazzfile));
        if (fo2 != null) {
            try {
                String url = classToSourceURL (fo2, logger);
                if (url != null)
                    editorContext.updateTimeStamp (debugger, url);
                InputStream is = fo2.getInputStream ();
                long fileSize = fo2.getSize ();
                byte[] bytecode = new byte [(int) fileSize];
                is.read (bytecode);
                map.put (
                    classname, 
                    bytecode
                );
                logger.info(" " + classname);
            } catch (IOException ex) {
                ex.printStackTrace ();
            }
        }
        
        if (logger.isInfoEnabled()) {
            logger.info("Reloaded classes: "+map.keySet());
        }
        if (map.size () == 0) {
            logger.info (" No class to reload");
            return;
        }
        String error = null;
        try {
            debugger.fixClasses (map);
        } catch (UnsupportedOperationException uoex) {
            error = "The virtual machine does not support this operation: "+uoex.getLocalizedMessage();
        } catch (NoClassDefFoundError ncdfex) {
            error = "The bytes don't correspond to the class type (the names don't match): "+ncdfex.getLocalizedMessage();
        } catch (VerifyError ver) {
            error = "A \"verifier\" detects that a class, though well formed, contains an internal inconsistency or security problem: "+ver.getLocalizedMessage();
        } catch (UnsupportedClassVersionError ucver) {
            error = "The major and minor version numbers in bytes are not supported by the VM. "+ucver.getLocalizedMessage();
        } catch (ClassFormatError cfer) {
            error = "The bytes do not represent a valid class. "+cfer.getLocalizedMessage();
        } catch (ClassCircularityError ccer) {
            error = "A circularity has been detected while initializing a class: "+ccer.getLocalizedMessage();
        }
        if (error != null) {
            throw new MojoFailureException(error);
        }
    }
    
    private String classToSourceURL (FileObject fo, Log logger) {
        try {
            ClassPath cp = ClassPath.getClassPath (fo, ClassPath.EXECUTE);
            FileObject root = cp.findOwnerRoot (fo);
            String resourceName = cp.getResourceName (fo, '/', false);
            if (resourceName == null) {
                logger.info("Can not find classpath resource for "+fo+", skipping...");
                return null;
            }
            int i = resourceName.indexOf ('$');
            if (i > 0)
                resourceName = resourceName.substring (0, i);
            FileObject[] sRoots = SourceForBinaryQuery.findSourceRoots 
                (root.getURL ()).getRoots ();
            ClassPath sourcePath = ClassPathSupport.createClassPath (sRoots);
            FileObject rfo = sourcePath.findResource (resourceName + ".java");
            if (rfo == null) return null;
            return rfo.getURL ().toExternalForm ();
        } catch (FileStateInvalidException ex) {
            ex.printStackTrace ();
            return null;
        }
    }
    
}
