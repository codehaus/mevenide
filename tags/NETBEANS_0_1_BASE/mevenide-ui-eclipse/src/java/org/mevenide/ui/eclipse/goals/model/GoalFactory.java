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
package org.mevenide.ui.eclipse.goals.model;

import org.apache.commons.lang.StringUtils;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalFactory.java,v 1.1 15 sept. 2003 Exp gdodinet 
 * 
 */
public abstract class GoalFactory {
	public static Goal newGoal(String fullyQualifiedGoalName) {                
		String[] splittedGoal = StringUtils.split(fullyQualifiedGoalName, Goal.SEPARATOR);
		String pluginName = splittedGoal[0];
		Plugin plugin = new Plugin();
		plugin.setName(pluginName);
	
		String goalName = Goal.DEFAULT_GOAL;
		if ( splittedGoal.length > 1 ) {
			goalName = splittedGoal[1];
		}
		Goal goal = new Goal();
		goal.setName(goalName);
		goal.setPlugin(plugin);
		
		return goal; 
	}
}
