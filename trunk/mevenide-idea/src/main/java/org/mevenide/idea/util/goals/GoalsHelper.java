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
package org.mevenide.idea.util.goals;

import org.mevenide.goals.grabber.IGoalsGrabber;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arik
 */
public abstract class GoalsHelper {
    public static final String DEFAULT_GOAL_NAME = "(default)";

    public static String buildFullyQualifiedName(final String pPluginName,
                                                 final String pGoalName) {
        if(pPluginName == null || pPluginName.trim().length() == 0)
            return pGoalName;

        final StringBuffer sb = new StringBuffer(pPluginName);
        if(pGoalName != null && pGoalName.trim().length() > 0 && !pGoalName.equalsIgnoreCase("(Default)")) {
            sb.append(':');
            sb.append(pGoalName);
        }

        return sb.toString();
    }

    public static String getPluginName(final String pFullyQualifiedGoalName) {
        final int colonIndex = pFullyQualifiedGoalName.indexOf(':');
        if (colonIndex < 0)
            return pFullyQualifiedGoalName;
        else
            return pFullyQualifiedGoalName.substring(0, colonIndex);
    }

    public static String getGoalSimpleName(final String pFullyQualifiedGoalName) {
        final int colonIndex = pFullyQualifiedGoalName.indexOf(':');
        if(colonIndex < 0)
            return DEFAULT_GOAL_NAME;
        else
            return pFullyQualifiedGoalName.substring(colonIndex + 1);
    }

    public static Map asMap(final IGoalsGrabber pGrabber) {
        final String[] plugins = pGrabber.getPlugins();
        if(plugins == null || plugins.length == 0)
            return new HashMap();

        final Map<String, Set> pluginsMap = new HashMap<String, Set>(plugins.length);
        for(final String plugin : plugins) {
            final String[] goals = pGrabber.getGoals(plugin);
            final Set<String> goalsSet;
            if(goals == null)
                goalsSet = new HashSet<String>();
            else
                goalsSet = new HashSet<String>(goals.length);

            pluginsMap.put(plugin, goalsSet);

            for(final String goal : goals)
                goalsSet.add(goal);
        }

        return pluginsMap;
    }
}
