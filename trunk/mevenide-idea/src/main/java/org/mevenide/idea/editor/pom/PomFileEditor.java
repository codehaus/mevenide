/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.editor.pom;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.mevenide.idea.editor.pom.ui.layer.PomLayerPanel;
import org.mevenide.idea.support.AbstractFileEditor;

import javax.swing.*;
import java.awt.*;

/**
 * @author Arik
 */
public class PomFileEditor extends AbstractFileEditor {

    /**
     * The user interface component displaying the POM editor.
     */
    private PomLayerPanel ui;

    /**
     * Creates an instance for the given IDEA project and POM file.
     *
     * @param pProject the IDEA project
     * @param pPomFile the POM file to edit
     */
    public PomFileEditor(final Project pProject,
                         final VirtualFile pPomFile) {
        super("editor.name", pProject, pPomFile);
        ui = new PomLayerPanel(project, document);
    }

    /**
     * Returns the user interface component for the POM - a {@link PomLayerPanel} instance.
     *
     * @return {@code PomLayerPanel}.
     */
    public JComponent getComponent() {
        return ui;
    }

    public FileEditorLocation getCurrentLocation() {
        return new FileEditorLocation() {
            public FileEditor getEditor() {
                return PomFileEditor.this;
            }

            public int compareTo(final FileEditorLocation o) {
                return 0;
            }
        };
    }

    /**
     * Returns the current state of the editor. State includes the selected tab,
     * and current component in that tab.
     *
     * @param level ignored
     * @return pom editor state
     */
    public FileEditorState getState(final FileEditorStateLevel level) {
        final PomFileEditorState pomFileEditorState = new PomFileEditorState();
        ui.getState(pomFileEditorState);

        return pomFileEditorState;
    }

    /**
     * Applies the given state. If the state is an instance of {@link PomFileEditorState},
     * than the current tab and component are extracted from it and focused.
     *
     * @param pState the state to set
     */
    public void setState(FileEditorState pState) {
        if(pState instanceof PomFileEditorState)
            ui.setState((PomFileEditorState) pState);
    }

    /**
     * Returns the component that should receive the focus.
     *
     * @todo this does not work yet, for some reason
     * @return a JComponent
     */
    @Override public JComponent getPreferredFocusedComponent() {
        final PomFileEditorState state = (PomFileEditorState) getState(null);
        final Component comp = state.getCurrentField();
        final String name = comp == null ? "null" : comp.getName();
        LOG.trace("PomFileEditor.getPreferredFocusedComponent - comp is " + name);
        return (JComponent) comp;
    }

    /**
     * Returns {@code true} if the user interface components were modified, or if the
     * IDEA document has been modified and not saved.
     *
     * @return {@code boolean}
     */
    public boolean isModified() {
        if(PsiDocumentManager.getInstance(project).isUncommited(document))
            return true;

        return FileDocumentManager.getInstance().isDocumentUnsaved(document);
    }

    /**
     * @return
     */
    public boolean isValid() {
        final PsiDocumentManager mgr = PsiDocumentManager.getInstance(project);
        final PsiFile file = mgr.getCachedPsiFile(document);
        return file.isValid();
    }
}
