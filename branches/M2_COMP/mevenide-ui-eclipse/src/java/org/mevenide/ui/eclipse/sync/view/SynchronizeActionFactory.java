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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.action.AddPropertyAction;
import org.mevenide.ui.eclipse.sync.action.AddToClasspathAction;
import org.mevenide.ui.eclipse.sync.action.AddToMvnIgnoreAction;
import org.mevenide.ui.eclipse.sync.action.AddToPomAction;
import org.mevenide.ui.eclipse.sync.action.RemoveFromClasspathAction;
import org.mevenide.ui.eclipse.sync.action.RemoveFromPomAction;
import org.mevenide.ui.eclipse.sync.action.ToggleViewAction;
import org.mevenide.ui.eclipse.sync.action.ToggleWritePropertiesAction;
import org.mevenide.ui.eclipse.sync.model.ArtifactNode;
import org.mevenide.ui.eclipse.sync.model.EclipseProjectNode;
import org.mevenide.ui.eclipse.sync.model.MavenArtifactNode;

/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class SynchronizeActionFactory {
	private static Log log = LogFactory.getLog(SynchronizeActionFactory.class);

	public static final String ADD_TO_CLASSPATH = "ADD_TO_CLASSPATH";
	public static final String ADD_TO_POM = "ADD_TO_POM";
	public static final String REMOVE_FROM_PROJECT = "REMOVE_FROM_PROJECT";
	public static final String REMOVE_FROM_POM = "REMOVE_FROM_POM";
	public static final String ADD_DEPENDENCY_PROPERTIES = "ADD_DEP_PROPERTY";
	public static final String REFRESH_ALL = "REFRESH_ALL";

	public static final String VIEW_CONFLICTS = "VIEW_CONFLICTS";
	public static final String VIEW_OUTGOING = "VIEW_OUTGOING";
	public static final String VIEW_INCOMING = "VIEW_INCOMING";

	public static final String WRITE_PROPERTIES = "WRITE_PROPERTIES";
	
	public static final String MARK_AS_MERGED = "MARK_AS_MERGED";

	public static final String PROPERTIES = "PROPERTIES";

	public static final String MVN_IGNORE = "MVN_IGNORE";
	
	private static Map factories = new HashMap();
	
	private SynchronizeView synchronizeView;
	
	private Map actionIds = new HashMap();
	
	
	private SynchronizeActionFactory (SynchronizeView view) {
		this.synchronizeView = view;
		initActions();
	}
	
	public static synchronized SynchronizeActionFactory getFactory(SynchronizeView view) {
		if ( view == null ) {
			return null;
		}
		if ( factories.containsKey(view) ) {
			return (SynchronizeActionFactory) factories.get(view);
		}
		SynchronizeActionFactory factory = new SynchronizeActionFactory(view);
		factories.put(view, factory);
		return factory;
	}
	
	public Action getAction(String actionId) {
		return (Action) actionIds.get(actionId);
	}
	
	private void initActions() {
		createAddToClasspathAction();
		createMarkAsMergedAction();
		createPushToPomAction();
		createRefreshAllAction();
		createRemoveFromPomAction();
		createRemoveFromProjectAction();
		createAddToIgnoreListAction();
		createAddDependencyPropertyAction();
		
		createViewConflictsAction();
		createViewIdeToPomAction();
		createViewPomToIdeAction();
		createViewPropertiesAction();
		createWritePropertiesAction();
	}
	
	private void createAddToIgnoreListAction() {
		final AddToMvnIgnoreAction action = new AddToMvnIgnoreAction();
		Action addToIgnoreList = new Action() {
			public void run() {
				List selections = ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).toList();
				
				for (int i = 0; i < selections.size(); i++) {
					if ( selections.get(i) instanceof ArtifactNode ) { 
					ArtifactNode selectedNode = (ArtifactNode) selections.get(i);
						try  {
							int direction = synchronizeView.getDirection();
							
							if ( direction == ArtifactNode.OUTGOING ) {
								IContainer container = (IContainer) ((EclipseProjectNode) selectedNode.getParent().getParent()).getData();
								action.addEntry(selectedNode,  container);
							}
							else {
								MavenProject mavenProject = (MavenProject) selectedNode.getParent().getData();
								action.addEntry(selectedNode,  mavenProject);
							}
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getData() + " to ignore list ", e );
						}
					}
				}
			}
		};
		action.addActionListener(synchronizeView);
		addToIgnoreList.setId(MVN_IGNORE);
		addToIgnoreList.setText("Add to .mvnignore");
		actionIds.put(MVN_IGNORE, addToIgnoreList);
	}

	private void createViewPropertiesAction() {
		Action viewProperties = new Action() {
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.PropertySheet");
				}
				catch ( PartInitException e ) {
					log.debug(e, e);
				}
			}
		};
		viewProperties.setId(PROPERTIES);
		viewProperties.setText("Properties");
		actionIds.put(PROPERTIES, viewProperties);
	}

	private void createAddDependencyPropertyAction() {
		final AddPropertyAction action = new AddPropertyAction();
		Action editProperties = new Action() {
			public void run() {
				MavenArtifactNode node = (MavenArtifactNode) ((StructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).getFirstElement();
				action.addProperty(node);
			}
		};
		action.addActionListener(synchronizeView);
		editProperties.setId(ADD_DEPENDENCY_PROPERTIES);
		editProperties.setText("Add Property");
		actionIds.put(ADD_DEPENDENCY_PROPERTIES, editProperties);
	}
	
	private void createMarkAsMergedAction() {
		Action markAsMerged = new Action() {
			public void run() {
				
			}
		};
		markAsMerged.setId(MARK_AS_MERGED);
		markAsMerged.setText("Mark as Merged");
		actionIds.put(MARK_AS_MERGED, markAsMerged);
	}

	private void createRemoveFromPomAction() {
		final RemoveFromPomAction action = new RemoveFromPomAction();
		Action removeFromPom = new Action() {
			public void run() {
				List selections = ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).toList();
				
				for (int i = 0; i < selections.size(); i++) {
					if ( selections.get(i) instanceof ArtifactNode ) { 
						ArtifactNode selectedNode = (ArtifactNode) selections.get(i);
						try  {
							MavenProject mavenProject = (MavenProject) selectedNode.getParent().getData();
							if ( mavenProject != null ) {
								action.removeEntry(selectedNode, mavenProject);
							}
						}
						catch ( Exception e ) {
							log.debug("Unable to remove item " + selectedNode.getData() + " from pom ", e );
						}
					}
				}
			}
		};
		action.addActionListener(synchronizeView);
		removeFromPom.setId(REMOVE_FROM_POM);
		removeFromPom.setText("Remove from Pom");
		actionIds.put(REMOVE_FROM_POM, removeFromPom);
	}

	private void createRemoveFromProjectAction() {
		final RemoveFromClasspathAction action = new RemoveFromClasspathAction();
		Action removeFromProject = new Action() {
			public void run() {
				List selections = ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).toList();
				
				for (int i = 0; i < selections.size(); i++) {
					if ( selections.get(i) instanceof ArtifactNode ) { 
						ArtifactNode selectedNode = (ArtifactNode) selections.get(i);
						IContainer container = (IContainer) ((EclipseProjectNode) selectedNode.getParent().getParent()).getData();
						IProject project = container.getProject().getProject();
						try  {
							action.removeEntry(selectedNode, project);
						}
						catch ( Exception e ) {
							log.debug("Unable to remove item " + selectedNode.getData() + " from classpath ", e );
						}
					}
				}
			}
		};
		action.addActionListener(synchronizeView);
		removeFromProject.setId(REMOVE_FROM_PROJECT);
		removeFromProject.setText("Remove from project");
		actionIds.put(REMOVE_FROM_PROJECT, removeFromProject);
	}

	private void createPushToPomAction() {
		final AddToPomAction action = new AddToPomAction();
		Action pushToPom = new Action() {
			public void run() {
				List selections = ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).toList();
				
				for (int j = 0; j < selections.size(); j++) {
					if ( selections.get(j) instanceof ArtifactNode ) { 
						ArtifactNode selectedNode = (ArtifactNode) selections.get(j);
						MavenProject currentPom = (MavenProject) selectedNode.getParent().getData();
						try  {
						    //IContainer f = synchronizeView.getInputContainer();
						    List mavenProjects = new PomChooser(currentPom).openPomChoiceDialog(false);
							for (int i = 0; i < mavenProjects.size(); i++) {
		                        MavenProject mavenProject = (MavenProject) mavenProjects.get(i); 
								log.debug("POM choice : " + mavenProject);
								if ( mavenProject != null ) {
									action.addEntry(selectedNode, mavenProject);
								}	
		                    }
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getData() + " to pom ", e );
						}
					}
				}
			}
		};
		synchronizeView.addSynchronizationConstraintListener(action);
		action.addActionListener(synchronizeView);
		pushToPom.setId(ADD_TO_POM);
		pushToPom.setText("Update Pom...");
		actionIds.put(ADD_TO_POM, pushToPom);
	}

	private void createViewPomToIdeAction() {
	    ToggleViewAction viewPomToIde = new ToggleViewAction(ArtifactNode.INCOMING);
		viewPomToIde.setId(VIEW_INCOMING);
		viewPomToIde.setToolTipText("Incoming Changes");
		viewPomToIde.setImageDescriptor(Mevenide.getImageDescriptor("pom_to_ide_sync.gif"));
		actionIds.put(VIEW_INCOMING, viewPomToIde);
		synchronizeView.addDirectionListener(viewPomToIde);
		viewPomToIde.addPropertyChangeListener(synchronizeView);
	}

	private void createViewIdeToPomAction() {
	    ToggleViewAction viewIdeToPom = new ToggleViewAction(ArtifactNode.OUTGOING);
		viewIdeToPom.setId(VIEW_OUTGOING);
		viewIdeToPom.setToolTipText("Outgoing changes");
		viewIdeToPom.setImageDescriptor(Mevenide.getImageDescriptor("ide_to_pom_sync.gif"));
		actionIds.put(VIEW_OUTGOING, viewIdeToPom);
		synchronizeView.addDirectionListener(viewIdeToPom);
		viewIdeToPom.addPropertyChangeListener(synchronizeView);
	}

	private void createViewConflictsAction() {
	    ToggleViewAction viewConflicts = new ToggleViewAction(ArtifactNode.CONFLICTING);
		viewConflicts.setId(VIEW_CONFLICTS);
		viewConflicts.setToolTipText("Conflicts");
		viewConflicts.setImageDescriptor(Mevenide.getImageDescriptor("conflict_synch.gif"));
		actionIds.put(VIEW_CONFLICTS, viewConflicts);
		synchronizeView.addDirectionListener(viewConflicts);
		viewConflicts.addPropertyChangeListener(synchronizeView);
	}

	private void createWritePropertiesAction() {
		Action writeProperties = new ToggleWritePropertiesAction();
		writeProperties.setId(WRITE_PROPERTIES);
		writeProperties.setToolTipText("Override project.properties");
		writeProperties.setImageDescriptor(Mevenide.getImageDescriptor("write_properties.gif"));
		actionIds.put(WRITE_PROPERTIES, writeProperties);
		writeProperties.addPropertyChangeListener(synchronizeView);
	}
	
	private void createRefreshAllAction() {
		Action refreshAll = new Action() {
			public void run() {
				synchronizeView.refreshAll(false);
			}
		};
		refreshAll.setId(REFRESH_ALL);
		refreshAll.setToolTipText("Refresh All");
		refreshAll.setImageDescriptor(Mevenide.getImageDescriptor("refresh.gif"));
		actionIds.put(REFRESH_ALL, refreshAll);
	}
	
	private void createAddToClasspathAction() {
		final AddToClasspathAction action = new AddToClasspathAction();
		Action addToClasspath = new Action() {
			public void run() {
				List selections = ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).toList();
				
				for (int i = 0; i < selections.size(); i++) {
					if ( selections.get(i) instanceof ArtifactNode ) { 
						ArtifactNode selectedNode = (ArtifactNode) selections.get(i);
						IContainer container = (IContainer) ((EclipseProjectNode) selectedNode.getParent().getParent()).getData();
						IProject project = container.getProject().getProject();
						try  {
							action.addEntry(selectedNode, project);
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getData() + " to classpath ", e );
						}
					}
				}
			}
		};
		action.addActionListener(synchronizeView);
		addToClasspath.setId(ADD_TO_CLASSPATH);
		addToClasspath.setText("Add to .classpath");
		actionIds.put(ADD_TO_CLASSPATH, addToClasspath);
	}	
}


