package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RefreshPomToolWindowAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RefreshPomToolWindowAction.class);

    public RefreshPomToolWindowAction() {
        super(RES.get("refresh.pom.win.action.name"),
              RES.get("refresh.pom.win.action.desc"),
              Icons.SYNC);
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final PomManager pomMgr = PomManager.getInstance(project);
        final PomManagerPanel ui = pomMgr.getToolWindowComponent();
        if (ui != null)
            ui.refresh();
    }
}
