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
package org.mevenide.ui.eclipse.util;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.mevenide.ProjectConstants;
import org.mevenide.util.MevenideUtil;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.source.SourceDirectory;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class FileUtil {
	private FileUtil() {
	}
	
	

	public static boolean inLocalRepository(String entryPath) {
		File localRepo = new File(Mevenide.getPlugin().getMavenRepository());
		return MevenideUtil.findFile(localRepo, entryPath);
	}

	public static boolean isClassFolder(String entryPath, IProject project) {
		return new File(project.getLocation().append(new Path(entryPath).removeFirstSegments(1)).toOSString()).isDirectory();
	}

	public static boolean isSource(SourceDirectory directory) {
		boolean b = directory.getDirectoryType().equals(ProjectConstants.MAVEN_ASPECT_DIRECTORY)
					|| directory.getDirectoryType().equals(ProjectConstants.MAVEN_SRC_DIRECTORY)
					|| directory.getDirectoryType().equals(ProjectConstants.MAVEN_TEST_DIRECTORY)
					|| directory.getDirectoryType().equals(ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		return b;					
	}
	
	
}
