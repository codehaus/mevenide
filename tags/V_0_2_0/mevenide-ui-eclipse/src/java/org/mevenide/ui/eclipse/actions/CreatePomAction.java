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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.mevenide.ui.eclipse.util.FileUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CreatePomAction extends AbstractMevenideAction {
	private static Log log = LogFactory.getLog(CreatePomAction.class);
	
	public void run(IAction action) {
		try {
			if ( FileUtils.getPom(currentProject) != null && !FileUtils.getPom(currentProject).exists() ) {
				FileUtils.createPom(currentProject);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Unable to create POM due to : " + e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		
//      i still have to figure out how to disable the ui associated to the action
//		if ( Mevenide.getPlugin().getPom().exists() ) {
//		    action.setEnabled(false); 
//		}
//		else {
//			action.setEnabled(true);
//		}

	}

}
