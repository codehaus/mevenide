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
package org.mevenide.ui.eclipse.sync.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.MavenUtils;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.project.ProjectComparator;
import org.mevenide.project.ProjectComparatorFactory;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.sync.action.SynchronizeActionFactory;
import org.mevenide.ui.eclipse.sync.event.IActionListener;
import org.mevenide.ui.eclipse.sync.event.IdeArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.PomArtifactEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactMappingContentProvider;
import org.mevenide.ui.eclipse.sync.model.EclipseContainerContainer;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNodeContainer;



/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class SynchronizeView extends ViewPart implements IActionListener, IResourceChangeListener, IPropertyChangeListener, IProjectChangeListener {
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
	private IProject project;
	private IContainer container;
	
    private IToolBarManager toolBarManager;

    private ProjectComparator comparator;
    
    public void createPartControl(Composite parent) {
        createArtifactViewer(parent);
        createActions();
        plugActions();
    }
    
    public void setFocus() {
        if (composite == null) return;
        composite.setFocus();
    }
    
    public void setInput(IContainer input) {
		try {
			this.container = input;
			//poms = FileUtils.getPoms(input);
		    poms = new PomChooser(input).openPomChoiceDialog(false);
		    
		    if ( poms != null ) {
		        synchronizeProjectWithPoms(input.getProject(), poms);
		    }
		} 
		catch (Exception e) {
			log.error("Cannot find pom for project " + input, e);
			//@TODO emit message to user
		}
    }
    
    public void setInput(Project input) {
        try {
            //poms = FileUtils.getPoms(input);
            	
            if ( input != null ) {
                IProject project = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(input.getFile().getAbsolutePath())).getProject();
                
				this.container = project;
				
				
				List poms = new ArrayList();
				
                poms.add(input);
                
                synchronizeProjectWithPoms(project, poms);
            }
        } 
        catch (Exception e) {
            log.error("Unable to synchronize POM " + input.getFile().getName() + " with project " + input, e);
        }
    }
    
    private void synchronizeProjectWithPoms(IProject project, List poms) {
        ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setPoms(poms);
        ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(this.direction);

        artifactMappingNodeViewer.setInput(project);
        
        for (int i = 0; i < poms.size(); i++) {
            Project mavenProject = (Project) poms.get(i);
            comparator = ProjectComparatorFactory.getComparator(mavenProject);
			comparator.addProjectChangeListener(ProjectComparator.BUILD, this);
			comparator.addProjectChangeListener(ProjectComparator.DEPENDENCIES, this); 
        }
        
        this.poms = poms;
        this.project = project;
        
        refreshAll(true);
        
        assertValidDirection();
    }

    private void assertValidDirection() {
        if ( direction != EclipseContainerContainer.INCOMING 
                && direction != EclipseContainerContainer.OUTGOING 
                && direction != EclipseContainerContainer.CONFLICTING ) {
            setDirection(EclipseContainerContainer.INCOMING);
        }
    }
    
    
    public void setDirection(int direction) {
		if ( direction != this.direction ) {
	        this.direction = direction;
	        if ( artifactMappingNodeViewer.getInput() != null ) {
	            ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(direction);
	        }
			artifactMappingNodeViewer.refresh(true);
	        artifactMappingNodeViewer.expandToLevel(1);
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
        artifactMappingNodeViewer = new TreeViewer(parent, SWT.MULTI | SWT.FULL_SELECTION);
        
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
				List selections = ((StructuredSelection) event.getSelection()).toList();
				
				boolean enablePushToPom = false, 
						enableRemoveFromProject = false, 
						enableAddToClasspath = false, 
						enableFromFromPom = false, 
						enableMarkAsMerged = false, 
						enableAddToIgnoreList = false;
				
				for (int i = 0; i < selections.size(); i++) {
					Object selection = selections.get(i);
					
					if ( selection instanceof IArtifactMappingNode ) {

						IArtifactMappingNode selectedNode = (IArtifactMappingNode) selection;
						
						//outgoing 
						enablePushToPom = (selectedNode.getChangeDirection() & EclipseContainerContainer.OUTGOING) != 0;
						enableRemoveFromProject = (selectedNode.getChangeDirection() & EclipseContainerContainer.OUTGOING) != 0;
							
						//incoming
						enableAddToClasspath = (selectedNode.getChangeDirection() & EclipseContainerContainer.INCOMING) != 0;
						enableFromFromPom = (selectedNode.getChangeDirection() & EclipseContainerContainer.INCOMING) != 0;
						
						//conflicting
						enableMarkAsMerged = (selectedNode.getChangeDirection() & EclipseContainerContainer.CONFLICTING) != 0;
						enableAddToIgnoreList = (selectedNode.getChangeDirection() & EclipseContainerContainer.CONFLICTING) == 0;
					}
				}
				
				pushToPom.setEnabled(enablePushToPom);
				removeFromProject.setEnabled(enableRemoveFromProject);
				addToClasspath.setEnabled(enableAddToClasspath);
				removeFromPom.setEnabled(enableFromFromPom);
				markAsMerged.setEnabled(enableMarkAsMerged);
				addToIgnoreList.setEnabled(enableAddToIgnoreList);
				
				if ( selections.size() == 1 ) {
					viewProperties.setEnabled(true);
				}
				else {
					viewProperties.setEnabled(false);
				}
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
		//topLevelMenuManager.add(markAsMerged);
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
		//toolBarManager.add(viewConflicts);
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
					if ( (selectedNode.getChangeDirection() & EclipseContainerContainer.OUTGOING) != 0 ) {
						manager.add(pushToPom);
						manager.add(removeFromProject);
					}
					if ( (selectedNode.getChangeDirection() & EclipseContainerContainer.INCOMING) != 0 ) {
						manager.add(addToClasspath);
						manager.add(removeFromPom);
					}
					if ( (selectedNode.getChangeDirection() & EclipseContainerContainer.CONFLICTING) != 0 ) {
						//manager.add(markAsMerged);
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
		updatePoms(event.getProject());
		refreshNode(artifact);
		try {
			IFile file = this.project.getFile(MavenUtils.makeRelativePath(this.project.getLocation().toFile(), event.getProject().getFile().getAbsolutePath()));
			file.refreshLocal(IResource.DEPTH_ZERO, null);
		} 
		catch (Exception e) {
			log.error("Uanble to refresh POM", e);
		}
		comparator.compare(event.getProject());
	}
	
	public void artifactRemovedFromPom(PomArtifactEvent event) {
		IArtifactMappingNode artifact = (IArtifactMappingNode) event.getArtifact();
		updatePoms(event.getProject());
		refreshNode(artifact);
		comparator.compare(event.getProject());
	}
	
	//@TODO evil.. but actions read poms instead of working on pom references.. 
	private void updatePoms(Project project) {
	    for (int i = 0; i < poms.size(); i++) {
	        Project pom = (Project) poms.get(i);
			if ( pom.getFile().equals(project.getFile()) ) {
				poms.remove(i);
				poms.add(i, project);
			} 
        }
	    ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setPoms(poms);
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
		try {
			final IProject project = (IProject) artifactMappingNodeViewer.getInput();
			final IFile dotClasspath = project.getFile(".classpath");
			
			IResourceDelta d= event.getDelta();
			if (d == null) {
				return;
			}
			d.accept(
					new IResourceDeltaVisitor() {
						public boolean visit(IResourceDelta delta) {
							if (delta != null) {
								IResource r = delta.getResource();									
								if (r instanceof IFile) {
									IFile file = (IFile) r;
									if ( file.equals(dotClasspath) ) {
										refreshAll(false);
									}
									for (int i = 0; i < poms.size(); i++) {
										File f = ((Project) poms.get(i)).getFile();
										if ( new File(file.getLocation().toOSString()).equals(f) ) {
				
											try {
                                                updatePoms(ProjectReader.getReader().read(f));
                                            } 
											catch (Exception e) {
                                                log.error("Unable to update pom list", e);
                                            }
										}
									}
									refreshAll(false);
								}
								if ( r instanceof IProject ) {
									IProject prj = (IProject) r;
									if ( prj.getName().equals(project.getName()) ) {
										refreshAll(false);
									}
								}
							}
							return true;
						}

					}
			);
		} 
		catch (Exception e) {
			log.debug("error processing resource delta", e); //$NON-NLS-1$
		}		
	}

	public void refreshAll(boolean shouldExpand) {
		artifactMappingNodeViewer.refresh(true);
		if ( shouldExpand ) {
			artifactMappingNodeViewer.expandAll();
		}
	}

    public IContainer getInputContainer() {
        return container;
    }
	
	public void projectChanged(ProjectChangeEvent e) {
	    log.debug("received project change notification. Attribute : " + e.getAttribute());
    	String attribute = e.getAttribute();
		if ( ProjectComparator.BUILD.equals(attribute) || ProjectComparator.DEPENDENCIES.equals(attribute) ) {
		    updatePoms(e.getPom());
			refreshAll(true);
		}     
	}
}
