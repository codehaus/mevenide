package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class TeamCRUDTablePanel extends CRUDTablePanel {

    public TeamCRUDTablePanel(final XmlFile pFile,
                              final String pContainerTagName,
                              final String pRowTagName) {
        super(pFile,
              new TeamTableModel(pFile, pContainerTagName, pRowTagName));
        component.setColumnModel(new TeamTableColumnModel(project));
        component.setAutoCreateColumnsFromModel(false);
    }
}
