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
import org.apache.maven.project.MavenProject;
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
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
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
import org.mevenide.ui.eclipse.sync.action.ToggleViewAction;
import org.mevenide.ui.eclipse.sync.action.ToggleWritePropertiesAction;
import org.mevenide.ui.eclipse.sync.event.IActionListener;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationConstraintListener;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationDirectionListener;
import org.mevenide.ui.eclipse.sync.event.IdeArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.NodeEvent;
import org.mevenide.ui.eclipse.sync.event.PomArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.SynchronizationConstraintEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactMappingContentProvider;
import org.mevenide.ui.eclipse.sync.model.ArtifactNode;
import org.mevenide.ui.eclipse.sync.model.EclipseProjectNode;
import org.mevenide.ui.eclipse.sync.model.MavenArtifactNode;
import org.mevenide.ui.eclipse.sync.model.PropertyNode;
import org.mevenide.util.MevenideUtils;



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

    //global actions
    private Action refreshAll;
    private Action viewIdeToPom;
    private Action viewPomToIde;
    private Action viewConflicts;
    private IContributionItem writeProperties; 
    
    //contextual actions
    private Action pushToPom;
    private Action addToClasspath;
    private Action markAsMerged;
    private Action viewProperties;
    private Action removeFromPom;
    private Action removeFromProject;
    private Action addToIgnoreList;
    private Action addDependencyProperty;
    private Separator separator;
    
    private int direction;
    private List directionListeners = new ArrayList(); 
    
    private List synchronizationConstraintListeners = new ArrayList();
    
	private List poms;
	private IProject project;
	private IContainer container;
	
    private IToolBarManager toolBarManager;

    private ProjectComparator comparator;
    
	private boolean isDisposed;
	
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
    
    public void setInput(MavenProject input) {
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
    	ArtifactMappingContentProvider provider = (ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider();
    	artifactMappingNodeViewer.setInput(provider.new RootNode(project, poms));
        
        for (int i = 0; i < poms.size(); i++) {
            MavenProject mavenProject = (MavenProject) poms.get(i);
            comparator = ProjectComparatorFactory.getComparator(mavenProject);
			comparator.addProjectChangeListener(ProjectComparator.BUILD, this);
			comparator.addProjectChangeListener(ProjectComparator.DEPENDENCIES, this); 
			comparator.addProjectChangeListener(ProjectComparator.UNIT_TESTS, this);
			comparator.addProjectChangeListener(ProjectComparator.RESOURCES, this);
        }
        
        this.poms = poms;
        this.project = project;
        
        refreshAll(true);
        
        assertValidDirection();
    }

    private void assertValidDirection() {
        if ( direction != ArtifactNode.INCOMING 
                && direction != ArtifactNode.OUTGOING 
                && direction != ArtifactNode.CONFLICTING ) {
            setDirection(ArtifactNode.INCOMING);
        }
    }
    
    
    public void setDirection(int direction) {
		if ( direction != this.direction ) {
	        this.direction = direction;
	        if ( artifactMappingNodeViewer.getInput() != null ) {
	            //((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(direction);
	        }
			artifactMappingNodeViewer.refresh(true);
	        //artifactMappingNodeViewer.expandToLevel(1);
			activateWritePropertiesAction(direction);
			fireDirectionChanged();
		}
    }

    private void activateWritePropertiesAction(int direction) {
    	if ( direction == ArtifactNode.OUTGOING ) {
    		getViewSite().getActionBars().getToolBarManager().add(writeProperties);
    	}
    	else {
    		getViewSite().getActionBars().getToolBarManager().remove(writeProperties);
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
        artifactMappingNodeViewer.setAutoExpandLevel(4);
        
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
        isDisposed = false;
    }
    
    private void createActions() {
    	SynchronizeActionFactory actionFactory = SynchronizeActionFactory.getFactory(this);
    	
        refreshAll = actionFactory.getAction(SynchronizeActionFactory.REFRESH_ALL);
       
		viewConflicts = actionFactory.getAction(SynchronizeActionFactory.VIEW_CONFLICTS);
		viewIdeToPom = actionFactory.getAction(SynchronizeActionFactory.VIEW_OUTGOING);
		viewPomToIde = actionFactory.getAction(SynchronizeActionFactory.VIEW_INCOMING);
		writeProperties = new ActionContributionItem(actionFactory.getAction(SynchronizeActionFactory.WRITE_PROPERTIES));
		
		pushToPom = actionFactory.getAction(SynchronizeActionFactory.ADD_TO_POM);
		removeFromProject = actionFactory.getAction(SynchronizeActionFactory.REMOVE_FROM_PROJECT);
		addToClasspath = actionFactory.getAction(SynchronizeActionFactory.ADD_TO_CLASSPATH);
		removeFromPom = actionFactory.getAction(SynchronizeActionFactory.REMOVE_FROM_POM);

		markAsMerged = actionFactory.getAction(SynchronizeActionFactory.MARK_AS_MERGED);

		viewProperties = actionFactory.getAction(SynchronizeActionFactory.PROPERTIES);

		addToIgnoreList = actionFactory.getAction(SynchronizeActionFactory.MVN_IGNORE);
		
		addDependencyProperty = actionFactory.getAction(SynchronizeActionFactory.ADD_DEPENDENCY_PROPERTIES);
		
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
						enableRemoveFromPom = false, 
						enableMarkAsMerged = false, 
						enableAddToIgnoreList = false,
						enableAddDependencyProperty = false;
				
				for (int i = 0; i < selections.size(); i++) {
					Object selection = selections.get(i);
					
					if ( selection instanceof ArtifactNode ) {

						ArtifactNode selectedNode = (ArtifactNode) selection;
						
						//outgoing 
						enablePushToPom = (selectedNode.getDirection() & ArtifactNode.OUTGOING) != 0;
						enableRemoveFromProject = (selectedNode.getDirection() & ArtifactNode.OUTGOING) != 0;
						enableAddDependencyProperty = (selectedNode.getDirection() & ArtifactNode.OUTGOING) != 0;
							
						//incoming
						enableAddToClasspath = (selectedNode.getDirection() & ArtifactNode.INCOMING) != 0;
						enableRemoveFromPom = (selectedNode.getDirection() & ArtifactNode.INCOMING) != 0;
						
						//conflicting
						enableMarkAsMerged = (selectedNode.getDirection() & ArtifactNode.CONFLICTING) != 0;
						enableAddToIgnoreList = (selectedNode.getDirection() & ArtifactNode.CONFLICTING) == 0;
					}
				}
				
				pushToPom.setEnabled(enablePushToPom);
				removeFromProject.setEnabled(enableRemoveFromProject);
				addToClasspath.setEnabled(enableAddToClasspath);
				removeFromPom.setEnabled(enableRemoveFromPom);
				markAsMerged.setEnabled(enableMarkAsMerged);
				addToIgnoreList.setEnabled(enableAddToIgnoreList);
				
				if ( selections.size() == 1 ) {
					viewProperties.setEnabled(true);
					addDependencyProperty.setEnabled(enableAddDependencyProperty);
				}
				else {
					viewProperties.setEnabled(false);
					addDependencyProperty.setEnabled(false);
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
		if ( event.getSource() instanceof ToggleViewAction ) {
			setDirection(((ToggleViewAction) event.getSource()).getDirection());
		}
		if ( event.getSource() instanceof ToggleWritePropertiesAction && Action.CHECKED.equals(event.getProperty())) {
			fireSynchronizationConstraintEvent(new SynchronizationConstraintEvent(SynchronizationConstraintEvent.WRITE_PROPERTIES, ((Boolean) event.getNewValue()).booleanValue()));
		}
		if ( toolBarManager != null ) {
			log.debug("property changed. updating");
	    	toolBarManager.update(true);
		}
    }
	
	private void fireSynchronizationConstraintEvent(SynchronizationConstraintEvent event) {
		for (int i = 0; i < synchronizationConstraintListeners.size(); i++) {
			ISynchronizationConstraintListener listener = (ISynchronizationConstraintListener) synchronizationConstraintListeners.get(i);
			listener.constraintsChange(event);
		}
	}
	
	public void addSynchronizationConstraintListener(ISynchronizationConstraintListener listener) {
		synchronizationConstraintListeners.add(listener);	
	}
	
	public void removeSynchronizationConstraintListener(ISynchronizationConstraintListener listener) {
		synchronizationConstraintListeners.remove(listener);	
	}
	
	private void createContextualMenu() {
		MenuManager contextManager = new MenuManager();
		contextManager.setRemoveAllWhenShown(true);
		Menu menu = contextManager.createContextMenu(artifactMappingNodeViewer.getControl());
		artifactMappingNodeViewer.getControl().setMenu(menu);
		contextManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
			    Object selection = ((StructuredSelection) artifactMappingNodeViewer.getSelection()).getFirstElement();
			    if ( selection instanceof ArtifactNode ) {
			    	ArtifactNode selectedNode = (ArtifactNode) selection;
					if ( (selectedNode.getDirection() & ArtifactNode.OUTGOING) != 0 ) {
						manager.add(pushToPom);
						manager.add(removeFromProject);
						if ( selection instanceof MavenArtifactNode ) {
							manager.add(addDependencyProperty);
						}
					}
					if ( (selectedNode.getDirection() & ArtifactNode.INCOMING) != 0 ) {
						manager.add(addToClasspath);
						manager.add(removeFromPom);
					}
					if ( (selectedNode.getDirection() & ArtifactNode.CONFLICTING) != 0 ) {
						//manager.add(markAsMerged);
					}
					else {
						manager.add(addToIgnoreList);
					}
					manager.add(separator);
					manager.add(viewProperties);		
				}
				if ( selection instanceof PropertyNode ) {
					manager.add(viewProperties);
				}
			}
		});
	}

	
	public void artifactAddedToClasspath(IdeArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
		log.debug("artifact modified : " + artifact);
    	refreshNode(artifact);
	}

	public void propertyAdded(NodeEvent event) {
		log.debug("propertyAdded to " + event.getNode());
		artifactMappingNodeViewer.refresh(event.getNode());
	}
	
	private void refreshNode(ArtifactNode artifact) {
		artifactMappingNodeViewer.refresh(container);
	}
	
	private ITreeContentProvider getContentProvider() {
		return ((ITreeContentProvider) artifactMappingNodeViewer.getContentProvider());
	}

	public void artifactAddedToPom(PomArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
		log.debug("artifact modified : " + artifact);
		refreshNode(artifact);
		updatePoms(event.getProject());
		try {
			IFile file = this.project.getFile(MevenideUtils.makeRelativePath(this.project.getLocation().toFile(), event.getProject().getFile().getAbsolutePath()));
			file.refreshLocal(IResource.DEPTH_ZERO, null);
		} 
		catch (Exception e) {
			log.error("Uanble to refresh POM", e);
		}
		comparator.compare(event.getProject());
	}
	
	public void artifactRemovedFromPom(PomArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
		updatePoms(event.getProject());
		refreshNode(artifact);
		comparator.compare(event.getProject());
	}
	
	//@TODO evil.. but actions read poms instead of working on pom references.. 
	private void updatePoms(MavenProject project) {
	    for (int i = 0; i < poms.size(); i++) {
	        MavenProject pom = (MavenProject) poms.get(i);
			if ( pom.getFile().equals(project.getFile()) ) {
				poms.remove(i);
				poms.add(i, project);
			} 
        }
	    //((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setPoms(poms);
    }

    public TreeViewer getArtifactMappingNodeViewer() {
		return artifactMappingNodeViewer;
	}
	
	public void artifactRemovedFromClasspath(IdeArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
		refreshNode(artifact);	
	}
	
	public void artifactIgnored(IdeArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
		refreshNode(artifact);
	}
	
	public void artifactIgnored(PomArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
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

	/**
	 * @todo FIXME this method becomes ever more scary each time i discover a NPE
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
			final IProject project = artifactMappingNodeViewer != null ? (IProject) artifactMappingNodeViewer.getInput() : null;
			final IFile dotClasspath = project != null ? project.getFile(".classpath") : null;
			
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
										asyncRefresh(false);
									}
									if ( poms != null ) {
										for (int i = 0; i < poms.size(); i++) {
											File f = ((MavenProject) poms.get(i)).getFile();
											if ( new File(file.getLocation().toOSString()).equals(f) ) {
					
												try {
	                                                updatePoms(ProjectReader.getReader().read(f));
	                                            } 
												catch (Exception e) {
	                                                log.error("Unable to update pom list", e);
	                                            }
											}
										}
									}
									asyncRefresh(false);
								}
								if ( r instanceof IProject ) {
									IProject prj = (IProject) r;
									if ( project != null && prj.getName().equals(project.getName()) ) {
										asyncRefresh(false);
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
	
	private void asyncRefresh(boolean shouldExpand) {
		artifactMappingNodeViewer.getControl().getDisplay().asyncExec(
				new Runnable() {
					public void run () {
						refreshAll(false);
					}
				}
		);
	}

	public void refreshAll(boolean shouldExpand) {
		artifactMappingNodeViewer.refresh(true);
		if ( shouldExpand ) {
			//artifactMappingNodeViewer.expandAll();
		}
	}

    public IContainer getInputContainer() {
        return container;
    }
	
	public void projectChanged(ProjectChangeEvent e) {
	    log.debug("received project change notification. Attribute : " + e.getAttribute());
    	String attribute = e.getAttribute();
		if ( ProjectComparator.RESOURCES.equals(attribute) 
				|| ProjectComparator.UNIT_TESTS.equals(attribute) 
				|| ProjectComparator.BUILD.equals(attribute) 
				|| ProjectComparator.DEPENDENCIES.equals(attribute) ) {
		    updatePoms(e.getPom());
			refreshAll(false);
		}     
	}
	
	public void dispose() {
		super.dispose();
		this.isDisposed = true;
	}
	
	public boolean isDisposed() {
		return isDisposed;
	}
	
	public List getPoms() {
		return poms;
	}
}

