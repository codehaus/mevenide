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

package org.mevenide.ui.eclipse.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractMevenideAction implements IWorkbenchWindowActionDelegate {
    private IProject currentProject;
    private IWorkbenchWindow workbenchWindow;

    protected AbstractMevenideAction() {
        this.workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        this.workbenchWindow = null;
        this.currentProject = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        this.workbenchWindow = (window != null)? window: PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {

        IProject selectedProject = getParentProject(selection);
        if (selectedProject != null) {

            this.currentProject = selectedProject.getProject();
            String cdir = this.currentProject.getLocation().toFile().getAbsolutePath();

            Mevenide.getInstance().setCurrentDir(cdir);
            Mevenide.getInstance().setProject(selectedProject);
            Mevenide.getInstance().initEnvironment();
        }
    }

    private IProject getParentProject(ISelection selection) {
        IProject project = null;

        Object firstElement = ((StructuredSelection) selection).getFirstElement();
        if (firstElement instanceof IResource) {
            project = ((IResource) firstElement).getProject();
        } else if (firstElement instanceof IJavaElement) {
            project = ((IJavaElement) firstElement).getJavaProject().getProject();
        }

        return project;
    }

    /**
     * @return Returns the currentProject.
     */
    protected IProject getCurrentProject() {
        return currentProject;
    }

    /**
     * @return Returns the workbenchWindow.
     */
    protected IWorkbenchWindow getWorkbenchWindow() {
        return workbenchWindow;
    }
}
