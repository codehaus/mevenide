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
package org.mevenide.ui.eclipse.sync.pom;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.ui.eclipse.sync.IPathResolverDelegate;


/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ArtifactVisitor {
	
	PomSynchronizer pomSynchronizer;
	
	ArtifactVisitor(PomSynchronizer synchronizer) {
		this.pomSynchronizer = synchronizer;
	}
	
	public void add(SourceEntry entry) throws Exception {
		
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		
		ProjectWriter writer = ProjectWriter.getWriter();
		IPathResolverDelegate pathResolver = pomSynchronizer.getPathResolver();
		IProject project = pomSynchronizer.getProject();
		String pathToAdd = pathResolver.computePath(classpathEntry, project);

		writer.addSource(
			pathToAdd, 
			pomSynchronizer.getPom(), 
			pathResolver.getMavenSourceType(classpathEntry, project)
		);
	}
	
	public void add(DependencyEntry entry) throws Exception {
//		IClasspathEntry[] cpe = PreferenceConstants.getDefaultJRELibrary();
//		for (int i = 0; i < cpe.length; i++) {
//			System.out.println(cpe[i].getPath());
//		}

		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		
		ProjectWriter writer = ProjectWriter.getWriter();
		IPathResolverDelegate pathResolver = pomSynchronizer.getPathResolver();
		
		writer.addDependency(
			pathResolver.getAbsolutePath(classpathEntry.getPath()), 
			pomSynchronizer.getPom()
		);
	}
	
	public void add(ProjectEntry entry) throws Exception {
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		
		IPath projectPath = classpathEntry.getPath();
		
		File referencedPom = projectPath.append("project.xml").toFile();
		if ( !referencedPom.exists() ) {
			//project isnot mavenized, mavenize it as well
		}
		
		ProjectWriter writer = ProjectWriter.getWriter();
		writer.addProject(referencedPom, pomSynchronizer.getPom());
	}

}

