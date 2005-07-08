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

import java.io.File;

import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.view.SynchronizationView;
import org.mevenide.ui.eclipse.util.StatusConstants;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 */
public class SynchronizePomAction extends AbstractMevenideAction {

    private Project mavenProject;
//    private IQueryContext context;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        try {
            final IWorkbench workbench = PlatformUI.getWorkbench();
            final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
            final IWorkbenchPage page = window.getActivePage();
            SynchronizationView view = (SynchronizationView) page.showView(Mevenide.SYNCHRONIZE_VIEW_ID);
            view.setInput(this.mavenProject);
        } catch (PartInitException e) {
            final String message = "Unable to open the POM synchronization view.";
            Mevenide.displayError("Internal MevenIDE Error", message, e);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);

//        this.context = Mevenide.getInstance().getPOMManager().getQueryContext(super.currentProject);
//        action.setEnabled(this.context != null);

        Object firstElement = ((StructuredSelection) selection).getFirstElement();
        if (firstElement != null) {
            action.setEnabled(true);
            if (firstElement instanceof IFile) {
                IFile selectedFile = (IFile) firstElement;
                File projectFile = selectedFile.getLocation().toFile();

                try {
                    this.mavenProject = ProjectReader.getReader().read(projectFile);
                    this.mavenProject.setFile(projectFile);
                } catch (Exception e) {
                    final String message = "Unable to read the Maven project file " + selectedFile + ".";
                    final IStatus status = new Status(IStatus.ERROR, Mevenide.PLUGIN_ID, StatusConstants.INTERNAL_ERROR, message, e);
                    Mevenide.getInstance().getLog().log(status);
        
//                    log.error("Unable to read project file", e); //$NON-NLS-1$
//                    shouldnot warn b/c malformed xml will then prevent user to do any action on that resource
//                    MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Invalid POM", "Unable to read project descriptor.");
                }
            }
        }
    }
}