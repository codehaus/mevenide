package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.Table;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import org.mevenide.idea.util.ui.AbstractDocumentCRUDPanel;

/**
 * @author Arik
 */
public class CRUDTablePanel<ModelType extends CRUDTableModel> extends AbstractDocumentCRUDPanel<JTable> {
    /**
     * The action listener, invoked by the Add button, which adds a new row to the table.
     */
    private final ActionListener addActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent pEvent) {
            getTableModel().appendRow();
        }
    };

    /**
     * The action listener, invoked by the Remove button, which removes the selected row(s) from the
     * table.
     */
    private final ActionListener removeActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            getTableModel().removeRows(component.getSelectedRows());
        }
    };

    /**
     * Creates a new dependencies panel for the given file.
     *
     * @param pPsiFile the file to edit
     */
    public CRUDTablePanel(final PsiFile pPsiFile) {
        this(pPsiFile, null);
    }

    /**
     * Creates a new dependencies panel for the given file and model.
     *
     * @param pPsiFile the file to edit
     * @param pModel the model to use
     */
    public CRUDTablePanel(final PsiFile pPsiFile,
                          final ModelType pModel) {
        super(new Table(),
              true,
              false,
              true,
              true,
              pPsiFile.getProject(),
              FileDocumentManager.getInstance().getDocument(
                  pPsiFile.getVirtualFile()));

        if(pModel != null)
            component.setModel(pModel);
        component.setCellSelectionEnabled(false);
        component.setColumnSelectionAllowed(false);
        component.setRowSelectionAllowed(true);
        component.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setAddAction(addActionListener);
        setRemoveAction(removeActionListener);
    }

    public ModelType getTableModel() {
        return (ModelType) component.getModel();
    }

    public void setTableModel(ModelType pModel) {
        component.setModel(pModel);
    }

    public JTable getTable() {
        return getComponent();
    }

    public int getSelectedRow() {
        return getComponent().getSelectedRow();
    }
}
