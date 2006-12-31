package org.mevenide.idea.editor.pom;

/**
 * @author Arik
 */
public interface PomFileEditorStateHandler {
    void getState(PomFileEditorState pState);

    void setState(PomFileEditorState pState);
}
