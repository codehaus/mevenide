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
package org.codehaus.mevenide.ui.eclipse.actions;

import org.codehaus.mevenide.ui.eclipse.ErrorHandler;
import org.codehaus.mevenide.ui.eclipse.launch.M2LaunchShortcut;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class RunMaven2Action extends AbstractM2Action {
	private ISelection selection;
	
	public void run(IAction action) {
		String file = currentProject.getFile("pom.xml").getLocation().toOSString();
		
	    try {
	        M2LaunchShortcut shortcut = new M2LaunchShortcut();
			shortcut.setShowDialog(true);
			shortcut.launch(selection, null);
        }
        catch (Exception e) {
            String message = "Unable to run M2";
            e.printStackTrace();
            ErrorHandler.handleException(message, e);
        }
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection; 
		super.selectionChanged(action, selection);
	}

   
	
}
