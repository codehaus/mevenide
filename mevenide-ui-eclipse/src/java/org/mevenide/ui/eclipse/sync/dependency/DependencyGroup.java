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
package org.mevenide.ui.eclipse.sync.dependency;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.ui.eclipse.sync.DefaultPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.IPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.pom.ArtifactGroup;

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
		dependencies = new ArrayList();
		IClasspathEntry[] classpathEntries = project.getResolvedClasspath(true);
		
		IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate();
		
		for (int i = 0; i < classpathEntries.length; i++) {
			if ( classpathEntries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				String path = pathResolver.getAbsolutePath(classpathEntries[i].getPath());
				Dependency dependency = DependencyFactory.getFactory().getDependency(path);
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
