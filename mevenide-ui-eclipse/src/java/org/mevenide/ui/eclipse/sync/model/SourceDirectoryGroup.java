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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.ProjectConstants;
import org.mevenide.ui.eclipse.*;
import org.mevenide.ui.eclipse.sync.*;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryGroup extends ArtifactGroup {
	
	private List sourceDirectories;
	
	public SourceDirectoryGroup(IProject project)  {
		super(project);	
	}
	
	protected void initialize() throws Exception {
		sourceDirectories = new ArrayList();
		IClasspathEntry[] classpathEntries = project.getResolvedClasspath(true);
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				String path = new DefaultPathResolver().getRelativeSourceDirectoryPath(classpathEntries[i], project.getProject());
				SourceDirectory sourceDirectory = new SourceDirectory(path);
				sourceDirectory.setDirectoryType(ProjectConstants.MAVEN_SRC_DIRECTORY); 
				sourceDirectories.add(sourceDirectory);
			}
		}
	}
	
	public void addSourceDirectory(SourceDirectory sourceDirectory) {
		sourceDirectories.add(sourceDirectory);
	}
	
	public List getSourceDirectories() {
		return sourceDirectories;
	}
	
	

	public void setSourceDirectories(List list) {
		sourceDirectories = list;
	}

	public boolean equals(Object obj) {
		return  obj != null 
				&& (obj instanceof SourceDirectoryGroup)
				&& areEquals(((SourceDirectoryGroup)obj).sourceDirectories, sourceDirectories)
				&& areEquals(((SourceDirectoryGroup)obj).project, project);
	}
	
	private boolean areEquals(Object o1, Object o2) {
		boolean allNull = 
				o1 == null && o2 == null;
		boolean firstNotNull = 
				o1 != null && o1.equals(o2);
		return allNull || firstNotNull;
	}
	
	
}
