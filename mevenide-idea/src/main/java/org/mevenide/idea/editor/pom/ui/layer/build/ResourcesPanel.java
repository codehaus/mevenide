package org.mevenide.idea.editor.pom.ui.layer.build;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class ResourcesPanel extends CRUDTablePanel {

    public ResourcesPanel(final XmlFile pFile,
                          final String pResourceContainerTagName) {
        super(pFile,
              new ResourcesTableModel(pFile, pResourceContainerTagName));
        component.setColumnModel(new ResourcesTableColumnModel(project));
        component.setAutoCreateColumnsFromModel(false);
    }
}
