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
package org.mevenide.ui.eclipse.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectUtil {
	private ProjectUtil() {
	}

	public static List getJreEntryList(IProject project) throws Exception {
		IPathResolver pathResolver = new DefaultPathResolver();
		
		IClasspathEntry jreEntry = JavaRuntime.getJREVariableEntry();
		IClasspathEntry resolvedJreEntry = JavaCore.getResolvedClasspathEntry(jreEntry);
		String jrePath = pathResolver.getAbsolutePath(resolvedJreEntry.getPath());
		
		IClasspathContainer container = JavaCore.getClasspathContainer(new Path("org.eclipse.jdt.launching.JRE_CONTAINER"), JavaCore.create(project));
		IClasspathEntry[] jreEntries = container.getClasspathEntries();
		
		List jreEntryList = new ArrayList();
		
		for (int i = 0; i < jreEntries.length; i++) {
			jreEntryList.add(pathResolver.getAbsolutePath(jreEntries[i].getPath()));
		}    
		jreEntryList.add(jrePath);
		return jreEntryList;
	}

	public static List getCrossProjectDependencies() throws Exception {
		List deps = new ArrayList();
		IProject[] referencedProjects = Mevenide.getPlugin().getProject().getReferencedProjects();		
		for (int i = 0; i < referencedProjects.length; i++) {
			IProject referencedProject = referencedProjects[i];
			File referencedPom = FileUtil.getPom(referencedProject);
			//check if referencedPom exists, tho it should since we just have created it
			if ( !referencedPom.exists() ) {
				FileUtil.createPom(referencedProject);
			}
			ProjectReader reader = ProjectReader.getReader();
			Dependency projectDependency = reader.getDependency(referencedPom);
			deps.add(projectDependency);
		}
		return deps;
	}
	
	
	

}
