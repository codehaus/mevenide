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
package org.mevenide.ui.eclipse.util;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.mevenide.project.io.ProjectSkeleton;
import org.mevenide.ui.eclipse.sync.DefaultPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.IPathResolverDelegate;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectUtil {
	private ProjectUtil() {
	}

	public static void createPom(IProject project) throws Exception, CoreException {
		
		 String referencedPomSkeleton = ProjectSkeleton.getSkeleton( project.getName() );
		 IFile referencedProjectFile = project.getFile("project.xml"); 
		 referencedProjectFile.create(new ByteArrayInputStream(referencedPomSkeleton.getBytes()), false, null);
	}
	
	public static File getPom(IProject project) {
		IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate();
		IPath referencedProjectLocation = project.getLocation();
		return new File(pathResolver.getAbsolutePath(referencedProjectLocation.append("project.xml")) );
	}
}
