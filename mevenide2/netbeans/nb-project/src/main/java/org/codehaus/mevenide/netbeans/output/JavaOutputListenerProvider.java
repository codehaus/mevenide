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

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.model.Plugin;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;




/**
 * compilation output processing
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class JavaOutputListenerProvider implements OutputProcessor {
    
    private static final String[] JAVAGOALS = new String[] {
        "mojo-execute#compiler:compile",
        "mojo-execute#compiler:testCompile"
    };
    private Pattern failPattern;
    private NbMavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public JavaOutputListenerProvider(NbMavenProject proj) {
		//[javac] required because of forked compilation
        failPattern = failPattern.compile("\\s*(?:\\[javac\\])?\\s*(.*)\\.java\\:\\[([0-9]*),([0-9]*)\\] (.*)");
        project = proj;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String clazz = match.group(1);
                String lineNum = match.group(2);
                String text = match.group(4);
                visitor.setOutputListener(new CompileAnnotation(project, clazz, lineNum, 
                        text), text.indexOf("[deprecation]") < 0);
            }
    }

    public String[] getRegisteredOutputSequences() {
        return JAVAGOALS;
    }

    public void sequenceStart(String sequenceId, OutputVisitor visitor) {
    }

    public void sequenceEnd(String sequenceId, OutputVisitor visitor) {
    }
    
    public void sequenceFail(String sequenceId, OutputVisitor visitor) {
    }
    
}
