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

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
import org.mevenide.ui.eclipse.MavenPlugin;
import org.mevenide.ui.eclipse.sync.views.SourceDirectoryTypePart;
//import org.mevenide.ui.eclipse.sync.pom.views.SourceDirectoryPropertyView;
//import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * either synchronize pom add .classpath 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SynchronizeAction extends AbstractMavenAction {
	
    public void run(IAction action) {
		try {
            if ( action.getId().equals("maven-plugin.Synchronize") ) {
            	String mavenHome = MavenPlugin.getPlugin().getMavenHome();
				if ( mavenHome == null || mavenHome.trim().equals("") ) {
					MessageBox dialog = new MessageBox (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.OK);
					dialog.setText ("Mevenide");
					dialog.setMessage ("Cannot synchronize : Maven Home has not been set.");
					dialog.open ();
				}
				else {
					SynchronizerFactory.getSynchronizer(ISynchronizer.POM_TO_IDE).synchronize();
				}
			}
			if ( action.getId().equals("maven-plugin.SynchronizePom") ) {
				SourceDirectoryTypePart.synchronizeWithoutPrompting(currentProject);
			}
			if ( action.getId().equals("maven-plugin.mapSourceDirectories") ) {
				SourceDirectoryTypePart.sourceDirectoriesPrompt(currentProject);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}