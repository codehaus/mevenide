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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.nature.MevenideNature;
import org.mevenide.ui.eclipse.pom.manager.POMChangeEvent;
import org.mevenide.ui.eclipse.pom.manager.POMChangeListener;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.util.Tracer;

/**
 * Manages the classpaths for all Maven enabled projects.
 */
public class MavenClasspathManager implements ClasspathManager {
    private static final IPath CONTAINER_PATH = new Path(MavenClasspathContainer.ID);
    private static final IPath MAVEN_REPO = JavaCore.getClasspathVariable("MAVEN_REPO");
    private static final IPath MAVEN_REPO_ROOT = new Path("MAVEN_REPO");

    private static final String AUTOSYNC_ENABLED = MevenidePreferenceKeys.AUTOSYNC_ENABLED;

    private boolean autosyncEnabled = false;

    public void initialize() {
        IPreferenceStore preferenceStore = Mevenide.getInstance().getPreferenceStore();
        preferenceStore.addPropertyChangeListener(this.propertyChangeListener);
        this.autosyncEnabled = preferenceStore.getBoolean(AUTOSYNC_ENABLED);
        Mevenide.getInstance().getPOMManager().addListener(this.pomChangeListener);
    }

    public void dispose() {
        Mevenide.getInstance().getPOMManager().removeListener(this.pomChangeListener);
        IPreferenceStore preferenceStore = Mevenide.getInstance().getPreferenceStore();
        preferenceStore.removePropertyChangeListener(this.propertyChangeListener);
    }

