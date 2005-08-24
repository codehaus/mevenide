/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.MevenideRuntimeException;
import org.mevenide.project.IProjectChangeListener;
import org.mevenide.project.ProjectChangeEvent;
import org.mevenide.project.ProjectComparator;
import org.mevenide.project.ProjectComparatorFactory;
import org.mevenide.ui.eclipse.IImageRegistry;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.action.ToggleViewAction;
import org.mevenide.ui.eclipse.sync.action.ToggleWritePropertiesAction;
import org.mevenide.ui.eclipse.sync.event.IActionListener;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationConstraintListener;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationDirectionListener;
import org.mevenide.ui.eclipse.sync.event.ISynchronizationNodeListener;
import org.mevenide.ui.eclipse.sync.event.IdeArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.NodeEvent;
import org.mevenide.ui.eclipse.sync.event.PomArtifactEvent;
import org.mevenide.ui.eclipse.sync.event.SynchronizationConstraintEvent;
import org.mevenide.ui.eclipse.sync.model.ArtifactNode;
import org.mevenide.ui.eclipse.sync.model.ISelectableNode;
import org.mevenide.ui.eclipse.sync.model.ISynchronizationNode;
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
public class SynchronizationView extends ViewPart implements IActionListener, IResourceChangeListener, IPropertyChangeListener, IProjectChangeListener, ISynchronizationNodeListener {
    public static final String ID = Mevenide.PLUGIN_ID  + ".synchronize.view.SynchronizationView"; //$NON-NLS-1$

	private static final Log log = LogFactory.getLog(SynchronizationView.class);

    private static final String SYNC_DIRECTION_VIEW = "SynchronizationView.SYNC_DIRECTION_VIEW"; //$NON-NLS-1$
	private static final String SYNC_SHOULD_WRITE_PROPERTIES = "SynchronizationView.SYNC_SHOULD_WRITE_PROPERTIES"; //$NON-NLS-1$

    private Composite composite;
    private TreeViewer artifactMappingNodeViewer;
    private SynchronizationNodeFilter synchronizationNodeFilter; 
    
	private MavenArtifactNodeFilter artifactNodeFilter;
	private DirectoryNodeFilter directoryNodeFilter;
	
    //global actions
    private Action refreshAll;
    private Action viewIdeToPom;
    private Action viewPomToIde;
    private Action viewConflicts;
    private Action openFilterDialogAction;  
    
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
	
    private boolean initialShouldWriteProperties;
	
	public SynchronizationView() {
		super();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
		direction = getPreferenceStore().getInt(SYNC_DIRECTION_VIEW);
		initializeDirection();
	}
	
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
		    poms = new PomChooser(input.getProject()).openPomChoiceDialog(false);
		    
