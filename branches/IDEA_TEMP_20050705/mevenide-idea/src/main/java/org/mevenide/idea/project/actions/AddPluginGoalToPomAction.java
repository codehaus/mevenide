package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.PluginGoal;
import org.mevenide.idea.project.goals.PomPluginGoalsManager;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.project.util.PomUtils;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class AddPluginGoalToPomAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddPluginGoalToPomAction.class);

    private static final String DLG_TITLE = RES.get("add.goal.dlg.title");
    private static final String DLG_LABEL = RES.get("add.goal.dlg.label");

    public AddPluginGoalToPomAction() {
        super(RES.get("add.goal.action.name"),
              RES.get("add.goal.action.desc"),
              Icons.ADD_DEPENDENCY);
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
            if (projects.length > 0) {
                pEvent.getPresentation().setEnabled(false);
                return;
            }

            final Goal[] goals = ui.getSelectedGoalsForPom(null);
            pEvent.getPresentation().setEnabled(goals.length > 0);
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

            final String[] projects = ui.getPomsWithSelectedGoals(false);
            final Goal[] goals = ui.getSelectedGoalsForPom(null);
            if (projects.length > 0 || goals.length == 0)
                return;

            final String url = PomUtils.selectPom(project, DLG_TITLE, DLG_LABEL);
            if (url == null || url.trim().length() == 0)
                return;

            final PomPluginGoalsManager plgMgr = PomPluginGoalsManager.getInstance(project);
            for (Goal goal : goals)
                if (goal instanceof PluginGoal)
                    plgMgr.addPluginGoal(url, ((PluginGoal) goal));
        }
    }
}
