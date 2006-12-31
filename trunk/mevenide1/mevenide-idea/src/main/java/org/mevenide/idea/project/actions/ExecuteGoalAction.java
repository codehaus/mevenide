package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.goals.Goal;
import org.mevenide.idea.project.goals.PomPluginGoalsManager;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.project.util.PomUtils;
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

    private static final String DLG_TITLE = RES.get("exec.goal.dlg.title");
    private static final String DLG_LABEL = RES.get("exec.goal.dlg.label");

    public ExecuteGoalAction() {
        this(RES.get("exec.goal.action.name"), RES.get("exec.goal.action.desc"));
    }

    protected ExecuteGoalAction(final String pText) {
        this(pText, null);
    }

    protected ExecuteGoalAction(final String pText, final String pDescription) {
        this(pText, pDescription, Icons.EXECUTE);
    }

    protected ExecuteGoalAction(final String pText, final String pDescription, final Icon pIcon) {
        super(pText, pDescription, pIcon);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final PomManager pomMgr = PomManager.getInstance(project);
        if (PomManagerPanel.PLACE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null) {
                pEvent.getPresentation().setEnabled(false);
                return;
            }

            final Goal[] goals = ui.getSelectedGoals();
            pEvent.getPresentation().setEnabled(
                    goals != null && goals.length > 0);
        }
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Goal[] goals = getGoals(pEvent);
        final VirtualFile file = getPomFile(pEvent);
        final Project project = getProject(pEvent);
        if (project != null && goals != null && goals.length > 0 && file != null && file.isValid() && !file.isDirectory())
            PomPluginGoalsManager.getInstance(project).execute(file, goals);
    }

    protected VirtualFile getPomFile(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            return null;

        final PomManager pomMgr = PomManager.getInstance(getProject(pEvent));

        final String[] poms;
        if (PomManagerPanel.PLACE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui != null)
                poms = ui.getPomsWithSelectedGoals(false);
            else
                poms = new String[0];
        }
        else
            poms = pomMgr.getFileUrls();

        final String pomUrl = PomUtils.selectPom(project, poms, DLG_TITLE, DLG_LABEL);
        return pomMgr.getVirtualFile(pomUrl);
    }

    protected Goal[] getGoals(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            return new Goal[0];

        if (PomManagerPanel.PLACE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = PomManager.getInstance(project).getToolWindowComponent();
            if (ui != null) {
                final Goal[] goals = ui.getSelectedGoals();
                if (goals != null)
                    return goals;
            }
        }

        return new Goal[0];
    }
}
