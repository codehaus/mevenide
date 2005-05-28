package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.idea.util.ui.table.SimpleTagBasedXmlPsiTableModel;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class ResourcesPanel extends CRUDTablePanel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ResourcesPanel.class);

    private static final String[] EMPTY_ARRAY = new String[0];

    private final ApplyPatternsRunnable includesEditor = new ApplyPatternsRunnable(
            "Select includes patterns",
            "includes",
            "include");

    private final ApplyPatternsRunnable excludesEditor = new ApplyPatternsRunnable(
            "Select excludes patterns",
            "excludes",
            "exclude");

    public ResourcesPanel(final Project pProject, final Document pDocument) {
        super(pProject, pDocument, new ResourcesTableModel(pProject, pDocument));
        component.setColumnModel(new ResourcesTableColumnModel());
        component.setAutoCreateColumnsFromModel(false);
    }

    private class ResourcesTableColumnModel extends DefaultTableColumnModel {

        public ResourcesTableColumnModel() {
            final TableColumn directoryColumn = new TableColumn(0, 100);
            directoryColumn.setHeaderValue("Directory");
            directoryColumn.setIdentifier("header");
            addColumn(directoryColumn);

            final TableColumn targetPathColumn = new TableColumn(1, 100);
            targetPathColumn.setHeaderValue("Target Path");
            targetPathColumn.setIdentifier("targetPath");
            addColumn(targetPathColumn);

            final TableColumn includesColumn = new TableColumn(
                    2,
                    100,
                    new ResourcePatternsCellRenderer(),
                    new ResourcePatternsCellEditor(includesEditor));
            includesColumn.setHeaderValue("Includes");
            includesColumn.setIdentifier("includes");
            addColumn(includesColumn);

            final TableColumn excludesColumn = new TableColumn(
                    3,
                    100,
                    new ResourcePatternsCellRenderer(),
                    new ResourcePatternsCellEditor(excludesEditor));
            excludesColumn.setHeaderValue("Excludes");
            excludesColumn.setIdentifier("excludes");
            addColumn(excludesColumn);
        }
    }

    private static class ResourcesTableModel extends SimpleTagBasedXmlPsiTableModel {
        public ResourcesTableModel(final Project pProject,
                                   final Document pDocument) {
            super(pProject,
                  pDocument,
                  "build/resources",
                  "resource",
                  new String[]{
                      "Directory",
                      "Target Path",
                      "Includes",
                      "Excludes"
                  },
                  new String[]{
                      "directory",
                      "targetPath",
                      null,
                      null
                  });
        }

        private XmlTag findResourceTag(final int pRow) {
            final XmlTag resourcesTag = findContainerTag();
            return resourcesTag.getSubTags()[pRow];
        }

        @Override protected void setValueInTag(final XmlTag pRowTag,
                                               final Object pValue,
                                               final int pRow,
                                               final int pColumn) {
            if (pColumn <= 1)
                super.setValueInTag(pRowTag, pValue, pRow, pColumn);
            else {
                final Runnable command = new Runnable() {
                    public void run() {
                        try {
                            final String containerTagName;
                            final String rowTagName;
                            if (pColumn == 2) {
                                containerTagName = "includes";
                                rowTagName = "include";
                            }
                            else {
                                containerTagName = "excludes";
                                rowTagName = "exclude";
                            }

                            final XmlTag resourceTag = findResourceTag(pRow);
                            XmlTag patternContainerTag = resourceTag.findFirstSubTag(containerTagName);
                            if(patternContainerTag == null) {
                                patternContainerTag = resourceTag.createChildTag(containerTagName,
                                                                                 resourceTag.getNamespace(),
                                                                                 null,
                                                                                 false);
                                patternContainerTag = (XmlTag) resourceTag.add(patternContainerTag);
                            }
                            else {
                                final XmlTag[] children = patternContainerTag.findSubTags(rowTagName);
                                for(XmlTag child : children)
                                    child.delete();
                            }

                            final String[] patterns = (String[]) (pValue == null ? EMPTY_ARRAY : pValue);
                            for(String pattern : patterns) {
                                final XmlTag rowTag = patternContainerTag.createChildTag(rowTagName,
                                                                                         resourceTag.getNamespace(),
                                                                                         pattern,
                                                                                         false);
                                patternContainerTag.add(rowTag);
                            }
                        }
                        catch (IncorrectOperationException e) {
                            LOG.error(e.getMessage(), e);
                        }
                    }
                };
                IDEUtils.runCommand(project, command);
            }
        }

        @Override protected Object getValueFromTag(final XmlTag pTag,
                                                   final int pRow,
                                                   final int pColumn) {
            if (pColumn <= 1)
                return super.getValueFromTag(pTag, pRow, pColumn);

            final String patternContainerTagName;
            final String patternRowTagName;
            if (pColumn == 2) {
                patternContainerTagName = "includes";
                patternRowTagName = "include";
            }
            else {
                patternContainerTagName = "excludes";
                patternRowTagName = "exclude";
            }

            final XmlTag patternContainerTag = pTag.findFirstSubTag(patternContainerTagName);
            if (patternContainerTag == null)
                return null;

            final XmlTag[] patternRowTags = patternContainerTag.findSubTags(patternRowTagName);
            final String[] patterns = new String[patternRowTags.length];
            for (int i = 0; i < patterns.length; i++)
                patterns[i] = patternRowTags[i].getValue().getTrimmedText();

            return patterns;
        }
    }

    private static class ResourcePatternsCellRenderer extends DefaultTableCellRenderer {

        @Override public Component getTableCellRendererComponent(JTable table,
                                                                 Object value,
                                                                 boolean isSelected,
                                                                 boolean hasFocus,
                                                                 int row,
                                                                 int column) {

            if(column <= 1)
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            final String[] patterns = (String[]) value;
            final String text = StringUtils.join(patterns, ", ");
            final Component comp = super.getTableCellRendererComponent(table,
                                                                       text,
                                                                       isSelected,
                                                                       hasFocus,
                                                                       row,
                                                                       column);
            if(comp instanceof JComponent) {
                final JComponent jc = (JComponent) comp;
                jc.setToolTipText(value == null ? null : value.toString());
            }

            return comp;
        }
    }

    private class ResourcePatternsCellEditor extends AbstractCellEditor implements TableCellEditor {
        private String[] value = EMPTY_ARRAY;
        private final TextFieldWithBrowseButton field;
        private final ApplyPatternsRunnable editor;

        public ResourcePatternsCellEditor(final ApplyPatternsRunnable pEditor) {
            editor = pEditor;

            final ActionListener action = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    IDEUtils.runCommand(project, editor);
                    if(editor.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
                        final String[] selectedPatterns = editor.getPatterns();
                        value = selectedPatterns;
                        field.setText(StringUtils.join(selectedPatterns, ", "));
                    }
                }
            };

            field = new TextFieldWithBrowseButton(action);
            field.setBorder(null);
            field.setEditable(false);
            field.getTextField().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }

        public Component getTableCellEditorComponent(final JTable pTable,
                                                     final Object pValue,
                                                     final boolean pSelected,
                                                     final int pRow,
                                                     final int pColumn) {
            field.setOpaque(false);
            field.getTextField().setOpaque(false);

            value = pValue == null ? EMPTY_ARRAY : (String[]) pValue;
            editor.setPatterns(value);
            field.setText(StringUtils.join(value, ", "));

            editor.setRow(pRow);

            return field;
        }

        public Object getCellEditorValue() {
            return value;
        }
    }

    private class ApplyPatternsRunnable implements Runnable {

        private final String title;
        private final String containerTagName;
        private final String rowTagName;
        private int row;
        private int exitCode;
        private String[] patterns;

        public ApplyPatternsRunnable(final String pTitle,
                                     final String pPatternContainerTagName,
                                     final String pPatternRowTagName) {
            title = pTitle;
            containerTagName = pPatternContainerTagName;
            rowTagName = pPatternRowTagName;
        }

        public String getContainerTagName() {
            return containerTagName;
        }

        public String getRowTagName() {
            return rowTagName;
        }

        public int getRow() {
            return row;
        }

        public void setRow(final int pRow) {
            row = pRow;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String[] getPatterns() {
            return patterns;
        }

        public void setPatterns(final String[] pPatterns) {
            patterns = pPatterns == null ? EMPTY_ARRAY : pPatterns;
        }

        public void run() {
            final ResourcePatternsPanel patternsPanel = new ResourcePatternsPanel(patterns);

            final DialogBuilder builder = new DialogBuilder(ResourcesPanel.this);
            builder.addCancelAction();
            builder.addOkAction();
            builder.setCenterPanel(patternsPanel);
            builder.setTitle(title);

            exitCode = builder.show();
            patterns = patternsPanel.getSelectedPatterns();

        }
    }
}
