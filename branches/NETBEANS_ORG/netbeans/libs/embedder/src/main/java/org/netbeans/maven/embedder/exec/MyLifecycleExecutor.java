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


package org.netbeans.maven.embedder.exec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.BuildFailureException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.lifecycle.DefaultLifecycleExecutor;
import org.apache.maven.lifecycle.LifecycleExecutionException;
import org.apache.maven.monitor.event.EventDispatcher;
import org.apache.maven.project.MavenProject;

/**
 *
 * @author mkleint
 */
public class MyLifecycleExecutor extends DefaultLifecycleExecutor {

    private static ThreadLocal<List<File>> fileList = new ThreadLocal<List<File>>();
    
    public static List<File> getAffectedProjects() {
        return fileList.get() == null ? Collections.<File>emptyList() : fileList.get();
    }
    public List<File> readAffectedProjects(ReactorManager rm) {
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
        return Collections.<File>emptyList(); 
    }
    
    /**
     * Execute a task. Each task may be a phase in the lifecycle or the
     * execution of a mojo.
     *
     * @param session
     * @param rm
     * @param dispatcher
     */
    @Override
    public void execute( MavenSession session, ReactorManager rm, EventDispatcher dispatcher )
        throws BuildFailureException, LifecycleExecutionException
    {
        fileList.set(readAffectedProjects(rm));
        super.execute( session, rm, dispatcher);
    }
}
