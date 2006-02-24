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
package org.codehaus.mevenide.plugin.debugger;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.Transport;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.DebuggerManagerAdapter;
import org.netbeans.api.debugger.jpda.DebuggerStartException;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.debugger.jpda.MethodBreakpoint;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;


/**
 * Start the JPDA debugger
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 * @goal jpdastart
 * @requiresProject
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class JPDAStartMojo extends AbstractMojo {
    
    /**
     * @parameter expression="${project}
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * @parameter expression="${jpda-transport}" defaultvalue="dt_socket"
     */
    private String transport;
    
    /**
     * @parameter expression="${project.artifactId}"
     */
    private String name;
    
    /**
     * @parameter
     */
    private String stopClassName;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ListeningConnector lc = null;
            Iterator i = Bootstrap.virtualMachineManager().
                    listeningConnectors().iterator();
            for (; i.hasNext();) {
                lc = (ListeningConnector) i.next();
                Transport t = lc.transport();
                if (t != null && t.name().equals(transport)) break;
            }
            if (lc == null)
                throw new MojoFailureException
                        ("No trasports named " + transport + " found!");
            
            // TODO: revisit later when http://developer.java.sun.com/developer/bugParade/bugs/4932074.html gets integrated into JDK
            // This code parses the address string "HOST:PORT" to extract PORT and then point debugee to localhost:PORT
            // This is NOT a clean solution to the problem but it SHOULD work in 99% cases
            Map args = lc.defaultArguments();
            String address = lc.startListening(args);
            int port = -1;
            try {
                port = Integer.parseInt(address.substring(address.indexOf(':') + 1));
//                    getProject ().setNewProperty (getAddressProperty (), "localhost:" + port);
                Connector.IntegerArgument portArg = (Connector.IntegerArgument) args.get("port");
                portArg.setValue(port);
            } catch (Exception e) {
                // this address format is not known, use default
//                    getProject ().setNewProperty (getAddressProperty (), address);
            }
            
            Project proj = ProjectManager.getDefault().findProject(FileUtil.toFileObject(project.getBasedir()));
            ClassPath sourcePath = createSourcePath(proj, project);
            ClassPath jdkSourcePath = createJDKSourcePath(proj);
            
            if (stopClassName != null && stopClassName.length() > 0) {
                MethodBreakpoint b = createBreakpoint(stopClassName);
                DebuggerManager.getDebuggerManager().addDebuggerListener(
                        DebuggerManager.PROP_DEBUGGER_ENGINES,
                        new Listener(b)
                        );
            }
            
            
            Map properties = new HashMap();
            properties.put ("sourcepath", sourcePath);
            properties.put("name", name);
            properties.put("jdksources", jdkSourcePath);
            JPDADebugger.startListening(
                    lc,
                    args,
                    new Object[] {properties}
            );
        } catch (java.io.IOException ioex) {
            getLog().error("IO Error", ioex);
            org.openide.ErrorManager.getDefault().notify(ioex);
//                lock[1] = ioex;
        } catch (DebuggerStartException dsex) {
            getLog().error("Debugger start Ex", dsex);
            org.openide.ErrorManager.getDefault().notify(dsex);
//                lock[1] = dsex;
        } catch (com.sun.jdi.connect.IllegalConnectorArgumentsException icaex) {
            getLog().error("Illegal COnnector", icaex);
        } finally {
//                lock.notify ();
        }
        
        
    }
    
    
    // support methods .........................................................
    
    private MethodBreakpoint createBreakpoint(String stopClassName) {
        MethodBreakpoint breakpoint = MethodBreakpoint.create(
                stopClassName,
                "*"
                );
        breakpoint.setHidden(true);
        DebuggerManager.getDebuggerManager().addBreakpoint(breakpoint);
        return breakpoint;
    }
    // innerclasses ............................................................
    
    private static class Listener extends DebuggerManagerAdapter {
        
        private MethodBreakpoint    breakpoint;
        private Set                 debuggers = new HashSet();
        
        
        Listener(MethodBreakpoint breakpoint) {
            this.breakpoint = breakpoint;
        }
        
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName() == JPDADebugger.PROP_STATE) {
                int state = ((Integer) e.getNewValue()).intValue();
                if ( (state == JPDADebugger.STATE_DISCONNECTED) ||
                        (state == JPDADebugger.STATE_STOPPED)
                        ) {
//                    RequestProcessor.getDefault ().post (new Runnable () {
//                        public void run () {
                    if (breakpoint != null) {
                        DebuggerManager.getDebuggerManager().
                                removeBreakpoint(breakpoint);
                        breakpoint = null;
                    }
//                        }
//                    });
                    dispose();
                }
            }
            return;
        }
        
        private void dispose() {
            DebuggerManager.getDebuggerManager().removeDebuggerListener(
                    DebuggerManager.PROP_DEBUGGER_ENGINES,
                    this
                    );
            Iterator it = debuggers.iterator();
            while (it.hasNext()) {
                JPDADebugger d = (JPDADebugger) it.next();
                d.removePropertyChangeListener(
                        JPDADebugger.PROP_STATE,
                        this
                        );
            }
        }
        
        public void engineAdded(DebuggerEngine engine) {
            JPDADebugger debugger = (JPDADebugger) engine.lookupFirst
                    (null, JPDADebugger.class);
            if (debugger == null) return;
            debugger.addPropertyChangeListener(
                    JPDADebugger.PROP_STATE,
                    this
                    );
            debuggers.add(debugger);
        }
        
        public void engineRemoved(DebuggerEngine engine) {
            JPDADebugger debugger = (JPDADebugger) engine.lookupFirst
                    (null, JPDADebugger.class);
            if (debugger == null) return;
            debugger.removePropertyChangeListener(
                    JPDADebugger.PROP_STATE,
                    this
                    );
            debuggers.remove(debugger);
        }
    }
    
    
    static ClassPath createSourcePath(Project proj, MavenProject mproject) {
        FileObject[] roots;
        ClassPath cp;
        try {
            roots = FileUtilities.convertStringsToFileObjects(mproject.getRuntimeClasspathElements());
            cp = convertToSourcePath(roots);
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
            cp = ClassPathSupport.createClassPath(new FileObject[0]);
        }
        
        roots = FileUtilities.convertStringsToFileObjects(mproject.getCompileSourceRoots());
        ClassPath sp = convertToClassPath(roots);
        
        ClassPath sourcePath = ClassPathSupport.createProxyClassPath(
                new ClassPath[] {cp, sp}
        );
        return sourcePath;
    }
    
    static ClassPath createJDKSourcePath(Project nbproject) {
//        FileObject[] roots = FileUtilities.project.getCompileSourceRoots();
        
        //TODO for now just assume the default platform...
        JavaPlatform jp = JavaPlatform.getDefault();
        if (jp != null) {
            return jp.getSourceFolders();
        } else {
            return ClassPathSupport.createClassPath(Collections.EMPTY_LIST);
        }
    }
    
    private static ClassPath convertToClassPath(FileObject[] roots) {
        List l = new ArrayList();
        for (int i = 0; i < roots.length; i++) {
            URL url = fileToURL(FileUtil.toFile(roots[i]));
            l.add(url);
        }
        URL[] urls = (URL[]) l.toArray(new URL[l.size()]);
        return ClassPathSupport.createClassPath(urls);
    }
    
    /**
     * This method uses SourceForBinaryQuery to find sources for each
     * path item and returns them as ClassPath instance. All path items for which
     * the sources were not found are omitted.
     *
     */
    private static ClassPath convertToSourcePath(FileObject[] fos) {
        List lst = new ArrayList();
        Set existingSrc = new HashSet();
        for (int i = 0; i < fos.length; i++) {
            URL url = fileToURL(FileUtil.toFile(fos[i]));
            try {
                FileObject[] srcfos = SourceForBinaryQuery.findSourceRoots(url).getRoots();
                for (int j = 0; j < srcfos.length; j++) {
                    if (FileUtil.isArchiveFile(srcfos[j]))
                        srcfos [j] = FileUtil.getArchiveRoot(srcfos [j]);
                    try {
                        url = srcfos[j].getURL();
                    } catch (FileStateInvalidException ex) {
                        ErrorManager.getDefault().notify
                                (ErrorManager.EXCEPTION, ex);
                        continue;
                    }
                    if (url == null) continue;
                    if (!existingSrc.contains(url)) {
                        lst.add(ClassPathSupport.createResource(url));
                        existingSrc.add(url);
                    }
                } // for
            } catch (IllegalArgumentException ex) {
                //TODO??
            }
        }
        return ClassPathSupport.createClassPath(lst);
    }
    
    
    private static URL fileToURL(File file) {
        try {
            FileObject fileObject = FileUtil.toFileObject(file);
            if (fileObject == null) return null;
            if (FileUtil.isArchiveFile(fileObject))
                fileObject = FileUtil.getArchiveRoot(fileObject);
            return fileObject.getURL();
        } catch (FileStateInvalidException e) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
            return null;
        }
    }
    
    
}
