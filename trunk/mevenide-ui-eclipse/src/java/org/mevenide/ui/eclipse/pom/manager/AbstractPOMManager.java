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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.nature.MevenideNature;

/**
 * Provides the basic behavior of a POMManager.
 */
public abstract class AbstractPOMManager implements POMManager {

    /**
     * Holds the mapping between a project path and a query context.
     */
    private Map projectMap = new HashMap();

    /**
     * Halds the mapping between an artifact and a query context.
     */
    private Map artifactMap = new HashMap();

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#getQueryContext(org.eclipse.core.resources.IProject)
     */
    public IQueryContext getQueryContext(IProject project) {
        IQueryContext result = null;

        if (project != null) {
            String location = project.getLocation().toOSString();
            result = (IQueryContext) this.projectMap.get(location);
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#getQueryContext(java.lang.String, java.lang.String)
     */
    public IQueryContext getQueryContext(String groupId, String artifactId) {
        final String key = getProjectKey(groupId, artifactId);
        return (IQueryContext)this.artifactMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#getQueryContext(org.apache.maven.project.Dependency)
     */
    public IQueryContext getQueryContext(Dependency dependency) {
        if (dependency != null) {
            final String groupId = dependency.getGroupId();
            final String artifactId = dependency.getArtifactId();
            return this.getQueryContext(groupId, artifactId);
        }

        return null;
    }

    protected final void addProject(IProject project) throws CoreException {
        if (project != null && MevenideNature.hasNature(project)) {
            String location = project.getLocation().toOSString();
            if (!this.projectMap.containsKey(location)) {
                IQueryContext context = createQueryContext(location);
                addQueryContext(location, context);
            }
        }
    }

    protected final void removeProject(IProject project) throws CoreException {
        if (project != null && !MevenideNature.hasNature(project)) {
            String location = project.getLocation().toOSString();
            removeQueryContext(location);
        }
    }

    /**
     * Creates a new query context for the POM at the given location.
     * @param location the absolute path to the folder that contains the POM.
     * @return a newly created query context.
     */
    protected IQueryContext createQueryContext(String location) {
        return new DefaultQueryContext(new File(location));
    }

    /**
     * Registers the given context with this manager.
     * @param location the absolute path to the Eclipse project that contains the POM.
     * @param context the context representing the Maven POM.
     */
    protected void addQueryContext(String location, IQueryContext context) {
        if (!this.projectMap.containsKey(location)) {
            this.artifactMap.put(getProjectKey(context), context);
            this.projectMap.put(location, context);
        }
    }

    protected void removeQueryContext(String location) {
        IQueryContext context = (IQueryContext)projectMap.get(location);
        if (context != null) {
            this.artifactMap.remove(getProjectKey(context));
            this.projectMap.remove(location);
        }
    }

    /**
     * Constructs a string suitable for use as a key.
     * @param group the group
     * @param artifact the artifact
     * @return the key
     */
    private static final String getProjectKey(final String group, final String artifact) {
        return group + ":" + artifact;
    }

    private static final String getProjectKey(final Project project) {
        return getProjectKey(project.getGroupId(), project.getArtifactId());
    }

    private static final String getProjectKey(final IQueryContext context) {
        return getProjectKey(context.getPOMContext().getFinalProject());
    }
}
