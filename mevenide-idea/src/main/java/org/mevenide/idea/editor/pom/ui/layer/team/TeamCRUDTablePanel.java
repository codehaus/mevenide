package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class TeamCRUDTablePanel extends CRUDTablePanel {

    public TeamCRUDTablePanel(final Project pProject,
                              final Document pDocument,
                              final String pContainerTagName,
                              final String pRowTagName) {
        super(pProject,
              pDocument,
              new TeamTableModel(pProject,
                                 pDocument,
                                 pContainerTagName,
                                 pRowTagName));
        component.setColumnModel(new TeamTableColumnModel(project));
        component.setAutoCreateColumnsFromModel(false);
    }
}
