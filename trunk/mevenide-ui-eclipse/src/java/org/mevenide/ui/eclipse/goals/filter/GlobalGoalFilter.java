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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.ui.eclipse.goals.model.Element;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;


public class GlobalGoalFilter extends ViewerFilter {
	private static final Log log = LogFactory.getLog(GlobalGoalFilter.class);
	
	public static final String ORIGIN_FILTER_GOALS = "mevenide.goals.outline.filter.origin.goals";
	
	private List filteredGoals = new ArrayList(); 
	
	private IGoalsGrabber defaultGoalsGrabber;
	private IGoalsGrabber goalsGrabber;

	public GlobalGoalFilter() {
		try {
			defaultGoalsGrabber = new DefaultGoalsGrabber();

			PreferencesManager preferencesManager = PreferencesManager.getManager();
			preferencesManager.loadPreferences();
			
			setFilteredGoals(preferencesManager.getValue(ORIGIN_FILTER_GOALS));
		} 
		catch (Exception e) {
			log.error("Unable to create DefaultGoalsGrabber : ", e);
		}
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !isElementFiltered((Element) element);  
	}

	public void setFilteredGoals(String goalListAsString) {
		if ( goalListAsString == null ) {
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(goalListAsString, ",");
		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			log.debug("Adding \"" + token + "\" to Global Goals filters");
			filteredGoals.add(token);
		}
	}
	
	private boolean isElementFiltered(Element element) {
		return filteredGoals.contains(element.getFullyQualifiedName());
	}
	
	public void setGoalsGrabber(IGoalsGrabber goalsGrabber) {
		this.goalsGrabber = goalsGrabber;
	}
}