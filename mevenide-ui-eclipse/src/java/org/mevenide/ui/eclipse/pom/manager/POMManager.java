/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse.pom.manager;

import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.mevenide.context.IQueryContext;

/**
 * Provides a central access point for all POMs in the workspace.
 */
public interface POMManager {

    /**
     * Locates a query context for the given Eclipse project.
     * @param  project the Eclipse project
     * @return the query context for the given project or
     *         <tt>null</tt> if the project does not have
     *         the Maven nature or project is <tt>null</tt>.
     */
    IQueryContext getQueryContext(IProject project);

    /**
     * Locates a query context for the given artifact.
     * @param  groupId the artifact's group
     * @param  artifactId the artifact's id
     * @return the query context for the given artifact or
     *         <tt>null</tt> if the artifact does not exist
     *         in the workspace.
     */
    IQueryContext getQueryContext(String groupId, String artifactId);

    /**
     * Locates a query context for the given artifact.
     * @param  groupId the artifact's group
     * @param  artifactId the artifact's id
     * @return the query context for the given artifact or
     *         <tt>null</tt> if the artifact does not exist
     *         in the workspace.
     */
    IQueryContext getQueryContext(Dependency dependency);
}
