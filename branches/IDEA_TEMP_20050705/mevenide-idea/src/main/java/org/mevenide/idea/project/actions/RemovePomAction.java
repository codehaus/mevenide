package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RemovePomAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddPomAction.class);

    public RemovePomAction() {
        super(RES.get("remove.pom.action.name"),
              RES.get("remove.pom.action.desc"),
              Icons.REMOVE_DEPENDENCY);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final VirtualFile file = getVirtualFile(pEvent);
        final PomManager pomMgr = PomManager.getInstance(project);
        pEvent.getPresentation().setEnabled(file != null && pomMgr.contains(file));
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final VirtualFile file = getVirtualFile(pEvent);
        if (file == null)
            return;

        final PomManager pomMgr = PomManager.getInstance(project);
        if (!pomMgr.contains(file))
            return;

        pomMgr.remove(file);
    }
}
