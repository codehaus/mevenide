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
package org.mevenide.ui.eclipse.sync.pom;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.mevenide.project.DependencyFactory;
import org.mevenide.project.DependencyUtil;
import org.mevenide.project.InvalidDependencyException;
import org.mevenide.project.io.DefaultProjectMarshaller;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.sync.AbstractPomSynchronizer;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.source.DefaultPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.source.IPathResolverDelegate;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class PomSynchronizer extends AbstractPomSynchronizer implements ISynchronizer {
	/** per-project synchronizers related */
    private static Hashtable pomSynchronizers = new Hashtable();
	
    /** factory synchronization object */
    private static Object lock = new Object();

    /** the project under control */
	private IProject project;
    
    /** the POM under control */
	private IFile pom;

    /** helper instance */
    private IPathResolverDelegate pathResolver;
    
    /**
     * @todo GENERALIZE add a POM_FILE_NAME project property
	 */
	public void initialize() {
		
		this.project = MavenPlugin.getPlugin().getProject();
		this.pom = project.getFile("project.xml");
		assertPomNotEmpty();
		pathResolver = new DefaultPathResolverDelegate(); 
	}

	private void assertPomNotEmpty() {
		try {
			InputStream inputStream = pom.getContents(true);
			if ( inputStream.read() == -1 ) {
				InputStream stream = PomSynchronizer.class.getResourceAsStream("/templates/standard/project.xml"); 
				pom.setContents(stream, true, true, null);
				stream.close();
			}
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer
     */
	protected void mavenize() {
		try {
			assertPomNotEmpty();
			IClasspathEntry[] cpEntries = JavaCore.create(project).getResolvedClasspath(true);
			for (int i = 0; i < cpEntries.length; i++) {
            	updatePom(cpEntries[i]);
			}
			removeUnusedDependencies(cpEntries);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void removeUnusedDependencies(IClasspathEntry[] cpEntries) throws Exception {
		Project mavenProject = ProjectReader.getReader().read(getPom());
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
					getProject().getReferencedProjects(), 
					(Dependency)dependencies.get(i)) ) {
					newDependencies.add((Dependency)dependencies.get(i));
				}
			}
		}
		mavenProject.setDependencies(newDependencies);
		FileWriter writer = new FileWriter(getPom());
		new DefaultProjectMarshaller().marshall(writer, mavenProject);
	}

	private boolean matchReferencedProjects(IProject[] referencedProjects, Dependency declaredDependency) throws Exception {
		for (int i = 0; i < referencedProjects.length; i++) {
			IProject referencedProject = referencedProjects[i];
			IPath referencedProjectLocation = referencedProject.getLocation(); 
			File referencedPom = new File(pathResolver.getAbsolutePath(referencedProjectLocation.append("project.xml")) );
			//check if referencedPom exists, tho it should since we just have created it
			if ( !referencedPom.exists() ) {
				//@todo project isnot mavenized, mavenize it as well
			}
			ProjectReader reader = ProjectReader.getReader();
			Dependency projectDependency = reader.getDependency(referencedPom);
			if ( DependencyUtil.areEquals(projectDependency, declaredDependency) ) {
				return true;
			}
		}
		return false;
	}
	
	private List getDeclaredDependencies(IClasspathEntry[] cpEntries)
		throws Exception, InvalidDependencyException {
		DependencyFactory dependencyFactory = DependencyFactory.getFactory(); 
		List declaredDependencies = new ArrayList();
		for (int i = 0; i < cpEntries.length; i++) {
			if ( cpEntries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY ) {
				String path = pathResolver.getAbsolutePath(cpEntries[i].getPath());
				declaredDependencies.add(dependencyFactory.getDependency(path));
			}
		}
		return declaredDependencies;
	}
	
    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#preSynchronization()
     */
	public void preSynchronization() {
		try {
			//Environment.prepareEnv(project.getLocation().toFile().getAbsolutePath());
			//if ( !pom.getFullPath().toFile().exists() ) {
			MavenPlugin.getPlugin().createPom();
			//
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#postSynchronization()
     */
	public void postSynchronization() {
		
	}

	

    /**
     * add this classpathentry' information to the pom  
     *
     * @param classpathEntry
     * @throws Exception
     */
	private void updatePom(IClasspathEntry classpathEntry) throws Exception {
		ArtifactEntry entry = ArtifactEntry.getEntry(classpathEntry);
		ArtifactVisitor visitor = new ArtifactVisitor(this);
		if ( entry != null ) {
			entry.accept(visitor);
		}
	}

    /**
     * utility method 
     * return the File instance correponding to the IFile pom attribute
     * 
     * @return the maven Project Object Model
     */
	File getPom() {
		return new File(pathResolver.getAbsolutePath(pom.getLocation()));
	}
	
    public IPathResolverDelegate getPathResolver() {
		return pathResolver;
	}

	public IProject getProject() {
		return project;
	}

}