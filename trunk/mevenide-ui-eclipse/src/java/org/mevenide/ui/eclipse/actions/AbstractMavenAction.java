/*
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractMavenAction implements IWorkbenchWindowActionDelegate  {
    private IWorkbenchWindow window;
    protected IProject currentProject;
	
	public void selectionChanged(IAction action, ISelection selection) {
        
        IProject selectedProject = getParentProject(selection);
        
        if ( selectedProject != null ) {
    		MavenPlugin plugin = MavenPlugin.getPlugin();
    		
            currentProject = selectedProject.getProject();
    		String cdir = currentProject.getLocation().toFile().getAbsolutePath();
    		plugin.setCurrentDir(cdir);
            
            plugin.setProject(selectedProject);
            
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
