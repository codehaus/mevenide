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

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.mevenide.netbeans.project.MavenProject;

/**
 * factory creating different sets of processors
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public final class OutputProcessorFactory {
    
    private static OutputProcessorFactory instance;
    
    /** Creates a new instance of OutputProcessorFactory */
    private OutputProcessorFactory() {
    }
    
    public static OutputProcessorFactory getDefault() {
        if (instance == null) {
            instance = new OutputProcessorFactory();
        }
        return instance;
    }
    
    
    public Set createDetaultProcessorsSet(MavenProject project) {
        HashSet processors = new HashSet();
        processors.add(new TestOutputListenerProvider(project));
        processors.add(new JavaOutputListenerProvider(project));
        processors.add(new AnnouncementOutputListenerProvider(project));
        processors.add(new PmdOutputListenerProvider(project));
        processors.add(new CheckstyleOutputListenerProvider(project));
        String javadoc = project.getPropertyResolver().getResolvedValue("maven.javadoc.destdir"); //NOI18N
        if (javadoc != null) {
            File fil = new File(javadoc, "index.html"); //NOI18N
            String[] goals = new String[] { "maven-javadoc-plugin:report:" };
            processors.add(new ViewInBrowseProcessor(goals,fil, "Do you want to view the javadoc in browser?", 10));
        }
        String docs = project.getPropertyResolver().getResolvedValue("maven.docs.dest"); //NOI18N
        if (docs != null) {
            File fil = new File(docs, "index.html"); //NOI18N
            String[] goals = new String[] { "site:" };
            processors.add(new ViewInBrowseProcessor(goals, fil, "Do you want to view the generated site in browser?", 100));
        }
        return processors;
    }
}
