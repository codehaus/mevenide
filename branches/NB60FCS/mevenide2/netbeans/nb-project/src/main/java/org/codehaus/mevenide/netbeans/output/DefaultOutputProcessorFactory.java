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

import java.util.HashSet;
import java.util.Set;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessorFactory;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DefaultOutputProcessorFactory implements OutputProcessorFactory {
    
    /** Creates a new instance of DefaultOutputProcessor */
    public DefaultOutputProcessorFactory() {
    }

    public Set<OutputProcessor> createProcessorsSet(NbMavenProject project) {
        Set<OutputProcessor> toReturn = new HashSet<OutputProcessor>();
        toReturn.add(new GlobalOutputProcessor());
        if (project != null) {
            toReturn.add(new JavaOutputListenerProvider());
            toReturn.add(new TestOutputListenerProvider());
            toReturn.add(new JavadocOutputProcessor());
            toReturn.add(new SiteOutputProcessor(project));
            toReturn.add(new ExecPluginOutputListenerProvider(project));
            toReturn.add(new DependencyAnalyzeOutputProcessor(project));
        }
        return toReturn;
    }
    
}