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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.ProjectConstants;
import org.mevenide.ui.eclipse.DefaultPathResolver;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryGroup extends ArtifactGroup {
	
	public SourceDirectoryGroup(IProject project)  {
		super(project);	
	}
	
	protected void initialize() throws Exception {
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				String path = new DefaultPathResolver().getRelativeSourceDirectoryPath(classpathEntries[i], javaProject.getProject());
				SourceDirectory sourceDirectory = new SourceDirectory(path);
				sourceDirectory.setDirectoryType(ProjectConstants.MAVEN_SRC_DIRECTORY); 
				addSourceDirectory(sourceDirectory);
				
			}
		}
	}
	
	public void addSourceDirectory(SourceDirectory sourceDirectory) {
		for (int j = 0; j < excludedArtifacts.size(); j++) {
			SourceDirectory excluded = (SourceDirectory) excludedArtifacts.get(j);
			if ( sourceDirectory.getDirectoryPath().equals(excluded.getDirectoryPath()) ) {
				excludedArtifacts.remove(excluded);
				sourceDirectory.setDirectoryType(excluded.getDirectoryType());
			}
		}
		artifacts.add(sourceDirectory);
	}
	
	public List getSourceDirectories() {
		return artifacts;
	}
	
	public void excludeSourceDirectory(SourceDirectory directory) {
		artifacts.remove(directory);
		excludedArtifacts.add(directory);
	}

	public void setSourceDirectories(List list) {
		artifacts = list;
	}

	public boolean equals(Object obj) {
		return  obj != null 
				&& (obj instanceof SourceDirectoryGroup)
				&& areEquals(((SourceDirectoryGroup)obj).artifacts, artifacts)
				&& areEquals(((SourceDirectoryGroup)obj).javaProject, javaProject);
	}
	
	private boolean areEquals(Object o1, Object o2) {
		boolean allNull = 
				o1 == null && o2 == null;
		boolean firstNotNull = 
				o1 != null && o1.equals(o2);
		return allNull || firstNotNull;
	}
	
	
}
