/*
 *  Copyright 2008 Mevenide Team.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.netbeans.embedder.exec;

import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.LifecycleLoaderException;
import org.apache.maven.lifecycle.LifecycleSpecificationException;
import org.apache.maven.lifecycle.plan.BuildPlan;
import org.apache.maven.lifecycle.plan.DefaultBuildPlanner;
import org.apache.maven.lifecycle.plan.LifecyclePlannerException;
import org.apache.maven.project.MavenProject;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha
 */
public class NBBuildPlanner extends DefaultBuildPlanner {

    private MavenSession mavenSession;

    @Override
    public synchronized BuildPlan constructInitialProjectBuildPlan(MavenProject arg0, MavenSession arg1) throws LifecycleLoaderException, LifecycleSpecificationException, LifecyclePlannerException {
        this.mavenSession = arg1;
        this.notifyAll();
        return super.constructInitialProjectBuildPlan(arg0, arg1);
    }

    @Override
    public synchronized void constructInitialProjectBuildPlans(MavenSession arg0) throws LifecycleLoaderException, LifecycleSpecificationException, LifecyclePlannerException {
        this.mavenSession = arg0;
        this.notifyAll();
        super.constructInitialProjectBuildPlans(arg0);
    }

    public synchronized MavenSession getMavenSession() {
        if (mavenSession == null) {
            try {
                this.wait(100000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return mavenSession;
    }
    
    @Override
    public synchronized BuildPlan constructBuildPlan(List tasks, MavenProject project, MavenSession session, boolean allowUnbindableMojos) 
            throws LifecycleLoaderException, LifecycleSpecificationException, LifecyclePlannerException {
        return super.constructBuildPlan(tasks, project, session, allowUnbindableMojos);
    }
    
}
