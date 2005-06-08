package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.psi.xml.XmlFile;
import javax.swing.JPanel;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.LabeledPanel;
import org.mevenide.idea.util.ui.SplitPanel;

/**
 * @author Arik
 */
public class TeamPanel extends SplitPanel<JPanel, JPanel> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(TeamPanel.class);

    public TeamPanel(final XmlFile pFile) {
        super(
            new LabeledPanel(RES.get("developers.desc"),
                             new TeamCRUDTablePanel(pFile,
                                                    "project/developers",
                                                    "developer")),
            new LabeledPanel(RES.get("contributors.desc"),
                             new TeamCRUDTablePanel(pFile,
                                                    "project/contributors",
                                                    "contributor")));
    }
}
