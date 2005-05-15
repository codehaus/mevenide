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
package org.mevenide.ui.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: IPathResolver.java 4 mai 2003 12:02:3013:34:35 Exp gdodinet 
 * 
 */
public interface IPathResolver {
	/**
	 * extract the source path to add to the pom from the given classpathentry
	 * 
	 * @param classpathEntry
	 * @return
	 */
	public abstract String getRelativeSourceDirectoryPath(IClasspathEntry classpathEntry, IProject project);
       
	public String getRelativePath(IProject project, IPath path);
	
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