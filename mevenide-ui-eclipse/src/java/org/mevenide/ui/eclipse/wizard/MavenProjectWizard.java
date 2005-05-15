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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author	<a href="mailto:jens@iostream.net">Jens Andersen</a>, Last updated by $Author$
 * @version $Id$
 */
public class MavenProjectWizard extends NewElementWizard implements IExecutableExtension{
    private MavenProjectWizardFirstPage fFirstPage;
    private MavenProjectWizardSecondPage fSecondPage;
    
    private IConfigurationElement fConfigElement;	
	/**
	 * 
	 */
	public MavenProjectWizard() {
		super();
		//TODO - create image for the wizard
		//setDefaultPageImageDescriptor(JavaPluginImages.DESC_WIZBAN_NEWJPRJ);
		setWindowTitle(Mevenide.getResourceString("MavenProjectWizard.title")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard#addPages()
	 */
    public void addPages() {
        fFirstPage = new MavenProjectWizardFirstPage();
        addPage(fFirstPage);
        fSecondPage= new MavenProjectWizardSecondPage(fFirstPage);
        addPage(fSecondPage);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
    	fSecondPage.performFinish(monitor);
        BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		selectAndReveal(fSecondPage.getJavaProject().getProject());
    }
    
    /*(non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getSchedulingRule()
     */
    protected ISchedulingRule getSchedulingRule() {
    	return fFirstPage.getProjectHandle();
    }
    
    
    protected void handleFinishException(Shell shell, InvocationTargetException e) {
        String title= Mevenide.getResourceString("MavenProjectWizard.op_error.title");//$NON-NLS-1$
        String message= Mevenide.getResourceString("MavenProjectWizard.op_error_create.message");//$NON-NLS-1$
        ExceptionHandler.handle(e, getShell(), title, message);
    }	
    
    /*
     * Stores the configuration element for the wizard.  The config element will be used
     * in <code>performFinish</code> to set the result perspective.
     */
    public void setInitializationData(IConfigurationElement cfig, String propertyName, Object data) {
        fConfigElement= cfig;
    }
    
    /* (non-Javadoc)
     * @see IWizard#performCancel()
     */
    public boolean performCancel() {
        fSecondPage.performCancel();
        return super.performCancel();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    public boolean canFinish() {
        return super.canFinish();
    }
}
