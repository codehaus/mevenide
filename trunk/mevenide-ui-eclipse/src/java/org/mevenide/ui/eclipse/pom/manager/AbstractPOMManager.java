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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.IQueryErrorCallback;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * Provides the basic behavior of a POMManager.
 */
public abstract class AbstractPOMManager implements POMManager {
    private List listeners = new ArrayList(1);

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

    protected final IQueryContext addProject(IProject project) {
        if (project != null && project.isOpen()) {
            String location = project.getLocation().toOSString();
            if (!this.projectMap.containsKey(location)) {
                IQueryContext context = createQueryContext(location);
                if (addQueryContext(location, context)) {
                    fireProjectChange(new POMChangeEventImpl(context, POMChangeEvent.POM_ADDED, project));
                    return context;
                }
            }
        }

        return null;
    }

    protected final IQueryContext removeProject(IProject project) {
        if (project != null && project.isOpen()) {
            String location = project.getLocation().toOSString();
            IQueryContext context = removeQueryContext(location);
            if (context != null) {
                fireProjectChange(new POMChangeEventImpl(context, POMChangeEvent.POM_REMOVED, project));
                return context;
            }
        }

        return null;
    }

    protected final void updateProject(IProject project) {
        if (project != null && project.isOpen()) {
            IQueryContext context = this.getQueryContext(project);
            if (context != null) {
                context.getPOMContext().getFinalProject();
                fireProjectChange(new POMChangeEventImpl(context, POMChangeEvent.POM_CHANGED, project));
            }
        }
    }

    /**
     * Creates a new query context for the POM at the given location.
     * @param location the absolute path to the folder that contains the POM.
     * @return a newly created query context.
     */
    private IQueryContext createQueryContext(String location) {
        return new DefaultQueryContext(new File(location), this.callbackHandler);
    }

    /**
     * Registers the given context with this manager.
     * @param location the absolute path to the Eclipse project that contains the POM.
     * @param context the context representing the Maven POM.
     */
    private boolean addQueryContext(String location, IQueryContext context) {
        if (!this.projectMap.containsKey(location)) {
            this.artifactMap.put(getProjectKey(context), context);
            this.projectMap.put(location, context);
            return true;
        }
        return false;
    }

    private IQueryContext removeQueryContext(String location) {
        IQueryContext context = (IQueryContext)projectMap.get(location);
        if (context != null) {
            this.artifactMap.remove(getProjectKey(context));
            this.projectMap.remove(location);
        }

        return context;
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

    private IQueryErrorCallback callbackHandler = new IQueryErrorCallback() {

        /* (non-Javadoc)
         * @see org.mevenide.context.IQueryErrorCallback#handleError(int, java.lang.Exception)
         */
        public void handleError(int errorNumber, Exception exception) {
            Mevenide.displayError(decode(errorNumber), exception);
        }

        /* (non-Javadoc)
         * @see org.mevenide.context.IQueryErrorCallback#discardError(int)
         */
        public void discardError(int errorNumber) {
        }

        private final String decode(int errorNumber) {
            switch (errorNumber) {
            case IQueryErrorCallback.ERROR_UNPARSABLE_POM: return "Unable to parse Maven POM file.";
            case IQueryErrorCallback.ERROR_UNREADABLE_PROP_FILE: return "Unable to read Maven POM file.";
            case IQueryErrorCallback.ERROR_CANNOT_FIND_POM: return "Cannot find Maven POM file.";
            case IQueryErrorCallback.ERROR_CANNOT_FIND_PARENT_POM: return "Cannot find Maven POM's parent file.";
            default: return "Unknown error received from mevenide-config: " + errorNumber;
            }
        }
    };

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#addListener(org.mevenide.ui.eclipse.pom.manager.POMChangeListener)
     */
    public void addListener(POMChangeListener listener) {
        synchronized (listeners) {
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.pom.manager.POMManager#removeListener(org.mevenide.ui.eclipse.pom.manager.POMChangeListener)
     */
    public void removeListener(POMChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Fires a POM change event to all registered listeners. Only
     * listeners registered at the time this method is called are notified.
     * Listener notification makes use of an ISafeRunnable to ensure that
     * client exceptions do not effect the notification to other clients.
     */
    protected void fireProjectChange(final POMChangeEvent e) {
        POMChangeListener[] listener;
        // Copy the listener list so we're not calling client code while synchronized
        synchronized (this.listeners) {
            listener = (POMChangeListener[]) this.listeners.toArray(new POMChangeListener[this.listeners.size()]);
        }
        // Notify the listeners safely so all will receive notification
        for (int i = 0; i < listener.length; ++i) {
            final POMChangeListener l = listener[i];

            Platform.run(new ISafeRunnable() {
                public void handleException(Throwable exception) {
                    // don't log the exception....it is already being logged in
                    // Platform#run
                }
                public void run() throws Exception {
                    l.pomChanged(e);
                }
            });
        }
    }
}
