/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Mevenide @ Sourceforge.net.  All rights
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.ViewPart;
import org.mevenide.ui.eclipse.Mevenide;



/**
 * 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 *
 */
public class SynchronizeView extends ViewPart {
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
        ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(this.direction);
        artifactMappingNodeViewer.refresh(true);
        artifactMappingNodeViewer.expandAll();
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
        if ( artifactMappingNodeViewer.getInput() != null ) {
            ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(direction);
        }
        artifactMappingNodeViewer.refresh(true);
        artifactMappingNodeViewer.expandAll();
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
        refreshAll = new Action() {
            public void run() {
                artifactMappingNodeViewer.refresh(true);
                artifactMappingNodeViewer.expandAll();
            }
        };
		refreshAll.setId("REFRESH_VIEWER");
		refreshAll.setToolTipText("Refresh All");
		refreshAll.setImageDescriptor(Mevenide.getImageDescriptor("refresh.gif"));
       
		viewConflicts = new Action() {
		    public void run() {
		        setDirection(ProjectContainer.CONFLICTING);
		    }
		};
		viewConflicts.setId("CONFLICTING");
		viewConflicts.setToolTipText("Conflicts");
		viewConflicts.setImageDescriptor(Mevenide.getImageDescriptor("conflicting.gif"));
		
		viewIdeToPom = new Action() {
		    public void run() {
		        setDirection(ProjectContainer.OUTGOING);
		    }
		};
		viewIdeToPom.setId("IDE_TO_POM");
		viewIdeToPom.setToolTipText("Outgoing changes");
		viewIdeToPom.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD_HOVER));

		viewPomToIde = new Action() {
		    public void run() {
		        setDirection(ProjectContainer.INCOMING);
		    }
		};
		viewPomToIde.setId("POM_TO_IDE");
		viewPomToIde.setToolTipText("Incoming Changes");
		viewPomToIde.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(org.eclipse.ui.ISharedImages.IMG_TOOL_BACK_HOVER));

		pushToPom = new Action() {
		    public void run() {
		        
		    }
		};
		pushToPom.setId("PUSH_TO_POM");
		pushToPom.setText("Update Pom...");

		removeFromProject = new Action() {
			public void run() {
				
			}
		};
		removeFromProject.setId("REM_FROM_PROJECT");
		removeFromProject.setText("Remove from project");
		
		addToClasspath = new AddToClasspathAction(this);
		
		removeFromPom = new Action() {
			public void run() {
				
			}
		};
		removeFromPom.setId("REM_FROM_POM");
		removeFromPom.setText("Remove from Pom");

		markAsMerged = new Action() {
		    public void run() {
		        
		    }
		};
		markAsMerged.setId("MERGE");
		markAsMerged.setText("Mark as Merged");

		viewProperties = new Action() {
		    public void run() {
				try {
			        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.PropertySheet");
			 	}
				catch ( PartInitException e ) {
					log.debug(e, e);
				}
		    }
		};
		viewProperties.setId("PROPERTIES");
		viewProperties.setText("Properties");

		addToIgnoreList = new Action() {
			public void run() {
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.PropertySheet");
				}
				catch ( PartInitException e ) {
					log.debug(e, e);
				}
			}
		};
		addToIgnoreList.setId("MVN_IGNORE");
		addToIgnoreList.setText("Add to .mvnignore");
		
		separator  = new Separator();
		
		addToClasspath.setEnabled(false);
		pushToPom.setEnabled(false);
		markAsMerged.setEnabled(false);
		viewProperties.setEnabled(false);
		removeFromPom.setEnabled(false);
		removeFromProject.setEnabled(false);
		addToIgnoreList.setEnabled(false);
		
		artifactMappingNodeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
				Object selection = ((StructuredSelection) event.getSelection()).getFirstElement();
				if ( !(selection instanceof IArtifactMappingNode) ) {
					addToClasspath.setEnabled(false);
					pushToPom.setEnabled(false);
					markAsMerged.setEnabled(false);
					viewProperties.setEnabled(false);
					return;
				}
				viewProperties.setEnabled(true);
				IArtifactMappingNode selectedNode = (IArtifactMappingNode) selection;
				if ( (selectedNode.getChangeDirection() & ProjectContainer.OUTGOING) != 0 ) {
					pushToPom.setEnabled(true);
					removeFromProject.setEnabled(true);
				}
				else {
				    pushToPom.setEnabled(false);
				    removeFromProject.setEnabled(false);
				}
				if ( (selectedNode.getChangeDirection() & ProjectContainer.INCOMING) != 0 ) {
					addToClasspath.setEnabled(true);
					removeFromPom.setEnabled(true);
				}
				else {
					addToClasspath.setEnabled(false);
					removeFromPom.setEnabled(false);
				}
				if ( (selectedNode.getChangeDirection() & ProjectContainer.CONFLICTING) != 0 ) {
					markAsMerged.setEnabled(true);
					addToIgnoreList.setEnabled(false);
				}
				else {
					addToIgnoreList.setEnabled(true);
				    markAsMerged.setEnabled(false);
				    
				}
            }
		});
    }
    
    private void plugActions() {
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
		
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		toolBarManager.add(refreshAll);
		toolBarManager.add(viewIdeToPom);
		toolBarManager.add(viewPomToIde);
		toolBarManager.add(viewConflicts);

		
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
    
    public TreeViewer getArtifactMappingNodeViewer() {
		return artifactMappingNodeViewer;
	}

}
