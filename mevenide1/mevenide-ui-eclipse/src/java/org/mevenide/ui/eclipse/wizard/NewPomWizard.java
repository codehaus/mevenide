/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet </a>
 * @version $Id$
 */
public class NewPomWizard extends BasicNewResourceWizard implements INewWizard {

    private NewPomWizardFirstPage page;
    private NewPomWizardSecondPage page2;
    private ISelection selection;
    private IWorkbench workbench;
    
    private String containerName;
    
    public NewPomWizard() {
        super();
        setDefaultPageImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_POM_WIZ)); 
        setNeedsProgressMonitor(true);
    }

    public void addPages() {
        page = new NewPomWizardFirstPage((IStructuredSelection) selection);
        page2 = new NewPomWizardSecondPage();
        addPage(page);
        addPage(page2);
    }
    
       
    public boolean performFinish() {
        IFile file = page.createNewFile();
        if ( file == null ) {
            return false;
        }
        InputStream stream = null;
        try {
            stream = page2.getInitialContents();
            if ( file.exists() ) {
                file.setContents(stream, true, true, null);
            }
            else {
                file.create(stream, true, null);
            }
        }
        catch (Exception e) { }
        finally {
            try {
                if ( stream != null ) {
                    stream.close();
                }
            }
            catch (IOException e1) { }
        }
        selectAndReveal(file);
        // Open editor on new file.
        IWorkbenchWindow dw = getWorkbench().getActiveWorkbenchWindow();
        try {
            if ( dw != null ) {
                IWorkbenchPage page = dw.getActivePage();
                if ( page != null ) {
                    IDE.openEditor(page, file, true);
                }
            }
        } catch (PartInitException e) {
            final String title = Mevenide.getResourceString("NewPomWizard.Error.Title");
            final String message = "Unable to open an editor for " + file.getLocation() + ".";
            Mevenide.displayError(title, message, e);
        }
        return true;
    }

    public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
    	this.workbench = workbench;
    	this.selection = currentSelection;
    	initializeDefaultPageImageDescriptor();
    }
    
    protected void setContainerName(String containerName) {
        this.containerName = containerName;
    }
    
    public String getContainerName() {
        return containerName;
    }
    
    public IWorkbench getWorkbench() {
        return workbench;
    }
}