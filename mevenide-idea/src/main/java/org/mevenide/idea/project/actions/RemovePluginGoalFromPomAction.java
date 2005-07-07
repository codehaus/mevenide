package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PomPluginGoalsManager;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemovePluginGoalFromPomAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RemovePluginGoalFromPomAction.class);

    public RemovePluginGoalFromPomAction() {
        super(RES.get("remove.goal.action.name"),
              RES.get("remove.goal.action.desc"),
              Icons.REMOVE_DEPENDENCY);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final PomManager pomMgr = PomManager.getInstance(project);
        if (PomManagerPanel.TITLE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null) {
                pEvent.getPresentation().setEnabled(false);
                return;
            }

            final String[] projects = ui.getPomsWithSelectedGoals(false);
            pEvent.getPresentation().setEnabled(projects.length > 0);
        }
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            return;

        final PomManager pomMgr = PomManager.getInstance(project);
        if (PomManagerPanel.TITLE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null)
                return;

            final PomPluginGoalsManager plgMgr = PomPluginGoalsManager.getInstance(project);

            final String[] projects = ui.getPomsWithSelectedGoals(false);
            for (String url : projects) {
                final Goal[] goals = ui.getSelectedGoalsForPom(url);
                for (Goal goal : goals)
                    if (goal instanceof PluginGoal)
                        plgMgr.removePluginGoal(url, (PluginGoal) goal);
            }

        }
    }
}
