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
package org.mevenide.ui.eclipse.sync.wizard;

import java.io.File;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.Wizard;
import org.mevenide.ProjectConstants;
import org.mevenide.project.io.ProjectWriter;
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
public class SynchronizeWizard extends Wizard {
	
	private SourceDirectorySynchronizeWizardPage sourcePage;
	private DependencySynchronizeWizardPage dependencyPage;
	
	private IProject project;
	
	public SynchronizeWizard(IProject project) {
		this.project = project;
		sourcePage = new SourceDirectorySynchronizeWizardPage();
		dependencyPage = new DependencySynchronizeWizardPage();
		addPage(sourcePage);
		addPage(dependencyPage);
	}

	public boolean performFinish() {
		try {
			
			SourceDirectoryGroup sourceGroup = sourcePage.getInput();
			DependencyGroup dependencyGoup = dependencyPage.getInput();
			
			ProjectWriter pomWriter = ProjectWriter.getWriter();
			IPathResolverDelegate pathResolver = new DefaultPathResolverDelegate();
			
			IFile pom = project.getFile(new Path("project.xml"));
			
			File pomFile = new File(pathResolver.getAbsolutePath(pom.getLocation())); 
			
			//WICKED if/else
			for (int i = 0; i < sourceGroup.getSourceDirectories().size(); i++) {
				SourceDirectory directory = (SourceDirectory) sourceGroup.getSourceDirectories().get(i);
				if ( isSource(directory) ) {
					pomWriter.addSource(directory.getDirectoryPath(), pomFile, directory.getDirectoryType());
				}
				if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_RESOURCE ) ) {
					pomWriter.addResource(directory.getDirectoryPath(), pomFile);
				}
				if ( directory.getDirectoryType().equals(ProjectConstants.MAVEN_TEST_RESOURCE ) ) {
				}				
			}
			
//			for (int i = 0; i < dependencyGoup.getDependencies().size(); i++) {
//				Dependency dependency = (Dependency) dependencyGoup.getDependencies().get(i);
//				pomWriter.addDependency(dependency, pomFile);
//			}

			pomWriter.setDependencies(dependencyGoup.getDependencies(), pomFile);
			
			sourcePage.saveState();
			dependencyPage.saveState();
			
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		
		return true;
	}
	
	public boolean performCancel() {
		return super.performCancel();
	}
	
	private boolean isSource(SourceDirectory directory) {
		return directory.getDirectoryType().equals(ProjectConstants.MAVEN_ASPECT_DIRECTORY)
				|| directory.getDirectoryType().equals(ProjectConstants.MAVEN_SRC_DIRECTORY)
				|| directory.getDirectoryType().equals(ProjectConstants.MAVEN_TEST_DIRECTORY)
				|| directory.getDirectoryType().equals(ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY);
	}

	public IProject getProject() {
		return project;
	}

}
