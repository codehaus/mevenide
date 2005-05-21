package org.mevenide.idea.editor.pom.ui.layer;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.Table;
import org.mevenide.idea.util.ui.table.CRUDPanel;
import org.mevenide.idea.util.ui.LabeledPanel;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class PomDependenciesPanel extends AbstractPomLayerPanel {

    private final JTable dependenciesTable;
    private final JTable propertiesTable;
    private final DependenciesTablePanel depsView;
    private final DependencyPropertiesTablePanel propsView;

    public PomDependenciesPanel(final Project pProject, final Document pPomDocument) {
        super(pProject, pPomDocument);

        final PomDependenciesTableModel model = new PomDependenciesTableModel(project, editorDocument);

        dependenciesTable = new Table(model);
        depsView = new DependenciesTablePanel();

        propertiesTable = new Table(model);
        propsView = new DependencyPropertiesTablePanel();

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

    public JTable getDependenciesTable() {
        return dependenciesTable;
    }

    public JTable getPropertiesTable() {
        return propertiesTable;
    }

    private class DependenciesTablePanel extends CRUDPanel {

        public DependenciesTablePanel() {
            super(new JScrollPane(dependenciesTable), true, false, true);
            setAddAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LOG.trace("Adding dependency");
                }
            });
            setEditAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LOG.trace("Editing dependency");
                }
            });
            setRemoveAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LOG.trace("Removing dependency");
                }
            });
        }
    }

    private class DependencyPropertiesTablePanel extends CRUDPanel {

        public DependencyPropertiesTablePanel() {
            super(new JScrollPane(propertiesTable), true, false, true);
            setAddAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LOG.trace("Adding dependency property");
                }
            });
            setEditAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LOG.trace("Editing dependency property");
                }
            });
            setRemoveAction(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LOG.trace("Removing dependency property");
                }
            });
        }
    }
}
