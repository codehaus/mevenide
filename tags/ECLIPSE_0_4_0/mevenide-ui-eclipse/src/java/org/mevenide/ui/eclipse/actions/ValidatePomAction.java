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
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.mevenide.ui.eclipse.pom.validation.ValidationJob;

/**
 * @author <a href="rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 */
public class ValidatePomAction extends AbstractMevenideAction {
	
	private static Log log = LogFactory.getLog(ValidatePomAction.class);
	 
	private IFile pomFile;
	
	public void run(IAction action) {
	    if ( pomFile != null ) {
	        new ValidationJob(pomFile).schedule();
	    }
	}
	
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
        Object firstElement = ((StructuredSelection) selection).getFirstElement();
        if ( firstElement instanceof IFile ) {
            pomFile = (IFile) firstElement;
        }
    }

}
