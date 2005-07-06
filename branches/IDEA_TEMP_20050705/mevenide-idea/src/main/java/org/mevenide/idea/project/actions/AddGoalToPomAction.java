package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SelectFromListDialog;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.model.GoalInfo;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class AddGoalToPomAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddGoalToPomAction.class);

    public AddGoalToPomAction() {
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

        final VirtualFilePointer[] pointers = pomMgr.getPomPointers();
        if (pointers.length == 0)
            return;

        if (PomManagerPanel.TITLE.equals(pEvent.getPlace())) {
            final PomManagerPanel ui = pomMgr.getToolWindowComponent();
            if (ui == null) {
                pEvent.getPresentation().setEnabled(false);
                return;
            }

            final VirtualFile[] projects = ui.getSelectedProjects(false);
            final GoalInfo[] goals = ui.getSelectedGoals(null);
            pEvent.getPresentation().setEnabled(
                    goals.length > 0 && projects.length == 0);
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
            if (ui == null) {
                pEvent.getPresentation().setEnabled(false);
                return;
            }

            final VirtualFile[] projects = ui.getSelectedProjects(false);
            if (projects.length > 0)
                return;

            final GoalInfo[] goals = ui.getSelectedGoals(null);
            if (goals.length == 0)
                return;

            final String url;
            if (pointers.length == 1)
                url = pointers[0].getUrl();
            else {
                final SelectFromListDialog dlg = new SelectFromListDialog(
                        getProject(pEvent),
                        pointers,
                        new SelectFromListDialog.ToStringAspect() {
                            public String getToStirng(Object obj) {
                                final VirtualFilePointer p = (VirtualFilePointer) obj;
                                return p.getPresentableUrl();
                            }
                        },
                        "Select POM to add goals to",
                        ListSelectionModel.SINGLE_SELECTION);
                dlg.setModal(true);
                dlg.setResizable(true);
                dlg.show();

                if (!dlg.isOK())
                    return;

                final VirtualFilePointer pointer = (VirtualFilePointer) dlg.getSelection()[0];
                url = pointer.getUrl();
            }

            for (GoalInfo goal : goals)
                pomMgr.addGoal(url, goal);
        }
    }
}
