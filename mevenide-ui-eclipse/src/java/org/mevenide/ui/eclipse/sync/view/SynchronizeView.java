/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.sync.action.SynchronizeActionFactory;
import org.mevenide.ui.eclipse.sync.event.IActionListener;
import org.mevenide.ui.eclipse.sync.event.IdeArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.PomArtifactEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactMappingContentProvider;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNodeContainer;
import org.mevenide.ui.eclipse.sync.model.ProjectContainer;
import org.mevenide.ui.eclipse.util.FileUtils;



/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class SynchronizeView extends ViewPart implements IActionListener, IResourceChangeListener, IPropertyChangeListener {
    private static final Log log = LogFactory.getLog(SynchronizeView.class);

    private Composite composite;
    private TreeViewer artifactMappingNodeViewer;
    private IPageSite site;

    //global view actions
    private Action refreshAll;
    private Action viewIdeToPom;
    private Action viewPomToIde;
    private Action viewConflicts;
    
    //contextual actions
    private Action pushToPom;
    private Action addToClasspath;
    private Action markAsMerged;
    private Action viewProperties;
    private Action removeFromPom;
    private Action removeFromProject;
    private Action addToIgnoreList;
    private Separator separator;
    
    private int direction;
    private List directionListeners = new ArrayList(); 
    
	private List poms;

    private IToolBarManager toolBarManager;
    
    public void createPartControl(Composite parent) {
        createArtifactViewer(parent);
        createActions();
        plugActions();
    }
    
    public void setFocus() {
        if (composite == null) return;
        composite.setFocus();
    }
    
    public void setInput(IProject input) {
        artifactMappingNodeViewer.setInput(input);
		try {
			poms = FileUtils.getPoms(input);
		} 
		catch (Exception e) {
			log.error("Cannot find pom for project " + input, e);
		}
		((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(this.direction);
        artifactMappingNodeViewer.refresh(true);
        artifactMappingNodeViewer.expandAll();
        assertValidDirection();
    }
    
    private void assertValidDirection() {
        if ( direction != ProjectContainer.INCOMING 
                && direction != ProjectContainer.OUTGOING 
                && direction != ProjectContainer.CONFLICTING ) {
            setDirection(ProjectContainer.INCOMING);
        }
    }
    
    
    public void setDirection(int direction) {
		if ( direction != this.direction ) {
	        this.direction = direction;
	        if ( artifactMappingNodeViewer.getInput() != null ) {
	            ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(direction);
	        }
			artifactMappingNodeViewer.refresh(true);
	        artifactMappingNodeViewer.expandAll();
			fireDirectionChanged();
		}
    }

	private void fireDirectionChanged() {
		for (int i = 0; i < directionListeners.size(); i++) {
            ISynchronizationDirectionListener listener = (ISynchronizationDirectionListener) directionListeners.get(i);
			listener.directionChanged(direction);
        }
	}
    
	public void addDirectionListener(ISynchronizationDirectionListener listener) {
		directionListeners.add(listener);
	}

	public void removeDirectionListener(ISynchronizationDirectionListener listener) {
		directionListeners.remove(listener);
	}

    private void createArtifactViewer(Composite parent) {
        artifactMappingNodeViewer = new TreeViewer(parent, SWT.FULL_SELECTION);
        
        GridLayout gridLayout= new GridLayout();
        gridLayout.makeColumnsEqualWidth= false;
        gridLayout.marginWidth= 0;
        gridLayout.marginHeight = 0;
        gridLayout.verticalSpacing = 0;
        artifactMappingNodeViewer.getTree().setLayout(gridLayout);
        
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        artifactMappingNodeViewer.getTree().setLayoutData(gridData);
        
        configureViewer();
      
		getSite().setSelectionProvider(artifactMappingNodeViewer);
    }

    private void configureViewer() {
        artifactMappingNodeViewer.setContentProvider(new ArtifactMappingContentProvider());
        artifactMappingNodeViewer.setLabelProvider(new ArtifactMappingLabelProvider());
    }
    
    private void createActions() {
    	SynchronizeActionFactory actionFactory = SynchronizeActionFactory.getFactory(this);
    	
        refreshAll = actionFactory.getAction(SynchronizeActionFactory.REFRESH_ALL);
       
		viewConflicts = actionFactory.getAction(SynchronizeActionFactory.VIEW_CONFLICTS);
		viewIdeToPom = actionFactory.getAction(SynchronizeActionFactory.VIEW_OUTGOING);
		viewPomToIde = actionFactory.getAction(SynchronizeActionFactory.VIEW_INCOMING);

		pushToPom = actionFactory.getAction(SynchronizeActionFactory.ADD_TO_POM);
		removeFromProject = actionFactory.getAction(SynchronizeActionFactory.REMOVE_FROM_PROJECT);
		addToClasspath = actionFactory.getAction(SynchronizeActionFactory.ADD_TO_CLASSPATH);
		removeFromPom = actionFactory.getAction(SynchronizeActionFactory.REMOVE_FROM_POM);

		markAsMerged = actionFactory.getAction(SynchronizeActionFactory.MARK_AS_MERGED);

		viewProperties = actionFactory.getAction(SynchronizeActionFactory.PROPERTIES);

		addToIgnoreList = actionFactory.getAction(SynchronizeActionFactory.MVN_IGNORE);
		
		separator  = new Separator();
		
		disableContextualActions();
		
		createSelectionChangedListener();
    }
   
    

	private void disableContextualActions() {
		addToClasspath.setEnabled(false);
		pushToPom.setEnabled(false);
		markAsMerged.setEnabled(false);
		viewProperties.setEnabled(false);
		removeFromPom.setEnabled(false);
		removeFromProject.setEnabled(false);
		addToIgnoreList.setEnabled(false);
	}

	private void createSelectionChangedListener() {
		artifactMappingNodeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
				Object selection = ((StructuredSelection) event.getSelection()).getFirstElement();
				
				//temp
				if ( !(selection instanceof IArtifactMappingNode) ) {
					addToClasspath.setEnabled(false);
					pushToPom.setEnabled(false);
					markAsMerged.setEnabled(false);
					viewProperties.setEnabled(false);
					return;
				}
				
				viewProperties.setEnabled(true);
				
				IArtifactMappingNode selectedNode = (IArtifactMappingNode) selection;
				
				//outgoing 
				pushToPom.setEnabled((selectedNode.getChangeDirection() & ProjectContainer.OUTGOING) != 0);
				removeFromProject.setEnabled((selectedNode.getChangeDirection() & ProjectContainer.OUTGOING) != 0);
					
				//incoming
				addToClasspath.setEnabled((selectedNode.getChangeDirection() & ProjectContainer.INCOMING) != 0);
				removeFromPom.setEnabled((selectedNode.getChangeDirection() & ProjectContainer.INCOMING) != 0);
				
				//conflicting
				markAsMerged.setEnabled((selectedNode.getChangeDirection() & ProjectContainer.CONFLICTING) != 0);
				addToIgnoreList.setEnabled((selectedNode.getChangeDirection() & ProjectContainer.CONFLICTING) == 0);
					
            }
		});
	}

	

	private void plugActions() {
		createMenuManager();
		createToolBarManager();
		createContextualMenu();
    }

	private void createMenuManager() {
		IMenuManager topLevelMenuManager = getViewSite().getActionBars().getMenuManager();
		topLevelMenuManager.add(pushToPom);
		topLevelMenuManager.add(removeFromProject);
		topLevelMenuManager.add(separator);
		topLevelMenuManager.add(addToClasspath);
		topLevelMenuManager.add(removeFromPom);
		topLevelMenuManager.add(separator);
		topLevelMenuManager.add(markAsMerged);
		topLevelMenuManager.add(separator);
		topLevelMenuManager.add(addToIgnoreList);
		topLevelMenuManager.add(separator);
		topLevelMenuManager.add(viewProperties);
	}

	private void createToolBarManager() {
		toolBarManager = getViewSite().getActionBars().getToolBarManager();
        toolBarManager.add(refreshAll);
		toolBarManager.add(viewIdeToPom);
		toolBarManager.add(viewPomToIde);
		toolBarManager.add(viewConflicts);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if ( toolBarManager != null ) {
			log.debug("property changed. updating");
	    	toolBarManager.update(true);
		}
    }
	
	private void createContextualMenu() {
		MenuManager contextManager = new MenuManager();
		contextManager.setRemoveAllWhenShown(true);
		Menu menu = contextManager.createContextMenu(artifactMappingNodeViewer.getControl());
		artifactMappingNodeViewer.getControl().setMenu(menu);
		contextManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
			    Object selection = ((StructuredSelection) artifactMappingNodeViewer.getSelection()).getFirstElement();
			    if ( selection instanceof IArtifactMappingNode ) {
				    IArtifactMappingNode selectedNode = (IArtifactMappingNode) selection;
					if ( (selectedNode.getChangeDirection() & ProjectContainer.OUTGOING) != 0 ) {
						manager.add(pushToPom);
						manager.add(removeFromProject);
					}
					if ( (selectedNode.getChangeDirection() & ProjectContainer.INCOMING) != 0 ) {
						manager.add(addToClasspath);
						manager.add(removeFromPom);
					}
					if ( (selectedNode.getChangeDirection() & ProjectContainer.CONFLICTING) != 0 ) {
						manager.add(markAsMerged);
					}
					else {
						manager.add(addToIgnoreList);
					}
					manager.add(separator);
					manager.add(viewProperties);		
				}
			}
		});
	}

	
	public void artifactAddedToClasspath(IdeArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		log.debug("artifact modified : " + artifact);
    	refreshNode(artifact);
	}

	private void refreshNode(IArtifactMappingNode artifact) {
		IArtifactMappingNodeContainer container = (IArtifactMappingNodeContainer) getContentProvider().getParent(artifact);
    	container.removeNode(artifact);
    	artifactMappingNodeViewer.refresh(container);
	}
	
	private ITreeContentProvider getContentProvider() {
		return ((ITreeContentProvider) artifactMappingNodeViewer.getContentProvider());
	}

	public void artifactAddedToPom(PomArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		log.debug("artifact modified : " + artifact);
		refreshNode(artifact);
	}
	
	public void artifactRemovedFromPom(PomArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		refreshNode(artifact);
	}
	
	public TreeViewer getArtifactMappingNodeViewer() {
		return artifactMappingNodeViewer;
	}
	
	public void artifactRemovedFromClasspath(IdeArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		refreshNode(artifact);	
	}
	
	public void artifactIgnored(IdeArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		refreshNode(artifact);
	}
	
	public void artifactIgnored(PomArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		refreshNode(artifact);
	}
	
	public int getDirection() {
		return direction;
	}

	public SynchronizeView() {
		super();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);				
	}

	public void resourceChanged(IResourceChangeEvent event) {
		final IProject project = (IProject) artifactMappingNodeViewer.getInput();
		final IFile dotClasspath = project.getFile(".classpath");
		
		IResourceDelta d= event.getDelta();
		if (d == null) {
			return;
		}
		try {
			d.accept(
					new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta delta) {
							if (delta != null) {
								IResource r = delta.getResource();									
								if (r instanceof IFile) {
									IFile file = (IFile) r;
									if ( file.equals(dotClasspath) ) {
										refreshAll();
									}
									for (int i = 0; i < poms.size(); i++) {
										File f = (File) poms.get(i);
										if ( new File(file.getLocation().toOSString()).equals(f) ) {
											refreshAll();
										}
									}
								}
								if ( r instanceof IProject ) {
									IProject prj = (IProject) r;
									if ( prj.getName().equals(project.getName()) ) {
										//doesnot seem to work ??
										refreshAll();
									}
								}
							}
							return true;
						}

					}
			);
		} 
		catch (CoreException e) {
			log.error("processing resource delta", e); //$NON-NLS-1$
		}		
	}

	private void refreshAll() {
		artifactMappingNodeViewer.refresh(true);
		artifactMappingNodeViewer.expandAll();
	}

}
