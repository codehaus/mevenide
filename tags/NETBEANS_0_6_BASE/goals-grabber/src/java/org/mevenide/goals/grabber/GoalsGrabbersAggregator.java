/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.goals.grabber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsGrabberAggregator.java 6 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsGrabbersAggregator implements IGoalsGrabber {
	
	
	private List goalsGrabbers = new ArrayList();
	
	public void refresh() throws Exception {
        
        Iterator iterator = goalsGrabbers.iterator();
        while ( iterator.hasNext() ) {
        	IGoalsGrabber goalsGrabber = (IGoalsGrabber) iterator.next();
        	goalsGrabber.refresh();
        }
    }
    
    public String getName() {
        return "Aggregator";
    }
    
    public String getOrigin(String fullyQualifiedGoalName) {
        String origin = null;
        for (int i = 0; i < goalsGrabbers.size(); i++) {
            origin = ((IGoalsGrabber)goalsGrabbers.get(i)).getOrigin(fullyQualifiedGoalName);
            if ( origin != null ) {
                break;
            }
        }
        return origin;
        
    }
        
    public void addGoalsGrabber(IGoalsGrabber goalsGrabber) {
    	goalsGrabbers.add(goalsGrabber);
    } 

	public void removeGoalsGrabber(IGoalsGrabber goalsGrabber) {
		goalsGrabbers.remove(goalsGrabber);
	}
    
    public String getDescription(String fullyQualifiedGoalName) {
        String description = null;
        for (int i = 0; i < goalsGrabbers.size(); i++) {
            description = ((IGoalsGrabber)goalsGrabbers.get(i)).getDescription(fullyQualifiedGoalName);
			if ( description != null ) {
				return description;
			}
        }
        return description;
    }

    public String[] getGoals(String plugin) {
		String[] goals = null;
		for (int i = 0; i < goalsGrabbers.size(); i++) {
			goals = ((IGoalsGrabber)goalsGrabbers.get(i)).getGoals(plugin);
			if ( goals != null ) {
				return goals;
			}
		}
		return goals;
    }

    public String[] getPlugins() {
        String[] plugins = new String[0];
		for (int i = 0; i < goalsGrabbers.size(); i++) {
			String[] currentPlugins = ((IGoalsGrabber)goalsGrabbers.get(i)).getPlugins();

			String[] tmpArray = new String[plugins.length];
			System.arraycopy(plugins, 0, tmpArray, 0, plugins.length);
			
			plugins = new String[plugins.length + currentPlugins.length];
			System.arraycopy(currentPlugins, 0, plugins, 0, currentPlugins.length);
			System.arraycopy(tmpArray, 0, plugins, currentPlugins.length, tmpArray.length);
		}
		return plugins;
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        String[] prereqs = null;
        for (int i = 0; i < goalsGrabbers.size(); i++) {
			prereqs = ((IGoalsGrabber)goalsGrabbers.get(i)).getPrereqs(fullyQualifiedGoalName);
            if ( prereqs != null ) {
				return prereqs;
			} 
        }
        return prereqs;
    }

    public List getGoalsGrabbers() {
        return goalsGrabbers;
    }

    public void setGoalsGrabbers(List grabbers) {
        this.goalsGrabbers = grabbers;
    }
    
}
