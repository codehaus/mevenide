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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

/**
 * i guess it will be dropped when i ll take a look at the Eclipse LaunchConfiguration
 * However for now its quicker to implement
 * 
 * @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LaunchHistory {
	private LaunchedAction lastlaunched;
	
	private List launchedActions = new ArrayList();
	
	private static LaunchHistory history;
	
	static {
		history = new LaunchHistory();
	}
	
	private LaunchHistory() {
		load();
	}
	
	
	public static LaunchHistory getHistory() {
		return history;
	}
	
	public LaunchedAction[] getLaunchedActions() {
		LaunchedAction[] hist = new LaunchedAction[launchedActions.size()];
		for (int i = 0; i < hist.length; i++) {
			hist[i] = (LaunchedAction) launchedActions.get(i);
		}
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
		LaunchedAction action = new LaunchedAction(this, project, options, goals);
		if ( launchedActions.contains(action) ) {
			launchedActions.remove(action);
		}
		launchedActions.add(0, action);
		lastlaunched = action;
		
	}
	
	/**
	 * delete memory history and file on disk
	 *
	 */
	public void clear() {
		
	}
	
	/**
	 * 
	 * load previously saved config 
	 *
	 */
	private void load() {
	
	}
	
	/**
	 * @return
	 */
	public LaunchedAction getLastlaunched() {
		return lastlaunched;
	}

}
