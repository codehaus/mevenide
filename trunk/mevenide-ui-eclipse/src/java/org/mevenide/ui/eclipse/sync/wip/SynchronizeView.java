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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
    private Action viewProperties;
    
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
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
        if ( artifactMappingNodeViewer.getInput() != null ) {
            ((ArtifactMappingContentProvider) artifactMappingNodeViewer.getContentProvider()).setDirection(direction);
        }
        artifactMappingNodeViewer.refresh(true);
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
		pushToPom.setText("Update Pom");

		addToClasspath = new Action() {
		    public void run() {
		        
		    }
		};
		addToClasspath.setId("POP_POM");
		addToClasspath.setText("Add to .classpath");

		viewProperties = new Action() {
		    public void run() {
		        
		    }
		};
		viewProperties.setId("PROPERTIES");
		viewProperties.setText("Properties");
    }
    
    private void plugActions() {
        getViewSite().getActionBars().getMenuManager().add(pushToPom);
		getViewSite().getActionBars().getMenuManager().add(addToClasspath);
		getViewSite().getActionBars().getMenuManager().add(viewProperties);

		getViewSite().getActionBars().getToolBarManager().add(refreshAll);
		getViewSite().getActionBars().getToolBarManager().add(viewIdeToPom);
		getViewSite().getActionBars().getToolBarManager().add(viewPomToIde);
		getViewSite().getActionBars().getToolBarManager().add(viewConflicts);

    }
    
    
}
