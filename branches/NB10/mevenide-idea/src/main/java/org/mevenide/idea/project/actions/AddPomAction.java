package org.mevenide.idea.project.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * Registers a new POM file.
 *
 * <p>This action inspects the place at which the action was invoked, and based on it discovers the
 * file to add. If the place is not a predefined supported place, the user is asked to browse for
 * the POM file.</p>
 *
 * @author Arik
 */
public class AddPomAction extends AbstractPomAnAction {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(AddPomAction.class);

    public AddPomAction() {
        super(RES.get("add.pom.action.name"),
              RES.get("add.pom.action.desc"),
              Icons.ADD_DEPENDENCY);
    }

    public void actionPerformed(final AnActionEvent pEvent) {
        final Project project = getProject(pEvent);
        if (project == null) {
            pEvent.getPresentation().setEnabled(false);
            return;
        }

        final PomManager pomMgr = PomManager.getInstance(project);
        final String url = getSelectedPomUrl(pEvent);
        if (pomMgr.contains(url))
            return;

        pomMgr.add(url);
    }

}
