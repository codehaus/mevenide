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

package org.mevenide.ui.eclipse.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.apache.maven.repository.Artifact;
import org.apache.maven.repository.DefaultArtifactFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.pom.manager.POMManager;
import org.mevenide.util.ResolverUtils;

/**
 * TODO: Describe what MavenClasspathContainer represents.
 */
public class MavenClasspathContainer implements IClasspathContainer {
    public static final String ID = Mevenide.PLUGIN_ID + ".autosync.dependencies"; //$NON-NLS-1$

    private Project project;
    private Map path2DependencyMap = new LinkedHashMap();
    private Map dependency2PathMap = new LinkedHashMap();

    /**
     * Initializes a new instance of MavenClasspathContainer.
     */
    public MavenClasspathContainer(IQueryContext context) {
        super();
        this.project = context.getPOMContext().getFinalProject();
        initialize();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.IClasspathContainer#getClasspathEntries()
     */
    public IClasspathEntry[] getClasspathEntries() {
        final Collection entries = this.path2DependencyMap.keySet();
        return (IClasspathEntry[]) entries.toArray(new IClasspathEntry[entries.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.IClasspathContainer#getDescription()
     */
    public String getDescription() {
        return Mevenide.getResourceString("classpath.container.description"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.IClasspathContainer#getKind()
     */
    public int getKind() {
        return K_APPLICATION;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.IClasspathContainer#getPath()
     */
    public IPath getPath() {
        return new Path(ID);
    }

    private void initialize() {
        if (this.project != null) {
            Iterator iterator = this.project.getDependencies().iterator();
            while (iterator.hasNext()) {
                addDependency((Dependency) iterator.next());
            }
        }
    }

    public List getDependencies() {
        return new ArrayList(this.dependency2PathMap.keySet());
    }

    public void setDependencies(List dependencies) {
        if (dependencies == null) {
            dependencies = Collections.EMPTY_LIST;
        }

        this.dependency2PathMap.clear();
        this.path2DependencyMap.clear();

        Iterator iterator = dependencies.iterator();
        while (iterator.hasNext()) {
            addDependency((Dependency) iterator.next());
        }
    }

    /**
     * TODO: Describe what addDependency does.
     * @param project
     * @param dependency
     */
    private void addDependency(Dependency dependency) {
        if (dependency != null) {
            final String type = dependency.getType();
            if ("jar".equals(type) || "ejb".equals(type)) {
                IClasspathEntry classpath = buildClasspath(dependency);
                this.path2DependencyMap.put(classpath, dependency);
                this.dependency2PathMap.put(dependency, classpath);
            }
        }
    }

    /**
     * TODO: Describe what buildClasspath does.
     * @param dependency
     * @return
     */
    public IClasspathEntry buildClasspath(Dependency dependency) {

        // use a project from the workspace if available
        IClasspathEntry classpath = findWorkspaceEntry(dependency);
        if (classpath == null) {
            Artifact artifact = DefaultArtifactFactory.createArtifact(dependency);
            String artifactLocation = ResolverUtils.getInstance().resolve(this.project, artifact.getPath());
            IPath artifactPath = getRepositoryPath().append(artifactLocation);
            IPath groupPath = artifactPath.removeLastSegments(2); // move up two levels
            IPath distributionsPath = groupPath.append("distributions");
            IPath sourcePath = distributionsPath.append(dependency.getArtifactId() + "-" + dependency.getVersion() + "-src.zip");
            if (!sourcePath.toFile().exists()) sourcePath = null;
            classpath = JavaCore.newLibraryEntry(artifactPath, sourcePath, null);
        }

        return classpath;
    }

    /**
     * TODO: Describe what findWorkspaceEntry does.
     * @param dependency
     * @return
     */
    private static final IClasspathEntry findWorkspaceEntry(final Dependency dependency) {
        IClasspathEntry result = null;

        if (dependency != null) {
            final String     groupId    = dependency.getGroupId();
            final String     artifactId = dependency.getArtifactId();
            final POMManager pomManager = Mevenide.getInstance().getPOMManager();

            final IQueryContext queryContext = pomManager.getQueryContext(groupId, artifactId);
            if (queryContext != null) {
                File projectDirectory = queryContext.getProjectDirectory();
                IPath projectPath = makeWorkspacePath(projectDirectory);
                result = JavaCore.newProjectEntry(projectPath);
            }
        }

        return result;
    }

    /**
     * Constructs a new workspace path from the file specifier. The specifier
     * must represent a valid file system path within the workspace.
     *  
     * @param file
     * @return
     */
    private static final IPath makeWorkspacePath(final File file) {
        IPath path = null;
        IPath root = ResourcesPlugin.getWorkspace().getRoot().getLocation();

        try {
            path = Path.fromOSString(file.getCanonicalPath());

            int cnt = root.matchingFirstSegments(path);
            path = path.removeFirstSegments(cnt).makeAbsolute().setDevice(null);
        } catch (IOException e) {
            Mevenide.displayError("Internal MevenIDE Error", e.getLocalizedMessage(), e);
        }

        return path;
    }

    /**
     * TODO: Describe what getRepositoryPath does.
     * @return
     */
    private IPath getRepositoryPath() {
        IPath mavenRepo = JavaCore.getClasspathVariable("MAVEN_REPO");
        if (mavenRepo == null) mavenRepo = new Path("");
        return mavenRepo;
    }

    public  IClasspathEntry getClasspathEntry(Dependency dependency) {
        return (IClasspathEntry) this.dependency2PathMap.get(dependency);
    }

}
