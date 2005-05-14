package org.mevenide.idea.editor.pom;

import com.intellij.openapi.fileEditor.FileEditorState;

/**
 * @author Arik
 */
public interface PomFileEditorStateHandler {

    void getState(PomFileEditorState pState);

    void setState(PomFileEditorState pState);
}
