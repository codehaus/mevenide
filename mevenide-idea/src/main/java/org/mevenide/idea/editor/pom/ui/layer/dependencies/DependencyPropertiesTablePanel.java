package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.Table;

import javax.swing.JTable;

/**
 * @author Arik
 */
public class DependencyPropertiesTablePanel extends AbstractDocumentCRUDPanel<JTable> {

    /**
     * Creates a new dependencies panel for the given project and document.
     *
     * @param pProject the project we belong to
     * @param pEditorDocument the document serving as the backing model
     */
    public DependencyPropertiesTablePanel(final Project pProject,
                                          final Document pEditorDocument) {
        super(new Table(), true, false, true, true, pProject, pEditorDocument);
    }
}
