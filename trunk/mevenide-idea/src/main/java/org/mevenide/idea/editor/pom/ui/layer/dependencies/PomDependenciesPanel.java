package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.util.ui.LabeledPanel;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;

/**
 * @author Arik
 */
public class PomDependenciesPanel extends AbstractPomLayerPanel {

    private DependenciesTablePanel depsView;
    private DependencyPropertiesTablePanel propsView;

    public PomDependenciesPanel(final Project pProject,
                                final Document pPomDocument) {
        super(pProject, pPomDocument);

        depsView = new DependenciesTablePanel(project, editorDocument);
        propsView = new DependencyPropertiesTablePanel(project, editorDocument);

        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        final JComponent depsPanel = new LabeledPanel(RES.get("dep.list.desc"),
                                                      depsView);
        final JComponent propsPanel = new LabeledPanel(RES.get("dep.props.desc"),
                                                       propsView);
        final JComponent splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                    true,
                                                    depsPanel,
                                                    propsPanel);
        add(splitPane, BorderLayout.CENTER);
    }
}
