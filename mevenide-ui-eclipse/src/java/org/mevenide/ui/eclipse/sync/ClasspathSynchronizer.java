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
package org.mevenide.ui.eclipse.sync;

import org.eclipse.core.resources.IProject;
import org.mevenide.sync.AbstractIdeSynchronizer;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.ui.eclipse.Mevenide;


/**
 * 
 * update .classpath - use Maven Eclipse plugin
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class ClasspathSynchronizer extends AbstractIdeSynchronizer implements ISynchronizer {
	
    /** the project we want to keep in synch with the pom */
	private IProject project ;
	
    /**
     * 
     * @see org.mevenide.core.sync.ISynchronizer#initialize()
     */
	public void initialize() {
		setProject(Mevenide.getPlugin().getProject());
		synchronizationGoal = Mevenide.getResourceString("eclipse.synch.project.goal");
	}
    
    /**
     * not needed anymore since eclipse plugin has been refactored to 
     * keep .project and .classpath generation separated
     * @see org.mevenide.core.sync.ISynchronizer#postSynchronization() 
     */
	public void postSynchronization() {
	}
    
    /**
     * @see org.mevenide.core.sync.ISynchronizer#preSynchronization()
     */
	public void preSynchronization() {
		
	}
    /**
     * @param IProject project
     */
	public void setProject(IProject project) {
		this.project = project;
	}
}
