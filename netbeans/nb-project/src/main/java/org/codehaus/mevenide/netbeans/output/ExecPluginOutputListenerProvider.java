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

import java.util.regex.Pattern;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputUtils;
import org.codehaus.mevenide.netbeans.api.output.OutputVisitor;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.openide.windows.OutputListener;




/**
 * exec plugin output processing, just handle stacktraces.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class ExecPluginOutputListenerProvider implements OutputProcessor {
    
    private static final String[] EXECGOALS = new String[] {
        "mojo-execute#exec:exec", //NOI18N
        "mojo-execute#exec:java" //NOI18N
    };
    private NbMavenProject project;
    
    /** Creates a new instance of ExecPluginOutputListenerProvider */
    public ExecPluginOutputListenerProvider(NbMavenProject proj) {
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
