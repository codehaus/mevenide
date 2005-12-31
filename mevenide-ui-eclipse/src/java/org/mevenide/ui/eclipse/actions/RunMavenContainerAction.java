/* ==========================================================================
 * Copyright 2003-2005 Apache Software Foundation
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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.launch.configuration.MavenLaunchShortcut;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 */
public class RunMavenContainerAction extends AbstractMevenideAction {
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
        action.setEnabled(hasQueryContext(getCurrentProject()));
    }

    /**
     * A convienence method.
     * @return <tt>true</tt> if the given project has an associated IQueryContext
     */
    private static final boolean hasQueryContext(IProject project) {
        return Mevenide.getInstance().getPOMManager().getQueryContext(project) != null;
    }
}
