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
import org.mevenide.ui.eclipse.nature.MevenideNature;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class AddMavenNatureAction extends AbstractMevenideAction {
	
	private static Log log = LogFactory.getLog(AddMavenNatureAction.class);
	 
	public void run(IAction action) {
		try {
			if ( currentProject != null ) {	
				MevenideNature.configureProject(currentProject);
			}
		} 
		catch(Exception e) {
			log.debug("Unable to  add Maven Nature", e); //$NON-NLS-1$
		}
	}

}
