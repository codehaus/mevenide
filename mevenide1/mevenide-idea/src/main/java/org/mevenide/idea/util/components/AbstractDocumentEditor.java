package org.mevenide.idea.util.components;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * @author Arik
 */
public abstract class AbstractDocumentEditor extends AbstractFileEditor {
    /**
     * The IDEA project to which this editor belongs.
     */
    protected final Project project;

    /**
     * The IDEA document for the file. Any modifications we make are to this object - IDEA will
     * synchronize this with the file system when appropriate.
     */
    protected final Document document;

    /**
     * Creates an instance.
     *
     * @param pNameKey the key for the editor name in the resource bundle
     * @param pProject the IDEA project this editor belongs to
     * @param pFile    the file being edited
     */
    protected AbstractDocumentEditor(final String pNameKey,
                                     final Project pProject,
                                     final VirtualFile pFile) {
        super(pNameKey);
        project = pProject;
        document = FileDocumentManager.getInstance().getDocument(pFile);
    }
}
