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

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
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
	
    public void run(IAction action) {
		try {
            if ( action.getId().equals("maven-plugin.Synchronize") ) {
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
			e.printStackTrace();
		}
	}

    private boolean isNull(String strg) {
		return strg == null || strg.trim().equals("");
    }
}