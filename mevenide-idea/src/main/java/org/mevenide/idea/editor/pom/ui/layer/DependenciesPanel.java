package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.table.AbstractDocumentCRUDPanel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class DependenciesPanel extends AbstractDocumentCRUDPanel<JTable> {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(DependenciesPanel.class);

    /**
     * The dependency table model.
     */
    private final DependenciesTableModel model;

    /**
     * A runnable that adds a new dependency. Called by the add button.
     */
    private final Runnable addDependencyRunnable = new Runnable() {
        public void run() {
            try {
                model.addRow();
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    };

    /**
     * A runnable that removes the selected dependency(ies). Called by the remove button.
     */
    private final Runnable removeDependenciesRunnable = new Runnable() {
        public void run() {
            try {
                model.removeRows(component.getSelectedRows());
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    };

    /**
     * Creates a new dependencies panel for the given project and document.
     *
     * @param pProject        the project we belong to
     * @param pEditorDocument the document serving as the backing model
     */
    public DependenciesPanel(final Project pProject,
                             final Document pEditorDocument) {
        super(new Table(), true, false, true, true, pProject, pEditorDocument);

        model = new DependenciesTableModel(project, editorDocument);

        component.setModel(model);
        component.setCellSelectionEnabled(false);
        component.setColumnSelectionAllowed(false);
        component.setRowSelectionAllowed(true);
        component.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setAddAction(new ActionListener() {
            public void actionPerformed(ActionEvent pEvent) {
                IDEUtils.runCommand(project, addDependencyRunnable);
            }
        });

        setRemoveAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IDEUtils.runCommand(project, removeDependenciesRunnable);
            }
        });
    }
}
