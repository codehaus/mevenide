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

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.sync.view.SynchronizationView;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SynchronizeContainerAction extends AbstractMevenideAction {

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        try {
            final IWorkbenchPage page = getWorkbenchWindow().getActivePage();
            SynchronizationView view = (SynchronizationView) page.showView(SynchronizationView.ID);
            view.setInput(getCurrentProject());
        } catch (PartInitException e) {
            final String message = "Unable to open the POM synchronization view.";
            Mevenide.displayError(message, e);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
        action.setEnabled(!isAutosyncEnabled() && hasQueryContext(getCurrentProject()));
    }

    /**
     * A convienence method.
     * @return <tt>true</tt> if the given project has an associated IQueryContext
     */
    private static final boolean hasQueryContext(IProject project) {
        return Mevenide.getInstance().getPOMManager().getQueryContext(project) != null;
    }

    /**
     * A convienence method.
     * @return <tt>true</tt> if automatic POM synchronization is enabled
     */
    private static final boolean isAutosyncEnabled() {
        return Mevenide.getInstance().getPreferenceStore().getBoolean(MevenidePreferenceKeys.AUTOSYNC_ENABLED);
    }
}