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
package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import org.mevenide.idea.editor.pom.PomFileEditorState;
import org.mevenide.idea.editor.pom.PomFileEditorStateHandler;
import org.mevenide.idea.editor.pom.ui.layer.dependencies.PomDependenciesPanel;

import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Component;

/**
 * This panel displays a single POM layer.
 * 
 * @author Arik
 */
public class PomLayerPanel extends AbstractPomLayerPanel implements PomFileEditorStateHandler {
    private final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    private final PomGeneralInfoPanel generalInfoPanel = new PomGeneralInfoPanel(project, editorDocument);
    private final PomDependenciesPanel dependenciesPanel = new PomDependenciesPanel(project, editorDocument);

    public PomLayerPanel(final com.intellij.openapi.project.Project pProject,
                         final Document pPomDocument) {
        super(pProject, pPomDocument);
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        tabs.add("General", new JScrollPane(generalInfoPanel));
        tabs.add("Dependencies", dependenciesPanel);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
    }

    public void getState(final PomFileEditorState pState) {
        pState.setSelectedTabIndex(tabs.getSelectedIndex());

        final Component component = tabs.getSelectedComponent();
        if(component instanceof PomFileEditorStateHandler) {
            PomFileEditorStateHandler handler = (PomFileEditorStateHandler) component;
            handler.getState(pState);
        }
    }

    public void setState(final PomFileEditorState pState) {
        tabs.setSelectedIndex(pState.getSelectedTabIndex());

        final Component component = tabs.getSelectedComponent();
        if(component instanceof PomFileEditorStateHandler) {
            PomFileEditorStateHandler handler = (PomFileEditorStateHandler) component;
            handler.setState(pState);
        }
    }
}