    private IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent event) {
            if (event != null && AUTOSYNC_ENABLED.equals(event.getProperty())) {
                boolean newValue = ((Boolean)event.getNewValue()).booleanValue();
                if (MavenClasspathManager.this.autosyncEnabled != newValue) {
                    MavenClasspathManager.this.autosyncEnabled = newValue;
    
                    if (Tracer.isDebugging()) {
                        boolean oldValue = ((Boolean)event.getOldValue()).booleanValue();
                        final String msg = AUTOSYNC_ENABLED + ": old = " + oldValue + " new = " + newValue;
                        Tracer.trace(msg);
                    }
        
                    if (newValue) {
                        handleAutosyncEnable();
                    } else {
                        handleAutosyncDisable();
                    }
                }
            }
        }
    };

    private POMChangeListener pomChangeListener = new POMChangeListener() {
        public void pomChanged(POMChangeEvent e) {
            if (MavenClasspathManager.this.autosyncEnabled && e != null && e.getProject() != null && e.getQueryContext() != null) {
                switch (e.getFlags()) {
                case POMChangeEvent.POM_ADDED:   pomAdded(e.getProject(), e.getQueryContext());   break;
                case POMChangeEvent.POM_REMOVED: pomRemoved(e.getProject(), e.getQueryContext()); break;
                case POMChangeEvent.POM_CHANGED: pomUpdated(e.getProject(), e.getQueryContext()); break;
                }
            }
        }
    };

    /**
     * TODO: Describe what pomAdded does.
     * <p>
     * When a POM is added to the workspace, add a Maven classpath container.
     * Then visit all other projects to see if any depend on the artifact
     * created by this POM. Each dependent project should then be updated
     * to refer to this project instead of the artifact in the local Maven
     * repository.
     * </p>
     * 
     * @param eclipseProject
     * @param context
     */
    private void pomAdded(IProject eclipseProject, IQueryContext context) {
        if (this.autosyncEnabled) {
            IProject[] referer = eclipseProject.getReferencingProjects();
            for (int i = 0; i < referer.length; ++i) {
                if (Tracer.isDebugging()) {
                    Tracer.trace("Added classpath entry to " + referer[i].getName() + ".");
                }

                IJavaProject javaProject = JavaCore.create(referer[i]);
                if (javaProject != null) {
                    try {
                        initializeClasspathContainer(CONTAINER_PATH, javaProject);
                    } catch (CoreException e) {
                        final String msg = "Unable to redirect " + referer[i].getName() + " to workspace project.";
                        Mevenide.displayError(msg, e);
                    }
                }
            }
        }
    }

    /**
     * TODO: Describe what pomRemoved does.
     * <p>
     * When a POM is removed from the workspace, visit all other projects to
     * see if any depend on this project. Each dependent project should then
     * be updated to refer to the artifact in the local Maven repository instead
     * of this project.
     * </p>
     * 
     * @param eclipseProject
     * @param context
     */
    private void pomRemoved(final IProject eclipseProject, final IQueryContext context) {
        if (this.autosyncEnabled) {
            IProject[] referer = eclipseProject.getReferencingProjects();
            for (int i = 0; i < referer.length; ++i) {
                if (Tracer.isDebugging()) {
                    Tracer.trace("Removed classpath entry from " + referer[i].getName() + ".");
                }

                IJavaProject javaProject = JavaCore.create(referer[i]);
                if (javaProject != null) {
                    try {
                        initializeClasspathContainer(CONTAINER_PATH, javaProject);
                    } catch (CoreException e) {
                        final String msg = "Unable to redirect " + referer[i].getName() + " to local Maven repository.";
                        Mevenide.displayError(msg, e);
                    }
                }
            }
        }
    }

    /**
     * TODO: Describe what pomUpdated does.
     * <p>
     * When a POM is modified and the modification impacts a classpath related
     * entry, then recreate the Maven classpath container. If the name of the
     * artifact changed then update all dependent projects to either refer to
     * the artifact in the local Maven repository or ask the user if he wishes
     * to update all dependent POMs to depend on the new artifact name.
     * </p>
     * 
     * @param eclipseProject
     * @param context
     */
    private void pomUpdated(IProject eclipseProject, IQueryContext context) {
        if (this.autosyncEnabled) {
            IProject[] referer = eclipseProject.getReferencingProjects();
            for (int i = 0; i < referer.length; ++i) {
                if (Tracer.isDebugging()) {
                    Tracer.trace("Update classpath entry in " + referer[i].getName() + ".");
                }

                IJavaProject javaProject = JavaCore.create(eclipseProject);
                if (javaProject != null) {
                    try {
                        initializeClasspathContainer(CONTAINER_PATH, javaProject);
                    } catch (CoreException e) {
                        final String msg = "Unable to update classpath for " + referer[i].getName() + ".";
                        Mevenide.displayError(msg, e);
                    }
                }
            }
        }
    }

    private void handleAutosyncEnable() {

        IProject[] project = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < project.length; ++i) {
            try {
                if (project[i].hasNature(JavaCore.NATURE_ID)) {
                    IJavaProject javaProject = JavaCore.create(project[i]);
                    if (javaProject != null && MevenideNature.hasNature(project[i])) {
                        addClasspathContainer(CONTAINER_PATH, javaProject);
                    }
                }
            } catch (CoreException e) {
                final String msg = "Unable to initialize the Maven classpath container " + CONTAINER_PATH + ".";
                Mevenide.displayError(msg, e);
            }
        }

    }

    private void handleAutosyncDisable() {

        IProject[] project = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (int i = 0; i < project.length; ++i) {
            try {
                if (project[i].hasNature(JavaCore.NATURE_ID)) {
                    IJavaProject javaProject = JavaCore.create(project[i]);
                    if (javaProject != null) {
                        removeClasspathContainer(javaProject);
                    }
                }
            } catch (CoreException e) {
                final String msg = "Unable to remove the Maven classpath container " + CONTAINER_PATH + ".";
                Mevenide.displayError(msg, e);
            }
        }

    }

    /**
     * Adds a Maven classpath container to the given Java project. If the
     * project's classpath contains any entries that would be duplicated
     * within the container then they are removed. If the project already
     * has a Maven classpath container then this method does nothing.
     * <p><strong>Do not call this method from within the container's initializer.</strong></p>
     *  
     * @param javaProject the project to receive the Maven classpath container.
     * @return <tt>true</tt> if the project's classpath was actually modified.
     * @throws CoreException if unable to overwrite the project's classpath. 
     */
    private static final boolean addClasspathContainer(final IPath containerPath, final IJavaProject javaProject) throws CoreException {
        boolean dirty = false;

        if (javaProject != null) {
            // holds the entries that are not in the container
            LinkedHashMap entries = new LinkedHashMap();

            // initially fill with all entries
            IClasspathEntry[] entry = javaProject.getRawClasspath();
            for (int i = 0; i < entry.length; ++i) {

                // abandon all work and return false if this project
                // already has a Maven classpath container
                if (isMavenClasspathContainer(entry[i])) {
                    return false;
                }

                entries.put(getResolvedPath(entry[i]), entry[i]);
            }

            // remove entries that are in the container
            IProject project = javaProject.getProject();
            IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(project);
            if (context != null) {
                MavenClasspathContainer container = new MavenClasspathContainer(context);
                entry = container.getClasspathEntries();
                for (int i = 0; i < entry.length; ++i) {
                    IPath path = entry[i].getPath();
                    if (entries.containsKey(path)) {
                        entries.remove(path);
                        dirty = true;
                    }
                }

                if (dirty) {
                    entries.put(containerPath, JavaCore.newContainerEntry(containerPath));
                    entry = (IClasspathEntry[])entries.values().toArray(new IClasspathEntry[entries.size()]);
                    javaProject.setRawClasspath(entry, null);
                }

//                update(containerPath, javaProject, container);
            }
        }

        return dirty;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.classpath.ClasspathManager#initializeClasspathContainer(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public void initializeClasspathContainer(final IPath containerPath, final IJavaProject javaProject) throws CoreException {

        if (javaProject != null) {
            // remove entries that are in the container
            IProject project = javaProject.getProject();
            IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(project);
            if (context != null) {
                MavenClasspathContainer container = new MavenClasspathContainer(context);
                update(containerPath, javaProject, container);
            }
        }

    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.classpath.ClasspathManager#removeClasspathContainer(org.eclipse.jdt.core.IJavaProject)
     */
    public boolean removeClasspathContainer(final IJavaProject javaProject) throws CoreException {
        boolean dirty = false;

        IProject project = javaProject.getProject();
        IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(project);
        if (context != null) {
            ArrayList entries = new ArrayList();

            IClasspathEntry[] entry = javaProject.getRawClasspath();
            for (int i = 0; i < entry.length; ++i) {
                if (isMavenClasspathContainer(entry[i])) {
                    addContainerEntries(entries, context);
                    dirty = true;
                } else {
                    entries.add(entry[i]);
                }
            }

            if (dirty) {
                entry = (IClasspathEntry[])entries.toArray(new IClasspathEntry[entries.size()]);
                javaProject.setRawClasspath(entry, null);
            }
        }

        return dirty;
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.classpath.ClasspathManager#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public boolean canUpdateClasspathContainer(final IPath containerPath, final IJavaProject project) {
        try {
            IClasspathContainer container = JavaCore.getClasspathContainer(containerPath, project);
            return container != null && container instanceof MavenClasspathContainer;
        } catch (JavaModelException e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.mevenide.ui.eclipse.classpath.ClasspathManager#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject, org.eclipse.jdt.core.IClasspathContainer)
     */
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
    }

    /**
     * @return <tt>true</tt> if the given entry is a Maven classpath container.
     */
    private static final boolean isMavenClasspathContainer(final IClasspathEntry entry) {
        return entry != null && 
               entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER &&
               MavenClasspathContainer.ID.equals(entry.getPath().segment(0));
    }

    private static final IPath getResolvedPath(final IClasspathEntry entry) {
        IPath result = null;

        if (entry != null) {
            if (entry.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
                result = JavaCore.getResolvedVariablePath(entry.getPath());
                if (result == null) {
                    // the path did not resolve
                    result = entry.getPath();
                }
            } else {
                result = entry.getPath();
            }
        }

        return result;
    }

    /**
     * Adds the classpath entries described by the context to the provided list.
     * @param entries the classpath entries are added to this list
     * @param context the context that contains the classpath entries
     */
    private static final void addContainerEntries(final List entries, final IQueryContext context) {
        MavenClasspathContainer container = new MavenClasspathContainer(context);

        IClasspathEntry[] entry = container.getClasspathEntries();
        for (int i = 0; i < entry.length; ++i) {
            switch (entry[i].getEntryKind()) {

            case IClasspathEntry.CPE_LIBRARY: {
                entries.add(convertToVariableEntry(entry[i]));
                break;
            }

            case IClasspathEntry.CPE_PROJECT: {
                entries.add(entry[i]);
                break;
            }
            }
        }
    }

    /**
     * Converts an absolute classpath entry into a relative path within the local Maven repository.
     * @param entry the absolute path to the library entry
     * @return a relative classpath entry within the local Maven reposity
     */
    private static final IClasspathEntry convertToVariableEntry(final IClasspathEntry entry) {
        IClasspathEntry newEntry = null;

        if (entry != null) {
            newEntry = JavaCore.newVariableEntry(
                convertToVariablePath(entry.getPath()),
                convertToVariablePath(entry.getSourceAttachmentPath()),
                entry.getSourceAttachmentRootPath(),
// These do not appear in IClasspathEntry until v3.1 
//                entry.getAccessRules(),
//                entry.getExtraAttributes(),
                entry.isExported()
            );
        }

        return newEntry;
    }

    /**
     * Converts an absolute path to a library entry into a relative path within the local Maven repository.
     * @param path the absolute path to the library entry
     * @return a relative path within the local Maven reposity
     */
    private static final IPath convertToVariablePath(final IPath path) {
        if (path == null) return null;
        return MAVEN_REPO_ROOT.append(path.removeFirstSegments(MAVEN_REPO.segmentCount()));
    }

    /**
     * Notifies the JDT that the container has changed or adds the container
     * if it does not exist in the classpath.
     * 
     * @param containerPath - the name of the container reference, which is being updated
     * @param javaProject the Java project for which this container is being bound
     * @param container  the container being updated
     * @throws JavaModelException
     */
    private static final void update(final IPath containerPath, final IJavaProject javaProject, final IClasspathContainer container) throws JavaModelException {
        IJavaProject[] javaProjects = { javaProject };
        IClasspathContainer[] containers = { container };
        JavaCore.setClasspathContainer(containerPath, javaProjects, containers, null);
    }
}
