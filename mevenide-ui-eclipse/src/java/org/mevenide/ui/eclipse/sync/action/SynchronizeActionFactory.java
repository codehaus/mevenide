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
package org.mevenide.ui.eclipse.sync.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.model.IArtifactMappingNode;
import org.mevenide.ui.eclipse.sync.model.EclipseContainerContainer;
import org.mevenide.ui.eclipse.sync.view.PomChooser;
import org.mevenide.ui.eclipse.sync.view.SynchronizeView;

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

	public static final String REFRESH_ALL = "REFRESH_ALL";

	public static final String VIEW_CONFLICTS = "VIEW_CONFLICTS";
	public static final String VIEW_OUTGOING = "VIEW_OUTGOING";
	public static final String VIEW_INCOMING = "VIEW_INCOMING";

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

		createViewConflictsAction();
		createViewIdeToPomAction();
		createViewPomToIdeAction();
		createViewPropertiesAction();
	}
	
	private void createAddToIgnoreListAction() {
		final AddToMvnIgnoreAction action = new AddToMvnIgnoreAction();
		Action addToIgnoreList = new Action() {
			public void run() {
				List selections = ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).toList();
				
				for (int i = 0; i < selections.size(); i++) {
					if ( selections.get(i) instanceof IArtifactMappingNode ) { 
					IArtifactMappingNode selectedNode = (IArtifactMappingNode) selections.get(i);
						try  {
							int direction = synchronizeView.getDirection();
							
							if ( direction == EclipseContainerContainer.OUTGOING ) {
								EclipseContainerContainer container = (EclipseContainerContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
								IContainer project = container.getProject();
								action.addEntry(selectedNode,  project);
							}
							else {
								Project mavenProject = ProjectReader.getReader().read(selectedNode.getDeclaringPom());
								action.addEntry(selectedNode,  mavenProject);
							}
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getArtifact() + " to classpath ", e );
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
					if ( selections.get(i) instanceof IArtifactMappingNode ) { 
						IArtifactMappingNode selectedNode = (IArtifactMappingNode) selections.get(i);
						try  {
							log.debug(selectedNode.getDeclaringPom());
							Project mavenProject = ProjectReader.getReader().read(selectedNode.getDeclaringPom());
							if ( mavenProject != null ) {
								action.removeEntry(selectedNode, mavenProject);
							}
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getArtifact() + " to classpath ", e );
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
					if ( selections.get(i) instanceof IArtifactMappingNode ) { 
						IArtifactMappingNode selectedNode = (IArtifactMappingNode) selections.get(i);
						EclipseContainerContainer container = (EclipseContainerContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
						IProject project = container.getProject().getProject();
						try  {
							action.removeEntry(selectedNode, project);
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getResolvedArtifact() + " to classpath ", e );
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
					if ( selections.get(j) instanceof IArtifactMappingNode ) { 
						IArtifactMappingNode selectedNode = (IArtifactMappingNode) selections.get(j);
						EclipseContainerContainer container = (EclipseContainerContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
						IContainer project = container.getProject();
						try  {
						    //IContainer f = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(project.getLocation());
						    IContainer f = synchronizeView.getInputContainer();
						    List mavenProjects = new PomChooser(f).openPomChoiceDialog(false);
						    for (int i = 0; i < mavenProjects.size(); i++) {
		                        Project mavenProject = (Project) mavenProjects.get(i); 
								log.debug("POM choice : " + mavenProject);
								if ( mavenProject != null ) {
									action.addEntry(selectedNode, mavenProject);
								}	
		                    }
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getResolvedArtifact() + " to classpath ", e );
						}
					}
				}
			}
		};
		action.addActionListener(synchronizeView);
		pushToPom.setId(ADD_TO_POM);
		pushToPom.setText("Update Pom...");
		actionIds.put(ADD_TO_POM, pushToPom);
	}

	private void createViewPomToIdeAction() {
	    Action viewPomToIde = new ToggleViewAction(synchronizeView, EclipseContainerContainer.INCOMING);
		viewPomToIde.setId(VIEW_INCOMING);
		viewPomToIde.setToolTipText("Incoming Changes");
		viewPomToIde.setImageDescriptor(Mevenide.getImageDescriptor("pom_to_ide_sync.gif"));
		actionIds.put(VIEW_INCOMING, viewPomToIde);
	}

	private void createViewIdeToPomAction() {
	    Action viewIdeToPom = new ToggleViewAction(synchronizeView, EclipseContainerContainer.OUTGOING);
		viewIdeToPom.setId(VIEW_OUTGOING);
		viewIdeToPom.setToolTipText("Outgoing changes");
		viewIdeToPom.setImageDescriptor(Mevenide.getImageDescriptor("ide_to_pom_sync.gif"));
		actionIds.put(VIEW_OUTGOING, viewIdeToPom);
	}

	private void createViewConflictsAction() {
	    Action viewConflicts = new ToggleViewAction(synchronizeView, EclipseContainerContainer.CONFLICTING);
		viewConflicts.setId(VIEW_CONFLICTS);
		viewConflicts.setToolTipText("Conflicts");
		viewConflicts.setImageDescriptor(Mevenide.getImageDescriptor("conflict_synch.gif"));
		actionIds.put(VIEW_CONFLICTS, viewConflicts);
	}

	private void createRefreshAllAction() {
		Action refreshAll = new Action() {
			public void run() {
				synchronizeView.refreshAll();
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
					if ( selections.get(i) instanceof IArtifactMappingNode ) { 
						IArtifactMappingNode selectedNode = (IArtifactMappingNode) selections.get(i);
						EclipseContainerContainer container = (EclipseContainerContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
						IProject project = container.getProject().getProject();
						try  {
							action.addEntry(selectedNode, project);
						}
						catch ( Exception e ) {
							log.debug("Unable to add item " + selectedNode.getArtifact() + " to classpath ", e );
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


