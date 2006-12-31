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

package org.mevenide.ui.eclipse.classpath;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Manages the classpaths for all Maven enabled projects.
 */
public interface ClasspathManager {

    /**
     * Adds a Maven classpath container to the given Java project. If the
     * project's classpath contains any entries that would be duplicated
     * within the container then they are removed. If the project already
     * has a Maven classpath container then this method does nothing.
     *  
     * @param javaProject the project to receive the Maven classpath container.
     * @return <tt>true</tt> if the project's classpath was actually modified.
     * @throws CoreException if unable to overwrite the project's classpath. 
     */
    void initializeClasspathContainer(IPath containerPath, IJavaProject javaProject) throws CoreException;

    /**
     * Removes all Maven classpath containers from the given Java project. The
     * classpath entries are converted to equivalent variable entries for each
     * item in the local Maven repository.
     *  
     * @param javaProject the project that may or may not contain a Maven classpath container.
     * @return <tt>true</tt> if the project's classpath was actually modified.
     * @throws CoreException if unable to overwrite the project's classpath. 
     */
    boolean removeClasspathContainer(IJavaProject javaProject) throws CoreException;

    /**
     * Returns <code>true</code> if this manager can be requested to perform updates 
     * on its own container values. If so, then an update request will be performed using
     * <code>ClasspathManager#requestClasspathContainerUpdate</code>/
     * <p>
     * @param containerPath the path of the container which requires to be updated
     * @param project the project for which the container is to be updated
     * @return returns <code>true</code> if the container can be updated
     */
    boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project);

    /**
     * Request a registered container definition to be updated according to a container suggestion. The container suggestion 
     * only acts as a place-holder to pass along the information to update the matching container definition(s) held by the 
     * container initializer. In particular, it is not expected to store the container suggestion as is, but rather adjust 
     * the actual container definition based on suggested changes.
     * <p>
     * IMPORTANT: In reaction to receiving an update request, a container initializer will update the corresponding
     * container definition (after reconciling changes) at its earliest convenience, using 
     * <code>JavaCore#setClasspathContainer(IPath, IJavaProject[], IClasspathContainer[], IProgressMonitor)</code>. 
     * Until it does so, the update will not be reflected in the Java Model.
     * <p>
     * In order to anticipate whether the container initializer allows to update its containers, the predicate
     * <code>ClasspathManager#canUpdateClasspathContainer</code> should be used.
     * <p>
     * @param containerPath the path of the container which requires to be updated
     * @param project the project for which the container is to be updated
     * @param containerSuggestion a suggestion to update the corresponding container definition
     * @throws CoreException when <code>JavaCore#setClasspathContainer</code> would throw any.
     * @see JavaCore#setClasspathContainer(IPath, IJavaProject[], IClasspathContainer[], org.eclipse.core.runtime.IProgressMonitor)
     * @see ClasspathManager#canUpdateClasspathContainer(IPath, IJavaProject)
     */
    void requestClasspathContainerUpdate(IPath containerPath, IJavaProject project, IClasspathContainer containerSuggestion) throws CoreException;

}