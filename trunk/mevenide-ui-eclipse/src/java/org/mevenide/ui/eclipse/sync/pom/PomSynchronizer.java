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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.mevenide.sync.AbstractPomSynchronizer;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.DefaultPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.IPathResolverDelegate;
import org.mevenide.project.io.ProjectSkeleton;

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
			IClasspathEntry[] cpEntries = JavaCore.create(project).getResolvedClasspath(true);
            for (int i = 0; i < cpEntries.length; i++) {
				updatePom(cpEntries[i]);
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
//			Environment.prepareEnv(project.getLocation().toFile().getAbsolutePath());
//			if ( !ProjectReader.isWellFormed(pom.getFullPath().toFile()) ) {
//				createPom();
//			}
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
     * create a new POM skeleton if no project.xml currently exists
     * 
     * @todo GENERALIZE add a POM_FILE_NAME property somewhere
     * @todo EXTERNALIZE POM_FILE_NAME
	 * @throws Exception
	 */
	private void createPom() throws Exception {
		pom = project.getFile("project.xml");
		if ( !pom.getLocation().toFile().exists() ) {
			String skel = ProjectSkeleton.getSkeleton(project.getName());
			pom.create(new ByteArrayInputStream(skel.getBytes()), false, null);
		}
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