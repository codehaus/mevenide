package org.mevenide.idea.editor.pom.ui.layer.model;

import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;

/**
 * @author Arik
 */
public class VersionsTableModel extends SimpleTagBasedXmlPsiTableModel {
    /**
     * The column titles.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "ID",
        "Name",
        "Tag"
    };

    /**
     * The JavaBeans property names respective of each column index.
     */
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "id",
        "name",
        "tag"
    };

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public VersionsTableModel(final Project pProject,
                              final Document pIdeaDocument) {
        super(pProject,
              pIdeaDocument,
              "versions",
              "version",
              COLUMN_TITLES,
              VALUE_TAG_NAMES);
    }
}
