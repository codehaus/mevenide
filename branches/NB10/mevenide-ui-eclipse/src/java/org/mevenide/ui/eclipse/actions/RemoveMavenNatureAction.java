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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.nature.MevenideNature;

/**
 * Removes the Maven Nature from the currently selected project.
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 */
public class RemoveMavenNatureAction extends AbstractMevenideAction {

    public void run(IAction action) {
        try {
            if (getCurrentProject() != null) {
                MevenideNature.removeFromProject(getCurrentProject());
            }
        } catch (CoreException e) {
            final String msg = "Unable to remove Maven nature from " + getCurrentProject().getName() + ".";
            Mevenide.displayError(action.getDescription(), msg, e);
        }
    }

}
