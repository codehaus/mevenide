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
package org.mevenide.ui.eclipse.sync.wip;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.mevenide.project.io.ProjectReader;
import org.mevenide.ui.eclipse.Mevenide;

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
		createToIgnoreListAction();

		createViewConflictsAction();
		createViewIdeToPomAction();
		createViewPomToIdeAction();
		createViewPropertiesAction();
	}
	
	private void createToIgnoreListAction() {
		Action addToIgnoreList = new Action() {
			public void run() {
				
			}
		};
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
				IArtifactMappingNode selectedNode = (IArtifactMappingNode) ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).getFirstElement();
				
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
				IArtifactMappingNode selectedNode = (IArtifactMappingNode) ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).getFirstElement();
				
				ProjectContainer container = (ProjectContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
				IProject project = container.getProject();
				
				try  {
					
					action.removeEntry(selectedNode, project);
					
				}
				catch ( Exception e ) {
					log.debug("Unable to add item " + selectedNode.getResolvedArtifact() + " to classpath ", e );
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
				IArtifactMappingNode selectedNode = (IArtifactMappingNode) ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).getFirstElement();
				
				ProjectContainer container = (ProjectContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
				IProject project = container.getProject();
				
				try  {
					Project mavenProject = new PomChooser(project).openPomChoiceDialog();
					log.debug("POM choice : " + mavenProject);
					
					if ( mavenProject != null ) {
						action.addEntry(selectedNode, mavenProject);
					}
					
				}
				catch ( Exception e ) {
					log.debug("Unable to add item " + selectedNode.getResolvedArtifact() + " to classpath ", e );
				}
			}
		};
		action.addActionListener(synchronizeView);
		pushToPom.setId(ADD_TO_POM);
		pushToPom.setText("Update Pom...");
		actionIds.put(ADD_TO_POM, pushToPom);
	}

	private void createViewPomToIdeAction() {
		Action viewPomToIde = new Action() {
			public void run() {
				synchronizeView.setDirection(ProjectContainer.INCOMING);
			}
		};
		viewPomToIde.setId(VIEW_INCOMING);
		viewPomToIde.setToolTipText("Incoming Changes");
		viewPomToIde.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_HOVER));
		actionIds.put(VIEW_INCOMING, viewPomToIde);
	}

	private void createViewIdeToPomAction() {
		Action viewIdeToPom = new Action() {
			public void run() {
				synchronizeView.setDirection(ProjectContainer.OUTGOING);
			}
		};
		viewIdeToPom.setId(VIEW_OUTGOING);
		viewIdeToPom.setToolTipText("Outgoing changes");
		viewIdeToPom.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_HOVER));
		actionIds.put(VIEW_OUTGOING, viewIdeToPom);
	}

	private void createViewConflictsAction() {
		Action viewConflicts = new Action() {
			public void run() {
				synchronizeView.setDirection(ProjectContainer.CONFLICTING);
			}
		};
		viewConflicts.setId(VIEW_CONFLICTS);
		viewConflicts.setToolTipText("Conflicts");
		viewConflicts.setImageDescriptor(Mevenide.getImageDescriptor("conflicting.gif"));
		actionIds.put(VIEW_CONFLICTS, viewConflicts);
	}

	private void createRefreshAllAction() {
		Action refreshAll = new Action() {
			public void run() {
				synchronizeView.getArtifactMappingNodeViewer().refresh(true);
				synchronizeView.getArtifactMappingNodeViewer().expandAll();
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
				IArtifactMappingNode selectedNode = (IArtifactMappingNode) ((IStructuredSelection) synchronizeView.getArtifactMappingNodeViewer().getSelection()).getFirstElement();
				
				ProjectContainer container = (ProjectContainer) synchronizeView.getArtifactMappingNodeViewer().getTree().getItems()[0].getData();
				IProject project = container.getProject();
				
				try  {
					action.addEntry(selectedNode, project);
				}
				catch ( Exception e ) {
					log.debug("Unable to add item " + selectedNode.getArtifact() + " to classpath ", e );
				}
			}
		};
		action.addActionListener(synchronizeView);
		addToClasspath.setId(ADD_TO_CLASSPATH);
		addToClasspath.setText("Add to .classpath");
		actionIds.put(ADD_TO_CLASSPATH, addToClasspath);
	}	
}


