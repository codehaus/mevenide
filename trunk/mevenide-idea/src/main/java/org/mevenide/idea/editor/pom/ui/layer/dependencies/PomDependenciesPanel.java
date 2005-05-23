package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.util.ui.LabeledPanel;

import javax.swing.JComponent;
import java.awt.BorderLayout;

/**
 * @author Arik
 */
public class PomDependenciesPanel extends AbstractPomLayerPanel {

    private DependenciesTablePanel depsView;

    public PomDependenciesPanel(final Project pProject,
                                final Document pPomDocument) {
        super(pProject, pPomDocument);

        depsView = new DependenciesTablePanel(project, editorDocument);

        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        final JComponent depsPanel = new LabeledPanel(
                RES.get("dep.list.desc"), depsView);
        
        add(depsPanel, BorderLayout.CENTER);
    }
}
