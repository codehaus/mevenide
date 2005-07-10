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
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.util.Tracer;

/**
 * The default implementation of a POMManager. This implementation scans all workspace projects
 * and registers any POMs it finds.
 */
public class DefaultPOMManager extends AbstractPOMManager implements IResourceChangeListener, IResourceDeltaVisitor {
    private Set fileToProjectMap;
    private CustomLocationFinder customLocationFinder;
    private LocationFinderAggregator defaultLocationFinder;

    /**
     * Initializes this manager.
     * @throws CoreException if anything goes wrong
     */
    public void initialize() throws CoreException {
        this.fileToProjectMap = new HashSet();

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        IProject[] project = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < project.length; ++i) {
            addProject(project[i]);
        }

        this.customLocationFinder = new PreferenceBasedLocationFinder(Mevenide.getInstance().getCustomPreferenceStore());
        this.defaultLocationFinder = new LocationFinderAggregator(DefaultQueryContext.getNonProjectContextInstance());
        this.defaultLocationFinder.setCustomLocationFinder(this.customLocationFinder);
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
//                } else {
//                    String name = (resource == null)? "UNKNOWN": resource.getName();
//                    String flag = deltaFlag2String(delta.getFlags());
//                    if (Tracer.isDebugging()) {
//                        Tracer.trace("post change[CHANGED]: " + name + " {" + flag + "}");
//                    }
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
            } else {
                String name = (resource == null)? "UNKNOWN": resource.getName();
                String kind = deltaKind2String(delta.getKind());
                String flag = deltaFlag2String(delta.getFlags());
                if (Tracer.isDebugging()) {
                    Tracer.trace("post change[" + kind + "]: " + name + " {" + flag + "}");
                }
            }

            return false;
        }

        String name = (resource == null)? "UNKNOWN": resource.getName();
        String kind = deltaKind2String(delta.getKind());
        String flag = deltaFlag2String(delta.getFlags());
        if (Tracer.isDebugging()) {
            Tracer.trace("post change[" + kind + "]: " + name + " {" + flag + "}");
        }

        return false;
    }

    /**
     * TODO: Describe what fileContentChanged does.
     * @param file
     */
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
    public CustomLocationFinder getCustomLocationFinder() {
        return this.customLocationFinder;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#getDefaultLocationFinder()
     */
    public ILocationFinder getDefaultLocationFinder() {
        return this.defaultLocationFinder;
    }

    private static final String deltaKind2String(final int kind) {
        switch (kind) {
            case IResourceDelta.NO_CHANGE:       return "NO_CHANGE";
            case IResourceDelta.ADDED:           return "ADDED";
            case IResourceDelta.ADDED_PHANTOM:   return "ADDED_PHANTOM";
            case IResourceDelta.CHANGED:         return "CHANGED";
            case IResourceDelta.REMOVED:         return "REMOVED";
            case IResourceDelta.REMOVED_PHANTOM: return "REMOVED_PHANTOM";
            default:                             return "UNKNOWN";
        }
    }

    private static final String deltaFlag2String(final int flag) {
        StringBuffer buffer = new StringBuffer();

        if (flag == IResourceDelta.NO_CHANGE) buffer.append("|NO_CHANGE");
        if ((flag & IResourceDelta.CONTENT) == IResourceDelta.CONTENT) buffer.append("|CONTENT");
        if ((flag & IResourceDelta.DESCRIPTION) == IResourceDelta.DESCRIPTION) buffer.append("|DESCRIPTION");
        if ((flag & IResourceDelta.ENCODING) == IResourceDelta.ENCODING) buffer.append("|ENCODING");
        if ((flag & IResourceDelta.MARKERS) == IResourceDelta.MARKERS) buffer.append("|MARKERS");
        if ((flag & IResourceDelta.MOVED_FROM) == IResourceDelta.MOVED_FROM) buffer.append("|MOVED_FROM");
        if ((flag & IResourceDelta.MOVED_TO) == IResourceDelta.MOVED_TO) buffer.append("|MOVED_TO");
        if ((flag & IResourceDelta.OPEN) == IResourceDelta.OPEN) buffer.append("|OPEN");
        if ((flag & IResourceDelta.REPLACED) == IResourceDelta.REPLACED) buffer.append("|REPLACED");
        if ((flag & IResourceDelta.SYNC) == IResourceDelta.SYNC) buffer.append("|SYNC");
        if ((flag & IResourceDelta.TYPE) == IResourceDelta.TYPE) buffer.append("|TYPE");

        return buffer.substring(1);
    }
}
