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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mevenide.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.ui.eclipse.sync.source.IPathResolverDelegate;



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
	
	
		if ( pathResolver.getMavenSourceType(pathToAdd, project).equals(ProjectConstants.MAVEN_RESOURCE) ) {
			writer.addResource(pathToAdd, pomSynchronizer.getPom());
		}
	
		writer.addSource(
			pathToAdd, 
			pomSynchronizer.getPom(), 
			pathResolver.getMavenSourceType(pathToAdd, project)
		);
	}
	
	public void add(DependencyEntry entry) throws Exception {
		ProjectWriter writer = ProjectWriter.getWriter();
		IPathResolverDelegate pathResolver = pomSynchronizer.getPathResolver();
		
		IClasspathEntry jreEntry = JavaRuntime.getJREVariableEntry();
		IClasspathEntry resolvedJreEntry = JavaCore.getResolvedClasspathEntry(jreEntry);
		String jrePath = pathResolver.getAbsolutePath(resolvedJreEntry.getPath());
		
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		String entryPath = pathResolver.getAbsolutePath(classpathEntry.getPath());
		
		if ( !jrePath.equals(entryPath) && !isClassFolder(entryPath) ) {
			writer.addDependency(
				entryPath, 
				pomSynchronizer.getPom()
			);
 		}
		
	}
	
	private boolean isClassFolder(String entryPath) {
		return new File(Platform.getLocation().append(new Path(entryPath)).toOSString()).isDirectory();
	}
	
	public void add(ProjectEntry entry) throws Exception {
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		
		IPath projectPath = classpathEntry.getPath();
		
		File referencedPom = new File(projectPath.append("project.xml").toOSString());
		if ( !referencedPom.exists() ) {
			//@todo project isnot mavenized, mavenize it as well
		}
		
		ProjectWriter writer = ProjectWriter.getWriter();
		writer.addProject(referencedPom, pomSynchronizer.getPom());
	}

}

