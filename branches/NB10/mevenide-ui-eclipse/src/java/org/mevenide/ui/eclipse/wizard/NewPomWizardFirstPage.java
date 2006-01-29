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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet </a>
 * @version $Id$
 */
public class NewPomWizardFirstPage extends WizardPage implements Listener {

    private static final Log log = LogFactory.getLog(NewPomWizardFirstPage.class);
    
    private static final int SIZING_CONTAINER_GROUP_HEIGHT = 250;
    
    private IStructuredSelection currentSelection;
    
    private ResourceAndContainerGroup resourceGroup;
    
    //initial value stores
    private String initialFileName;
    private IPath initialContainerFullPath;
    private IFile newFile;

    public NewPomWizardFirstPage(IStructuredSelection selection) {
        super(Mevenide.getResourceString("NewPomWizardFirstPage.Name")); //$NON-NLS-1$
        setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.MAVEN_POM_WIZ));
        setTitle(Mevenide.getResourceString("NewPomWizardFirstPage.Title")); //$NON-NLS-1$
        setDescription(Mevenide.getResourceString("NewPomWizardFirstPage.Description")); //$NON-NLS-1$
        setPageComplete(false);
        this.currentSelection = selection;
    }

    public void handleEvent(Event event) {
        setPageComplete(validatePage());
    }

    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        
        // top level group
        Composite topLevel = new Composite(parent, SWT.NONE);
        topLevel.setLayout(new GridLayout());
        topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());
    
        // resource and container group
        resourceGroup = new ResourceAndContainerGroup(topLevel, 
                										this, 
                										getNewFileLabel(), 
                										Mevenide.getResourceString("NewPomWizardFirstPage.File.Name"),  //$NON-NLS-1$
                										false, 
                										SIZING_CONTAINER_GROUP_HEIGHT); 
        resourceGroup.setAllowExistingResources(false);
        
        initialPopulateContainerNameField();
        
        if ( initialFileName != null ) {
            resourceGroup.setResource(initialFileName);
        }
        validatePage();
        
        // Show description on opening
        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }

    protected String getNewFileLabel() {
        return Mevenide.getResourceString("NewPomWizardFirstPage.File.Label"); //$NON-NLS-1$
    }

    protected void initialPopulateContainerNameField() {
        if ( initialContainerFullPath != null )
            resourceGroup.setContainerFullPath(initialContainerFullPath);
        else {
            Iterator en = currentSelection.iterator();
            if ( en.hasNext() ) {
                Object object = en.next();
                IResource selectedResource = null;
                if ( object instanceof IResource ) {
                    selectedResource = (IResource) object;
                }
                else if ( object instanceof IAdaptable ) {
                    selectedResource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
                }
                if ( selectedResource != null ) {
                    if ( selectedResource.getType() == IResource.FILE ) {
                        selectedResource = selectedResource.getParent();
                    }
                    if ( selectedResource.isAccessible() ) {
                        resourceGroup.setContainerFullPath(selectedResource.getFullPath());
                    }
                }
            }
        }
    }

    protected boolean validatePage() {
        boolean valid = true;
        if ( !resourceGroup.areAllValuesValid() ) {
            // if blank name then fail silently
            if ( resourceGroup.getProblemType() == ResourceAndContainerGroup.PROBLEM_RESOURCE_EMPTY || 
                 resourceGroup.getProblemType() == ResourceAndContainerGroup.PROBLEM_CONTAINER_EMPTY ) {
                setMessage(resourceGroup.getProblemMessage());
                setErrorMessage(null);
            }
            else {
                setErrorMessage(resourceGroup.getProblemMessage());
            }
            valid = false;
        }
        
        if ( valid ) {
            setMessage(null);
            setErrorMessage(null);
        }
        return valid;
    }

    public String getFileName() {
        if ( resourceGroup == null ) {
            return initialFileName;
        }
        return resourceGroup.getResource();
    }

    protected IFile createFileHandle(IPath filePath) {
        return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(filePath);
    }

  
    public IFile createNewFile() {
        if ( newFile != null ) {
            return newFile;
        }
        // create the new file and cache it if successful
        final IPath containerPath = resourceGroup.getContainerFullPath();
        IPath newFilePath = containerPath.append(resourceGroup.getResource());
        final IFile newFileHandle = createFileHandle(newFilePath);
        
        ((NewPomWizard) getWizard()).setContainerName(newFilePath.toFile().getParentFile().getName());
        
        WorkspaceModifyOperation op = new WorkspaceModifyOperation(null) {
            protected void execute(IProgressMonitor monitor) throws CoreException, InterruptedException {
                try {
                    monitor.beginTask(Mevenide.getResourceString("NewPomWizardFirstPage.Progress"), 2000); //$NON-NLS-1$
                    ContainerGenerator generator = new ContainerGenerator(containerPath);
                    generator.generateContainer(new SubProgressMonitor(monitor, 1000));
                    createFile(newFileHandle, null, new SubProgressMonitor(monitor, 1000));
                }
                finally {
                    monitor.done();
                }
            }
        };

        try {
            getContainer().run(true, true, op);
        }
        catch (InterruptedException e) {
            return null;
        }
        catch (InvocationTargetException e) {
            if ( e.getTargetException() instanceof CoreException ) {
                ErrorDialog.openError(getContainer().getShell(), 
                        			  Mevenide.getResourceString("NewPomWizardFirstPage.Error.Title"), //$NON-NLS-1$
                        			  null, 
                        			  ((CoreException) e.getTargetException()).getStatus());
            }
            else {
                log.error("Error creating new file", e.getTargetException()); //$NON-NLS-1$
                MessageDialog.openError(getContainer().getShell(), 
                        				Mevenide.getResourceString("NewPomWizardFirstPage.InternalError.Title"),  //$NON-NLS-1$
                        				Mevenide.getResourceString("NewPomWizardFirstPage.InternalError.Message",  //$NON-NLS-1$
                        				new String[]{e.getTargetException().getMessage()}));
            }
            return null;
        }
        
        newFile = newFileHandle;
        return newFile;
    }

    protected void createFile(IFile fileHandle, InputStream contents, IProgressMonitor monitor) throws CoreException {
        try {
	        if ( contents == null ) {
	            contents = new ByteArrayInputStream(new byte[0]);
	        }
	        try {
	            // Create a new file resource in the workspace
	            fileHandle.create(contents, false, monitor);
	        }
	        catch (CoreException e) {
	            // If the file already existed locally, just refresh to get contents
	            if ( e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED ) {
	                fileHandle.refreshLocal(IResource.DEPTH_ZERO, null);
	            }
	            else {
	                throw e;
	            }
	        }
        }
        finally {
            if ( contents != null ) {
                try {
                    contents.close();
                }
                catch (IOException e) {
                    //@todo : generated catch block
                    String message = null; 
                    log.error(message, e);
                }
            }
        }
        if ( monitor.isCanceled() ) {
            throw new OperationCanceledException();
        }
    }
}