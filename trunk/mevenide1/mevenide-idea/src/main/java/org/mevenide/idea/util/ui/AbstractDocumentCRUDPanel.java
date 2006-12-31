package org.mevenide.idea.util.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractDocumentCRUDPanel<T extends JComponent>
        extends CRUDPanel<T> {
    /**
     * The project this panel belongs to.
     */
    protected final Project project;

    /**
     * The IDEA document we are modifying/reading.
     */
    protected final Document document;

    protected AbstractDocumentCRUDPanel(final T pComponent,
                                        final Project pProject,
                                        final Document pEditorDocument) {
        super(pComponent);
        project = pProject;
        document = pEditorDocument;
    }

    protected AbstractDocumentCRUDPanel(final T pComponent,
                                        final boolean pShowAddButton,
                                        final boolean pShowEditButton,
                                        final boolean pShowRemoveButton,
                                        final Project pProject,
                                        final Document pEditorDocument) {
        super(pComponent, pShowAddButton, pShowEditButton, pShowRemoveButton);
        project = pProject;
        document = pEditorDocument;
    }

    protected AbstractDocumentCRUDPanel(final T pComponent,
                                        final boolean pShowAddButton,
                                        final boolean pShowEditButton,
                                        final boolean pShowRemoveButton,
                                        final boolean pWrapInScrollPane,
                                        final Project pProject,
                                        final Document pEditorDocument) {
        super(pComponent,
              pShowAddButton,
              pShowEditButton,
              pShowRemoveButton,
              pWrapInScrollPane);
        project = pProject;
        document = pEditorDocument;
    }

    protected final VirtualFile getFile() {
        return FileDocumentManager.getInstance().getFile(document);
    }
}
