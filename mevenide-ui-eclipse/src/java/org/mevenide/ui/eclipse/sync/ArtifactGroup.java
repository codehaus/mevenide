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
 * 
 */
package org.mevenide.ui.eclipse.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public abstract class ArtifactGroup {
	private static Log log = LogFactory.getLog(ArtifactGroup.class);
	
	protected IJavaProject project;
	
	
	public ArtifactGroup(IProject project)  {
		try {
			if ( project != null && project.hasNature(JavaCore.NATURE_ID) ) {
				this.project = JavaCore.create(project);
				initialize();
			}
		}
		catch ( Exception ex ) {
			log.debug("Error in ArtifactGroup initializer. reason : " + ex);
		}
	}
	
	protected abstract void initialize() throws Exception; 
	
	public IJavaProject getProject() {
		return project;
	}

	public void setProject(IJavaProject project) throws Exception {
		this.project = project;
		initialize();
	}
}
