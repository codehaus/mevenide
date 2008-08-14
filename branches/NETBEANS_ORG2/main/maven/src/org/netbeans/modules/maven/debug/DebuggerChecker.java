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
package org.netbeans.modules.maven.debug;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.debugger.DebuggerEngine;
import org.netbeans.api.debugger.DebuggerManager;
import org.netbeans.api.debugger.jpda.JPDADebugger;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.api.classpath.ProjectSourcesClassPathProvider;
import org.netbeans.modules.maven.api.execute.ExecutionContext;
import org.netbeans.modules.maven.api.execute.ExecutionResultChecker;
import org.netbeans.modules.maven.api.execute.LateBoundPrerequisitesChecker;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.spi.debugger.jpda.EditorContext;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ActionProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.windows.OutputWriter;

/**
 *
 * @author mkleint
 */
public class DebuggerChecker implements LateBoundPrerequisitesChecker, ExecutionResultChecker {
    private Logger LOGGER = Logger.getLogger(DebuggerChecker.class.getName());

    public boolean checkRunConfig(RunConfig config, ExecutionContext context) {
        if (config.getProject() == null) {
            //cannot act on execution without a project instance..
            return true;
        }
        
        if ("true".equals(config.getProperties().getProperty(Constants.ACTION_PROPERTY_JPDALISTEN)) ||
            "maven".equals(config.getProperties().getProperty(Constants.ACTION_PROPERTY_JPDALISTEN))) {//NOI18N
                //NOI18N
            try {
                JPDAStart start = new JPDAStart(context.getInputOutput());
                NbMavenProject prj = config.getProject().getLookup().lookup(NbMavenProject.class);
                start.setName(prj.getMavenProject().getArtifactId());
                start.setStopClassName(config.getProperties().getProperty("jpda.stopclass")); //NOI18N
                String val = start.execute(config.getProject());
                Enumeration en = config.getProperties().propertyNames();
                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    String value = config.getProperties().getProperty(key);
                    StringBuffer buf = new StringBuffer(value);
                    String replaceItem = "${jpda.address}"; //NOI18N
                    int index = buf.indexOf(replaceItem);
                    while (index > -1) {
                        String newItem = val;
                        newItem = newItem == null ? "" : newItem; //NOI18N
                        buf.replace(index, index + replaceItem.length(), newItem);
                        index = buf.indexOf(replaceItem);
                    }
                    //                System.out.println("setting property=" + key + "=" + buf.toString());
                    config.setProperty(key, buf.toString());
                }
                config.setProperty("jpda.address", val); //NOI18N
            } catch (Throwable th) {
                LOGGER.log(Level.INFO, th.getMessage(), th);
            }
        }
        if (ActionProvider.COMMAND_DEBUG_STEP_INTO.equals(config.getActionName())) {
            //TODO - change the goal from compile to test-compile in case of file coming from
            //the test source roots..
        }
        return true;
    }

    public void executionResult(RunConfig config, ExecutionContext res, int resultCode) {
        if (config.getProject() != null && resultCode == 0 && "debug.fix".equals(config.getActionName())) { //NOI18N
            String cname = config.getProperties().getProperty("jpda.stopclass"); //NOI18N
            if (cname != null) {
                reload(config.getProject(), res.getInputOutput().getOut(), cname);
            } else {
                res.getInputOutput().getErr().println("Missing jpda.stopclass property in action mapping definition. Cannot reload class.");
            }
        }
    }
    
    
    public void reload(Project project, OutputWriter logger, String classname) {
        // check debugger state
        DebuggerEngine debuggerEngine = DebuggerManager.getDebuggerManager ().
            getCurrentEngine ();
        if (debuggerEngine == null) {
            logger.println("NetBeans: No debugging sessions was found.");
            return;
        }
        JPDADebugger debugger = (JPDADebugger) debuggerEngine.lookupFirst 
            (null, JPDADebugger.class);
        if (debugger == null) {
            logger.println("NetBeans: Current debugger is not JPDA one.");
            return;
        }
        if (!debugger.canFixClasses ()) {
            logger.println("NetBeans: The debugger does not support Fix action.");
            return;
        }
        if (debugger.getState () == JPDADebugger.STATE_DISCONNECTED) {
            logger.println("NetBeans: The debugger is not running");
            return;
        }
        
        logger.println ("NetBeans: Classes to be reloaded:");
        
        Map map = new HashMap ();
        EditorContext editorContext = (EditorContext) DebuggerManager.
            getDebuggerManager ().lookupFirst (null, EditorContext.class);

        String clazz = classname.replace('.', '/') + ".class"; //NOI18N
        ProjectSourcesClassPathProvider prv = project.getLookup().lookup(ProjectSourcesClassPathProvider.class);
        ClassPath[] ccp = prv.getProjectClassPaths(ClassPath.COMPILE);
        FileObject fo2 = null;
        for (ClassPath cp : ccp) {
            fo2 = cp.findResource(clazz);
            if (fo2 != null) {
                break;
            }
        }
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
                logger.println(" " + classname);
            } catch (IOException ex) {
                ex.printStackTrace ();
            }
        }
        
        logger.println("NetBeans: Reloaded classes: "+map.keySet());
        if (map.size () == 0) {
            logger.println("NetBeans: No class to reload");
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
            logger.println("NetBeans:" + error);
        }
    }
    
    private String classToSourceURL (FileObject fo, OutputWriter logger) {
        try {
            ClassPath cp = ClassPath.getClassPath (fo, ClassPath.EXECUTE);
            FileObject root = cp.findOwnerRoot (fo);
            String resourceName = cp.getResourceName (fo, '/', false);
            if (resourceName == null) {
                logger.println("Can not find classpath resource for "+fo+", skipping...");
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
