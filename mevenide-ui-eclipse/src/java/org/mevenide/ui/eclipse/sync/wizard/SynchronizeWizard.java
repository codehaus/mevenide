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
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.IPathResolver;

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
			IPathResolver pathResolver = new DefaultPathResolver();
			
			IFile pom = project.getFile(new Path("project.xml"));
			File pomFile = new File(pathResolver.getAbsolutePath(pom.getLocation())); 
			
			sourcePage.saveState();
			dependencyPage.saveState();
			
			SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		
		return true;
	}

	public boolean performCancel() {
		return super.performCancel();
	}
	
	public IProject getProject() {
		return project;
	}

}
