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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.sync.ArtifactGroup;
import org.mevenide.ui.eclipse.util.FileUtil;
import org.mevenide.ui.eclipse.util.ProjectUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyGroup extends ArtifactGroup {
	private List dependencies;

	public DependencyGroup(IProject project) {
		super(project);
	}

	protected void initialize() throws Exception {
		
		if ( dependencies == null ) {
			dependencies = new ArrayList();
		}
		
		IClasspathEntry[] classpathEntries = project.getResolvedClasspath(true);
		
		IPathResolver pathResolver = new DefaultPathResolver();
		
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY
					&& !FileUtil.isClassFolder(classpathEntries[i].getPath().toOSString(), project.getProject()) 
					&& !ProjectUtil.getJreEntryList(project.getProject()).contains(pathResolver.getAbsolutePath(classpathEntries[i].getPath())) ) {
				
				String path = classpathEntries[i].getPath().toOSString(); 
				Dependency dependency = DependencyFactory.getFactory().getDependency(path);
				
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
				
				dependencies.add(dependency);
				
			}
		}
		
	}
	
	
	public List getDependencies() {
		return dependencies;
	}

	public void setDependencies(List list) {
		dependencies = list;
	}
	
	public void addDependency(Dependency dep) {
		dependencies.add(dep);
	}
}

