package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemovePomAction extends AbstractPomAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddPomAction.class);

    public RemovePomAction() {
        super(RES.get("remove.pom.action.name"),
              RES.get("remove.pom.action.desc"),
              Icons.REMOVE_DEPENDENCY);
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null)
            return;

        final PomManager pomMgr = PomManager.getInstance(project);
        final String url = getSelectedPomUrl(pEvent);
        if (!pomMgr.contains(url))
            return;

        pomMgr.remove(url);
    }
}
