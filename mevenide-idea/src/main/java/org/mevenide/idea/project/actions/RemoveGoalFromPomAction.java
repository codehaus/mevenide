package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemoveGoalFromPomAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RemoveGoalFromPomAction.class);

    public RemoveGoalFromPomAction() {
        super(RES.get("remove.goal.action.name"),
              RES.get("remove.goal.action.desc"),
              Icons.REMOVE_DEPENDENCY);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        pEvent.getPresentation().setEnabled(false);

        final Project project = getProject(pEvent);
        if (project == null)
            return;

        final PomManager pomMgr = PomManager.getInstance(project);

        final VirtualFilePointer[] pointers = pomMgr.getPomPointers();
        if (pointers.length == 0)
            return;

        if (PomManagerPanel.TITLE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null) {
                return;
            }

            final VirtualFile[] projects = ui.getSelectedProjects(false);
            if (projects.length != 1)
                return;

            final GoalInfo[] goals = ui.getSelectedGoals(projects[0]);
            pEvent.getPresentation().setEnabled(goals.length > 0);
        }
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final PomManager pomMgr = PomManager.getInstance(project);

        final VirtualFilePointer[] pointers = pomMgr.getPomPointers();
        if (pointers.length == 0)
            return;

        if (PomManagerPanel.TITLE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null)
                return;

            final VirtualFile[] projects = ui.getSelectedProjects(false);
            if (projects.length != 1)
                return;

            final GoalInfo[] goals = ui.getSelectedGoals(projects[0]);
            for (GoalInfo goal : goals)
                pomMgr.removeGoal(projects[0], goal);
        }
    }
}
