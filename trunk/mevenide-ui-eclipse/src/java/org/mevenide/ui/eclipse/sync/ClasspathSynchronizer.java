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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private Log log = LogFactory.getLog(ClasspathSynchronizer.class);
	
    /** the project we want to keep in synch with the pom */
	private IProject project ;
	
    /**
     * @todo pullup synchronizationGoal initialization
     * @see org.mevenide.core.sync.ISynchronizer#initialize()
     */
	public void initialize() {
		setProject(Mevenide.getPlugin().getProject());
		synchronizationGoal = Mevenide.getResourceString("eclipse.synch.project.goal");
	}
	
	protected void process() {
		if ( SynchronizerUtil.shouldSynchronizeProject(project) ) {
			super.process();
		}
	}

    
    /**
     * @see org.mevenide.core.sync.ISynchronizer#postSynchronization() 
     */
	public void postSynchronization() {
		try {
			project.refreshLocal(IProject.DEPTH_INFINITE, null);
		}
		catch ( Exception e ) {
			log.debug("Unable to refresh project due to : " + e);
		}
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
