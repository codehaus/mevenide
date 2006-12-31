/* ==========================================================================
 * Copyright 2003-2006 Mevenide Team
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

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.mevenide.context.IQueryContext;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.launch.configuration.MavenLaunchShortcut;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class RunMavenAction extends AbstractMevenideAction {
	private ISelection selection;
	
	public void run(IAction action) {
		MavenLaunchShortcut shortcut = new MavenLaunchShortcut();
		shortcut.setShowDialog(true);
		shortcut.launch(selection, null);
		
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection; 
        super.selectionChanged(action, selection);
        action.setEnabled(isPOMSelected(selection));
    }

    /**
     * A convienence method.
     * @return <tt>true</tt> if the given project has an associated IQueryContext
     */
    private static final boolean isPOMSelected(ISelection selection) {
        if (((StructuredSelection) selection).size() == 1) {
            Object firstElement = ((StructuredSelection) selection).getFirstElement();
            if (firstElement instanceof IResource) {
                final IResource resource = (IResource)firstElement;
                final IQueryContext context = Mevenide.getInstance().getPOMManager().getQueryContext(resource.getProject());
                if (context != null) {
                    final File pomFile = context.getPOMContext().getFinalProject().getFile();
                    return pomFile.equals(resource.getLocation().toFile());
                }
            }
        }

        return false;
    }
}
