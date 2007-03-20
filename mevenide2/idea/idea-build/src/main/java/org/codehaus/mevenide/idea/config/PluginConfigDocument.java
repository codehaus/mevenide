package org.codehaus.mevenide.idea.config;

import java.util.List;

public interface PluginConfigDocument {
    public interface PluginConfig {
        Maven getMaven();

        interface Maven {

            Goals getGoals();

            interface Goals {

                Standard getStandard();

                interface Standard {
                    List<GoalDocument.Goal> getGoalList();
                }
            }
        }
    }
}
