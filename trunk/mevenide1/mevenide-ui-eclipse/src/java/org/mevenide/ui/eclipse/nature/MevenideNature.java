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

package org.mevenide.ui.eclipse.nature;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.view.SynchronizationView;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * Responsible for adding MevenIDE specific behavior to a project.
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 */
public class MevenideNature implements IProjectNature {
    public static final String NATURE_ID = Mevenide.PLUGIN_ID + ".mavennature"; //$NON-NLS-1$

    private IProject project;

    //@todo add a preference to control the behaviour
    private boolean createPomOnActivation = true;

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#configure()
     */
    public void configure() throws CoreException {
        // Add nature-specific information for the project,
        // such as adding a builder to a project's build spec.

        try {
            MavenBuilder.addToProject(getProject());
            synchronizeProject(getProject());
        } catch (Exception e) {
            final String msg = "Unable to add MevenIDE nature to project " + getProject().getName();
            Mevenide.displayError("Add MevenIDE Nature", msg, e);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#deconfigure()
     */
    public void deconfigure() throws CoreException {
        MavenBuilder.removeFromProject(getProject());
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
     */
    public void setProject(IProject project) {
        this.project = project;
    }

    private void synchronizeProject(IProject project) throws Exception {
        if (createPomOnActivation && FileUtils.getPom(project) != null && !FileUtils.getPom(project).exists()) {
            FileUtils.createPom(project);
        }
        openSynchronizationView();
    }

    /**
     * Shows the POM synchronization view in the currently active workbench window
     * and gives it focus. If the view is already open in this page, it is given focus.
     * 
     * @throws PartInitException if the view could not be initialized
     */
    private void openSynchronizationView() throws PartInitException {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow activeWnd = workbench.getActiveWorkbenchWindow();

        if (activeWnd != null) {
            final IWorkbenchPage activePage = activeWnd.getActivePage();
            SynchronizationView view = (SynchronizationView) activePage.showView(SynchronizationView.ID);
            view.setInput(getProject());
        }
    }

////////////////////////////////////////////////////////////////////////////////

    public static final void addToProject(final IProject project) throws CoreException {
        final Set natures = getNatures(project);
        if (!hasNature(natures, NATURE_ID) && hasRequiredNatures(natures)) {
            natures.add(NATURE_ID);
            setNatures(project, natures);
        }
    }

    public static final void removeFromProject(final IProject project) throws CoreException {
        final Set natures = getNatures(project);
        if (hasNature(natures, NATURE_ID)) {
            natures.remove(NATURE_ID);
            setNatures(project, natures);
        }
    }

    public static final boolean hasNature(final IProject project) throws CoreException {
        return hasNature(getNatures(project), NATURE_ID);
    }

    public static final boolean hasRequiredNatures(final IProject project) throws CoreException {
        return hasRequiredNatures(getNatures(project));
    }

    private static final Set getNatures(final IProject project) throws CoreException {
        final Set result = new LinkedHashSet();

        if (project != null && project.exists()) {
            final IProjectDescription description = project.getDescription();
            final String[] nature = description.getNatureIds();
            result.addAll(Arrays.asList(nature));
        }

        return result;
    }

    private static final void setNatures(final IProject project, final Set natures) throws CoreException {
        if (project != null) {
            final String[] nature = (natures == null) ? new String[0]: (String[]) natures.toArray(new String[natures.size()]);
            final IProjectDescription description = project.getDescription();
            description.setNatureIds(nature);
            project.setDescription(description, null);
        }
    }

    private static boolean hasNature(final Set natures, final String id) {
        return natures == null ? false : natures.contains(id);
    }

    private static final boolean hasRequiredNatures(final Set natures) {
        return true;
    }

}