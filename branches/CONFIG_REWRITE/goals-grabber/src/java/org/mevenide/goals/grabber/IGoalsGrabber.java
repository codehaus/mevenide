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


/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: IGoalsGrabber.java 3 mai 2003 22:20:0113:34:35 Exp gdodinet 
 * 
 */
public interface IGoalsGrabber {
	/**
	 * return all available plugins 
	 *  
	 * @return Collection
	 */
	String[] getPlugins();

	/**
	 * return the goals declared by the plugin whose name is passed as parameter
	 * 
	 * @param plugin   
	 * @return Collection
	 */
	String[] getGoals(String plugin);
	
	/**
	 * return the description of plugin:goal
	 *  
	 * @param plugin
	 * @param goal
	 * 
	 */
	String getDescription(String fullyQualifiedGoalName);
	
	String[] getPrereqs(String fullyQualifiedGoalName);
	
	void refresh() throws Exception;
	
    String ORIGIN_PROJECT = "Project";
    String ORIGIN_PLUGIN = "Plugin";
    
    /**
     * will return where the goal is defined. 
     * @return ORIGIN_PLUGIN or ORIGIN_PROJECT
     */
    String getOrigin(String fullyQualifiedGoalName);
    
    String getName();
}