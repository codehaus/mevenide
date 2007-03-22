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
package org.codehaus.mevenide.netbeans.output;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputUtils;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.OutputListener;




/**
 * exec plugin output processing, just handle stacktraces.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ExecPluginOutputListenerProvider implements OutputProcessor {
    
    private static final String[] EXECGOALS = new String[] {
        "mojo-execute#exec:exec",
        "mojo-execute#exec:java"
    };
    private Pattern failPattern;
    private NbMavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public ExecPluginOutputListenerProvider(NbMavenProject proj) {
        //[javac] required because of forked compilation
        //DOTALL seems to fix MEVENIDE-455 on windows. one of the characters seems to be a some kind of newline and that's why the line doesnt' get matched otherwise.
        failPattern = failPattern.compile("\\s*(?:\\[javac\\])?\\s*(.*)\\.java\\:\\[([0-9]*),([0-9]*)\\] (.*)", Pattern.DOTALL);
        project = proj;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        ClassPathProviderImpl cpp = project.getLookup().lookup(ClassPathProviderImpl.class);
        ClassPath[] cp = cpp.getProjectClassPaths(ClassPath.EXECUTE);
        OutputListener list = OutputUtils.matchStackTraceLine(line, ClassPathSupport.createProxyClassPath(cp));
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
