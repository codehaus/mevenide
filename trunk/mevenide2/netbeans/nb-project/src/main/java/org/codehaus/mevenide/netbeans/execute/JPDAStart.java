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
package org.codehaus.mevenide.netbeans.execute;

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
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
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
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;


/**
 * Start the JPDA debugger
 * @author <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class JPDAStart implements Runnable {
    
    
    /**
     * @parameter expression="${jpda.transport}"
     */
    private String transport = "dt_socket";
    
    /**
     * @parameter expression="${project.artifactId}"
     */
    private String name;
    
    /**
     * @parameter expression="${jpda.stopclass}"
     */
    private String stopClassName;
    
    private Object[] lock;
    
    private MavenEmbedderLogger log;
    
    private NbMavenProject project;
    
    public void setLog(MavenEmbedderLogger logger) {
        log = logger;
    }
    
    private MavenEmbedderLogger getLog() {
        return log;
    }
    /**
     * returns the port/address that the debugger listens to..
     */
    public String execute(NbMavenProject project) throws MojoExecutionException, MojoFailureException {
        this.project = project;
        getLog().info("JPDA Listening Starting...");
        lock = new Object [2];
        synchronized (lock) {
            getLog().debug("Entering synch lock");
            lock = new Object [2];
            synchronized (lock) {
                getLog().debug("Entered synch lock");
                RequestProcessor.getDefault().post(this);
                try {
                    getLog().debug("Entering wait");
                    lock.wait();
                    getLog().debug("Wait finished");
                    if (lock [1] != null) {
                        throw new MojoExecutionException("", (Throwable) lock [1]);
                    }
                } catch (InterruptedException e) {
                    throw new MojoExecutionException("Interrupted.", e);
                }
            }
        }
        return (String)lock[0];
    }
    
    public void run() {
        synchronized (lock) {
            
            try {
                
                ListeningConnector lc = null;
                Iterator i = Bootstrap.virtualMachineManager().
                        listeningConnectors().iterator();
                for (; i.hasNext();) {
                    lc = (ListeningConnector) i.next();
                    Transport t = lc.transport();
                    if (t != null && t.name().equals(getTransport())) break;
                }
                if (lc == null)
                    throw new RuntimeException
                            ("No trasports named " + getTransport() + " found!");
                
                // TODO: revisit later when http://developer.java.sun.com/developer/bugParade/bugs/4932074.html gets integrated into JDK
                // This code parses the address string "HOST:PORT" to extract PORT and then point debugee to localhost:PORT
                // This is NOT a clean solution to the problem but it SHOULD work in 99% cases
                final Map args = lc.defaultArguments();
                String address = lc.startListening(args);
                int port = -1;
                try {
                    port = Integer.parseInt(address.substring(address.indexOf(':') + 1));
//                    getProject ().setNewProperty (getAddressProperty (), "localhost:" + port);
                    Connector.IntegerArgument portArg = (Connector.IntegerArgument) args.get("port");
                    portArg.setValue(port);
                    lock[0] = Integer.toString(port);
                } catch (Exception e) {
                    // this address format is not known, use default
//                    getProject ().setNewProperty (getAddressProperty (), address);
                    lock[0] = address;
                }
                getLog().info("JPDA Address: " + address);
                getLog().info("Port=" + lock[0]);
                
                ClassPath sourcePath = createSourcePath(project, project.getOriginalMavenProject());
                ClassPath jdkSourcePath = createJDKSourcePath(project);
                
                if (getStopClassName() != null && getStopClassName().length() > 0) {
                    MethodBreakpoint b = createBreakpoint(getStopClassName());
                    DebuggerManager.getDebuggerManager().addDebuggerListener(
                            DebuggerManager.PROP_DEBUGGER_ENGINES,
                            new Listener(b));
                }
                
                
                final Map properties = new HashMap();
                properties.put("sourcepath", sourcePath);
                properties.put("name", getName());
                properties.put("jdksources", jdkSourcePath);
                getLog().info("sourcepath=" + sourcePath);
                getLog().info("jdkSources=" + jdkSourcePath);
                
                JPDADebugger.startListening(
                        lc,
                        args,
                        new Object[] {properties} );
            } catch (DebuggerStartException ex) {
                getLog().error("Debugger start Ex", ex);
                lock[1] = ex;
//                org.openide.ErrorManager.getDefault().notify(ex);
            } catch (java.io.IOException ioex) {
                getLog().error("IO Error", ioex);
//                org.openide.ErrorManager.getDefault().notify(ioex);
                lock[1] = ioex;
            } catch (com.sun.jdi.connect.IllegalConnectorArgumentsException icaex) {
                getLog().error("Illegal Connector", icaex);
                lock[1] = icaex;
            } finally {
                lock.notify();
            }
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
    
    static ClassPath createSourcePath(Project proj, MavenProject mproject) {
        FileObject[] roots;
        ClassPath cp;
        try {
            roots = FileUtilities.convertStringsToFileObjects(mproject.getTestClasspathElements());
            cp = convertToSourcePath(roots);
        } catch (DependencyResolutionRequiredException ex) {
            ex.printStackTrace();
            cp = ClassPathSupport.createClassPath(new FileObject[0]);
        }
        
        roots = FileUtilities.convertStringsToFileObjects(mproject.getTestCompileSourceRoots());
        ClassPath sp = convertToClassPath(roots);
        
        ClassPath sourcePath = ClassPathSupport.createProxyClassPath(
                new ClassPath[] {cp, sp}
        );
        return sourcePath;
    }
    
    static ClassPath createJDKSourcePath(Project nbproject) {
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

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStopClassName() {
        return stopClassName;
    }

    public void setStopClassName(String stopClassName) {
        this.stopClassName = stopClassName;
    }
    
    
    
    
}
