package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.util.ui.StringListEditPanel;

/**
 * A table cell editor for modifying a string list. This editor displays a non-editable
 * text field and a small browse button. The button opens a dialog with the {@link
 * org.mevenide.idea.util.ui.StringListEditPanel} used for editing the strings.
 *
 * @author Arik
 */
public class StringListTableCellEditor extends AbstractCellEditor
    implements TableCellEditor, ActionListener {
    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The project context.
     */
    private final Project project;

    /**
     * The current value of the editor.
     */
    private String[] value = EMPTY_ARRAY;

    /**
     * The title of the dialog that will be presented to the user for editing the list.
     */
    private final String dialogTitle;

    /**
     * The label for the text field where the user will enter new items (in the dialog).
     */
    private final String dialogItemLabel;

    /**
     * The field for editing the strings.
     */
    private final TextFieldWithBrowseButton field = new TextFieldWithBrowseButton(this);

    /**
     * Creates an instance.
     */
    public StringListTableCellEditor(final Project pProject,
                                     final String pDialogTitle,
                                     final String pDialogItemLabel) {
        project = pProject;
        dialogTitle = pDialogTitle;
        dialogItemLabel = pDialogItemLabel;
        field.setBorder(null);
        field.setEditable(false);
        field.setOpaque(false);
        field.getTextField().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        field.getTextField().setOpaque(false);
    }

    public Component getTableCellEditorComponent(final JTable pTable,
                                                 final Object pValue,
                                                 final boolean pSelected,
                                                 final int pRow,
                                                 final int pColumn) {

        value = pValue == null ? EMPTY_ARRAY : (String[]) pValue;
        field.setText(StringUtils.join(value, ", "));

        return field;
    }

    public Object getCellEditorValue() {
        return value;
    }

    public void actionPerformed(final ActionEvent pEvent) {
        final String[] items = StringListEditPanel.showDialog(
            project, dialogTitle, dialogItemLabel, value);

        if (items != null) {
            value = items;
            field.setText(StringUtils.join(value, ", "));
        }
    }
}