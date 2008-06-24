/* ==========================================================================
 * Copyright 2007 Mevenide Team
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
package org.netbeans.modules.maven.apisupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.netbeans.modules.maven.api.classpath.ProjectSourcesClassPathProvider;
import org.netbeans.modules.maven.api.output.OutputProcessor;
import org.netbeans.modules.maven.api.output.OutputUtils;
import org.netbeans.modules.maven.api.output.OutputVisitor;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.windows.OutputListener;




/**
 * exec plugin output processing, just handle stacktraces.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class IDEOutputListenerProvider implements OutputProcessor {
    
    private static final String[] EXECGOALS = new String[] {
        "mojo-execute#nbm:run-ide", //NOI18N
        "mojo-execute#nbm:run-platform" //NOI18N
    };
    private Project project;
    private ClassPath classpath;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public IDEOutputListenerProvider(Project proj) {
        project = proj;
        classpath = createCP(project, new HashSet<Project>());
    }
    
    private ClassPath createCP(Project prj, HashSet<Project> parents) {
        parents.add(prj);
        List<ClassPath> list = new ArrayList<ClassPath>();
        ProjectSourcesClassPathProvider cpp = prj.getLookup().lookup(ProjectSourcesClassPathProvider.class);
        ClassPath[] cp = cpp.getProjectClassPaths(ClassPath.EXECUTE);
        for (ClassPath c : cp) {
            list.add(c);
        }
        SubprojectProvider spp = prj.getLookup().lookup(SubprojectProvider.class);
        if (spp != null) {
            for (Project sub : spp.getSubprojects()) {
                if (parents.contains(sub)) {
                    continue;
                }
                ClassPath c = createCP(sub, parents);
                if (c != null) {
                    list.add(c);
                }
            }
        }
        if (list.size() > 0) {
            return ClassPathSupport.createProxyClassPath(list.toArray(new ClassPath[list.size()]));
        }
        return null;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (classpath == null) {
            return;
        }
        OutputListener list = OutputUtils.matchStackTraceLine(line, classpath);
        if (list != null) {
            visitor.setOutputListener(list);
        }
    }

    public String[] getRegisteredOutputSequences() {
        return EXECGOALS;
    }

    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }

    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
}
