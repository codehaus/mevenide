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
package org.mevenide.ui.eclipse.sync.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.mevenide.ui.eclipse.sync.model.*;

/**
 * @deprecated made it clear at least =)
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryMappingViewPart extends ViewPart {
	private static Log log = LogFactory.getLog(SourceDirectoryMappingViewPart.class);
	
	/** 2 columns table viewer [source dir, source type] where source type is displayed in a CCombo */
	private TableViewer viewer;
	
	private IProject project;
	
	private static SourceDirectoryMappingViewPart partInstance; 
	
	//public constructor : it should be instantiated by Eclipse 
	public SourceDirectoryMappingViewPart() {
		partInstance = this;
	}
	
	public static SourceDirectoryMappingViewPart getInstance() {
		return partInstance;
	}
	
	public void createPartControl(Composite parent) {
		
		Composite composite = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout();
		layout.marginHeight=5;
		layout.marginWidth=5;
		composite.setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);
		
		viewer = SourceDirectoryMappingViewControl.getViewer(composite);

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
				log.info("Error occured while retrieving previously saved SourceDirectoryGroup for project '" + project.getName() + "'. Reason : " + e);
		
			}
			if ( newInput == null ) {
				newInput = new SourceDirectoryGroup(project);
			}
			
			viewer.setInput(newInput);
		}
	}

	private SourceDirectoryGroup getSavedInput(IProject project) throws Exception {
		
		String savedStates = Mevenide.getPlugin().getFile("sourceTypes.xml");
		
		return SourceDirectoryGroupMarshaller.getSourceDirectoryGroup(project, savedStates);
		
	}

	public void setFocus() {
	}

	private void addContributions() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		
		Action synchronizeAction = new Action() {
			public void run() {
				
				try {
					if ( project != null ) {
						saveState();
						SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
					}
				} catch (Exception e) {
					log.debug("Unable to synchronize POM due to : " + e);
				}
				
				
			}
		};	
		synchronizeAction.setImageDescriptor(Mevenide.getImageDescriptor("save-16.gif"));
		synchronizeAction.setToolTipText("Save");
		
		Action refreshAction = new Action() {
			public void run() {
				
				try {
					if ( project != null ) {
						setInput(project);
					}
				} catch (Exception e) {
					log.debug("Unable to refreash Dependency Mapping View due to : " + e);
				}
	
			}
		};
		refreshAction.setImageDescriptor(Mevenide.getImageDescriptor("refresh.gif"));
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
		SourceDirectoryGroupMarshaller.saveSourceDirectoryGroup((SourceDirectoryGroup)viewer.getInput(), Mevenide.getPlugin().getFile("sourceTypes.xml"));
	}
	
	public static void showView() throws Exception {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Mevenide.SYNCH_VIEW_ID); 
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
		String savedState = Mevenide.getPlugin().getFile("sourceTypes.xml");
		SourceDirectoryGroup sourceDirectoryGroup = SourceDirectoryGroupMarshaller.getSourceDirectoryGroup(currentProject, savedState);
		
		List sourceDirectories = sourceDirectoryGroup.getSourceDirectories();
		List sources = new ArrayList();
		
		for (int i = 0; i < sourceDirectories.size(); i++) {
			SourceDirectory directory = (SourceDirectory) sourceDirectories.get(i);
			sources.add(directory.getDirectoryPath());
		}
		
		boolean newSourceFolder = false;
		
		IClasspathEntry[] entries = JavaCore.create(currentProject).getResolvedClasspath(true);

		DefaultPathResolver pathResolver = new DefaultPathResolver();

		for (int i = 0; i < entries.length; i++) {
			if ( entries[i].getEntryKind() == IClasspathEntry.CPE_SOURCE ) {
				String entryPath = pathResolver.getRelativeSourceDirectoryPath(entries[i], currentProject);
				if ( !sources.contains(entryPath) ) {
					newSourceFolder = true;
					break;
				}
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
