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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.ide.IHelpContextIds;
import org.eclipse.ui.internal.ide.misc.ResourceAndContainerGroup;

/**
 *   
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */ 
public class NewPomWizardFirstPage extends WizardPage  implements Listener {
    private static final int SIZING_CONTAINER_GROUP_HEIGHT = 250;
    
    private Text containerText;
	private Text fileText;
	private IStructuredSelection currentSelection;

	
	private ResourceAndContainerGroup resourceGroup;
	
	//initial value stores
	private String initialFileName;
	private IPath initialContainerFullPath;
	private IFile newFile;
	
	public NewPomWizardFirstPage(String pageName, IStructuredSelection selection) {
		super(pageName);
		setTitle("Multi-page Editor File");
		setDescription("This wizard creates a new file with *.xml extension that can be opened by a multi-page editor.");
		setPageComplete(false);
		this.currentSelection = selection;
	}
	
	public void handleEvent(Event event) {
		setPageComplete(validatePage());
	}
	
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		// top level group
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(
			GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(parent.getFont());
		WorkbenchHelp.setHelp(topLevel, IHelpContextIds.NEW_FILE_WIZARD_PAGE);

		// resource and container group
		resourceGroup = new ResourceAndContainerGroup(topLevel, this, getNewFileLabel(), "MESSAGE", false, SIZING_CONTAINER_GROUP_HEIGHT); //$NON-NLS-1$
		resourceGroup.setAllowExistingResources(false);
		initialPopulateContainerNameField();
		
		if (initialFileName != null)
			resourceGroup.setResource(initialFileName);
		validatePage();
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}
	
	protected String getNewFileLabel() {
		return IDEWorkbenchMessages.getString("WizardNewFileCreationPage.fileLabel"); //$NON-NLS-1$
	}
	
	protected void initialPopulateContainerNameField() {
		if (initialContainerFullPath != null)
			resourceGroup.setContainerFullPath(initialContainerFullPath);
		else {
			Iterator enum = currentSelection.iterator();
			if (enum.hasNext()) {
				Object object = enum.next();
				IResource selectedResource = null;
				if (object instanceof IResource) {
					selectedResource = (IResource)object;
				} else if (object instanceof IAdaptable) {
					selectedResource = (IResource)((IAdaptable)object).getAdapter(IResource.class);
				}
				if (selectedResource != null) {
					if (selectedResource.getType() == IResource.FILE)
						selectedResource = selectedResource.getParent();
					if (selectedResource.isAccessible())
						resourceGroup.setContainerFullPath(selectedResource.getFullPath());
				}
			}
		}
	}
	
	protected boolean validatePage() {
		boolean valid = true;
		
		if (!resourceGroup.areAllValuesValid()) {
			// if blank name then fail silently
			if (resourceGroup.getProblemType() == ResourceAndContainerGroup.PROBLEM_RESOURCE_EMPTY
				|| resourceGroup.getProblemType() == ResourceAndContainerGroup.PROBLEM_CONTAINER_EMPTY) {
				setMessage(resourceGroup.getProblemMessage());
				setErrorMessage(null);			
			} else
				setErrorMessage(resourceGroup.getProblemMessage());
			valid = false;
		}
		
			
		// validateLinkedResource sets messages itself
		if (valid) {
			setMessage(null);
			setErrorMessage(null);
		}
		return valid;
	}
	
	public String getFileName() {
		if (resourceGroup == null)
			return initialFileName;
			
		return resourceGroup.getResource();
	}
	
	private void handleBrowse() {
		ContainerSelectionDialog dialog =
			new ContainerSelectionDialog(
				getShell(),
				ResourcesPlugin.getWorkspace().getRoot(),
				false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path)result[0]).toOSString());
			}
		}
	}
	
	protected IFile createFileHandle(IPath filePath) {
		return IDEWorkbenchPlugin.getPluginWorkspace().getRoot().getFile(filePath);
	}
	
	protected InputStream getInitialContents() {
	    //@todo
		return null;
	}
	
	public IFile createNewFile() {
		if (newFile != null)
			return newFile;

		// create the new file and cache it if successful

		final IPath containerPath = resourceGroup.getContainerFullPath();
		IPath newFilePath = containerPath.append(resourceGroup.getResource());
		final IFile newFileHandle = createFileHandle(newFilePath);
		final InputStream initialContents = getInitialContents();
		
		WorkspaceModifyOperation op = new WorkspaceModifyOperation(null) {
			protected void execute(IProgressMonitor monitor) throws CoreException,
				InterruptedException
			{
				try {
					monitor.beginTask(IDEWorkbenchMessages.getString("WizardNewFileCreationPage.progress"), 2000); //$NON-NLS-1$
					ContainerGenerator generator = new ContainerGenerator(containerPath);
					generator.generateContainer(new SubProgressMonitor(monitor, 1000));
					createFile(newFileHandle,initialContents, new SubProgressMonitor(monitor, 1000));
				} finally {
					monitor.done();
				}
			}
		};

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return null;
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof CoreException) {
				ErrorDialog.openError(
					getContainer().getShell(), // Was Utilities.getFocusShell()
					IDEWorkbenchMessages.getString("WizardNewFileCreationPage.errorTitle"),  //$NON-NLS-1$
					null,	// no special message
					((CoreException) e.getTargetException()).getStatus());
			}
			else {
				// CoreExceptions are handled above, but unexpected runtime exceptions and errors may still occur.
				IDEWorkbenchPlugin.log(MessageFormat.format("Exception in {0}.getNewFile(): {1}", new Object[] {getClass().getName(), e.getTargetException()}));//$NON-NLS-1$
				MessageDialog.openError(getContainer().getShell(), IDEWorkbenchMessages.getString("WizardNewFileCreationPage.internalErrorTitle"), IDEWorkbenchMessages.format("WizardNewFileCreationPage.internalErrorMessage", new Object[] {e.getTargetException().getMessage()})); //$NON-NLS-2$ //$NON-NLS-1$
			}
			return null;
		}

		newFile = newFileHandle;

		return newFile;
	}
	
	protected void createFile(IFile fileHandle, InputStream contents, IProgressMonitor monitor) throws CoreException {
		if (contents == null)
			contents = new ByteArrayInputStream(new byte[0]);

		try {
			// Create a new file resource in the workspace
			fileHandle.create(contents, false, monitor);
		}
		catch (CoreException e) {
			// If the file already existed locally, just refresh to get contents
			if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED)
				fileHandle.refreshLocal(IResource.DEPTH_ZERO, null);
			else
				throw e;
		}

		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}
	
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	
	
}