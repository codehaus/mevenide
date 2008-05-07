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

import java.util.HashSet;
import java.util.Set;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessor;
import org.codehaus.mevenide.netbeans.api.output.OutputProcessorFactory;
import org.netbeans.api.project.Project;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ApisupportOutputProcessorFactory implements OutputProcessorFactory {
    
    /** Creates a new instance of DefaultOutputProcessor */
    public ApisupportOutputProcessorFactory() {
    }

    public Set<OutputProcessor> createProcessorsSet(Project project) {
        Set<OutputProcessor> toReturn = new HashSet<OutputProcessor>();
        if (project != null) {
            toReturn.add(new IDEOutputListenerProvider(project));
        }
        return toReturn;
    }
    
}
