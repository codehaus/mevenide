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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryGroup;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryMarshaller;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryViewUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class SourceDirectorySynchronizeWizardPage extends WizardPage {
	
	private TableViewer viewer;
	
	private IProject project;
	
	public SourceDirectorySynchronizeWizardPage() {
		super("Source Directories Synchronization");
		setTitle("Source Directory Synchronization");
		setDescription("Please enter source directory types.");
		setImageDescriptor(Mevenide.getImageDescriptor("source-synch-64.gif"));
		
	}

	

	public void createControl(Composite arg0) {
		Composite composite = new Composite(arg0, SWT.NONE);
		viewer = SourceDirectoryViewUtil.getViewer(composite);
		setInput(((SynchronizeWizard)getWizard()).getProject());
		setControl(composite);
	}
	
	
	public void setInput(IProject project) {
		this.project = project;
		if ( viewer.getContentProvider() != null ) {
			SourceDirectoryGroup newInput = null ;
			try {
			
				newInput = getSavedInput(project);
			}
			catch (Exception e) {
				e.printStackTrace();
	
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
		
		return SourceDirectoryMarshaller.getSourceDirectoryGroup(project, savedStates);
		
		
	}


	public void saveState() throws Exception {
		SourceDirectoryMarshaller.saveSourceDirectoryGroup((SourceDirectoryGroup)viewer.getInput(), Mevenide.getPlugin().getFile("sourceTypes.xml"));
	}
	


}
