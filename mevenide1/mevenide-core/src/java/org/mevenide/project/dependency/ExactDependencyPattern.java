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
package org.mevenide.project.dependency;

import org.apache.maven.project.Dependency;
import org.mevenide.context.IQueryContext;

/**  
 * implementation of IDependencyPattern that requires a complete match
 * in dependency's artifactId, groupId and version. 
 *
 * @author Milos Kleint
 * 
 */
public class ExactDependencyPattern implements IDependencyPattern {
	
    private String artifact;
    private String group;
    private String version;
    
    public ExactDependencyPattern(String artifactId, String groupId, String currentversion) {
        version = currentversion;
        artifact = artifactId;
        group = groupId;
    }
    
    public boolean matches(Dependency dependency, IQueryContext context) {
        boolean matches = true;
        matches = matches && matchOne(artifact, dependency.getArtifactId());
        matches = matches && matchOne(group, dependency.getGroupId());
        matches = matches && matchOne(version, dependency.getVersion());
        return matches;
    }
    
    protected boolean matchOne(String first, String second) {
        if     ((first == null && second != null) ||
                (first != null && 
                !first.equals(second))) {
            return false;
        }
        return true;
    }

}