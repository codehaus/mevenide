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
package org.mevenide.ui.eclipse.sync.views;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.DefaultPathResolverDelegate;
import org.mevenide.ui.eclipse.sync.source.*;
import org.mevenide.ui.eclipse.sync.source.SourceDirectoryGroup;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryTypePart extends ViewPart {
	/** 2 columns table viewer [source dir, source type] where source type is displayed in a CCombo */
	private TableViewer viewer;
	
	private IProject project;
	
	private static SourceDirectoryTypePart partInstance; 
	
	//public constructor : it should be instantiated by Eclipse 
	public SourceDirectoryTypePart() {
		partInstance = this;
	}
	
	public static SourceDirectoryTypePart getInstance() {
		return partInstance;
	}
	
	public void createPartControl(Composite parent) {
		
		viewer = SourceDirectoryViewUtil.getViewer(parent);

		getSite().setSelectionProvider(viewer);
		
		//add action buttons
		addContributions();
		
		
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

	private SourceDirectoryGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = MavenPlugin.getPlugin().getFile("sourceTypes.xml");
		
		return SourceDirectoryMarshaller.getSourceDirectoryGroup(project, savedStates);
		
	}

	public void setFocus() {
	}

	private void addContributions() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		
		Action synchronizeAction = new Action() {
			public void run() {
				
				try {
					saveState();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
			}
		};	
		synchronizeAction.setImageDescriptor(MavenPlugin.getImageDescriptor("save-16.gif"));
		synchronizeAction.setToolTipText("Save");
		
		Action refreshAction = new Action() {
			public void run() {
				
				try {
					setInput(project);
				} catch (Exception e) {
					e.printStackTrace();
				}
	
			}
		};
		refreshAction.setImageDescriptor(MavenPlugin.getImageDescriptor("refresh.gif"));
		refreshAction.setToolTipText("Refresh View");
		
		tbm.add(refreshAction);
		tbm.add(synchronizeAction);
		
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
		SourceDirectoryMarshaller.saveSourceDirectoryGroup((SourceDirectoryGroup)viewer.getInput(), MavenPlugin.getPlugin().getFile("sourceTypes.xml"));
	}
	
	public static void showView() throws Exception {
		IViewPart consoleView =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(MavenPlugin.SYNCH_VIEW_ID); 
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
		String savedState = MavenPlugin.getPlugin().getFile("sourceTypes.xml");
		List lastSourceList = SourceDirectoryMarshaller.getLastStoredSourceDirectories(currentProject, savedState);
		
		boolean newSourceFolder = false;
		boolean newDependency = false;
		
		IClasspathEntry[] entries = JavaCore.create(currentProject).getResolvedClasspath(true);

		DefaultPathResolverDelegate pathResolver = new DefaultPathResolverDelegate();

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
