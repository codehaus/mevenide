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
package org.mevenide.ui.eclipse.sync;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import org.mevenide.core.pom.PomReader;
import org.mevenide.core.pom.PomWriter;
import org.mevenide.core.sync.AbstractPomSynchronizer;
import org.mevenide.core.sync.ISynchronizer;
import org.mevenide.ui.eclipse.MavenPlugin;

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
        pathResolver = new DefaultPathResolverDelegate(); 
	}

    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer
     */
	protected void mavenize() {
		try {
			IClasspathEntry[] cpEntries = JavaCore.create(project).getRawClasspath();
            for (int i = 0; i < cpEntries.length; i++) {
				updatePomDependencies(cpEntries[i]);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#preSynchronization()
     */
	public void preSynchronization() {
		try {
            //Environment.prepareEnv(project.getLocation().toFile().getAbsolutePath());
			if ( !PomReader.isWellFormed(pom.getFullPath().toFile()) ) {
				createPom();
			}
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
     * create a new POM skeleton if no project.xml exists
     * 
     * @todo GENERALIZE add a POM_FILE_NAME property somewhere
     * @todo EXTERNALIZE POM_FILE_NAME
	 * @throws Exception
	 */
	private void createPom() throws Exception {
		pom = project.getFile("project.xml");
		if ( !pom.getLocation().toFile().exists() ) {
			String skel = PomWriter.getWriter().getSkeleton(project.getName());
			pom.create(new ByteArrayInputStream(skel.getBytes()), false, null);
		}
	}

    /**
     * add this classpathentry' information to the pom  
     * @refactor PATTERN use Visitor instead
     * 
     * @param classpathEntry
     * @throws Exception
     */
	private void updatePomDependencies(IClasspathEntry classpathEntry) throws Exception {
		
		switch (classpathEntry.getEntryKind()) {
			case IClasspathEntry.CPE_SOURCE :
					addSource(classpathEntry);
					return;
			case IClasspathEntry.CPE_LIBRARY :
					addDependency(classpathEntry);
					return;
			case IClasspathEntry.CPE_VARIABLE :
					//IClasspathEntry resolved = JavaCore.getResolvedClasspathEntry(classpathEntry);
					//updatePomDependencies(resolved);
					return;
            default : throw new Exception("Unknown classpath entry kind (" + classpathEntry.getEntryKind() + ")");
			//@todo FUNCTIONAL eclipse projects dependencies
			//@todo FUNCTIONAL dependencies of type CPE_CONTAINER
		}

	}

    /**
     * add to the pom the dependency described by the given classpathentry
     * 
     * @param classpathEntry
     * @throws Exception
     */
	private void addDependency(IClasspathEntry classpathEntry) throws Exception {
		PomWriter.getWriter().addDependency(
            pathResolver.getAbsolutePath(classpathEntry.getPath()), 
            getPom()
        );
	}
    
    /**
     * utility method 
     * return the File instance correponding to the IFile pom attribute
     * 
     * @return the maven Project Object Model
     */
	private File getPom() {
		return new File(pathResolver.getAbsolutePath(pom.getLocation()));
	}
	
    /**
     * add to the pom the source directory described by the given classapthentry
     * @pre classpathentry.getEntryKind() == CPE_SOURCE  
     * 
     * @param classpathEntry
     * @throws Exception
     */
	private void addSource(IClasspathEntry classpathEntry) throws Exception {
		
        String pathToAdd = pathResolver.computePathToAdd(classpathEntry, project);
		
        PomWriter.getWriter().addSource(
            pathToAdd, 
            getPom(), 
            pathResolver.getMavenSourceType(classpathEntry, project)
        );
	}

}