package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.mevenide.idea.Res;
import org.mevenide.idea.toolwindows.repository.RepoToolWindow;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RefreshRepoAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RefreshRepoAction.class);

    public RefreshRepoAction() {
        super(RES.get("refresh.tree.action.text"),
              RES.get("refresh.tree.action.desc"),
              Icons.SYNC);
    }

    public boolean displayTextInToolbar() {
        return true;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final RepoToolWindow tw = RepoToolWindow.getInstance(getProject(pEvent));
        tw.refreshSelectedRepo();
    }

}
