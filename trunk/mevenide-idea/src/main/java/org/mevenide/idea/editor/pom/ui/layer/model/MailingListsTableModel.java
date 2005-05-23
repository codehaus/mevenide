package org.mevenide.idea.editor.pom.ui.layer.model;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;

/**
 * @author Arik
 */
public class MailingListsTableModel extends SimpleTagBasedXmlPsiTableModel {
    /**
     * The column titles.
     */
    private static final String[] COLUMN_TITLES = new String[]{
        "Name",
        "Subscribe",
        "Unsubscribe",
        "Archive"
    };

    /**
     * The JavaBeans property names respective of each column index.
     */
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "name",
        "subscribe",
        "unsubscribe",
        "archive"
    };

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public MailingListsTableModel(final Project pProject,
                                  final Document pIdeaDocument) {
        super(pProject,
              pIdeaDocument,
              "mailingLists",
              "mailingList",
              COLUMN_TITLES,
              VALUE_TAG_NAMES);
    }
}
