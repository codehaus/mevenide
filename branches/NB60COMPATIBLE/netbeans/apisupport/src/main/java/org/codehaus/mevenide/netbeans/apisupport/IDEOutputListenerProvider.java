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
package org.codehaus.mevenide.netbeans.apisupport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputUtils;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
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
        "mojo-execute#nbm:run-ide",
        "mojo-execute#nbm:run-platform"
    };
    private Pattern failPattern;
    private NbMavenProject project;
    private ClassPath classpath;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public IDEOutputListenerProvider(NbMavenProject proj) {
        //[javac] required because of forked compilation
        //DOTALL seems to fix MEVENIDE-455 on windows. one of the characters seems to be a some kind of newline and that's why the line doesnt' get matched otherwise.
        failPattern = failPattern.compile("\\s*(?:\\[javac\\])?\\s*(.*)\\.java\\:\\[([0-9]*),([0-9]*)\\] (.*)", Pattern.DOTALL);
        project = proj;
        classpath = createCP(project);
    }
    
    private ClassPath createCP(Project prj) {
        List<ClassPath> list = new ArrayList<ClassPath>();
        ClassPathProviderImpl cpp = prj.getLookup().lookup(ClassPathProviderImpl.class);
        ClassPath[] cp = cpp.getProjectClassPaths(ClassPath.EXECUTE);
        for (ClassPath c : cp) {
            list.add(c);
        }
        SubprojectProvider spp = prj.getLookup().lookup(SubprojectProvider.class);
        if (spp != null) {
            for (Project sub : spp.getSubprojects()) {
                ClassPath c = createCP(sub);
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
