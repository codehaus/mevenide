package org.mevenide.idea.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import org.mevenide.idea.global.MavenPluginsManager;
import org.mevenide.idea.project.actions.ExecuteGoalAction;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.GoalContainer;
import org.mevenide.idea.project.goals.PluginGoalContainer;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * @author Arik Kfir
 */
public class MavenActionsManager extends AbstractProjectComponent {
    private static final PluginId PLUGIN_ID = PluginId.getId("mevenide-idea");

    public MavenActionsManager(final Project pProject) {
        super(pProject);
    }

    public void projectOpened() {
        final Runnable actionRegistrar = new Runnable() {
            public void run() {
                final ActionManager actMgr = ActionManager.getInstance();
                final MavenPluginsManager pluginsMgr = MavenPluginsManager.getInstance(project);
                final PluginGoalContainer[] plugins = pluginsMgr.getPlugins();
                for (GoalContainer plugin : plugins) {
                    final Goal[] goals = plugin.getGoals();
                    for (Goal goal : goals) {
                        final AnAction action = new GoalAction(goal);
                        actMgr.registerAction(goal.getName(), action, PLUGIN_ID);
                    }
                }
            }
        };

        StartupManager.getInstance(project).registerPostStartupActivity(actionRegistrar);
    }

    private class GoalAction extends ExecuteGoalAction {
        private final Goal[] goals;

        public GoalAction(final Goal pGoal) {
            super(RES.get("goal.action.text", pGoal.getName()),
                  RES.get("goal.action.desc", pGoal.getName()));
            goals = new Goal[]{pGoal};
        }

        @Override
        protected Goal[] getGoals(final AnActionEvent pEvent) {
            return goals;
        }
    }
}
