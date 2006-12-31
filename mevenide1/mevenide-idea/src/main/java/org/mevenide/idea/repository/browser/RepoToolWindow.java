package org.mevenide.idea.repository.browser;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class RepoToolWindow extends RepositoryBrowser {
    /**
     * The tool window name.
     */
    public static final String NAME = PLACE;

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project
     */
    public RepoToolWindow(final Project pProject) {
        super(pProject);
    }

    @Override
    protected DefaultActionGroup createToolBarActionGroup() {
        final ActionGroup group = super.createToolBarActionGroup();
        final DefaultActionGroup grp;
        if (group instanceof DefaultActionGroup)
            grp = (DefaultActionGroup) group;
        else {
            grp = new DefaultActionGroup();
            final AnAction[] actions = group.getChildren(null);
            for (AnAction action : actions)
                grp.add(action);
        }

        grp.add(new AddAsDependencyAction(this));
        return grp;
    }

    /**
     * Registers the tool window in the given project's window.
     *
     * @param pProject the project
     */
    public static void register(final Project pProject) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(pProject);
        final RepoToolWindow toolWindowComp = new RepoToolWindow(pProject);
        toolMgr.registerToolWindow(NAME, toolWindowComp, ToolWindowAnchor.RIGHT);
        final ToolWindow toolWindow = toolMgr.getToolWindow(NAME);
        toolWindow.setIcon(Icons.REPOSITORY);
    }

    /**
     * Unregisters the tool window for the given project.
     *
     * @param project the project
     */
    public static void unregister(final Project project) {
        final ToolWindowManager toolMgr = ToolWindowManager.getInstance(project);
        toolMgr.unregisterToolWindow(NAME);
    }
}
