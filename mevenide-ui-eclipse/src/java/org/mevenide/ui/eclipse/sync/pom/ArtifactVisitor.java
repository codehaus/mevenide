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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mevenide.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.ui.eclipse.sync.IPathResolverDelegate;
import org.mevenide.ui.eclipse.util.FileUtil;
import org.mevenide.ui.eclipse.util.ProjectUtil;



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
		String pathToAdd = pathResolver.getRelativeSourceDirectoryPath(classpathEntry, project);
	
	
		if ( pathResolver.getMavenSourceType(pathToAdd, project).equals(ProjectConstants.MAVEN_RESOURCE) ) {
			writer.addResource(pathToAdd, pomSynchronizer.getPom());
		}
	
		writer.addSource(
			pathToAdd, 
			pomSynchronizer.getPom(), 
			pathResolver.getMavenSourceType(pathToAdd, project)
		);
	}
	
	/**
	 * this method is quite complex because we take care to not add anything from the jdk 
	 * so we check both JRE_LIB and JRE_CONTAINER
	 * 
	 * 
	 * @param entry
	 * @throws Exception
	 */
	public void add(DependencyEntry entry) throws Exception {
		ProjectWriter writer = ProjectWriter.getWriter();
		IPathResolverDelegate pathResolver = pomSynchronizer.getPathResolver();
		
		IClasspathEntry jreEntry = JavaRuntime.getJREVariableEntry();
		IClasspathEntry resolvedJreEntry = JavaCore.getResolvedClasspathEntry(jreEntry);
		String jrePath = pathResolver.getAbsolutePath(resolvedJreEntry.getPath());
		
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		String entryPath = pathResolver.getAbsolutePath(classpathEntry.getPath());
		
		IClasspathContainer container = JavaCore.getClasspathContainer(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"), JavaCore.create(this.pomSynchronizer.getProject()));
		IClasspathEntry[] jreEntries = container.getClasspathEntries();
		
		List jreEntryList = new ArrayList();
		
		for (int i = 0; i < jreEntries.length; i++) {
        	jreEntryList.add(pathResolver.getAbsolutePath(jreEntries[i].getPath()));
		}    
        
        boolean isClassFolder = FileUtil.isClassFolder(pathResolver.getRelativeSourceDirectoryPath(classpathEntry, pomSynchronizer.getProject()), pomSynchronizer.getProject());
        
        if ( !jrePath.equals(entryPath) && !jreEntryList.contains(entryPath) && !isClassFolder ) {
			writer.addDependency(
				entryPath, 
				pomSynchronizer.getPom()
			);
			
			//check if entryPath is in repo, if not warn the user about the guessed groupId
			//add it to the pomSynchronizer list of unresolved dependencies so that we can 
			//later display a MessageBox
			String path = new File(pathResolver.getAbsolutePath(classpathEntry.getPath())).getName();
			if ( !isClassFolder && !FileUtil.inLocalRepository(path) ) {
				pomSynchronizer.addUnresolvedDependency(entryPath);
			}
			
 		}
		
	}
	
	public void add(ProjectEntry entry) throws Exception {
		IClasspathEntry classpathEntry = entry.getClasspathEntry();
		IPath projectPath = classpathEntry.getPath();
		
		//IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate(); 
		
		IProject[] referencedProjects = pomSynchronizer.getProject().getReferencedProjects();
		for (int i = 0; i < referencedProjects.length; i++) {
			
			if ( referencedProjects[i].getFullPath().equals(projectPath) ) {
				File referencedPom = ProjectUtil.getPom(referencedProjects[i]);
				if ( !referencedPom.exists() ) {
					/* 
					 * project isnot mavenized, mavenize it as well
					 * since we're already in the process of mavenizing
					 * a project, the synch view is busy so we cannot rely on user input
					 * that why we just create a skeleton
					 */
					ProjectUtil.createPom(referencedProjects[i]);
				}
				ProjectWriter writer = ProjectWriter.getWriter();
				writer.addProject(referencedPom, pomSynchronizer.getPom());	
			}
			
		}
		
	}

}

