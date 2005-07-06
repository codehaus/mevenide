package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.execute.MavenExecuteManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class ExecuteGoalAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ExecuteGoalAction.class);

    public ExecuteGoalAction() {
        super(RES.get("exec.goal.action.name"),
              RES.get("exec.goal.action.desc"),
              Icons.EXECUTE);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        pEvent.getPresentation().setEnabled(false);

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

            final VirtualFile[] projects = ui.getSelectedProjects(false);
            if (projects.length == 1) {
                final GoalInfo[] goals = ui.getSelectedGoals(projects[0]);
                pEvent.getPresentation().setEnabled(goals.length > 0);
            }
        }
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final PomManager pomMgr = PomManager.getInstance(project);

        if (PomManagerPanel.TITLE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null)
                return;

            final VirtualFile[] projects = ui.getSelectedProjects(false);
            if (projects.length != 1)
                return;

            final VirtualFile pomFile = projects[0];
            final GoalInfo[] goals = ui.getSelectedGoals(pomFile);
            if (goals == null || goals.length == 0)
                return;

            MavenExecuteManager.getInstance(project).execute(pomFile, goals);
        }
    }
}
