package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.table.AbstractDocumentCRUDPanel;
import org.mevenide.idea.util.ui.table.TagBasedXmlPsiTableModel;

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
    protected final TagBasedXmlPsiTableModel model;

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
     * The action listener, invoked by the Remove button, which removes the
     * selected row(s) from the table.
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
                          final TagBasedXmlPsiTableModel pModel) {
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
}
