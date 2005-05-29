package org.mevenide.idea.editor.pom.ui.layer.team;

import com.intellij.openapi.project.Project;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.table.StringListTableCellEditor;
import org.mevenide.idea.util.ui.table.StringListTableCellRenderer;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * @author Arik
 */
public class TeamTableColumnModel extends DefaultTableColumnModel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(TeamTableColumnModel.class);

    public static final String[] COLUMN_TITLES = new String[] {
        RES.get("name.col.title"),
        RES.get("id.col.title"),
        RES.get("email.col.title"),
        RES.get("org.col.title"),
        RES.get("url.col.title"),
        RES.get("timezone.col.title"),
        RES.get("roles.col.title")
    };

    /**
     * The renderer used to render pattern list columns.
     */
    private final TableCellRenderer cellRenderer = new StringListTableCellRenderer();

    /**
     * The editor for modifying the column by the user.
     */
    private final TableCellEditor cellEditor;

    /**
     * Creates a new instance.
     */
    public TeamTableColumnModel(final Project pProject) {
        cellEditor = new StringListTableCellEditor(pProject,
                                                   RES.get("roles.dialog.title"),
                                                   RES.get("roles.field.title"));

        addColumn(createStandardColumn(0, "name"));
        addColumn(createStandardColumn(1, "id"));
        addColumn(createStandardColumn(2, "email"));
        addColumn(createStandardColumn(3, "org"));
        addColumn(createStandardColumn(4, "url"));
        addColumn(createStandardColumn(5, "timezone"));
        addColumn(createRolesColumn(6, "roles"));
    }

    /**
     * Creates the directory column.
     *
     * @return table column
     */
    private TableColumn createStandardColumn(final int pModelIndex,
                                             final String pIdentifier) {
        final TableColumn directoryColumn = new TableColumn(pModelIndex, 100);
        directoryColumn.setHeaderValue(COLUMN_TITLES[pModelIndex]);
        directoryColumn.setIdentifier(pIdentifier);
        return directoryColumn;
    }

    /**
     * Creates a roles list column using the given model index, title and identifier.
     *
     * <p>The new column will have custom cell renderer and editor.</p>
     *
     * @return table column
     */
    private TableColumn createRolesColumn(final int pModelIndex,
                                          final String pIdentifier) {
        final TableColumn column = new TableColumn(pModelIndex,
                                                   100,
                                                   cellRenderer,
                                                   cellEditor);
        column.setHeaderValue(COLUMN_TITLES[pModelIndex]);
        column.setIdentifier(pIdentifier);
        return column;
    }
}