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


package org.codehaus.mevenide.netbeans.embedder.exec;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.maven.BuildFailureException;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.extension.ExtensionManager;
import org.apache.maven.lifecycle.DefaultLifecycleExecutor;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.lifecycle.LifecycleExecutor;
import org.apache.maven.monitor.event.EventDispatcher;
import org.apache.maven.plugin.PluginManager;
import org.apache.maven.plugin.PluginNotFoundException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.AbstractLogEnabled;

/**
 *
 * @author mkleint
 */
public class MyLifecycleExecutor extends AbstractLogEnabled implements LifecycleExecutor {
    // ----------------------------------------------------------------------
    // Components
    // ----------------------------------------------------------------------

    private PluginManager pluginManager;

    private ExtensionManager extensionManager;

    private List lifecycles;

    private ArtifactHandlerManager artifactHandlerManager;

    private List defaultReports;

    private Map phaseToLifecycleMap;
    
    private static ThreadLocal<ReactorManager> reactorManager = new ThreadLocal<ReactorManager>();
    
    public static List<File> getAffectedProjects() {
        ReactorManager rm = reactorManager.get();
        if (rm != null) {
            List lst = rm.getSortedProjects();
            Iterator it = lst.iterator();
            List<File> toRet = new ArrayList<File>();
            while (it.hasNext()) {
                MavenProject elem = (MavenProject) it.next();
                toRet.add(elem.getFile());
            }
            return toRet;
        }
        return Collections.EMPTY_LIST; 
    }
    
    /**
     * Execute a task. Each task may be a phase in the lifecycle or the
     * execution of a mojo.
     *
     * @param session
     * @param rm
     * @param dispatcher
     */
    public void execute( MavenSession session, ReactorManager rm, EventDispatcher dispatcher )
        throws BuildFailureException, LifecycleExecutionException
    {
        reactorManager.set(rm);
        createExecutor().execute( session, rm, dispatcher);
    }

    private LifecycleExecutor createExecutor() {
        DefaultLifecycleExecutor exec = new DefaultLifecycleExecutor();
        setVar(exec, pluginManager, "pluginManager");
        setVar(exec, extensionManager, "extensionManager");
        setVar(exec, lifecycles, "lifecycles");
        setVar(exec, artifactHandlerManager, "artifactHandlerManager");
        setVar(exec, defaultReports, "defaultReports");
//        setVar(exec, phaseToLifecycleMap, "phaseToLifecycleMap");
        exec.enableLogging(getLogger());
        return exec;
    }

    private void setVar(Object exec, Object value, String name) {
        try {
            Field fld = exec.getClass().getDeclaredField(name);
            fld.setAccessible(true);
            fld.set(exec, value);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public Map getLifecycleMappings(MavenSession mavenSession, String string, String string0, MavenProject mavenProject) throws LifecycleExecutionException, BuildFailureException, PluginNotFoundException {
        return createExecutor().getLifecycleMappings(mavenSession, string, string0, mavenProject);
    }
    
}