		    if ( poms != null ) {
		        synchronizeProjectWithPoms(input.getProject(), poms);
		    }
		} 
		catch (Exception e) {
			log.error("Cannot find pom for project " + input, e); //$NON-NLS-1$
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
            log.error("Unable to synchronize POM " + input.getFile().getName() + " with project " + input, e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
    
    private void synchronizeProjectWithPoms(IProject project, List poms) {
    	SynchronizationNodeProvider provider = (SynchronizationNodeProvider) artifactMappingNodeViewer.getContentProvider();
    	SynchronizationNodeProvider.RootNode root = provider.new RootNode(project, poms);
    	root.addNodeListener(this);
    	
    	try {
    	    artifactMappingNodeViewer.setInput(root);
    	}
    	catch ( MevenideRuntimeException e ) {
    	    MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
    	            				Mevenide.getResourceString("SynchronizeView.InvalidPom.Title"),  //$NON-NLS-1$
    	            				Mevenide.getResourceString(e.getMessage()));
    	    hideView();
    	    return;
    	}
        
        for (int i = 0; i < poms.size(); i++) {
        	Project mavenProject = (Project) poms.get(i);
            comparator = ProjectComparatorFactory.getComparator(mavenProject);
			comparator.addProjectChangeListener(ProjectComparator.BUILD, this);
			comparator.addProjectChangeListener(ProjectComparator.DEPENDENCIES, this); 
			comparator.addProjectChangeListener(ProjectComparator.UNIT_TESTS, this);
			comparator.addProjectChangeListener(ProjectComparator.RESOURCES, this);
        }
        
        this.poms = poms;
        this.project = project;
        
        assertValidDirection();
        
        refreshAll();
    }

    private void hideView() {
        SynchronizationView view = (SynchronizationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ID);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(view);
    }

    private void assertValidDirection() {
        initializeDirection();
        setDirection(direction);
    }
    
    private void initializeDirection() {
		if ( (direction & ISelectableNode.INCOMING_DIRECTION) == 0 
                && (direction & ISelectableNode.OUTGOING_DIRECTION) == 0 
                && (direction & ISelectableNode.CONFLICTING_DIRECTION) == 0 ) {
        	direction = ISelectableNode.INCOMING_DIRECTION;
        }
	}

	public void setDirection(int direction) {
		if ( direction != this.direction ) {
	        this.direction = direction;
	        synchronizationNodeFilter.setDirection(direction);
	        artifactMappingNodeViewer.refresh();
	        activateWritePropertiesAction(direction);
			fireDirectionChanged();
            getPreferenceStore().setValue(SYNC_DIRECTION_VIEW, direction);
            commitChanges();
		}
    }

    private void activateWritePropertiesAction(int direction) {
    	if ( direction == ISelectableNode.OUTGOING_DIRECTION ) {
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
        artifactMappingNodeViewer.setAutoExpandLevel(3);
        
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
        artifactMappingNodeViewer.setContentProvider(new SynchronizationNodeProvider());
        artifactMappingNodeViewer.setLabelProvider(new SynchronizationNodeLabelProvider());
        
        synchronizationNodeFilter = new SynchronizationNodeFilter();
        artifactMappingNodeViewer.addFilter(synchronizationNodeFilter);
        synchronizationNodeFilter.setDirection(direction);
        
        artifactNodeFilter = new MavenArtifactNodeFilter();
        artifactMappingNodeViewer.addFilter(artifactNodeFilter);
        
        directoryNodeFilter = new DirectoryNodeFilter();
        artifactMappingNodeViewer.addFilter(directoryNodeFilter);
        
        ViewerSorter sorter = new SynchronizatioNodeSorter(); 
        artifactMappingNodeViewer.setSorter(sorter);
        
        artifactMappingNodeViewer.getTree().addListener (SWT.MouseDoubleClick, 
            	new Listener () {
            		public void handleEvent (Event event) {
            		    Object selection = ((IStructuredSelection) artifactMappingNodeViewer.getSelection()).getFirstElement();
        	            boolean isExpanded = artifactMappingNodeViewer.getExpandedState(selection);
        	            if ( !isExpanded ) {
        	                artifactMappingNodeViewer.expandToLevel(selection, 1);
        	            }
        	            else {
        	                artifactMappingNodeViewer.collapseToLevel(selection, 1);
        	            }
            		}
            	}
            );
        isDisposed = false;
    }
    
    private void createActions() {
    	SynchronizeActionFactory actionFactory = SynchronizeActionFactory.getFactory(this);
    	
        refreshAll = actionFactory.getAction(SynchronizeActionFactory.REFRESH_ALL);
       
		viewConflicts = actionFactory.getAction(SynchronizeActionFactory.VIEW_CONFLICTS);
		viewIdeToPom = actionFactory.getAction(SynchronizeActionFactory.VIEW_OUTGOING);
		viewPomToIde = actionFactory.getAction(SynchronizeActionFactory.VIEW_INCOMING);
		viewConflicts.setChecked(direction == ISelectableNode.CONFLICTING_DIRECTION);
		viewIdeToPom.setChecked(direction == ISelectableNode.OUTGOING_DIRECTION);
		viewPomToIde.setChecked(direction == ISelectableNode.INCOMING_DIRECTION);
		
		Action writePropertiesAction = actionFactory.getAction(SynchronizeActionFactory.WRITE_PROPERTIES); 
		writeProperties = new ActionContributionItem(writePropertiesAction);
		initialShouldWriteProperties = getPreferenceStore().getBoolean(SYNC_SHOULD_WRITE_PROPERTIES);
        writePropertiesAction.setChecked(initialShouldWriteProperties);
		
		pushToPom = actionFactory.getAction(SynchronizeActionFactory.ADD_TO_POM);
		removeFromProject = actionFactory.getAction(SynchronizeActionFactory.REMOVE_FROM_PROJECT);
		addToClasspath = actionFactory.getAction(SynchronizeActionFactory.ADD_TO_CLASSPATH);
		removeFromPom = actionFactory.getAction(SynchronizeActionFactory.REMOVE_FROM_POM);

		markAsMerged = actionFactory.getAction(SynchronizeActionFactory.MARK_AS_MERGED);

		viewProperties = actionFactory.getAction(SynchronizeActionFactory.PROPERTIES);

		addToIgnoreList = actionFactory.getAction(SynchronizeActionFactory.MVN_IGNORE);
		
		addDependencyProperty = actionFactory.getAction(SynchronizeActionFactory.ADD_DEPENDENCY_PROPERTIES);
		
		openFilterDialogAction = new Action() {
			public void run() {
				NodeFilterDialog dialog = new NodeFilterDialog();
				int result = dialog.open();
				if ( result == Window.OK ) {
					artifactNodeFilter.setFilterMavenArtifacts(dialog.shouldEnableArtifactFiltering());
					artifactNodeFilter.setGroupIdFilter(dialog.getGroupIdFilter());
					
					directoryNodeFilter.setFilterDirectoryNodes(dialog.shouldEnableDirectoryFiltering());
					directoryNodeFilter.setFilterSourceDirectories(dialog.shouldFilterSource());
					directoryNodeFilter.setFilterTestDirectories(dialog.shouldFilterTest());
					directoryNodeFilter.setFilterAspectDirectories(dialog.shouldFilterAspect());
					directoryNodeFilter.setFilterResourceDirectories(dialog.shouldFilterResource());
					directoryNodeFilter.setFilterOutputDirectories(dialog.shouldFilterOutput());
					
					//problem : we lose informations entered in property sheet..
					refreshAll();
				}
			}
		};
		openFilterDialogAction.setText(Mevenide.getResourceString("SynchronizationView.FilterDialog.Text")); //$NON-NLS-1$
		openFilterDialogAction.setImageDescriptor(Mevenide.getInstance().getImageRegistry().getDescriptor(IImageRegistry.OPEN_FILTER_DIALOG));
		
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
						
						//all directions
						enableAddToIgnoreList = true;
						
						//outgoing 
						enablePushToPom = (selectedNode.getDirection() & ISelectableNode.OUTGOING_DIRECTION) != 0;
						enableRemoveFromProject = (selectedNode.getDirection() & ISelectableNode.OUTGOING_DIRECTION) != 0;
						enableAddDependencyProperty = (selectedNode.getDirection() & ISelectableNode.OUTGOING_DIRECTION) != 0;
						//enableAddToIgnoreList = (selectedNode.getDirection() & ISelectableNode.OUTGOING_DIRECTION) != 0;
							
						//incoming
						enableAddToClasspath = (selectedNode.getDirection() & ISelectableNode.INCOMING_DIRECTION) != 0;
						enableRemoveFromPom = (selectedNode.getDirection() & ISelectableNode.INCOMING_DIRECTION) != 0;
						
						//conflicting
						enableMarkAsMerged = (selectedNode.getDirection() & ISelectableNode.CONFLICTING_DIRECTION) != 0;
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
		topLevelMenuManager.add(this.openFilterDialogAction);
		topLevelMenuManager.add(separator);
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
		toolBarManager.add(viewConflicts);
		activateWritePropertiesAction(direction);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if ( event.getSource() instanceof ToggleViewAction ) {
			setDirection(((ToggleViewAction) event.getSource()).getDirection());
		}
		if ( event.getSource() instanceof ToggleWritePropertiesAction && Action.CHECKED.equals(event.getProperty())) {
			boolean shouldWriteProperties = ((Boolean) event.getNewValue()).booleanValue();
            fireSynchronizationConstraintEvent(new SynchronizationConstraintEvent(SynchronizationConstraintEvent.WRITE_PROPERTIES, shouldWriteProperties));
            getPreferenceStore().setValue(SYNC_SHOULD_WRITE_PROPERTIES, shouldWriteProperties);
            commitChanges();
		}
		if ( toolBarManager != null ) {
			log.debug("property changed. updating"); //$NON-NLS-1$
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
					manager.add(addToIgnoreList);
					if ( (selectedNode.getDirection() & ISelectableNode.OUTGOING_DIRECTION) != 0 ) {
						manager.add(pushToPom);
						manager.add(removeFromProject);
						if ( selection instanceof MavenArtifactNode ) {
							manager.add(addDependencyProperty);
						}
					}
					if ( (selectedNode.getDirection() & ISelectableNode.INCOMING_DIRECTION) != 0 ) {
						manager.add(addToClasspath);
						manager.add(removeFromPom);
					}
					if ( (selectedNode.getDirection() & ISelectableNode.CONFLICTING_DIRECTION) != 0 ) {
						manager.add(markAsMerged);
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
		log.debug("artifact modified : " + artifact); //$NON-NLS-1$
    	refreshAll();
	}

	public void propertyAdded(NodeEvent event) {
		log.debug("propertyAdded to " + event.getNode()); //$NON-NLS-1$
		artifactMappingNodeViewer.refresh(event.getNode());
	}

	public void artifactAddedToPom(PomArtifactEvent event) {
		ArtifactNode artifact = (ArtifactNode) event.getArtifact();
		log.debug("artifact modified : " + artifact); //$NON-NLS-1$
		refreshAll();
		updatePoms(event.getProject());
		try {
			IFile file = this.project.getFile(MevenideUtils.makeRelativePath(this.project.getLocation().toFile(), event.getProject().getFile().getAbsolutePath()));
			file.refreshLocal(IResource.DEPTH_ZERO, null);
		} 
		catch (Exception e) {
			log.error("Unable to refresh POM", e); //$NON-NLS-1$
		}
		comparator.compare(event.getProject());
	}
	
	public void artifactRemovedFromPom(PomArtifactEvent event) {
		updatePoms(event.getProject());
		refreshAll();
		comparator.compare(event.getProject());
	}
	
	//@TODO evil.. but actions read poms instead of working on pom references.. 
	void updatePoms(Project project) {
	    for (int i = 0; i < poms.size(); i++) {
	        Project pom = (Project) poms.get(i);
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
		refreshAll();
	}
	
	public void artifactIgnored(IdeArtifactEvent event) {
		refreshAll();
	}
	
	public void artifactIgnored(PomArtifactEvent event) {
		refreshAll();
	}
	
	public int getDirection() {
		return direction;
	}


	public void resourceChanged(IResourceChangeEvent event) {
		try {
			IProject project = artifactMappingNodeViewer != null && artifactMappingNodeViewer.getInput() != null ? ((SynchronizationNodeProvider.RootNode) artifactMappingNodeViewer.getInput()).getProject() : null;
			
			IResourceDelta d= event.getDelta();
			if (d != null) {
				d.accept(new SynchronizationResourceDeltaVisitor(this, project));
			}
		} 
		catch (Exception e) {
			log.debug("error processing resource delta", e); //$NON-NLS-1$
		}		
	}
	
	void asyncRefresh(final boolean shouldExpand) {
	    refreshAll();
//		artifactMappingNodeViewer.getControl().getDisplay().asyncExec(
//				new Runnable() {
//					public void run () {
//						refreshAll();
//					}
//				}
//		);
	}

	public void refreshAll() {
	    artifactMappingNodeViewer.refresh();
	}

    public IContainer getInputContainer() {
        return container;
    }
	
    public void nodeChanged(ISynchronizationNode node) {
    	log.debug("Node changed : " + node); //$NON-NLS-1$
    	artifactMappingNodeViewer.update(node, null);
	}
    
	public void projectChanged(ProjectChangeEvent e) {
	    log.debug("received project change notification. Attribute : " + e.getAttribute()); //$NON-NLS-1$
    	String attribute = e.getAttribute();
		if ( ProjectComparator.RESOURCES.equals(attribute) 
				|| ProjectComparator.UNIT_TESTS.equals(attribute) 
				|| ProjectComparator.BUILD.equals(attribute) 
				|| ProjectComparator.DEPENDENCIES.equals(attribute) ) {
		    updatePoms(e.getPom());
			refreshAll();
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
	
    public boolean getInitialShouldWriteProperties() {
        return initialShouldWriteProperties;
    }

    /**
     * Saves the changes made to preferences.
     * @return <tt>true</tt> if the preferences were saved
     */
    private boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Unable to save preferences.", e);
        }

        return false;
    }

    /**
     * @return the preference store to use in this object
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
}

