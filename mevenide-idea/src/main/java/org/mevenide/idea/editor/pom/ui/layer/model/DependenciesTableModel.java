package org.mevenide.idea.editor.pom.ui.layer.model;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;

/**
 * @author Arik
 */
public class DependenciesTableModel extends SimpleTagBasedXmlPsiTableModel {
    /**
     * The column titles.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "Group ID",
        "Artifact ID",
        "Version",
        "Type"
    };

    /**
     * The JavaBeans property names respective of each column index.
     */
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "groupId",
        "artifactId",
        "version",
        "type"
    };

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public DependenciesTableModel(final Project pProject,
                                  final Document pIdeaDocument) {
        super(pProject,
              pIdeaDocument,
              "dependencies",
              "dependency",
              COLUMN_TITLES,
              VALUE_TAG_NAMES);
    }
}
