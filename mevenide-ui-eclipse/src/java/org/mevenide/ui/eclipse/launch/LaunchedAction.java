/*
 * Created on 28 juil. 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.mevenide.ui.eclipse.launch;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.mevenide.core.AbstractRunner;
import org.mevenide.ui.eclipse.Mevenide;


public class LaunchedAction extends Action {
	private static Log log = LogFactory.getLog(LaunchedAction.class);
	
	private IProject project;
	private String[] options;
	private String[] goals;
	
	public LaunchedAction(LaunchHistory history, IProject project, String[] options, String[] goals) {
		String text = "[" + project.getName() + "] >" ;
		for (int i = 0; i < goals.length; i++) {
			text += " " + goals[i] ;
		}
		setText(text);
		this.project = project;
		this.options = options;
		this.goals = goals;
	}
	
	public void run() {
		try {
			Mevenide.getPlugin().setProject(project);
			AbstractRunner.getRunner().run(options, goals);
		} catch (Exception e) {
			log.error("Unable to run LaunchedAction due to : " + e);
		}
	}
	
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
}