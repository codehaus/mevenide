package org.mevenide.idea.toolwindows.repository;

import com.intellij.openapi.actionSystem.AnActionEvent;
import java.awt.*;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RefreshRepoTreeAction extends AbstractAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(RefreshRepoTreeAction.class);

    public RefreshRepoTreeAction() {
        super(RES.get("refresh.tree.action.text"),
              RES.get("refresh.tree.action.desc"),
              Icons.SYNC);
    }

    public boolean displayTextInToolbar() {
        return true;
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Component source = (Component) pEvent.getInputEvent().getSource();
        final Container anscestor = SwingUtilities.getAncestorNamed(RepositoryToolWindow.NAME,
                                                                    source);
        if (!(anscestor instanceof RepositoryToolWindow))
            return;

        final RepositoryToolWindow toolWin = (RepositoryToolWindow) anscestor;
        toolWin.refreshModel();
    }
}
