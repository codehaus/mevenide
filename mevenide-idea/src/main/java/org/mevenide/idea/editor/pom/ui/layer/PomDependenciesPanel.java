package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.Table;

import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;

/**
 * @author Arik
 */
public class PomDependenciesPanel extends AbstractPomLayerPanel {

    private final JTable dependenciesTable;

    public PomDependenciesPanel(final Project pProject, final Document pPomDocument) {
        super(pProject, pPomDocument);
        dependenciesTable = new Table(new PomDependenciesTableModel(project, editorDocument));

        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(new JScrollPane(dependenciesTable), BorderLayout.CENTER);
    }
}
