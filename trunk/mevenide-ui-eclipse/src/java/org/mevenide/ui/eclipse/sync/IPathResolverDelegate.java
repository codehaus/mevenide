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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: IPathResolver.java 4 mai 2003 12:02:3013:34:35 Exp gdodinet 
 * 
 */
public interface IPathResolverDelegate {
	/**
	 * extract the source path to add to the pom from the given classpathentry
	 * 
	 * @param classpathEntry
	 * @return
	 */
	public abstract String computePath(
		IClasspathEntry classpathEntry,
		IProject project);
        
	/**
	 * utility method
	 * compute the absolute file location of the given ipath 
	 * 
	 * @param path
	 * @return
	 */
	public abstract String getAbsolutePath(IPath path);
    
	public abstract String getMavenSourceType(String sourceDirectoryPath, IProject project)
		throws Exception;
}