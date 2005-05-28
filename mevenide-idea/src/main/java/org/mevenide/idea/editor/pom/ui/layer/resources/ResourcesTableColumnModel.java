package org.mevenide.idea.editor.pom.ui.layer.resources;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 * Represents the resources table's column model. This model determines the appropriate renderers
 * and editors to use for each column, as well as the column titles, etc.
 *
 * @author Arik
 */
public class ResourcesTableColumnModel extends DefaultTableColumnModel {
    /**
     * The renderer used to render pattern list columns.
     */
    private final TableCellRenderer cellRenderer = new ResourcePatternsTableCellRenderer(new int[]{2, 3});

    /**
     * The editor for modifying the column by the user.
     */
    private final TableCellEditor cellEditor = new ResourcePatternsTableCellEditor();

    /**
     * Creates a new instance.
     */
    public ResourcesTableColumnModel() {
        addColumn(createDirectoryColumn());
        addColumn(createTargetPathColumn());
        addColumn(createPatternsColumn(2, "Includes", "includes"));
        addColumn(createPatternsColumn(3, "Excludes", "excludes"));
    }

    /**
     * Creates the directory column.
     *
     * @return table column
     */
    private TableColumn createDirectoryColumn() {
        final TableColumn directoryColumn = new TableColumn(0, 100);
        directoryColumn.setHeaderValue("Directory");
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
        targetPathColumn.setHeaderValue("Target Path");
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