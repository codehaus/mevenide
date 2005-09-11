/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
							((SourceDirectoryGroup) viewer.getInput()).getSourceDirectories().remove(item.getData());
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