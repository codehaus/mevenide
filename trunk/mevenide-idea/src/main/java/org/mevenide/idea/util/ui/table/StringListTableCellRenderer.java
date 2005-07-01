package org.mevenide.idea.util.ui.table;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.commons.lang.StringUtils;

/**
 * Renders the strings list cells by concatenating the strings array into a string
 * seperated by commas.
 *
 * @author Arik
 */
public class StringListTableCellRenderer extends DefaultTableCellRenderer {
    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    @Override
    public Component getTableCellRendererComponent(
        final JTable pTable,
        final Object pValue,
        final boolean pSelected,
        final boolean pHasFocus,
        final int pRow,
        final int pColumn) {

        final String[] patterns = pValue == null ? EMPTY_ARRAY : (String[]) pValue;
        final String text = StringUtils.join(patterns, ", ");
        final Component comp = super.getTableCellRendererComponent(pTable,
                                                                   text,
                                                                   pSelected,
                                                                   pHasFocus,
                                                                   pRow,
                                                                   pColumn);
        if (comp instanceof JComponent) {
            final JComponent jc = (JComponent) comp;
            jc.setToolTipText(StringUtils.join(patterns, "\n"));
        }

        return comp;
    }
}