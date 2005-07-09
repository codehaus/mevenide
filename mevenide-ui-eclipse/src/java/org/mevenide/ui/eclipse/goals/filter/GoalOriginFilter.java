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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.ui.eclipse.Mevenide;
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
	
	public static final String ORIGIN_FILTER_KEY = "mevenide.goals.outline.filter.origin"; //$NON-NLS-1$

	private boolean isFilteringEnable;
	
	private IGoalsGrabber defaultGoalsGrabber;
	
	public GoalOriginFilter() {
		try {
			defaultGoalsGrabber = new DefaultGoalsGrabber();
			isFilteringEnable = getPreferenceStore().getBoolean(ORIGIN_FILTER_KEY);
			
		} 
		catch (Exception e) {
			log.error("Unable to create DefaultGoalsGrabber : ", e); //$NON-NLS-1$
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
			if ( isFilteringEnable && plugins[j].equals(element.getName()) ) {
				return false;
			}
		}
		return true;
	}
	
	private boolean filterGoalOrigin(Goal element) {
		if ( isFilteringEnable && IGoalsGrabber.ORIGIN_PLUGIN.equals(defaultGoalsGrabber.getOrigin(element.getFullyQualifiedName()))) {
			return false;
		}
		return true;
	}
	
	public void setEnable(boolean enable) {
		this.isFilteringEnable = enable;
		
		//should see how to save them in more optimized way : i/o access everytime we set the value..
        getPreferenceStore().setValue(ORIGIN_FILTER_KEY, enable);
        commitChanges();
	}
	
	public boolean isEnabled() {
		return isFilteringEnable;
	}

    /**
     * Saves the changes made to preferences.
     * @return <tt>true</tt> if the preferences were saved
     */
    private boolean commitChanges() {
        try {
            getPreferenceStore().save();
            return true;
        } catch (IOException e) {
            Mevenide.displayError("Unable to save preferences.", e);
        }

        return false;
    }

    /**
     * @return the preference store to use in this object
     */
    private IPersistentPreferenceStore getPreferenceStore() {
        return Mevenide.getInstance().getCustomPreferenceStore();
    }
	
}