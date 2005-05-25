package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class CRUDTablePanel extends AbstractDocumentCRUDPanel<JTable> {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(CRUDTablePanel.class);

    /**
     * The table model.
     */
    protected final MutableXmlPsiTableModel model;

    /**
     * The action listener, invoked by the Add button, which adds a new row to the table.
     */
    private final ActionListener addActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent pEvent) {
            IDEUtils.runCommand(project, new Runnable() {
                public void run() {
                    try {
                        model.addRow();
                    }
                    catch (IncorrectOperationException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
        }
    };

    /**
     * The action listener, invoked by the Remove button, which removes the selected row(s) from the
     * table.
     */
    private final ActionListener removeActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            IDEUtils.runCommand(project, new Runnable() {
                public void run() {
                    try {
                        model.removeRows(component.getSelectedRows());
                    }
                    catch (IncorrectOperationException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            });
        }
    };

    /**
     * Creates a new dependencies panel for the given project and document.
     *
     * @param pProject        the project we belong to
     * @param pEditorDocument the document serving as the backing model
     */
    public CRUDTablePanel(final Project pProject,
                          final Document pEditorDocument,
                          final CRUDXmlPsiDescriptor pDescriptor) {
        this(pProject,
             pEditorDocument,
             new SimpleTagBasedXmlPsiTableModel(pProject,
                                                pEditorDocument,
                                                pDescriptor.getContainerTagName(),
                                                pDescriptor.getRowTagName(),
                                                pDescriptor.getColumnTitles(),
                                                pDescriptor.getValueTagNames()));
    }

    /**
     * Creates a new dependencies panel for the given project and document.
     *
     * @param pProject        the project we belong to
     * @param pEditorDocument the document serving as the backing model
     */
    public CRUDTablePanel(final Project pProject,
                          final Document pEditorDocument,
                          final MutableXmlPsiTableModel pModel) {
        super(new Table(), true, false, true, true, pProject, pEditorDocument);

        model = pModel;

        component.setModel(model);
        component.setCellSelectionEnabled(false);
        component.setColumnSelectionAllowed(false);
        component.setRowSelectionAllowed(true);
        component.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setAddAction(addActionListener);
        setRemoveAction(removeActionListener);
    }

    public MutableXmlPsiTableModel getModel() {
        return model;
    }

    public static interface CRUDXmlPsiDescriptor {
        String getContainerTagName();

        String getRowTagName();

        String[] getColumnTitles();

        String[] getValueTagNames();
    }

    public static class SimpleCRUDXmlPsiDescriptor implements CRUDXmlPsiDescriptor {
        private final String containerTagName;
        private final String rowTagName;
        private final String[] columnTitles;
        private final String[] valueTagNames;

        public SimpleCRUDXmlPsiDescriptor(final String pContainerTagName,
                                          final String pRowTagName,
                                          final String[] pColumnTitles,
                                          final String[] pValueTagNames) {
            containerTagName = pContainerTagName;
            rowTagName = pRowTagName;
            columnTitles = pColumnTitles;
            valueTagNames = pValueTagNames;
        }

        public String[] getColumnTitles() {
            return columnTitles;
        }

        public String getContainerTagName() {
            return containerTagName;
        }

        public String getRowTagName() {
            return rowTagName;
        }

        public String[] getValueTagNames() {
            return valueTagNames;
        }
    }
}
