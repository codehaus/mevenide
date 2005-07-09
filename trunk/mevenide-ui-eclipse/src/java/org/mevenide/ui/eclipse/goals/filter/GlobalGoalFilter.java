/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.ui.eclipse.Mevenide;
import org.mevenide.ui.eclipse.goals.model.Element;

/** 
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id: GlobalGoalFilter.java,v 1.2 4 avr. 2004 Exp gdodinet 
 * 
 */
public class GlobalGoalFilter extends ViewerFilter {
	private static final Log log = LogFactory.getLog(GlobalGoalFilter.class);
	
	public static final String ORIGIN_FILTER_GOALS = "mevenide.goals.outline.filter.origin.goals"; //$NON-NLS-1$
	
	private List filteredGoals = new ArrayList(); 

	public GlobalGoalFilter() {
		try {
		
			setFilteredGoals(getPreferenceStore().getString(ORIGIN_FILTER_GOALS));
		} 
		catch (Exception e) {
			log.error("Unable to create DefaultGoalsGrabber : ", e); //$NON-NLS-1$
		}
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return !filteredGoals.contains(((Element) element).getFullyQualifiedName());  
	}

	public void setFilteredGoals(String goalListAsString) {
		filteredGoals.clear();
		log.debug("setting up GlobalGoalFilter w/ : [" + goalListAsString + "]");  //$NON-NLS-1$//$NON-NLS-2$
		if ( goalListAsString == null ) {
			return;
		}
		StringTokenizer tokenizer = new StringTokenizer(goalListAsString, ","); //$NON-NLS-1$
		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			log.debug("Adding \"" + token + "\" to Global Goals filters");  //$NON-NLS-1$//$NON-NLS-2$
			filteredGoals.add(token);
		}
	}

    /**
     * @return the preference store to use in this object
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }

}