/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */

package org.mevenide.ui.eclipse.wizard;

import org.apache.maven.project.Dependency;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.util.StringUtils;

/**
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated by $Author$
 * @version $Id$
 */
public class NewDependencyWizard extends Wizard implements INewWizard {
	private DependencyWizardPage fPage;
	private Dependency fDependency;
	
	public NewDependencyWizard() {
        super();
        ImageDescriptor banner = Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.NEW_DEPENDENCY_WIZBAN);
		setDefaultPageImageDescriptor(banner);
		setWindowTitle(Mevenide.getResourceString("NewDependencyWizard.title")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		fPage = new NewDependencyWizardPage();
		addPage(fPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		fDependency = new Dependency();
		fDependency.setArtifactId(fPage.getArtifactId());
		fDependency.setGroupId(fPage.getGroupId());
		fDependency.setJar(fPage.getJar());
		fDependency.setName(fPage.getName());
		fDependency.setType(fPage.getType());
		fDependency.setUrl(fPage.getUrl());
		fDependency.setVersion(fPage.getVersion());
		return true;
	}
	
    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    public boolean canFinish() {
        return !StringUtils.isNull(fPage.getArtifactId()) && !StringUtils.isNull(fPage.getGroupId());
    }
	
	/** 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		//Do nothing
	}

    public Dependency getDependency() {
        return fDependency;
    }
}