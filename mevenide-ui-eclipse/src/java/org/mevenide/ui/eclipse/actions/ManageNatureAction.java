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

import org.eclipse.jface.action.IAction;
import org.mevenide.ui.eclipse.nature.MevenideNature;

/**
 * 
 * MavenPlugin listens to resource changes within maven enabled projects 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @todo EXTERNALIZE
 */
public class ManageNatureAction extends AbstractMevenideAction {
	
	public void run(IAction action) {
		try {
			
			if ( currentProject != null ) {	
				if ( action.getId().equals("org.mevenide.ui.eclipse.actions.addmavennature") ) {
					MevenideNature.configureProject(currentProject);
				}
				else {
					MevenideNature.deconfigureProject(currentProject);	
				}
			}
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
