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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractMevenideAction implements IWorkbenchWindowActionDelegate  {
    private IWorkbenchWindow window;
    protected IProject currentProject;
	
	public void selectionChanged(IAction action, ISelection selection) {
        
        IProject selectedProject = getParentProject(selection);
        
        if ( selectedProject != null ) {
    		Mevenide plugin = Mevenide.getInstance();
    		
            currentProject = selectedProject.getProject();
    		String cdir = currentProject.getLocation().toFile().getAbsolutePath();
    		plugin.setCurrentDir(cdir);
            
            plugin.setProject(selectedProject);
            
            plugin.initEnvironment();
    	}
	}
    
    private IProject getParentProject(ISelection selection) {
        IProject project = null;
        Object firstElement = ((StructuredSelection) selection).getFirstElement();
        if ( firstElement instanceof IProject ) {
            project = (IProject) firstElement;
        }
        if ( firstElement instanceof IJavaProject )  {
            project = ((IJavaProject) firstElement).getProject();
               
        }
        if ( firstElement instanceof IFile ) {
            project = ((IFile) firstElement).getProject();
        }
        return project;
    }
    
    public void dispose() {
    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }
}
