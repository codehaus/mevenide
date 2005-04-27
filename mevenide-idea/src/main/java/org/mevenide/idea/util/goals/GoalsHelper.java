package org.mevenide.idea.util.goals;

import org.mevenide.idea.Res;

/**
 * @author Arik
 */
public abstract class GoalsHelper {
    public static final String DEFAULT_GOAL_NAME = "(Default)";

    private static final Res RES = Res.getInstance(GoalsHelper.class);

    public static String buildFullyQualifiedName(final String pPluginName,
                                                 final String pGoalName) {
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
}
