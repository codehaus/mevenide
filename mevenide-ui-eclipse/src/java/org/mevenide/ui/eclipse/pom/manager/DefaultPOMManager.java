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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.Mevenide.PreferenceBasedLocationFinder;

/**
 * The default implementation of a POMManager. This implementation scans all workspace projects
 * and registers any POMs it finds.
 */
public class DefaultPOMManager extends AbstractPOMManager implements IResourceChangeListener {
    private CustomLocationFinder customLocationFinder;
    private LocationFinderAggregator defaultLocationFinder;

    /**
     * Initializes this manager.
     * @throws CoreException if anything goes wrong
     */
    public void initialize() throws CoreException {
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
        String    name     = (resource == null)? "UNKNOWN": resource.getName();

        switch (event.getType()) {
        case IResourceChangeEvent.POST_BUILD: {
            info("post build: " + name);
            break;
        }

        case IResourceChangeEvent.POST_CHANGE: {
//            IResourceDelta delta = event.getDelta();
//            IResource r = delta.getResource();
//            if (r instanceof IWorkspaceRoot) {
//                IResourceDelta[] child = delta.getAffectedChildren(IResourceDelta.CHANGED);
//                for (int i = 0; i < child.length; ++i) {
//                    IResource c = child[i].getResource();
//                    if (c instanceof IProject) {
//                        try {
//                            if (MevenideNature.hasNature((IProject)c)) {
//                                addProject((IProject)c);
//                            } else {
//                                removeProject((IProject)c);
//                            }
//                        } catch (CoreException e) {
//                            Mevenide.displayError("Internal MevenIDE Error", e.getLocalizedMessage(), e);
//                        }
//                    }
//                }
//            }
            info("post change: " + name);
            break;
        }

        case IResourceChangeEvent.PRE_BUILD: {
            info("pre build: " + name);
            break;
        }

        case IResourceChangeEvent.PRE_CLOSE: {
            info("pre close: " + name);
            break;
        }

        case IResourceChangeEvent.PRE_DELETE: {
            info("pre delete: " + name);
            break;
        }

        default: {
            info("Unknown event type.");
            break;
        }
        }

    }

    /**
     * Logs an informational message in the Eclipse log for this plugin.
     * @param message the message to log
     */
    private void info(final String message) {
        if (false) {
            final IStatus status = new Status(IStatus.INFO, Mevenide.PLUGIN_ID, 0, message, null);
            Mevenide.getInstance().getLog().log(status);
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
}
