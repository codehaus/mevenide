package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.VirtualFile;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * Registers a new POM file.
 *
 * <p>This action inspects the place at which the action was invoked, and based
 * on it discovers the file to add. If the place is not a predefined supported
 * place, the user is asked to browse for the POM file.</p>
 *
 * @author Arik
 */
public class AddPomAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddPomAction.class);

    public AddPomAction() {
        super(RES.get("add.pom.action.name"),
              RES.get("add.pom.action.desc"),
              Icons.ADD_DEPENDENCY);
    }

    @Override
    public void update(final AnActionEvent pEvent) {
        final VirtualFile file = getVirtualFile(pEvent);
        final PomManager pomMgr = PomManager.getInstance(getProject(pEvent));
        pEvent.getPresentation().setEnabled(file != null && !pomMgr.isPomRegistered(file));
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final VirtualFile file = getVirtualFile(pEvent);
        if(file == null)
            return;

        final PomManager pomMgr = PomManager.getInstance(getProject(pEvent));
        if(pomMgr.isPomRegistered(file))
            return;

        pomMgr.registerPom(file);
    }

}
