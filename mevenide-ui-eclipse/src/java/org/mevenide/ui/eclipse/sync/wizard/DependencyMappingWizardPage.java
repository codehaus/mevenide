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

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.DependencyGroup;
import org.mevenide.ui.eclipse.sync.model.DependencyGroupMarshaller;
import org.mevenide.ui.eclipse.sync.view.DependencyMappingViewControl;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyMappingWizardPage extends WizardPage {
	private static Log log = LogFactory.getLog(DependencyMappingWizardPage.class);
	
	private TableTreeViewer viewer;
	private Button addButton;
	private Button removeButton;
	private Button propertiesButton;
	private Button refreshButton;
	
	private IProject project;
	
	public DependencyMappingWizardPage() {
		super("Dependencies Synchronization");
		setTitle("Dependency Synchronization");
		setDescription("Please check the dependencies' groupId, artifactId and version");
		setImageDescriptor(Mevenide.getImageDescriptor("dep-synch-64.gif"));
		
		
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
		
		viewer = DependencyMappingViewControl.getViewer(composite, SWT.BORDER);
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
	
		addButton = new Button(composite, SWT.PUSH);
		addButton.setText("Add...");
		addButton.setToolTipText("Add a dependency");
		GridData addButtonData = new GridData(GridData.FILL_HORIZONTAL);
		addButtonData.grabExcessHorizontalSpace = true;
		addButton.setLayoutData(addButtonData);
		addButton.setEnabled(true);	
	
		removeButton = new Button(composite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setToolTipText("Remove dependency");
		GridData removeButtonData = new GridData(GridData.FILL_HORIZONTAL);
		removeButtonData.grabExcessHorizontalSpace = true;
		removeButton.setLayoutData(removeButtonData);
		removeButton.setEnabled(true);
		
		propertiesButton = new Button(composite, SWT.PUSH);
		propertiesButton.setText("Properties");
		propertiesButton.setToolTipText("Set depedencency properties");
		GridData propertiesButtonData = new GridData(GridData.FILL_HORIZONTAL);
		propertiesButtonData.grabExcessHorizontalSpace = true;
		propertiesButton.setLayoutData(propertiesButtonData);
		propertiesButton.setEnabled(false);
		
		refreshButton = new Button(composite, SWT.PUSH);
		refreshButton.setText("Refresh");
		refreshButton.setToolTipText("Refresh from .classpath");
		GridData refreshButtonData = new GridData(GridData.FILL_HORIZONTAL);
		refreshButtonData.grabExcessHorizontalSpace = true;
		refreshButton.setLayoutData(refreshButtonData);
		
		addButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						try {
							FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
							dialog.setFilterPath(Mevenide.getPlugin().getMavenRepository());
							String path = dialog.open();
							if ( path != null ) {
								((DependencyGroup)viewer.getInput()).addDependency(DependencyFactory.getFactory().getDependency(path));
								log.debug("Added Dependency : " + path);
							}
							viewer.refresh();
						}
						catch ( Exception ex ) {
							log.debug("Problem occured while trying to add a Dependency due to : " + e);
							ex.printStackTrace();
						}
					}
				}
		);
		
		removeButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						TableTreeItem[] items = viewer.getTableTree().getSelection();
						for (int i = 0; i < items.length; i++) {
							TableTreeItem item = items[i];
							while ( item.getParentItem() != null ) {
								item = item.getParentItem();
							}
							((DependencyGroup) viewer.getInput()).getDependencies().remove((Dependency) item.getData());
							((DependencyGroup) viewer.getInput()).excludeDependency((Dependency) item.getData());
							viewer.refresh();
						}
					}
				}
		);
		
		propertiesButton.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						DependencyPropertiesDialog dialog = new DependencyPropertiesDialog();
						dialog.open();
						Properties props = dialog.getProperties();
						if ( props != null && props.keys() != null ) {
							List keys = Collections.list(props.keys()); 
							for (int i = 0; i < keys.size(); i++) {
								String key = (String) keys.get(i);
								String value = (String) props.get(key);
								System.err.println(key + " = " + value);
							}
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
		viewer.setInput(new DependencyGroup(project));
	}
	
	public void setInput(IProject project) {
		this.project = project;
		if ( viewer.getContentProvider() != null ) {
			DependencyGroup newInput = null ;
			try {
			
				newInput = getSavedInput(project);
			}
			catch (Exception e) {
				log.debug("Error occured while restoring previously saved DependencyGroup for project '" + project.getName() + "'. Reason : " + e);
	
			}
			if ( newInput == null ) {
				newInput = new DependencyGroup(project);
			}
		
			viewer.setInput(newInput);
		}
	}
	
	public DependencyGroup getInput() {
		return (DependencyGroup) viewer.getInput();
	}
	
	
	private DependencyGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("statedDependencies.xml");
			
		return DependencyGroupMarshaller.getDependencyGroup(project, savedStates);
			
	}
	
	
	public void saveState() throws Exception {
		DependencyGroupMarshaller.saveDependencyGroup((DependencyGroup)viewer.getInput(), Mevenide.getPlugin().getFile("statedDependencies.xml"));
	}
	


	


}
