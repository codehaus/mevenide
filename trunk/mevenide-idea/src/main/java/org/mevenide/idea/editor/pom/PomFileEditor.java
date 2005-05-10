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

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.maven.project.Project;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.editor.pom.ui.PomPanel;
import org.mevenide.idea.support.AbstractIdeaComponent;
import org.mevenide.idea.util.io.IdeaDocumentWriter;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.project.io.DefaultProjectMarshaller;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;

/**
 * @author Arik
 */
public class PomFileEditor extends AbstractIdeaComponent implements FileEditor {
    static final String NAME = "POM Editor";

    private final com.intellij.openapi.project.Project project;
    private final VirtualFile file;
    private final Document document;

    private FileEditorState state = new PomFileEditorState();
    private PomPanel ui;

    private final IQueryContext queryContext;

    public PomFileEditor(final com.intellij.openapi.project.Project pProject,
                         final VirtualFile pPomFile) {
        project = pProject;
        file = pPomFile;
        document = FileDocumentManager.getInstance().getDocument(file);

        final File pomFile = new File(file.getPath());
        final File pomDir = pomFile.getParentFile();

        queryContext = new DefaultQueryContext(pomDir);

        ui = new PomPanel(project, document, queryContext);
        ui.addApplyAction(new Runnable() {
            public void run() {
                flush();
            }
        });
    }

    public void deselectNotify() {
    }

    public void flush() {
        final Runnable flusher = new Runnable() {
            public void run() {
                try {
                    final DefaultProjectMarshaller marshaller = new DefaultProjectMarshaller();
                    final Project project = queryContext.getPOMContext().getProjectLayers()[0];
                    final IdeaDocumentWriter writer = new IdeaDocumentWriter(document);
                    final BufferedWriter out = new BufferedWriter(writer);

                    document.deleteString(0, document.getTextLength());
                    marshaller.marshall(out, project);
                    out.flush();
                }
                catch (Exception e) {
                    UIUtils.showError(project, e.getMessage());
                    LOG.error(e.getMessage(), e);
                }
            }
        };

        final Runnable writeAction = new Runnable() {
            public void run() {
                CommandProcessor.getInstance().executeCommand(project,
                                                              flusher,
                                                              "Apply POM",
                                                              "POM");

            }
        };

        ApplicationManager.getApplication().runWriteAction(writeAction);
    }

    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

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

    public String getName() {
        return NAME;
    }

    public JComponent getPreferredFocusedComponent() {
        return ui;
    }

    public FileEditorState getState(FileEditorStateLevel level) {
        return state;
    }

    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    public boolean isModified() {
        return FileDocumentManager.getInstance().isDocumentUnsaved(document);
    }

    public boolean isValid() {
        return true;
    }

    public void selectNotify() {
    }

    public void setState(FileEditorState pState) {
        state = pState;
    }
}
