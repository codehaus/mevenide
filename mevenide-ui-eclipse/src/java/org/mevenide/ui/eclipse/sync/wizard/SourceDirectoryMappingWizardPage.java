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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.SourceDirectory;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroup;
import org.mevenide.ui.eclipse.sync.model.SourceDirectoryGroupMarshaller;
import org.mevenide.ui.eclipse.sync.view.SourceDirectoryMappingViewControl;
import org.mevenide.ui.eclipse.util.ResourceSorter;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SourceDirectoryMappingWizardPage extends WizardPage {
	
	private static Log log = LogFactory.getLog(SourceDirectoryMappingWizardPage.class);
	
	private TableViewer viewer;
	
	private IProject project;
	
	public SourceDirectoryMappingWizardPage() {
		super("Source Directories Synchronization");
		setTitle("Source Directory Synchronization");
		setDescription("Please enter source directory types.");
		setImageDescriptor(Mevenide.getImageDescriptor("source-synch-64.gif"));
		
	}

	

	public void createControl(Composite arg0) {
		Composite composite = new Composite(arg0, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		createViewer(composite);
		createButtons(composite);
		
		setControl(composite);
	}



	private void createViewer(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		viewer = SourceDirectoryMappingViewControl.getViewer(composite, SWT.BORDER);
		setInput(((SynchronizeWizard)getWizard()).getProject());
	}
	
	private void createButtons(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		Button addButton = new Button(composite, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setToolTipText("Add a dependency");
		GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.grabExcessHorizontalSpace = true;
		addButton.setLayoutData(addButtonData);
		addButton.setEnabled(true);
		
		Button removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setToolTipText("Remove dependency");
		GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.grabExcessHorizontalSpace = true;
		removeButton.setLayoutData(removeButtonData);
		removeButton.setEnabled(true);
		
		Button refreshButton = new Button(composite, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Refresh project dependencies");
		GridData refreshButtonData = new GridData(GridData.FILL_HORIZONTAL);
		refreshButtonData.grabExcessHorizontalSpace = true;
		refreshButton.setLayoutData(refreshButtonData);
		
		addButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						IContainer container = openSourceDirectoryDialog();
						if ( container != null ) {
							SourceDirectory directory = new SourceDirectory(container.getFullPath().removeFirstSegments(1).toOSString());
							((SourceDirectoryGroup) viewer.getInput()).addSourceDirectory(directory);
							viewer.refresh();
						}
						
					}
				}
		);
		
		removeButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						TableItem[] items = viewer.getTable().getSelection();
						for (int i = 0; i < items.length; i++) {
							TableItem item = items[i];
							//((SourceDirectoryGroup) viewer.getInput()).getSourceDirectories().remove(item.getData());
							((SourceDirectoryGroup) viewer.getInput()).excludeSourceDirectory((SourceDirectory) item.getData());
							viewer.refresh();
						}
					}
				}
		);
		
		refreshButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						initInput(project);
					}
				}
		);
	}
	
	private void initInput(IProject project) {
		viewer.setInput(new SourceDirectoryGroup(project));
	}
	
	/**
	 * method extracted from org.eclipse.jdt.internal.ui.wizards.buildpaths.OtuputLocationDialog
	 * 
	 * @return
	 */
	private IContainer openSourceDirectoryDialog() {
		IWorkspaceRoot root= project.getWorkspace().getRoot();
		Class[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
		ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
		IProject[] allProjects= root.getProjects();
		
		//modified ArrayList rejectedElements= new ArrayList(allProjects.length);
		ArrayList rejectedElements= new ArrayList();
		for (int i= 0; i < allProjects.length; i++) {
			if (!allProjects[i].equals(project)) {
				rejectedElements.add(allProjects[i]);
			}
		}
		
		//added
		List list = ((SourceDirectoryGroup)viewer.getInput()).getSourceDirectories();
		for (int i = 0; i < list.size(); i++) {
			SourceDirectory directory = (SourceDirectory) list.get(i);
			System.out.println(directory.getDirectoryPath());
			rejectedElements.add(project.getFolder(directory.getDirectoryPath()));
		} 
	
		ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

		ILabelProvider lp= new WorkbenchLabelProvider();
		ITreeContentProvider cp= new WorkbenchContentProvider();

		IResource initSelection= null;
		
		FolderSelectionDialog dialog= new FolderSelectionDialog(getShell(), lp, cp);
		dialog.setTitle(NewWizardMessages.getString("OutputLocationDialog.ChooseOutputFolder.title")); //$NON-NLS-1$
		dialog.setValidator(validator);
		dialog.setMessage(NewWizardMessages.getString("OutputLocationDialog.ChooseOutputFolder.description")); //$NON-NLS-1$
		dialog.addFilter(filter);
		dialog.setInput(root);
		dialog.setInitialSelection(initSelection);
		dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));

		if (dialog.open() == ElementTreeSelectionDialog.OK) {
			return (IContainer)dialog.getFirstResult();
		}
		return null;
	}
	
	public void setInput(IProject project) {
		this.project = project;
		if ( viewer.getContentProvider() != null ) {
			SourceDirectoryGroup newInput = null ;
			try {
			
				newInput = getSavedInput(project);
				log.debug("Found " + newInput.getSourceDirectories().size() + " previously stored SourceDirectories");
			}
			catch (Exception e) {
				log.debug("Error occured while restoring previously saved SourceDirectoryGroup for project '" + project.getName() + "'. Reason : " + e); 
	
			}
			if ( newInput == null ) {
				
				newInput = new SourceDirectoryGroup(project);
			}
		
			viewer.setInput(newInput);
		}
	}
	
	public SourceDirectoryGroup getInput() {
		return (SourceDirectoryGroup) viewer.getInput();
	}
	
	
	private SourceDirectoryGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("sourceTypes.xml");
		
		return SourceDirectoryGroupMarshaller.getSourceDirectoryGroup(project, savedStates);
		
		
	}


	public void saveState() throws Exception {
		SourceDirectoryGroupMarshaller.saveSourceDirectoryGroup((SourceDirectoryGroup)viewer.getInput(), Mevenide.getPlugin().getFile("sourceTypes.xml"));
	}
	


}
