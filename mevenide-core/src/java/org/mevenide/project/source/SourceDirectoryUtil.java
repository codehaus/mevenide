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
package org.mevenide.project.source;

import org.apache.maven.project.Build;
import org.apache.maven.project.Project;
import org.mevenide.ProjectConstants;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryUtil {
	
	private SourceDirectoryUtil() {
	}
	
	public static void addSource(Project project, String path, String sourceType) {
		if ( project.getBuild() == null ) {
			project.setBuild(new Build());
		}
		
		if ( ProjectConstants.MAVEN_ASPECT_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setAspectSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_SRC_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_TEST_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setUnitTestSourceDirectory(path);
		}
		if ( ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY.equals(sourceType) ) {
			project.getBuild().setIntegrationUnitTestSourceDirectory(path);
		}
	}
	
	public static boolean isSourceDirectoryPresent(Project project, String path) {
		if ( project.getBuild() == null ) {
			return false;
		}
		
		String srcDirectory = project.getBuild().getSourceDirectory();
		String aspectSrcDirectory = project.getBuild().getAspectSourceDirectory();
		String unitTestSourceDirectory = project.getBuild().getUnitTestSourceDirectory();
		String integrationUnitTestSourceDirectory = project.getBuild().getIntegrationUnitTestSourceDirectory();
		
		return path.equals(srcDirectory)
			   || path.equals(aspectSrcDirectory)
		       || path.equals(unitTestSourceDirectory)
			   || path.equals(integrationUnitTestSourceDirectory);
		
	}
}
