package org.mevenide.idea.util.components;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import javax.swing.*;

/**
 * @author Arik
 */
public abstract class AbstractFileEditor extends AbstractIdeaComponent
        implements FileEditor {
    /**
     * The editor name (both display name and ID).
     */
    protected final String NAME;

    /**
     * Creates an instance using the given editor name resource key (looked up from the {@link
     * org.mevenide.idea.Res resources} of the actual class' package), IDEA project and the edited
     * file.
     *
     * @param pNameKey the key for the editor name in the resource bundle
     */
    protected AbstractFileEditor(final String pNameKey) {
        this.NAME = RES.get(pNameKey);
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
