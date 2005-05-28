package org.mevenide.idea.util.ui.table;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;

/**
 * Renders the patterns list cells by concatenating the patterns array into
 * a string seperated by commas.
 *
 * @author Arik
 */
public class PatternsTableCellRenderer extends DefaultTableCellRenderer {

    /**
     * An empty array, to save instantiations when needed.
     */
    private static final String[] EMPTY_ARRAY = new String[0];

    /**
     * The indexes of columns that should be treated as pattern lists.
     */
    private final int[] patternsColumnIndexes;

    /**
     * Creates an instance using the given column indexes as the columns
     * that will be treated as pattern list columns. Other columns will
     * be rendered normally.
     *
     * @param pPatternsColumnIndexes the pattern lists column indexes
     */
    public PatternsTableCellRenderer(final int[] pPatternsColumnIndexes) {
        patternsColumnIndexes = pPatternsColumnIndexes;
    }

    @Override public Component getTableCellRendererComponent(
            final JTable pTable,
            final Object pValue,
            final boolean pSelected,
            final boolean pHasFocus,
            final int pRow,
            final int pColumn) {

        if (!ArrayUtils.contains(patternsColumnIndexes, pColumn))
            return super.getTableCellRendererComponent(pTable,
                                                       pValue,
                                                       pSelected,
                                                       pHasFocus,
                                                       pRow,
                                                       pColumn);

        final String[] patterns =
                pValue == null ? EMPTY_ARRAY : (String[]) pValue;

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