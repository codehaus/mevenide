/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
	
	public LaunchedAction(IProject project, String[] options, String[] goals) {
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
			LaunchHistory history = LaunchHistory.getHistory();
			history.save(project, options, goals); 
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