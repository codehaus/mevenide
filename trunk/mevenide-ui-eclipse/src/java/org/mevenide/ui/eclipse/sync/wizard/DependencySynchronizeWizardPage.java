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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.dependency.DependencyGroup;
import org.mevenide.ui.eclipse.sync.dependency.DependencyMarshaller;
import org.mevenide.ui.eclipse.sync.views.DependencyViewUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencySynchronizeWizardPage extends WizardPage {
	
	private TableTreeViewer viewer;
	
	private IProject project;
	
	public DependencySynchronizeWizardPage() {
		super("Dependencies Synchronization");
		setTitle("Dependency Synchronization");
		setDescription("Please check the dependencies' groupId, artifactId and version");
		setImageDescriptor(MavenPlugin.getImageDescriptor("dep-synch-64.gif"));
		
		
	}

	

	public void createControl(Composite arg0) {
		Composite composite = new Composite(arg0, SWT.NONE);
		viewer = DependencyViewUtil.getViewer(composite);
		setInput(((SynchronizeWizard)getWizard()).getProject());
		setControl(composite);
	}
	
	
	public void setInput(IProject project) {
		this.project = project;
		if ( viewer.getContentProvider() != null ) {
			DependencyGroup newInput = null ;
			try {
			
				newInput = getSavedInput(project);
			}
			catch (Exception e) {
				e.printStackTrace();
	
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
		
		String savedStates = MavenPlugin.getPlugin().getFile("statedDependencies.xml");
			
		return DependencyMarshaller.getDependencyGroup(project, savedStates);
			
	}
	
	
	public void saveState() throws Exception {
		DependencyMarshaller.saveDependencyGroup((DependencyGroup)viewer.getInput(), MavenPlugin.getPlugin().getFile("statedDependencies.xml"));
	}
	


	


}
