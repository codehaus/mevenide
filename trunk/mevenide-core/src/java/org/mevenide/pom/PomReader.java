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
package org.mevenide.pom;

import java.io.File;
import java.util.Hashtable;

import org.apache.maven.project.Project;
import org.jdom.input.SAXBuilder;
import org.mevenide.util.BetwixtHelper;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class PomReader {
	
	public static Hashtable getAllSourceDirectories(File pom) throws Exception {
		Hashtable srcDirs = new Hashtable();
		
		Project project = BetwixtHelper.readProject(pom);
		
		if ( project.getBuild().getAspectSourceDirectory() != null 
		 	&& !project.getBuild().getAspectSourceDirectory().trim().equals("")) {
			srcDirs.put(project.getBuild().getAspectSourceDirectory(), BuildConstants.MAVEN_ASPECT);
		}
		if ( project.getBuild().getSourceDirectory() != null 
			&& !project.getBuild().getSourceDirectory().trim().equals("")) {
			srcDirs.put(project.getBuild().getSourceDirectory(), BuildConstants.MAVEN_SRC);	
		}
		if ( project.getBuild().getUnitTestSourceDirectory() != null 
			&& !project.getBuild().getUnitTestSourceDirectory().trim().equals("")) {
			srcDirs.put(project.getBuild().getUnitTestSourceDirectory(), BuildConstants.MAVEN_TEST);
		}
		//srcDirs.put(project.getBuild().getUnitTestSourceDirectory()
	
		return srcDirs;
	}
	
	public static boolean isWellFormed(File pom) {
		try {
			new SAXBuilder().build(pom);
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
}
