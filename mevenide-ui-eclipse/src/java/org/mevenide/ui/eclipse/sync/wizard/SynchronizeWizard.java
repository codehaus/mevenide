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

import org.eclipse.jface.wizard.Wizard;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SynchronizeWizard extends Wizard {
	
	private SourceDirectorySynchronizeWizardPage sourcePage;
	private DependencySynchronizeWizardPage dependencyPage;
	
	public SynchronizeWizard() {
		
		sourcePage = new SourceDirectorySynchronizeWizardPage();
		dependencyPage = new DependencySynchronizeWizardPage();
		addPage(sourcePage);
		addPage(dependencyPage);
	}

	public boolean performFinish() {
		return true;
	}
	
	public boolean performCancel() {
		return super.performCancel();
	}


}
