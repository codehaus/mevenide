package org.mevenide.idea.editor.pom.ui.layer.resources;

import com.intellij.openapi.project.Project;
import org.mevenide.idea.util.ui.table.PatternsTableCellEditor;
import org.mevenide.idea.util.ui.table.PatternsTableCellRenderer;
import org.mevenide.idea.Res;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Represents the resources table's column model. This model determines the appropriate renderers
 * and editors to use for each column, as well as the column titles, etc.
 *
 * @author Arik
 */
public class ResourcesTableColumnModel extends DefaultTableColumnModel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ResourcesTableColumnModel.class);

    /**
     * The renderer used to render pattern list columns.
     */
    private final TableCellRenderer cellRenderer = new PatternsTableCellRenderer(new int[]{2, 3});

    /**
     * The editor for modifying the column by the user.
     */
    private final TableCellEditor cellEditor;

    /**
     * Creates a new instance.
     */
    public ResourcesTableColumnModel(final Project pProject) {
        cellEditor = new PatternsTableCellEditor(pProject);

        addColumn(createDirectoryColumn());
        addColumn(createTargetPathColumn());
        addColumn(createPatternsColumn(2, RES.get("includes.column.title"), "includes"));
        addColumn(createPatternsColumn(3, RES.get("excludes.column.title"), "excludes"));
    }

    /**
     * Creates the directory column.
     *
     * @return table column
     */
    private TableColumn createDirectoryColumn() {
        final TableColumn directoryColumn = new TableColumn(0, 100);
        directoryColumn.setHeaderValue(RES.get("directory.column.title"));
        directoryColumn.setIdentifier("header");
        return directoryColumn;
    }

    /**
     * Creates the target-path column.
     *
     * @return table column
     */
   private TableColumn createTargetPathColumn() {
        final TableColumn targetPathColumn = new TableColumn(1, 100);
        targetPathColumn.setHeaderValue(RES.get("targetPath.column.title"));
        targetPathColumn.setIdentifier("targetPath");
        return targetPathColumn;
    }

    /**
     * Creates a pattern list column using the given model index, title
     * and identifier.
     *
     * <p>The new column will have custom cell renderer and editor.</p>
     *
     * @return table column
     */
    private TableColumn createPatternsColumn(final int pModelIndex,
                                                    final String pTitle,
                                                    final String pIdentifier) {
        final TableColumn column = new TableColumn(pModelIndex, 100, cellRenderer, cellEditor);
        column.setHeaderValue(pTitle);
        column.setIdentifier(pIdentifier);
        return column;
    }
}