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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SynchronizeWizard extends Wizard {
	private static Log log = LogFactory.getLog(SynchronizeWizard.class);
	
	private SourceDirectoryMappingWizardPage sourcePage;
	private DependencyMappingWizardPage dependencyPage;
	
	private IProject project;
	
	public SynchronizeWizard(IProject project) {
		this.project = project;
		sourcePage = new SourceDirectoryMappingWizardPage();
		dependencyPage = new DependencyMappingWizardPage();
		addPage(sourcePage);
		addPage(dependencyPage);
	}

	public boolean performFinish() {
		try {
			
			
			sourcePage.saveState();
			log.debug("SourceDirectories saved");
			
			dependencyPage.saveState();
			log.debug("Dependencies saved");
			
			SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
		}
		catch ( Exception ex ) {
			log.debug("Unable to synchronize POM due to : " + ex);
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