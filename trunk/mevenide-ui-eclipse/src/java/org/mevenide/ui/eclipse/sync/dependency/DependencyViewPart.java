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
 */
package org.mevenide.ui.eclipse.sync.dependency;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.DefaultPathResolver;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryMarshaller;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyViewPart extends ViewPart {

	private TableTreeViewer viewer;
	
	private IProject project;
	
	private static DependencyViewPart partInstance; 
	
	//public constructor : it should be instantiated by Eclipse 
	public DependencyViewPart() {
		partInstance = this;
	}
	
	public static DependencyViewPart getInstance() {
		return partInstance;
	}
	
	public void createPartControl(Composite parent) {
		
		viewer = DependencyViewUtil.getViewer(parent);

		getSite().setSelectionProvider(viewer);
		
		//add action buttons
		addContributions();
		
		
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

	private DependencyGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("statedDependencies.xml");
		
		return DependencyMarshaller.getDependencyGroup(project, savedStates);
		
	}

	public void setFocus() {
	}

	private void addContributions() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		
		Action saveAction = new Action() {
			public void run() {
				
				try {
					if ( project != null ) {
						saveState();
						SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
		};	
		saveAction.setImageDescriptor(Mevenide.getImageDescriptor("save-16.gif"));
		saveAction.setToolTipText("Save");
		
		Action refreshAction = new Action() {
			public void run() {
				
				try {
					if ( project != null ) {
						setInput(project);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	
			}
		};
		refreshAction.setImageDescriptor(Mevenide.getImageDescriptor("refresh.gif"));
		refreshAction.setToolTipText("Refresh View");
		
		tbm.add(refreshAction);
		tbm.add(saveAction);
		
		getViewSite().getActionBars().updateActionBars();
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
	}
	
	public void init(IViewSite site) throws PartInitException {
		setSite(site);
	}
	
	/**
	 * not sure how to use memento efficiciently so doesnt use it for now. 
	 * have to figure out how it works. 
	 * 
	 * @see SourceDirectoryMarshaller 
	 */
	public void saveState(IMemento memento) {
	}

	public void saveState() throws Exception {
		DependencyMarshaller.saveDependencyGroup((DependencyGroup)viewer.getInput(), Mevenide.getPlugin().getFile("statedDependencies.xml"));
	}
	
	public static void showView() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.mevenide.sync.view.dep"); 
	}

	/**
	 * 
	 * try to figure out the SourceDirectory types from the previous state. if there are 
	 * new directories, the view is shown
	 * 
	 * @param currentProject
	 * @throws Exception
	 */
	public static void synchronizeWithoutPrompting(IProject currentProject) throws Exception {
		String savedState = Mevenide.getPlugin().getFile("statedDependencies.xml");
		List lastSourceList = SourceDirectoryMarshaller.getLastStoredSourceDirectories(currentProject, savedState);
		
		boolean newSourceFolder = false;
		boolean newDependency = false;
		
		IClasspathEntry[] entries = JavaCore.create(currentProject).getResolvedClasspath(true);

		DefaultPathResolver pathResolver = new DefaultPathResolver();

		for (int i = 0; i < entries.length; i++) {
			if ( entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE ) {
				String entryPath = pathResolver.getRelativeSourceDirectoryPath(entries[i], currentProject);
				if ( !lastSourceList.contains(entryPath) ) {
					newSourceFolder = true;
					break;
				}
			}
			if ( entries[i].getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
				
			}
		}
		if ( newSourceFolder ) {
			prompt(currentProject);
		}
		else {
			SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
		}
	}
	
	public static void prompt(IProject currentProject) throws Exception {
		showView();
		getInstance().setInput(currentProject);
	}
}
