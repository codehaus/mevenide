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
package org.mevenide.ui.eclipse.sync.model;

import java.io.File;

import org.mevenide.ProjectConstants;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectory {
	private String directoryPath = "";
	private String directoryType = "sourceDirectory";
	
	public SourceDirectory(String path) {
		//directoryPath = "${basedir}" + File.separator + path;
		directoryPath = path;
	}
	
	public String getDisplayPath() {
		if ( directoryPath.equals("${basedir}")) {
			return "${basedir}";
		}
		return "${basedir}" + File.separator +  directoryPath;
	}
	
	public String getDirectoryPath() {
		return directoryPath;
	}
	
	public String getDirectoryType() {
		return directoryType;
	}
	
	public void setDirectoryType(String newDirectoryType) {
		directoryType = newDirectoryType;
	}
	
	public boolean equals(Object o) {
		return (o instanceof SourceDirectory)
				&& ((SourceDirectory) o).getDirectoryPath() != null
				&& ((SourceDirectory) o).getDirectoryPath().equals(directoryPath);
				
	}
	
	public boolean isSource() {
		boolean b = getDirectoryType().equals(ProjectConstants.MAVEN_ASPECT_DIRECTORY)
					|| getDirectoryType().equals(ProjectConstants.MAVEN_SRC_DIRECTORY)
					|| getDirectoryType().equals(ProjectConstants.MAVEN_TEST_DIRECTORY)
					|| getDirectoryType().equals(ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
		return b;					
	}
}
