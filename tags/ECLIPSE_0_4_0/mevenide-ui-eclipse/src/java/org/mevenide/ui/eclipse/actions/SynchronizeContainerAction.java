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
package org.mevenide.ui.eclipse.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.sync.view.SynchronizationView;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SynchronizeContainerAction extends AbstractMevenideAction {
    
    private static Log log = LogFactory.getLog(SynchronizeContainerAction.class);
	
    private static final String SYNCHRONIZE_VIEW_ID = "org.mevenide.ui.synchronize.view.SynchronizationView"; //$NON-NLS-1$

    private IContainer container;

    public void run(IAction action) {
        try {
            SynchronizationView view = (SynchronizationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SYNCHRONIZE_VIEW_ID);
			IContainer f = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(container.getLocation());
			view.setInput(f);
        }
        catch ( Exception e ) {
            log.error("Unable to create Pom Synchronization View", e); //$NON-NLS-1$
        }
	}

	public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
		
		Object firstElement = ((StructuredSelection) selection).getFirstElement();
       
		if ( firstElement instanceof IContainer ) {
            container = (IContainer) firstElement;        
        }
        if ( firstElement instanceof IJavaProject ) {
            container = ((IJavaProject) firstElement).getProject(); 
        }
    }


}