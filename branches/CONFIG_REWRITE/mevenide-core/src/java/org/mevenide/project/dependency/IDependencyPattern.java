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
 * used by DependencyMatcher to find the projects that contain dependencies 
 * that match this pattern.
 * @author Milos Kleint
 * 
 */
public interface IDependencyPattern {
    /**
     * return true if the implementor of this class can match the given dependency in the
     * given context.
     */
    boolean matches(Dependency dependency, IQueryContext context);
}