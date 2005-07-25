package org.mevenide.idea.editor.pom.ui.dependencies;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mevenide.idea.editor.pom.ui.AbstractPomLayerPanel;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiDependencyProperties;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.SplitPanel;

/**
 * @author Arik
 */
public class DependenciesPanel extends AbstractPomLayerPanel
    implements ListSelectionListener {
    /**
     * The dependency list table panel.
     */
    private final DependenciesTablePanel depsPanel;

    /**
     * The dependencies properties table.
     */
    private final DependencyPropertiesTablePanel propsPanel;

    /**
     * The PSI dependencies model bean.
     */
    private final PsiDependencies model;

    public DependenciesPanel(final PsiProject pModel) {
        this(pModel.getDependencies());
    }

    public DependenciesPanel(final PsiDependencies pModel) {
        model = pModel;
        depsPanel = new DependenciesTablePanel(model);
        propsPanel = new DependencyPropertiesTablePanel(model.getProperties(-1));

        propsPanel.getAddButton().setEnabled(false);
        propsPanel.getRemoveButton().setEnabled(false);

        final JTable depsTable = depsPanel.getComponent();
        depsTable.getSelectionModel().addListSelectionListener(this);

        final SplitPanel<JPanel, JPanel> splitPanel;
        splitPanel = new SplitPanel<JPanel, JPanel>(depsPanel, propsPanel, true);

        setLayout(new BorderLayout());
        add(splitPanel, BorderLayout.CENTER);
    }

    public void valueChanged(ListSelectionEvent e) {
        final int row = depsPanel.getSelectedRow();

        final PsiDependencyProperties psiProps = model.getProperties(row);

        final DependencyPropertiesTableModel tableModel;
        tableModel = new DependencyPropertiesTableModel(psiProps);
        propsPanel.setTableModel(tableModel);

        propsPanel.getAddButton().setEnabled(row >= 0);
        propsPanel.getRemoveButton().setEnabled(row >= 0);
    }
}
