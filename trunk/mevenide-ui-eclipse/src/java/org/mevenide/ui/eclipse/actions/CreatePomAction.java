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
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.template.view.ChooseTemplateDialog;
import org.mevenide.ui.eclipse.util.FileUtils;
import org.mevenide.util.StringUtils;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CreatePomAction extends AbstractMevenideAction {
	private static Log log = LogFactory.getLog(CreatePomAction.class);
	
	private IContainer currentContainer;
	
	public void run(IAction action) {
		try {
			if ( FileUtils.getPom(currentProject) != null && !FileUtils.getPom(currentProject).exists() ) {
			    String pomTemplate = chooseTemplate();
			    boolean createPom = true;
			    if ( StringUtils.isNull(pomTemplate) ) {
			        createPom = MessageDialog.openQuestion(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            Mevenide.getResourceString("CreatePomAction.template.null.title"), 
                            Mevenide.getResourceString("CreatePomAction.template.null.message"));
			    }
			    if ( createPom ) {
		            FileUtils.createPom(currentContainer, pomTemplate);
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Unable to create POM due to : " + e);
		}
	}
	
	private String chooseTemplate() {
	    ChooseTemplateDialog dialog = new ChooseTemplateDialog();
	    int userChoice = dialog.open();
	    String result = null;
	    if ( userChoice == Window.OK ) {
	        result = dialog.getTemplate().getProject().getFile().getAbsolutePath();
	    }
	    return result;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		Object o =  ((StructuredSelection) selection).getFirstElement();
		if ( o instanceof IContainer ) {
		    this.currentContainer = (IContainer) o;
		}
		if ( o instanceof IJavaElement ) {
		    IResource resource = ((IJavaElement) o).getResource();
		    if ( resource instanceof IContainer ) {
		        this.currentContainer = (IContainer) resource;
		    }
		}
//      i still have to figure out how to disable the ui associated to the action
//		if ( Mevenide.getPlugin().getPom().exists() ) {
//		    action.setEnabled(false); 
//		}
//		else {
//			action.setEnabled(true);
//		}

	}

}
