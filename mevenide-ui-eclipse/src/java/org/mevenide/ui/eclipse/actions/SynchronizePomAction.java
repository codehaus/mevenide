/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.view.SynchronizationView;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SynchronizePomAction extends AbstractMevenideAction {
    private static Log log = LogFactory.getLog(SynchronizePomAction.class);
	
    private Project mavenProject;
    
    public void run(IAction action) {
        try {
            SynchronizationView view = (SynchronizationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Mevenide.SYNCHRONIZE_VIEW_ID);
            view.setInput(mavenProject);
        }
        catch ( Exception e ) {
            log.debug("Unable to synchronize project", e); //$NON-NLS-1$
        }
	}
    
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
        
        Object firstElement = ((StructuredSelection) selection).getFirstElement();
       
        try {
            if ( firstElement instanceof IFile ) {
		        IFile selectedFile = (IFile) firstElement;
		        File projectFile = selectedFile.getLocation().toFile();
	            mavenProject = ProjectReader.getReader().read(projectFile);
	            mavenProject.setFile(projectFile);
            }
        } 
        catch (Exception e) {
            log.error("Unable to read project file", e); //$NON-NLS-1$
            //shouldnot warn b/c malformed xml will then prevent user to do any action on that resource
            //MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Invalid POM", "Unable to read project descriptor.");
        }
        
    }


}