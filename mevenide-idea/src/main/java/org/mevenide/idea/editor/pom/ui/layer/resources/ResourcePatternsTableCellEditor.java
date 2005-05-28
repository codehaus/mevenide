package org.mevenide.idea.editor.pom.ui.layer.resources;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.apache.commons.lang.StringUtils;
import org.mevenide.idea.Res;

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
 * field and a small browse button. The button opens a dialog with the {@link ResourcePatternsPanel}
 * used for editing the patterns.
 *
 * @author Arik
 */
public class ResourcePatternsTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(ResourcePatternsTableCellEditor.class);

    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The current value of the editor.
     */
    private String[] value = EMPTY_ARRAY;

    /**
     * The field for editing the patterns.
     */
    private final TextFieldWithBrowseButton field = new TextFieldWithBrowseButton(this);

    /**
     * The table this editor belongs to.
     */
    private JTable table;

    /**
     * Creates an instance.
     */
    public ResourcePatternsTableCellEditor() {
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

        table = pTable;
        value = pValue == null ? EMPTY_ARRAY : (String[]) pValue;
        field.setText(StringUtils.join(value, ", "));

        return field;
    }

    public Object getCellEditorValue() {
        return value;
    }

    public void actionPerformed(final ActionEvent pEvent) {
        final ResourcePatternsPanel patternsPanel = new ResourcePatternsPanel(value);

        final DialogBuilder builder = new DialogBuilder(table);
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(patternsPanel);
        builder.setTitle(RES.get("pattern.dialog.title"));

        final int exitCode = builder.show();
        if(exitCode == DialogWrapper.OK_EXIT_CODE) {
            value = patternsPanel.getPatterns();
            field.setText(StringUtils.join(value, ", "));
        }
    }
}