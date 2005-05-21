package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.Table;
import org.mevenide.idea.util.ui.LabeledPanel;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * @author Arik
 */
public class PomDependenciesPanel extends AbstractPomLayerPanel {

    private final JTable dependenciesTable;
    private final JTable propertiesTable;
    private final JScrollPane depsView;
    private final JScrollPane propsView;

    public PomDependenciesPanel(final Project pProject, final Document pPomDocument) {
        super(pProject, pPomDocument);

        final PomDependenciesTableModel model = new PomDependenciesTableModel(project, editorDocument);

        dependenciesTable = new Table(model);
        depsView = new JScrollPane(dependenciesTable);
        depsView.setPreferredSize(new Dimension(100, 100));

        propertiesTable = new Table(model);
        propsView = new JScrollPane(propertiesTable);
        propsView.setPreferredSize(new Dimension(100, 100));

        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        final JComponent depsPanel = new LabeledPanel(RES.get("dep.list.title"),
                                                      RES.get("dep.list.desc"),
                                                      depsView);
        final JComponent propsPanel = new LabeledPanel(RES.get("dep.props.title"),
                                                       RES.get("dep.props.desc"),
                                                       propsView);
        final JComponent splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                                    true,
                                                    depsPanel,
                                                    propsPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    public JTable getDependenciesTable() {
        return dependenciesTable;
    }

    public JTable getPropertiesTable() {
        return propertiesTable;
    }
}
