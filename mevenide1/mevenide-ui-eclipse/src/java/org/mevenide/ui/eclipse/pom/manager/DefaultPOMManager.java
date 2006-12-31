/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.util.Tracer;

/**
 * The default implementation of a POMManager. This implementation scans all workspace projects
 * and registers any POMs it finds.
 * 
 * FIXME: A race condition exists when adding a project.
 *      : This listener is called as soon as the project is created and it may
 *      : attempt to create the query context before the Maven files are created.
 *      : In that case, the DefaultQueryContext cannot find all of the files.
 * FIXME: Discard a query context that had an error arrise during its creation.
 * TODO : The query context does not provide all of the files used to create it.
 * TODO : Cache change notifications until delta process is completed. 
 */
public class DefaultPOMManager extends AbstractPOMManager implements IResourceChangeListener, IResourceDeltaVisitor {
    private Set fileToProjectMap;
    private ILocationFinder customLocationFinder;
    private LocationFinderAggregator defaultLocationFinder;

    /**
     * Initializes this manager.
     * @throws CoreException if anything goes wrong
     */
    public void initialize() {
        this.fileToProjectMap = new HashSet();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        IProject[] project = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < project.length; ++i) {
            addProject(project[i]);
        }

        this.customLocationFinder = new PreferenceBasedLocationFinder(Mevenide.getInstance().getCustomPreferenceStore());
        this.defaultLocationFinder = new LocationFinderAggregator(DefaultQueryContext.getNonProjectContextInstance(), this.customLocationFinder);
    }

    public void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        IResource resource = event.getResource();

        switch (event.getType()) {
        case IResourceChangeEvent.POST_CHANGE: {
            IResourceDelta delta = event.getDelta();
            try {
                delta.accept(this);
            } catch (CoreException e) {
                Mevenide.displayError(e.getLocalizedMessage(), e);
            }
            break;
        }

        case IResourceChangeEvent.PRE_CLOSE: {
            projectClosed((IProject)resource);
            break;
        }

        case IResourceChangeEvent.PRE_DELETE: {
            projectRemoved((IProject)resource);
            break;
        }

        case IResourceChangeEvent.PRE_BUILD: break;
        case IResourceChangeEvent.POST_BUILD: break;
        }

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
     */
    public boolean visit(IResourceDelta delta) throws CoreException {
        final IResource resource = delta.getResource();

        // causes the vistor to visit each changed project
        if (resource instanceof IWorkspaceRoot) {
            return true;
        }

        if (resource instanceof IProject) {
            switch (delta.getKind()) {
            case IResourceDelta.ADDED: {
                projectAdded((IProject)resource);
                break;
            }

            case IResourceDelta.CHANGED: {
                if ((delta.getFlags() & IResourceDelta.OPEN) == IResourceDelta.OPEN) {
                    if (((IProject)resource).isOpen()) {
                        projectOpened((IProject)resource);
                    }
                }
                break;
            }

            case IResourceDelta.REMOVED:  break;
            case IResourceDelta.NO_CHANGE: break;
            case IResourceDelta.ADDED_PHANTOM: break;
            case IResourceDelta.REMOVED_PHANTOM: break;
            }

            return true;
        }

        if (resource instanceof IFile) {
            if ((delta.getFlags() & IResourceDelta.CONTENT) == IResourceDelta.CONTENT) {
                if (((IFile)resource).isAccessible()) {
                    fileContentChanged((IFile)resource);
                }
            }

            return false;
        }

        return false;
    }

    private void fileContentChanged(IFile file) {
        if (file != null) {
            if (this.fileToProjectMap.contains(file.getLocation().toFile().getAbsolutePath())) {
                if (Tracer.isDebugging()) {
                    Tracer.trace("project changed: " + file.getProject().getName());
                }

                updateProject(file.getProject());
            }
        }
    }

    private void projectAdded(IProject project) {
        if (project != null) {
            if (Tracer.isDebugging()) {
                Tracer.trace("project added: " + project.getName());
            }
            IQueryContext context = addProject(project);
            if (context != null) {
                File[] file = context.getPOMContext().getProjectFiles();
                for (int i = 0; i < file.length; ++i) {
                    this.fileToProjectMap.add(file[i].getAbsolutePath());
                }
            }
        }
    }

    private void projectRemoved(IProject project) {
        if (project != null) {
            if (Tracer.isDebugging()) {
                Tracer.trace("project removed: " + project.getName());
            }
            IQueryContext context = removeProject(project);
            if (context != null) {
                File[] file = context.getPOMContext().getProjectFiles();
                for (int i = 0; i < file.length; ++i) {
                    this.fileToProjectMap.remove(file[i].getAbsolutePath());
                }
            }
        }
    }

    private void projectOpened(IProject project) {
        if (project != null) {
            if (Tracer.isDebugging()) {
                Tracer.trace("project opened: " + project.getName());
            }
            IQueryContext context = addProject(project);
            if (context != null) {
                File[] file = context.getPOMContext().getProjectFiles();
                for (int i = 0; i < file.length; ++i) {
                    this.fileToProjectMap.add(file[i].getAbsolutePath());
                }
            }
        }
    }

    private void projectClosed(IProject project) {
        if (project != null) {
            if (Tracer.isDebugging()) {
                Tracer.trace("project closed: " + project.getName());
            }
            IQueryContext context = removeProject(project);
            if (context != null) {
                File[] file = context.getPOMContext().getProjectFiles();
                for (int i = 0; i < file.length; ++i) {
                    this.fileToProjectMap.remove(file[i].getAbsolutePath());
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#getCustomLocationFinder()
     */
    public ILocationFinder getCustomLocationFinder() {
        return this.customLocationFinder;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#getDefaultLocationFinder()
     */
    public ILocationFinder getDefaultLocationFinder() {
        return this.defaultLocationFinder;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#forceUpdate(org.eclipse.core.resources.IProject)
     */
    public void forceUpdate(IProject project) {
        super.updateProject(project);
    }

}
