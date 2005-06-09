package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.psi.xml.XmlFile;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mevenide.idea.editor.pom.ui.layer.AbstractPomLayerPanel;
import org.mevenide.idea.util.ui.SplitPanel;

/**
 * @author Arik
 */
public class DependenciesPanel extends AbstractPomLayerPanel implements ListSelectionListener {
    /**
     * The dependency list table panel.
     */
    private final DependenciesTablePanel depsPanel = new DependenciesTablePanel(file);

    /**
     * The dependencies properties table.
     */
    private final DependencyPropertiesTablePanel propsPanel = new DependencyPropertiesTablePanel(file);

    public DependenciesPanel(final XmlFile pFile) {
        super(pFile);

        propsPanel.getAddButton().setEnabled(false);
        propsPanel.getRemoveButton().setEnabled(false);

        final JTable depsTable = depsPanel.getComponent();
        final ListSelectionModel selectionModel = depsTable.getSelectionModel();
        selectionModel.addListSelectionListener(this);

        final SplitPanel<JPanel, JPanel> splitPanel;
        splitPanel = new SplitPanel<JPanel, JPanel>(depsPanel, propsPanel, true);

        setLayout(new BorderLayout());
        add(splitPanel, BorderLayout.CENTER);
    }

    public void valueChanged(ListSelectionEvent e) {
        final JTable depsTable = depsPanel.getComponent();
        final int row = depsTable.getSelectedRow();

        final DependencyPropertiesTableModel propsModel;
        propsModel = (DependencyPropertiesTableModel) propsPanel.getComponent().getModel();

        final String path;
        if(row < 0)
            path = null;
        else
            path = "project/dependencies/dependency[" + row + "]/properties";

        propsModel.setTagPath(path);
        propsPanel.getAddButton().setEnabled(row >= 0);
        propsPanel.getRemoveButton().setEnabled(row >= 0);
    }
}
