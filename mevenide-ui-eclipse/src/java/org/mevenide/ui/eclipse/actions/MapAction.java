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
import org.eclipse.jface.action.IAction;
import org.mevenide.ui.eclipse.sync.view.DependencyMappingViewPart;
import org.mevenide.ui.eclipse.sync.view.SourceDirectoryMappingViewPart;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MapAction extends AbstractMevenideAction {
	private static Log log = LogFactory.getLog(MapAction.class);
	
	public void run(IAction action) {
		boolean source = true;
		try {
			if ( action.getId().equals("maven-plugin.mapSourceDirectories") ) {
				SourceDirectoryMappingViewPart.prompt(currentProject);
			}
			if ( action.getId().equals("maven-plugin.mapDependencies") ) {
				source = false;
				DependencyMappingViewPart.prompt(currentProject);
			}	
		}
		catch ( Exception e ) {
			log.debug("Unable to show " + (source ? "SourceDirectory Mapping View" : "Dependency Mapping View") + " due to : " + e);
		}
	}

}
