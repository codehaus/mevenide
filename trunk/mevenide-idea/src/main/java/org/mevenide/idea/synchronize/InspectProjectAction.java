package org.mevenide.idea.synchronize;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import org.mevenide.idea.synchronize.ui.SynchronizationResultsPanel;
import org.mevenide.idea.util.actions.AbstractAnAction;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class InspectProjectAction extends AbstractAnAction {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(InspectProjectAction.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(InspectProjectAction.class);
    public static final String ID = "org.mevenide.idea.SyncProject";

    public InspectProjectAction() {
        super(RES.get("inspect.project.title"),
              RES.get("inspect.project.desc"),
              Icons.SYNC);
    }

    public void actionPerformed(AnActionEvent e) {
        final Project project = getProject(e);
        final ToolWindow tw = SynchronizationResultsPanel.getInstance(project);
        if(tw == null) {
            UIUtils.showError(project, "Could not find synchronization tool window.");
            return;
        }

        tw.setAvailable(true, null);
        tw.show(new Runnable() {
            public void run() {
                final SynchronizationResultsPanel ui;
                ui = (SynchronizationResultsPanel) tw.getComponent();

                final InspectionsManager mgr = InspectionsManager.getInstance(project);
                final ProblemInfo[] problems = mgr.inspect();
                ui.setProblems(problems);
            }
        });
    }
}
