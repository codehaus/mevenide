package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;

import javax.swing.JPanel;

/**
 * @author Arik
 */
public class TeamPanel extends SplitPanel<JPanel, JPanel> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(TeamPanel.class);

    public TeamPanel(final Project pProject, final Document pDocument) {
        super(new LabeledPanel(RES.get("developers.desc"),
                               new TeamCRUDTablePanel(pProject, pDocument, "developers", "developer")),
              new LabeledPanel(RES.get("contributors.desc"),
                               new TeamCRUDTablePanel(pProject, pDocument, "contributors", "contributor")));
    }
}
