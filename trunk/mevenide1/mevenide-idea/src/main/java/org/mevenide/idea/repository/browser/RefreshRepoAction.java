package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RefreshRepoAction extends AbstractBrowserAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RefreshRepoAction.class);

    public RefreshRepoAction(final RepositoryBrowser pBrowser) {
        super(pBrowser,
              RES.get("refresh.tree.action.text"),
              RES.get("refresh.tree.action.desc"),
              Icons.SYNC);
    }

    public boolean displayTextInToolbar() {
        return true;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        browser.refreshSelectedRepo();
    }

}
