package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.PatternsPanel;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A table cell editor for modifying resource patterns. This editor displays a non-editable text
 * field and a small browse button. The button opens a dialog with the {@link org.mevenide.idea.util.ui.PatternsPanel}
 * used for editing the patterns.
 *
 * @author Arik
 */
public class PatternsTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(PatternsTableCellEditor.class);

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
     * The field for editing the patterns.
     */
    private final TextFieldWithBrowseButton field = new TextFieldWithBrowseButton(this);

    /**
     * Creates an instance.
     */
    public PatternsTableCellEditor(final Project pProject) {
        project = pProject;
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
        final String[] patterns = PatternsPanel.showDialog(
                project, RES.get("pattern.dialog.title"), value);

        if(patterns != null) {
            value = patterns;
            field.setText(StringUtils.join(value, ", "));
        }
    }
}