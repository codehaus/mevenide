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

import org.mevenide.project.io.IContentProvider;

/**  
 * interface used in DependencyMatcher to replace the current dependency content 
 * with the changed/updated  one.
 *
 * @author Milos Kleint
 */
public interface IDependencyReplacer {
  
    /**
     * this method allows the replacer to influence the content provider's data structures
     * when writing down the pom file.
     * @param original content provider for the given dependency
     * @return either the original if the dependency doesn't match or a replacing one.
     */
    IContentProvider replace(IContentProvider original);
}