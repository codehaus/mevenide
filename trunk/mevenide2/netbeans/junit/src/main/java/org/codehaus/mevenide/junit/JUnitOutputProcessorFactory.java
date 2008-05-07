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

package org.codehaus.mevenide.junit;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import org.codehaus.mevenide.netbeans.api.output.ContextOutputProcessorFactory;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.netbeans.api.project.Project;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class JUnitOutputProcessorFactory implements ContextOutputProcessorFactory {
    
    /** Creates a new instance of DefaultOutputProcessor */
    public JUnitOutputProcessorFactory() {
    }

    public Set createProcessorsSet(Project project) {
        return Collections.<OutputProcessor>emptySet();
    }

    public Set<OutputProcessor> createProcessorsSet(Project project, RunConfig config) {
        if (config.getGoals().contains("test") || config.getGoals().contains("surefire:test")) { //NOI18N
            Set<OutputProcessor> toReturn = new HashSet<OutputProcessor>();
            if (project != null) {
                toReturn.add(new JUnitOutputListenerProvider(project));
            }
            return toReturn;
        }
        return Collections.<OutputProcessor>emptySet();
    }
    
}
