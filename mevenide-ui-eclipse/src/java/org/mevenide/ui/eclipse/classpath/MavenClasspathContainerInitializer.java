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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;

/**
 * Used to initialize a newly created MavenClasspathContainer.
 */
public class MavenClasspathContainerInitializer extends ClasspathContainerInitializer {

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#initialize(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public void initialize(IPath containerPath, IJavaProject javaProject) throws CoreException {
        boolean autosyncEnabled = Mevenide.getInstance().getPluginPreferences().getBoolean(MevenidePreferenceKeys.AUTOSYNC_ENABLED);
        if (autosyncEnabled) {
            try {
                MavenClasspathManager.initializeClasspathContainer(containerPath, javaProject);
            } catch (CoreException e) {
                final String msg = "Unable to initialize the Maven classpath container " + containerPath + ".";
                Mevenide.displayError("Internal MevenIDE Error", msg, e);
            }
        } else {
            // This project has the Maven classpath container but autosync is disabled.
            // This could occur is a project is imported or fetched from SCC. Convert
            // each classpath entry into the equivalent variable entry that refers
            // to an artifact in the local Maven repository.
            try {
                MavenClasspathManager.removeClasspathContainer(javaProject);
            } catch (CoreException e) {
                final String msg = "Unable to remove the Maven classpath container " + containerPath + ".";
                Mevenide.displayError("Internal MevenIDE Error", msg, e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#canUpdateClasspathContainer(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject)
     */
    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        try {
            IClasspathContainer container = JavaCore.getClasspathContainer(containerPath, project);
            return container != null && container instanceof MavenClasspathContainer;
        } catch (JavaModelException e) {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.ClasspathContainerInitializer#requestClasspathContainerUpdate(org.eclipse.core.runtime.IPath, org.eclipse.jdt.core.IJavaProject, org.eclipse.jdt.core.IClasspathContainer)
     */
    public void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException {
    }

}
