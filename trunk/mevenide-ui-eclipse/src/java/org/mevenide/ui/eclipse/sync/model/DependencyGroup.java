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
package org.mevenide.ui.eclipse.sync.model;

import java.util.List;

import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.util.FileUtil;
import org.mevenide.ui.eclipse.util.ProjectUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyGroup extends ArtifactGroup {
	
	public DependencyGroup(IProject project) {
		super(project);
	}

	protected void initialize() throws Exception {
		
		IClasspathEntry[] classpathEntries = javaProject.getResolvedClasspath(true);
		
		IPathResolver pathResolver = new DefaultPathResolver();
		
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY
					&& !FileUtil.isClassFolder(classpathEntries[i].getPath().toOSString(), javaProject.getProject()) 
					&& !ProjectUtil.getJreEntryList(javaProject.getProject()).contains(pathResolver.getAbsolutePath(classpathEntries[i].getPath())) ) {
				
				String path = classpathEntries[i].getPath().toOSString(); 
				Dependency dependency = DependencyFactory.getFactory().getDependency(path);
				
				addDependency(dependency);
				
			}
		}
		artifacts.addAll(ProjectUtil.getCrossProjectDependencies());
		
	}
	
	
	public List getDependencies() {
		return artifacts;
	}

	public void setDependencies(List list) {
		artifacts = list;
	}
	
	public void addDependency(Dependency dependency) {
		if ( dependency.getArtifactId() == null ) {
			dependency.setArtifactId("");
		}
		if ( dependency.getGroupId() == null ) {
			dependency.setArtifactId("");
		}
		if ( dependency.getVersion() == null ) {
			dependency.setVersion("");
		}
		if ( dependency.getType() == null ) {
			dependency.setType("");
		}
		if ( dependency.getArtifact() == null ) {
			dependency.setArtifact("");
		}
		artifacts.add(dependency);
	
		for (int i = 0; i < excludedArtifacts.size(); i++) {
			Dependency excluded = (Dependency) excludedArtifacts.get(i);
			if ( excluded.getArtifact().equals(dependency.getArtifact()) 
			 		|| DependencyUtil.areEquals(excluded, dependency) ) {
			 	 excludedArtifacts.remove(excluded);
			 }
		}
	}
	
	public void excludeDependency(Dependency dependency) {
		excludedArtifacts.add(dependency);
	}
	
	public List getExcludedDependencies() {
		return excludedArtifacts;
	}
}

