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
 */
package org.mevenide.ui.eclipse.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.IAction;
import org.mevenide.ui.eclipse.nature.MevenideNature;

/**
 * 
 * MavenPlugin listens to resource changes within maven enabled projects 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class ManageNatureAction extends AbstractMevenideAction {
	
	private static Log log = LogFactory.getLog(ManageNatureAction.class);
	 
	public void run(IAction action) {
		boolean add = true;
		try {
			
			if ( currentProject != null ) {	
				if ( action.getId().equals("org.mevenide.ui.eclipse.actions.addmavennature") ) {
					MevenideNature.configureProject(currentProject);
				}
				else {
					add = false;
					MevenideNature.deconfigureProject(currentProject);	
				}
			}
		} 
		catch(Exception e) {
			log.debug("Unable to " + (add ? " add " : " remove ") + " Maven Nature due to : " + e);
		}
	}

}
