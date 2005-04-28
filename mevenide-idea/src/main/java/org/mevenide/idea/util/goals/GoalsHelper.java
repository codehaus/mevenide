package org.mevenide.idea.util.goals;

import org.mevenide.idea.Res;
import org.mevenide.goals.grabber.IGoalsGrabber;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

/**
 * @author Arik
 */
public abstract class GoalsHelper {
    public static final String DEFAULT_GOAL_NAME = "(Default)";

    private static final Res RES = Res.getInstance(GoalsHelper.class);

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
/*
    public static String getPluginName(final String pFullyQualifiedGoalName) {
        if(pFullyQualifiedGoalName == null || pFullyQualifiedGoalName.trim().length() == 0)
            throw new IllegalArgumentException(RES.get("empty.arg",
                                                       new Object[]{"fullyQualifiedGoalName"}));


    }
*/
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
