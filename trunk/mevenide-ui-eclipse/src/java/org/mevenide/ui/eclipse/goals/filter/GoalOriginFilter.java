/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.ui.eclipse.goals.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.ui.eclipse.goals.model.Goal;
import org.mevenide.ui.eclipse.goals.model.Plugin;

/** 
* 
* @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
* @version $Id: GlobalOriginFilter.java,v 1.2 4 avr. 2004 Exp gdodinet 
* 
*/
public class GoalOriginFilter extends ViewerFilter {
	private static final Log log = LogFactory.getLog(GoalOriginFilter.class);
	
	private boolean filterOriginPlugin;
	
	private IGoalsGrabber defaultGoalsGrabber;
	private IGoalsGrabber goalsGrabber;

	public static final String ORIGIN_FILTER_KEY = "mevenide.goals.outline.filter.origin";

	public GoalOriginFilter() {
		try {
			defaultGoalsGrabber = new DefaultGoalsGrabber();
		} 
		catch (Exception e) {
			log.error("Unable to create DefaultGoalsGrabber : ", e);
		}
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if ( element instanceof Plugin ) {
			return filterPluginOrigin((Plugin) element);
		}
		if ( element instanceof Goal ) {  
			return filterGoalOrigin((Goal) element);
		}
		//if we reach this point then element is equal to Element.NULL_ROOT, never filter it
		return true;  
	}

	private boolean filterPluginOrigin(Plugin element) {
		//donot cache it.. we will eventually provide an option to not load it each time..
		String[] plugins = defaultGoalsGrabber.getPlugins();
		for (int j = 0; j < plugins.length; j++) {
			if ( filterOriginPlugin && plugins[j].equals(element.getName()) ) {
				return false;
			}
		}
		return true;
	}
	
	private boolean filterGoalOrigin(Goal element) {
		if ( filterOriginPlugin && IGoalsGrabber.ORIGIN_PLUGIN.equals(goalsGrabber.getOrigin(element.getFullyQualifiedName()))) {
			return false;
		}
		return true;
	}
	
	public void setFilterOriginPlugin(boolean filterOriginPlugin) {
		this.filterOriginPlugin = filterOriginPlugin;
	}
	
	public void setGoalsGrabber(IGoalsGrabber goalsGrabber) {
		this.goalsGrabber = goalsGrabber;
	}
}