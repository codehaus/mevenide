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
package org.mevenide.netbeans.project.output;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.netbeans.project.MavenProject;




/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class JavaOutputListenerProvider extends AbstractOutputProcessor {
    private static final Log logger = LogFactory.getLog(JavaOutputListenerProvider.class);
    
    private static final String[] JAVAGOALS = new String[] {
        "java:compile:",
        "test:compile:"
    };
    private Pattern failPattern;
    private MavenProject project;
    
    /** Creates a new instance of TestOutputListenerProvider */
    public JavaOutputListenerProvider(MavenProject proj) {
        failPattern = failPattern.compile("(.*)\\.java\\:([0-9]*)\\: (.*)");
        project = proj;
    }
    
    protected String[] getWatchedGoals() {
        return JAVAGOALS;
    }
    
    public void processLine(String line, OutputVisitor visitor) {
        if (isInWatchedGoals(line)) {
            Matcher match = failPattern.matcher(line);
            if (match.matches()) {
                String clazz = match.group(1);
                String lineNum = match.group(2);
                String text = match.group(3);
                visitor.setOutputListener(new CompileAnnotation(project, clazz, lineNum, 
                        text), !text.startsWith("warning"));
            }
        }
    }
    
}
