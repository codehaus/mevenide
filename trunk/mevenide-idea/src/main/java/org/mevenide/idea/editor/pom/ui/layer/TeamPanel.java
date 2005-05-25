package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import static org.mevenide.idea.editor.pom.ui.layer.TableModelConstants.CONTRIBUTORS;
import static org.mevenide.idea.editor.pom.ui.layer.TableModelConstants.DEVELOPERS;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

import javax.swing.JPanel;

/**
 * @author Arik
 */
class TeamPanel extends SplitPanel<JPanel, JPanel> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(TeamPanel.class);

    public TeamPanel(final Project pProject, final Document pDocument) {
        super(new LabeledPanel(RES.get("developers.desc"),
                               new CRUDTablePanel(pProject, pDocument, DEVELOPERS)),
              new LabeledPanel(RES.get("contributors.desc"),
                               new CRUDTablePanel(pProject, pDocument, CONTRIBUTORS)));
    }
}
