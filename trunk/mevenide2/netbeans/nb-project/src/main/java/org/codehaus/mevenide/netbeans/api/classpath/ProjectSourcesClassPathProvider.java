/*
 *  Copyright 2008 mkleint.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.codehaus.mevenide.netbeans.api.classpath;

import org.netbeans.api.java.classpath.ClassPath;

/**
 * Additional useful classpath related methods. Resides in project lookup.
 * @author mkleint
 */
public interface ProjectSourcesClassPathProvider {
    /**
     * Returns the given type of the classpath for the project sources
     * (i.e., excluding tests roots).
     */
    public ClassPath getProjectSourcesClassPath(String type);
    
    public ClassPath[] getProjectClassPaths(String type);
    
}