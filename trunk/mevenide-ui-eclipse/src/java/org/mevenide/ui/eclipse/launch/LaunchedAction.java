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

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.mevenide.core.AbstractRunner;
import org.mevenide.ui.eclipse.Mevenide;

/**
 * @author <a href="mailto:rhill@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class LaunchedAction extends Action {
	private static Log log = LogFactory.getLog(LaunchedAction.class);
	
	private IProject project;
	private String[] options;
	private String[] goals;
	
	private String text;
	
	public LaunchedAction(LaunchHistory history, IProject project, String[] options, String[] goals) {
		this.project = project;
		this.options = options;
		this.goals = goals;
		
		text = "[" + project.getName() + "] >" ;
		for (int i = 0; i < goals.length; i++) {
			text += " " + goals[i] ;
		}
		
		setText(text);
	}

	public void run() {
		try {
			Mevenide.getPlugin().setProject(project);
			AbstractRunner.getRunner().run(options, goals);
		} catch (Exception e) {
			log.error("Unable to run LaunchedAction due to : " + e);
		}
	}
	
	/**
	 * two LA are equals if and only if la1.project = la2.project AND la1.goals = la2.goals
	 * corrolary : options equality is not an equality parameter 
	 */
	public boolean equals(Object o) {
		
		if( !(o instanceof LaunchedAction)
		    || !project.equals(((LaunchedAction) o).project) ) {
		   return false;
		} 	
		
		boolean b = Arrays.equals(goals, ((LaunchedAction) o).goals);
		
		log.debug(this.toString() + " .equals( " + goals.toString() +" ) = " + (b));
		return b;
	}
	
	public String toString() {
		return "[LaunchedAction {project= + " + project.getName() + ", goals = " + goals + "} ]";	
	}
	
	public String[] getGoals() {
		return goals;
	}

	public String[] getOptions() {
		return options;
	}

	public IProject getProject() {
		return project;
	}

	public void setGoals(String[] strings) {
		goals = strings;
	}

	public void setOptions(String[] strings) {
		options = strings;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}