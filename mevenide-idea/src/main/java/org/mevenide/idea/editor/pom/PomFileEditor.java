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
import com.intellij.openapi.Disposable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.mevenide.idea.editor.pom.ui.layer.PomPanel;
import org.mevenide.idea.support.AbstractFileEditor;

import javax.swing.JComponent;

/**
 * An editor for POM files.
 *
 * <p>This editor displays a graphical user interface for POMs.</p>
 *
 * @author Arik
 */
public class PomFileEditor extends AbstractFileEditor implements Disposable {

    /**
     * The user interface component displaying the POM editor.
     */
    private PomPanel ui;

    /**
     * Creates an instance for the given IDEA project and POM file.
     *
     * @param pProject the IDEA project
     * @param pPomFile the POM file to edit
     */
    public PomFileEditor(final Project pProject,
                         final VirtualFile pPomFile) {
        super("editor.name", pProject, pPomFile);
        ui = new PomPanel(project, document);
    }

    /**
     * Returns the user interface component for the POM - a {@link PomPanel} instance.
     *
     * @return {@code PomPanel}.
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
     * Checks if the POM is valid by checking via the PSI file. This is not
     * a full check - but only checks that the XML is valid .
     *
     * @return {@code true} if the POM xml is valid, {@code false} otherwise.
     */
    public boolean isValid() {
        final PsiDocumentManager mgr = PsiDocumentManager.getInstance(project);
        final PsiFile file = mgr.getCachedPsiFile(document);
        return file.isValid();
    }

    public void dispose() {
        ui.dispose();
    }
}
