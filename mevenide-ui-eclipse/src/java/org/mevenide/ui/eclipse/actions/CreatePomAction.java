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
import org.eclipse.jface.viewers.ISelection;
import org.mevenide.ui.eclipse.util.FileUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CreatePomAction extends AbstractMevenideAction {
	private static Log log = LogFactory.getLog(AbstractMevenideAction.class);
	
	public void run(IAction action) {
		try {
			if ( !FileUtil.getPom(currentProject).exists() ) {
				FileUtil.createPom(currentProject);
//				SourceDirectoryViewPart.showView();
//				SourceDirectoryViewPart.getInstance().setInput(currentProject);
			}
		} catch (Exception e) {
			log.debug("Unable to create POM due to : " + e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		
//      i still have to figure out how to disable the ui associated to the action
//		if ( Mevenide.getPlugin().getPom().exists() ) {
//		    action.setEnabled(false); 
//		}
//		else {
//			action.setEnabled(true);
//		}

	}

}
