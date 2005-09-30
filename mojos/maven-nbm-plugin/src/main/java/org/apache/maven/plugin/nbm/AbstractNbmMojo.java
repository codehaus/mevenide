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
package org.apache.maven.plugin.nbm;

import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Taskdef;
import org.apache.maven.plugin.nbm.model.Dependency;

public abstract class AbstractNbmMojo extends AbstractMojo {


    protected Project registerNbmAntTasks() {
        Project antProject = new Project();
        antProject.init();

        Taskdef taskdef = (Taskdef) antProject.createTask( "taskdef" );
        taskdef.setClassname("org.netbeans.nbbuild.MakeListOfNBM" );
        taskdef.setName("genlist");
        taskdef.execute();
        
        taskdef = (Taskdef) antProject.createTask( "taskdef" );
        taskdef.setClassname("org.netbeans.nbbuild.MakeNBM" );
        taskdef.setName("makenbm");
        taskdef.execute();

        taskdef = (Taskdef) antProject.createTask( "taskdef" );
        taskdef.setClassname("org.netbeans.nbbuild.MakeUpdateDesc" );
        taskdef.setName("updatedist");
        taskdef.execute();
        
        taskdef = (Taskdef) antProject.createTask( "taskdef" );
        taskdef.setClassname("org.netbeans.nbbuild.CreateModuleXML" );
        taskdef.setName("createmodulexml");
        taskdef.execute();
        
        return antProject;
    }
    
    protected boolean matchesLibrary(Artifact artifact, List libraries) {
        if (!"jar".equals(artifact.getType())) {
            // just jars make sense.
            return false;
        }
        String artId = artifact.getArtifactId();
        String grId = artifact.getGroupId();
        String id = grId + ":" + artId;
        return libraries.remove(id);
    }
    
    protected Dependency resolveNetbeansDependency(Artifact artifact, List deps) {
        if (!"jar".equals(artifact.getType())) {
            // just jars make sense.
            return null;
        }
        String artId = artifact.getArtifactId();
        String grId = artifact.getGroupId();
        String id = grId + ":" + artId;
        Iterator it = deps.iterator();
        while (it.hasNext()) {
            Dependency dep = (Dependency)it.next();
            if (id.equals(dep.getId())) {
                return dep;
            }
        }
        return null;
    }

}
