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
package org.mevenide.idea.util.goals.grabber;

import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.util.goals.GoalsHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Arik
 */
public class FilteredGoalsGrabber implements IGoalsGrabber {

    private final String name;
    private final IGoalsGrabber delegate;
    private final Set<String> goalFilters;
    private final Set<String> pluginFilters;

    public FilteredGoalsGrabber(final String pName,
                                final IGoalsGrabber pDelegate,
                                final String[] pGoals) {
        delegate = pDelegate;
        name = pName;

        pluginFilters = new HashSet<String>(20);
        if(pGoals == null)
            goalFilters = new HashSet<String>(0);
        else
            goalFilters = new HashSet<String>(pGoals.length);

        setGoalFilters(pGoals);
    }

    public void setGoalFilters(final String[] pGoals) {
        goalFilters.clear();
        pluginFilters.clear();

        if(pGoals == null || pGoals.length == 0)
            return;

        for(String goal : pGoals) {
            goalFilters.add(goal);
            pluginFilters.add(GoalsHelper.getPluginName(goal));
        }
    }

    public String getDescription(String fullyQualifiedGoalName) {
        if(goalFilters.contains(fullyQualifiedGoalName))
            return delegate.getDescription(fullyQualifiedGoalName);
        else
            return null;
    }

    public String[] getGoals(String plugin) {
        final String[] delegateGoalsArr = delegate.getGoals(plugin);
        if(delegateGoalsArr == null)
            return null;

        if (!pluginFilters.contains(plugin))
            return new String[0];

        if(delegateGoalsArr.length == 0)
            return delegateGoalsArr;

        if(goalFilters.size() == 0)
            return new String[0];

        final Set<String> delegateGoals = new HashSet<String>(delegateGoalsArr.length);
        for(String goal : delegateGoalsArr) {
            final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
            if(goalFilters.contains(fqName))
                delegateGoals.add(goal);
        }

        return delegateGoals.toArray(new String[delegateGoals.size()]);
    }

    public String getName() {
        if(name == null || name.trim().length() == 0)
            return delegate.getName();
        else
            return name;
    }

    public String getOrigin(String fullyQualifiedGoalName) {
        if(!goalFilters.contains(fullyQualifiedGoalName))
            return null;

        return delegate.getOrigin(fullyQualifiedGoalName);
    }

    public String[] getPlugins() {
        final String[] delegatePluginsArr = delegate.getPlugins();
        if (delegatePluginsArr == null)
            return null;
        if (delegatePluginsArr.length == 0)
            return delegatePluginsArr;
        if (goalFilters.size() == 0)
            return new String[0];

        final Set<String> delegatePlugins = new HashSet<String>(delegatePluginsArr.length);
        for (String plugin : delegatePluginsArr) {
            if(pluginFilters.contains(plugin))
                delegatePlugins.add(plugin);
        }

        return delegatePlugins.toArray(new String[delegatePlugins.size()]);
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        if (!goalFilters.contains(fullyQualifiedGoalName))
            return new String[0];

        return delegate.getPrereqs(fullyQualifiedGoalName);
    }

    public void refresh() throws Exception {
        delegate.refresh();
    }
}
