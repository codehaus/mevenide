package org.mevenide.idea.editor.pom.ui.layer.resources;

import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;

/**
 * @author Arik
 */
public class ResourcesPanel extends CRUDTablePanel {

    public ResourcesPanel(final Project pProject,
                          final Document pDocument,
                          final String pResourceContainerTagName) {
        super(pProject,
              pDocument,
              new ResourcesTableModel(pProject,
                                      pDocument,
                                      pResourceContainerTagName));
        component.setColumnModel(new ResourcesTableColumnModel());
        component.setAutoCreateColumnsFromModel(false);
    }
}
