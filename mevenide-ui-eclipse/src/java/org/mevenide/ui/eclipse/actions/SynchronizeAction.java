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
import org.mevenide.sync.ISynchronizer;
import org.mevenide.sync.SynchronizerFactory;
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
				SynchronizerFactory.getSynchronizer(ISynchronizer.POM_TO_IDE).synchronize();
			}
			if ( action.getId().equals("maven-plugin.SynchronizePom") ) {
				//SynchronizerFactory.getSynchronizer(ISynchronizer.IDE_TO_POM).synchronize();
				SourceDirectoryTypePart.showView();
				System.out.println(currentProject);
				SourceDirectoryTypePart.getInstance().setInput(currentProject);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}