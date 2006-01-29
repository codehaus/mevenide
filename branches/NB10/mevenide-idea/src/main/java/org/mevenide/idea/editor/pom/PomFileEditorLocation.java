package org.mevenide.idea.editor.pom;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import javax.swing.*;

/**
 * @author Arik
 */
public class PomFileEditorLocation implements FileEditorLocation {
    private final PomFileEditor editor;

    public PomFileEditorLocation(final PomFileEditor pEditor) {
        editor = pEditor;
    }

    public FileEditor getEditor() {
        return editor;
    }

    public int compareTo(final FileEditorLocation o) {
        final JComponent component = editor.getComponent();
        return 0;
    }
}
