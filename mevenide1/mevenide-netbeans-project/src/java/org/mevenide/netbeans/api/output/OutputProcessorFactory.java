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

package org.mevenide.netbeans.api.output;

import java.util.Set;
import org.mevenide.netbeans.api.project.MavenProject;

/**
 * Factory of the OutputProcessors for given project, each build
 * asks this method again. Factory classes should be registered in
 * default <code>Lookup</code> (META-INF/services/org.mevenide.netbeans.api.output.OutputProcessorFactory)
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public interface OutputProcessorFactory {
    /**
     * returns a Set of <code>OutputProcessor</code> instances or empty set, never null.
     *
     */
    public Set createProcessorsSet(MavenProject project);
}
