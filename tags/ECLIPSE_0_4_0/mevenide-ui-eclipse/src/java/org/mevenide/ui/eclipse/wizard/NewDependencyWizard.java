/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.wizard.MavenProjectWizardDependencySettingsPage.Dependencies;
import org.mevenide.util.StringUtils;

/**
 * @author <a href="mailto:jens@iostream.net">Jens Andersen </a>, Last updated by $Author$
 * @version $Id$
 */
public class NewDependencyWizard extends Wizard implements INewWizard {
	private Dependencies fDependencies;
	private NewDependencyWizardPage fPage;
	private Dependency fDependency;
	
	public NewDependencyWizard(Dependencies dependencies) {
		fDependencies = dependencies;
		setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
		setWindowTitle(Mevenide.getResourceString("NewDependencyWizard.title")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		fPage = new NewDependencyWizardPage();
		addPage(fPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		//if(!fPage.getName().equalsIgnoreCase(""))
		//{
		fDependency = new Dependency();
		//fDependency.setId(fPage.getId());
		fDependency.setArtifactId(fPage.getArtifactId());
		fDependency.setGroupId(fPage.getGroupId());
		fDependency.setJar(fPage.getJar());
		fDependency.setName(fPage.getName());
		fDependency.setType(fPage.getType());
		fDependency.setUrl(fPage.getUrl());
		fDependency.setVersion(fPage.getVersion());
		fDependencies.addDependency(fDependency);
		return true;
		//}
		//not sure why name shouldnot be empty ?  
		//return false;
	}

	
    public boolean canFinish() {
        return !StringUtils.isNull(fPage.getArtifactId()) && !StringUtils.isNull(fPage.getGroupId());
    }
	
	/*
	 * (non-Javadoc)
	 * 
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