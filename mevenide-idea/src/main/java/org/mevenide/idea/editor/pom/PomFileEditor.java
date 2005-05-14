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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.project.Project;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.idea.editor.pom.ui.layer.PomLayerPanel;
import org.mevenide.idea.support.AbstractFileEditor;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.io.File;

/**
 * @todo Respond to external POM file changes (or even from the POM text editor!)
 * @author Arik
 */
public class PomFileEditor extends AbstractFileEditor {

    /**
     * The JDOM document for the POM. This is the memory representation of the
     * modifications we make (constantly flushed to the IDEA document when appropriate).
     */
    private org.jdom.Document projectDoc;

    /**
     * Temporary - this is the editor state. This shouldn't really be a field but for
     * now, we ignore the whole state facility so this is sufficient.
     */
    private FileEditorState state = new PomFileEditorState();

    /**
     * The user interface component displaying the POM editor.
     */
    private PomLayerPanel ui;

    /**
     * A {@link Runnable} that flushes the JDOM document into the IDEA document object.
     */
    private final Runnable writeAction = new ElementFlusherWriteAction();

    /**
     * A {@link FileDocumentManager} {@link FileDocumentManagerAdapter adapter} which
     * makes sure we flush our changes from the JDOM document back to the IDEA
     * {@link Document}.
     */
    private final FileDocumentManagerAdapter docManagerListener = new FileDocumentManagerAdapter() {
        public void beforeDocumentSaving(Document document) {
            flush();
        }
    };

    /**
     * Creates an instance for the given IDEA project and POM file.
     *
     * @param pProject the IDEA project
     * @param pPomFile the POM file to edit
     */
    public PomFileEditor(final Project pProject,
                         final VirtualFile pPomFile) {
        super("editor.name", pProject, pPomFile);

        final File pomFile = new File(pPomFile.getPath());
        final File pomDir = pomFile.getParentFile();

        final IQueryContext queryContext = new DefaultQueryContext(pomDir);
        final IProjectContext pomContext = queryContext.getPOMContext();
        final Element[] rootElementLayers = pomContext.getRootElementLayers();
        final Element clonedElt = ((Element) rootElementLayers[0].clone());

        projectDoc = new org.jdom.Document((Element) clonedElt.detach());
        ui = new PomLayerPanel(projectDoc, project, document);
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

    public FileEditorState getState(FileEditorStateLevel level) {
        return state;
    }

    /**
     * Returns {@code true} if the user interface components were modified, or if the
     * IDEA document has been modified and not saved.
     *
     * @return {@code boolean}
     */
    public boolean isModified() {
        return FileDocumentManager.getInstance().isDocumentUnsaved(document) ||
                ui.isModified();
    }

    /**
     * @todo implement isValid()
     * @return
     */
    public boolean isValid() {
        return true;
    }

    public void setState(FileEditorState pState) {
        state = pState;
    }

    /**
     * Registers a {@link FileDocumentManager} {@link FileDocumentManagerAdapter adapter}
     * that will flush the JDOM changes into the IDEA document before saving the document.
     */
    public void initComponent() {
        FileDocumentManager.getInstance().addFileDocumentManagerListener(docManagerListener);
    }

    /**
     * Removes the listener registered in {@link #initComponent()}.
     */
    public void disposeComponent() {
        FileDocumentManager.getInstance().removeFileDocumentManagerListener(docManagerListener);
    }

    /**
     * Flushes the JDOM document into the IDEA document, if the user has performed any
     * changes in the user interface.
     * @todo after flushing - remove the "dirty" flag from the ui components
     */
    private void flush() {
//        if(ui.isModified())
//            ApplicationManager.getApplication().runWriteAction(writeAction);
    }

    /**
     * A runnable that flushes the JDOM content into the IDEA document.
     */
    private class ElementFlusher implements Runnable {
        public void run() {
            final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
            final String lineSep = FileDocumentManager.getInstance().getLineSeparator(file, project);

            Format format = Format.getPrettyFormat();
            format.setIndent(StringUtils.repeat(" ", 4));
            format.setLineSeparator(lineSep);

            XMLOutputter out = new XMLOutputter(format);
            final String xml = out.outputString(projectDoc);
            document.replaceString(0, document.getTextLength(), xml);
        }
    }

    /**
     * An IDEA write action (a runnable) which uses the {@link ElementFlusher} to
     * flush the JDOM content into the IDEA document.
     */
    private class ElementFlusherWriteAction implements Runnable {
        private final Runnable flusher = new ElementFlusher();

        public void run() {
            CommandProcessor.getInstance().executeCommand(project,
                                                          flusher,
                                                          "Apply POM",
                                                          "POM");

        }
    }
}
