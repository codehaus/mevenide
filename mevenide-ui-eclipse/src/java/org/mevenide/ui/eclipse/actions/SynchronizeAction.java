/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */

package org.mevenide.ui.eclipse.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.sync.wizard.SynchronizeWizard;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SynchronizeAction extends AbstractMevenideAction {
	private static Log log = LogFactory.getLog(SynchronizeAction.class);
	
    public void run(IAction action) {
    	boolean pom = true;
		try {
            if ( action.getId().equals("maven-plugin.Synchronize") ) {
            	pom = false;
            	String mavenHome = Mevenide.getPlugin().getMavenHome();
            	String mavenRepository = Mevenide.getPlugin().getMavenRepository();
            	if ( isNull(mavenHome) || isNull(mavenRepository) ) {
					Mevenide.popUp("Mevenide", "Please set maven preferences before synchronizing");
				}
				else {
					if ( JavaCore.getClasspathVariable("MAVEN_REPO") == null ) {
						JavaCore.setClasspathVariable("MAVEN_REPO", new Path(Mevenide.getPlugin().getMavenRepository()), null);
					}
					SynchronizerFactory.getSynchronizer(ISynchronizer.POM_TO_IDE).synchronize();
				}
			}
			if ( action.getId().equals("maven-plugin.SynchronizePom") ) {
				//show synch wizard
				Wizard wizard = new SynchronizeWizard(currentProject);
				WizardDialog dialog 
					= new WizardDialog(
						PlatformUI.getWorkbench()
								  .getActiveWorkbenchWindow()
								  .getShell(), wizard);
				dialog.create();
				dialog.open();
			}
		}
		catch (Exception e) {
			log.debug("Unable to synchronize " + (pom ? "POM" : "project") + " due to : " + e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		try {
			if ( !currentProject.hasNature(JavaCore.NATURE_ID) ) {
				action.setEnabled(false);
			}
			else {
				action.setEnabled(true);
			}
		} 
		catch (CoreException e) {
			log.debug("Unable to disable action '" + action.getText() + "' due to : " + e);
		}
	}


    private boolean isNull(String strg) {
		return strg == null || strg.trim().equals("");
    }
}