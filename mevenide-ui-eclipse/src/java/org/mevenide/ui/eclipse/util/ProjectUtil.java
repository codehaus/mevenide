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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.mevenide.ProjectConstants;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.project.io.DefaultProjectMarshaller;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.project.io.ProjectSkeleton;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.project.source.SourceDirectoryUtil;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.DefaultPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.IPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.dependency.DependencyGroup;
import org.mevenide.ui.eclipse.sync.source.SourceDirectory;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryGroup;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ProjectUtil {
	private ProjectUtil() {
	}

	public static void createPom(IProject project) throws Exception, CoreException {
		
		 String referencedPomSkeleton = ProjectSkeleton.getSkeleton( project.getName() );
		 IFile referencedProjectFile = project.getFile("project.xml"); 
		 referencedProjectFile.create(new ByteArrayInputStream(referencedPomSkeleton.getBytes()), false, null);
	}
	
	public static File getPom(IProject project) {
		IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate();
		IPath referencedProjectLocation = project.getLocation();
		return new File(pathResolver.getAbsolutePath(referencedProjectLocation.append("project.xml")) );
	}

	public static List getJreEntryList(IProject project) throws Exception {
		IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate();
		
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
	
	private static void setBuildPath() throws Exception {
		Mevenide.getPlugin().createProjectProperties();
		IProject project = Mevenide.getPlugin().getProject();
		
		IJavaProject javaProject = JavaCore.create(project);
	 
		IFile props = project.getFile("project.properties");
		File f = new Path(project.getLocation().append("project.properties").toOSString()).toFile();
		Properties properties = new Properties();
		properties.load(new FileInputStream(f));
	
		IPathResolverDelegate resolver = new DefaultPathResolverDelegate();
	
		String buildPath = resolver.getRelativePath(project, javaProject.getOutputLocation()); 
		properties.setProperty("maven.build.dest", buildPath);
		properties.store(new FileOutputStream(f), null);
	}

	public static void updatePom(SourceDirectoryGroup sourceGroup, DependencyGroup dependencyGoup, File pomFile) throws Exception {
		Mevenide.getPlugin().createProjectProperties();
		
		SourceDirectoryUtil.resetSourceDirectories(pomFile);
		
		ProjectWriter pomWriter = ProjectWriter.getWriter();
		
		//WICKED if/else
		for (int i = 0; i < sourceGroup.getSourceDirectories().size(); i++) {
			SourceDirectory directory = (SourceDirectory) sourceGroup.getSourceDirectories().get(i);
			if ( FileUtil.isSource(directory) ) {
				pomWriter.addSource(directory.getDirectoryPath(), pomFile, directory.getDirectoryType());
			}
			if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_RESOURCE ) ) {
				pomWriter.addResource(directory.getDirectoryPath(), pomFile);
			}
			if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_TEST_RESOURCE ) ) {
			}				
		}
		
		pomWriter.setDependencies(dependencyGoup.getDependencies(), pomFile);
		
		setBuildPath();
	}
	
	/**
	 * 
	 * @wonder should we deprecate it ?
	 * 
	 * @param cpEntries
	 * @throws Exception
	 */
	public static void removeUnusedDependencies(IClasspathEntry[] cpEntries) throws Exception {
		Project mavenProject = ProjectReader.getReader().read(Mevenide.getPlugin().getPom());
		List dependencies = mavenProject.getDependencies();
		List declaredDependencies = getDeclaredDependencies(cpEntries);
		List newDependencies = new ArrayList();
	
		if ( dependencies != null ) {
			for (int i = 0; i < dependencies.size(); i++) {
				for (int j = 0; j < declaredDependencies.size(); j++) {
					if( DependencyUtil.areEquals(
							(Dependency)declaredDependencies.get(j), 
							(Dependency)dependencies.get(i)) ) {
						newDependencies.add(declaredDependencies.get(j));
					}
				}
				//O(n*n)
				if ( matchReferencedProjects(
					Mevenide.getPlugin().getProject().getReferencedProjects(), 
					(Dependency)dependencies.get(i)) ) {
					newDependencies.add((Dependency)dependencies.get(i));
				}
			}
		}
		mavenProject.setDependencies(newDependencies);
		FileWriter writer = new FileWriter(Mevenide.getPlugin().getPom());
		new DefaultProjectMarshaller().marshall(writer, mavenProject);
	}

	private static boolean matchReferencedProjects(IProject[] referencedProjects, Dependency declaredDependency) throws Exception {
		for (int i = 0; i < referencedProjects.length; i++) {
			IProject referencedProject = referencedProjects[i];
			File referencedPom = ProjectUtil.getPom(referencedProject);
			//check if referencedPom exists, tho it should since we just have created it
			if ( !referencedPom.exists() ) {
				ProjectUtil.createPom(referencedProject);
			}
			ProjectReader reader = ProjectReader.getReader();
			Dependency projectDependency = reader.getDependency(referencedPom);
			if ( DependencyUtil.areEquals(projectDependency, declaredDependency) ) {
				return true;
			}
		}
		return false;
	}

	private static List getDeclaredDependencies(IClasspathEntry[] cpEntries) throws Exception {
		DependencyFactory dependencyFactory = DependencyFactory.getFactory(); 
		List declaredDependencies = new ArrayList();
		IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate(); 
		for (int i = 0; i < cpEntries.length; i++) {
			if ( cpEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY ) {
				String path = pathResolver.getAbsolutePath(cpEntries[i].getPath());
				declaredDependencies.add(dependencyFactory.getDependency(path));
			}
		}
		return declaredDependencies;
	}
	
	
}
