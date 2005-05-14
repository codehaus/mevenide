package org.mevenide.idea.support;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;

import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractFileEditor extends AbstractIdeaComponent implements FileEditor {
    /**
     * The editor name (both display name and ID).
     */
    protected final String NAME;

    /**
     * The IDEA project to which this editor belongs.
     */
    protected final Project project;

    /**
     * The IDEA document for the file. Any modifications we make are to this object -
     * IDEA will synchronize this with the file system when appropriate.
     */
    protected final Document document;

    /**
     * Creates an instance using the given editor name resource key (looked up from
     * the {@link org.mevenide.idea.Res resources} of the actual class' package),
     * IDEA project and the edited file.
     *
     * @param pNameKey the key for the editor name in the resource bundle
     * @param pProject the IDEA project this editor belongs to
     * @param pFile the file being edited
     */
    protected AbstractFileEditor(final String pNameKey,
                                 final Project pProject,
                                 final VirtualFile pFile) {
        this.NAME = RES.get(pNameKey);
        project = pProject;
        document = FileDocumentManager.getInstance().getDocument(pFile);
    }

    public void selectNotify() {
        //Does nothing be default - should be overriden if special actions are to
        //be taken when selected
    }

    public void deselectNotify() {
        //does nothing by default - should be overriden if special actions are to
        //be taken when deselected
    }

    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    public void setState(FileEditorState state) {
    }

    public FileEditorState getState(FileEditorStateLevel level) {
        return null;
    }

    public String getName() {
        return NAME;
    }

    public JComponent getPreferredFocusedComponent() {
        return getComponent();
    }

    public JComponent getComponent() {
        return null;
    }
}
