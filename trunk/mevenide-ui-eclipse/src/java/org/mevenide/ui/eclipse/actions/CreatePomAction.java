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
import org.eclipse.jface.viewers.ISelection;
import org.mevenide.ui.eclipse.sync.views.SourceDirectoryTypePart;
import org.mevenide.ui.eclipse.util.ProjectUtil;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class CreatePomAction extends AbstractMavenAction {

	public void run(IAction action) {
		try {
			if ( !ProjectUtil.getPom(currentProject).exists() ) {
				ProjectUtil.createPom(currentProject);
				SourceDirectoryTypePart.showView();
				SourceDirectoryTypePart.getInstance().setInput(currentProject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		//thats the idea...
		//however it is doesnt bahaves exactly as expected (e.g. action disabled and ui activated)
		 
//		if ( ProjectUtil.getPom(currentProject).exists() ) {
//			action.setEnabled(false);
//		}

	}

}
