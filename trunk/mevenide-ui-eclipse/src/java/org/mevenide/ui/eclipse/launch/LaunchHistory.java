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
package org.mevenide.ui.eclipse.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;

/**
 * @todo implement-me
 * 
 * @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LaunchHistory {
	public class LaunchedAction extends Action {
		public void run() {
			super.run();
		}
	}
	
	private static LaunchHistory history = new LaunchHistory();
	
	public static LaunchHistory getHistory() {
		return history;
	}
	
	public LaunchedAction[] getLaunchedActions() {
		LaunchedAction[] hist = new LaunchedAction[0];
		return hist;
	}
	
	/**
	 * save config to disk and add it to memory list (top)
	 * 
	 * @param project
	 * @param options
	 * @param goals
	 */
	public void save(IProject project, String[] options, String[] goals) {
		
	}
	
	/**
	 * delete memory history and file on disk
	 *
	 */
	public void clear() {
		
	}
	
	
}
