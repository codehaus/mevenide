package org.mevenide.idea.editor.pom.ui.layer.model;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;

/**
 * @author Arik
 * @todo add support to roles
 */
public class DevelopersTableModel extends SimpleTagBasedXmlPsiTableModel {
    /**
     * The column titles.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "Name",
        "ID",
        "E-Mail",
        "Organization",
        "URL",
        "Timezone"
    };

    /**
     * The JavaBeans property names respective of each column index.
     */
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "name",
        "id",
        "email",
        "organization",
        "url",
        "timezone"
    };

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public DevelopersTableModel(final Project pProject,
                                final Document pIdeaDocument) {
        this(pProject, pIdeaDocument, "developers", "developer");
    }

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject          the project.
     * @param pIdeaDocument     the document.
     * @param pContainerTagName the container tag name
     * @param pRowTagName       the row tag name
     */
    public DevelopersTableModel(final Project pProject,
                                final Document pIdeaDocument,
                                final String pContainerTagName,
                                final String pRowTagName) {
        super(pProject,
              pIdeaDocument,
              pContainerTagName,
              pRowTagName,
              COLUMN_TITLES,
              VALUE_TAG_NAMES);
    }
}
