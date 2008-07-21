/*
 *  Copyright 2008 mkleint.
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

package org.netbeans.modules.maven.profiler;

import java.io.File;
import java.util.Properties;
import org.netbeans.modules.maven.api.execute.PrerequisitesChecker;
import org.netbeans.modules.maven.api.execute.RunConfig;
import org.netbeans.api.project.Project;
import org.netbeans.lib.profiler.common.Profiler;
import org.netbeans.modules.profiler.utils.ProjectUtilities;
import org.openide.util.RequestProcessor;

/**
 *
 * @author mkleint
 * @author Jiri Sedlacek
 */
public class RunCheckerImpl implements PrerequisitesChecker {
    
    private static final String ACTION_PROFILE = "profile"; // NOI18N
    private static final String ACTION_PROFILE_SINGLE = "profile-single"; // NOI18N
    private static final String ACTION_PROFILE_TESTS = "profile-tests"; // NOI18N
    
    private static final String EXEC_ARGS = "exec.args"; // NOI18N
    private static final String PROFILER_ARGS = "${profiler.args}"; // NOI18N
    private static final String EXEC_EXECUTABLE = "exec.executable"; // NOI18N
    private static final String PROFILER_JAVA = "${profiler.java}"; // NOI18N
    
    private Project project;

    
    public RunCheckerImpl(Project prj) {
        project = prj;
    }
    
    public boolean checkRunConfig(String actionName, RunConfig config) {
        if (ACTION_PROFILE.equals(actionName)) { // action "profile"
            // Get the ProjectTypeProfiler for Maven project
            final MavenProjectTypeProfiler ptp = (MavenProjectTypeProfiler)ProjectUtilities.getProjectTypeProfiler(project);
            
            // Resolve profiling session properties
            Properties sessionProperties = ptp.getLastSessionProperties();
            // Resolve config properties
            Properties configProperties = config.getProperties();
            
            // Set "${profiler.args}" value
            String execArgs = configProperties.getProperty(EXEC_ARGS);
            execArgs = execArgs.replace(PROFILER_ARGS, sessionProperties.getProperty("profiler.info.jvmargs") // NOI18N
                    + " " + sessionProperties.getProperty("profiler.info.jvmargs.agent").replace("\\", "/")); // NOI18N
            configProperties.setProperty(EXEC_ARGS, execArgs);
            
            // Set "${profiler.java}" value
            String execExecutable = configProperties.getProperty(EXEC_EXECUTABLE);
            String profilerJava = sessionProperties.getProperty("profiler.info.jvm"); // NOI18N
            execExecutable = execExecutable.replace(PROFILER_JAVA,
                    (profilerJava != null && new File(profilerJava).isFile()) ? profilerJava : "java"); // NOI18N
            configProperties.setProperty(EXEC_EXECUTABLE, execExecutable);
            
            // Set the properties back to config
            config.setProperties(configProperties);
            
            // Attach profiler engine (in separate thread) to profiled process
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    Profiler.getDefault().connectToStartedApp(ptp.getLastProfilingSettings(), ptp.getLastSessionSettings());
                }
            });
            
        } else if (ACTION_PROFILE_SINGLE.equals(actionName)) { // action "profile-single"
            // profile-single not supported yet, shouldn't get here
        } else if (ACTION_PROFILE_TESTS.equals(actionName)) {
            // profile-tests not supported yet, shouldn't get here // action "profile-tests"
        }
        
        return true;
    }

}
