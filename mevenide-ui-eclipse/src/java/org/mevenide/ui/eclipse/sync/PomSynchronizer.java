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
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.mevenide.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;
import org.mevenide.project.source.SourceDirectoryUtil;
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
		assertPomNotEmpty();
		pathResolver = new DefaultPathResolver(); 
	}

	private void assertPomNotEmpty() {
		try {
			if ( pom.exists() ) {
				InputStream inputStream = pom.getContents(true);
			
				if ( inputStream.read() == -1 ) {
					InputStream stream = PomSynchronizer.class.getResourceAsStream("/templates/standard/project.xml"); 
					pom.setContents(stream, true, true, null);
					stream.close();
				}
				inputStream.close();
			}
		} catch (Exception e) {
			log.debug("Unable to check if POM already exists due to : " + e);
		}
	}

    /**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer
     */
	protected void mavenize() {
		try {
			assertPomNotEmpty();
			
			synchronize(project);
			
			refresh(project);
			
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
		}
		catch (Exception e) {
			log.debug("Unable to create POM due to : " + e);
		}
	}
	
	/**
     * @see org.mevenide.core.sync.AbstractPomSynchronizer#postSynchronization()
     */
	public void postSynchronization() {
		
	}

	public void synchronize(IProject project) throws Exception {
		SourceDirectoryUtil.resetSourceDirectories(Mevenide.getPlugin().getPom());
	
		DependencyGroup dependencyGroup = DependencyGroupMarshaller.getDependencyGroup(project, Mevenide.getPlugin().getFile("statedDependencies.xml"));
		SourceDirectoryGroup sourceGroup = SourceDirectoryGroupMarshaller.getSourceDirectoryGroup(project, Mevenide.getPlugin().getFile("sourceTypes.xml"));
	
		updatePom(sourceGroup, dependencyGroup, Mevenide.getPlugin().getPom());
	}

	public void updatePom(SourceDirectoryGroup sourceGroup, DependencyGroup dependencyGoup, File pomFile) throws Exception {
		Mevenide.getPlugin().createProjectProperties();
		
		SourceDirectoryUtil.resetSourceDirectories(pomFile);
		
		ProjectWriter pomWriter = ProjectWriter.getWriter();
		
		//WICKED if/else
		for (int i = 0; i < sourceGroup.getSourceDirectories().size(); i++) {
			SourceDirectory directory = (SourceDirectory) sourceGroup.getSourceDirectories().get(i);
			if ( directory.isSource() ) {
				pomWriter.addSource(directory.getDirectoryPath(), pomFile, directory.getDirectoryType());
			}
			if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_RESOURCE ) ) {
				pomWriter.addResource(directory.getDirectoryPath(), pomFile);
			}
			if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_TEST_RESOURCE ) ) {
				log.debug("Unimplemented feature : " + ProjectConstants.MAVEN_TEST_RESOURCE );
			}				
		}
		
		List dependencies = dependencyGoup.getDependencies();
		log.debug("Writing back " + dependencies.size() + " dependencies to file '" + pomFile.getName() +"'");
		//dependencies.addAll(ProjectUtil.getCrossProjectDependencies());
		
		pomWriter.setDependencies(dependencies, pomFile);
		
		Mevenide.getPlugin().setBuildPath();
	}
	
	private void refresh(IProject project) throws Exception {
		IFile projectFile = project.getFile("project.xml");
		projectFile.refreshLocal(IProject.DEPTH_ZERO, null);
		IFile propertiesFile = project.getFile("project.properties");
		if ( propertiesFile.exists() ) {
			propertiesFile.refreshLocal(IProject.DEPTH_ZERO, null);
		}
	}
}