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

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.mevenide.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.sync.AbstractPomSynchronizer;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.DependencyGroup;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupMarshaller;
import org.mevenide.ui.eclipse.sync.model.SourceDirectory;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroup;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroupMarshaller;
import org.mevenide.ui.eclipse.util.FileUtil;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 */
public class PomSynchronizer extends AbstractPomSynchronizer implements ISynchronizer {
	private static Log log = LogFactory.getLog(PomSynchronizer.class);
	
	/** the project under control */
	private IProject project;
    
    /** the POM under control */
	private IFile pom;

    /** helper instance */
    private IPathResolver pathResolver;
    

    
    public void initialize() {
		this.project = Mevenide.getPlugin().getProject();
		this.pom = project.getFile("project.xml");
		pathResolver = new DefaultPathResolver(); 
	}

    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer
     */
	protected void mavenize() {
		try {
			
			if ( SynchronizerUtil.shouldSynchronizePom(project) ) {
				log.debug("About to update pom");
				DependencyGroup dependencyGroup = DependencyGroupMarshaller.getDependencyGroup(project, Mevenide.getPlugin().getFile("statedDependencies.xml"));
				SourceDirectoryGroup sourceGroup = SourceDirectoryGroupMarshaller.getSourceDirectoryGroup(project, Mevenide.getPlugin().getFile("sourceTypes.xml"));
				updatePom(sourceGroup, dependencyGroup, Mevenide.getPlugin().getPom());
			}
			
		}
		catch (Exception e) {
			log.debug("Unable to synchronize project '" + project.getName() + "' due to : " + e);
		}
	}

	/**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#preSynchronization()
     */
	public void preSynchronization() {
		try {
			Mevenide.getPlugin().createPom();
			FileUtil.assertPomNotEmpty(pom);
		}
		catch (Exception e) {
			log.debug("Unable to create POM due to : " + e);
		}
	}
	
	/**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#postSynchronization()
     */
	public void postSynchronization() {
		try {
			FileUtil.refresh(project);
		} 
		catch (Exception e) {
			log.debug("Unable to create POM due to : " + e);
		}
	}



	public void updatePom(SourceDirectoryGroup sourceGroup, DependencyGroup dependencyGoup, File pomFile) throws Exception {
		Mevenide.getPlugin().createProjectProperties();
		
		ProjectWriter pomWriter = ProjectWriter.getWriter();
		
		pomWriter.resetSourceDirectories(pomFile);
		
		//WICKED if/else -- to be removed when SourceDirectoryBatchUpdate is done - tho this will only move the problem backward
		for (int i = 0; i < sourceGroup.getSourceDirectories().size(); i++) {
			SourceDirectory directory = (SourceDirectory) sourceGroup.getSourceDirectories().get(i);
			if ( directory.isSource() ) {
				pomWriter.addSource(directory.getDirectoryPath(), pomFile, directory.getDirectoryType());
			}
			if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_RESOURCE ) ) {
				pomWriter.addResource(directory.getDirectoryPath(), pomFile);
			}
			if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_TEST_RESOURCE ) ) {
				pomWriter.addUnitTestResource(directory.getDirectoryPath(), pomFile);
			}				
		}
		
		List dependencies = dependencyGoup.getDependencies();
		log.debug("Writing back " + dependencies.size() + " dependencies to file '" + pomFile.getName() +"'");
		//dependencies.addAll(ProjectUtil.getCrossProjectDependencies());
		
		pomWriter.setDependencies(dependencies, pomFile);
		
		Mevenide.getPlugin().setBuildPath();
	}
	
}