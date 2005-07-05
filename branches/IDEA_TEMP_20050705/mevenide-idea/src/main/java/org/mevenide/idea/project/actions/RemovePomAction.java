package org.mevenide.idea.project.actions;

import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.actions.AddPomAction;
import org.mevenide.idea.project.PomManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;

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
              Icons.ADD_DEPENDENCY);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final VirtualFile file = getVirtualFile(pEvent);
        final PomManager pomMgr = PomManager.getInstance(getProject(pEvent));
        pEvent.getPresentation().setEnabled(file != null && pomMgr.isPomRegistered(file));
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final VirtualFile file = getVirtualFile(pEvent);
        if (file == null)
            return;

        final PomManager pomMgr = PomManager.getInstance(getProject(pEvent));
        if (!pomMgr.isPomRegistered(file))
            return;

        pomMgr.unregisterPom(file);
    }
}
