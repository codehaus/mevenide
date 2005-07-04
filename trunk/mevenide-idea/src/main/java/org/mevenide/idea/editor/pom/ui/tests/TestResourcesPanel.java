package org.mevenide.idea.editor.pom.ui.tests;

import com.intellij.psi.xml.XmlFile;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mevenide.idea.editor.pom.ui.AbstractPomLayerPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiResourcePatterns;
import org.mevenide.idea.psi.project.PsiResources;
import org.mevenide.idea.util.ui.SplitPanel;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class TestResourcesPanel extends AbstractPomLayerPanel
        implements ListSelectionListener {
    private final CRUDTablePanel<TestResourcesTableModel> resources;
    private final CRUDTablePanel<TestResourcePatternsTableModel> includes;
    private final CRUDTablePanel<TestResourcePatternsTableModel> excludes;

    private final PsiProject project;

    public TestResourcesPanel(final PsiProject pProject) {
        project = pProject;
        final XmlFile xmlFile = project.getXmlFile();

        final PsiResources psiResources = project.getTestResources();
        final TestResourcesTableModel resourcesModel = new TestResourcesTableModel(
                psiResources);
        resources = new CRUDTablePanel<TestResourcesTableModel>(xmlFile, resourcesModel);

        final PsiResourcePatterns psiIncludes = psiResources.getIncludes(-1);
        final TestResourcePatternsTableModel includesModel;
        includesModel = new TestResourcePatternsTableModel(psiIncludes);
        includes = new CRUDTablePanel<TestResourcePatternsTableModel>(xmlFile,
                                                                      includesModel);
        includes.getAddButton().setEnabled(false);
        includes.getRemoveButton().setEnabled(false);

        final TestResourcePatternsTableModel excludesModel;
        excludesModel = new TestResourcePatternsTableModel(psiResources.getExcludes(-1));
        excludes = new CRUDTablePanel<TestResourcePatternsTableModel>(xmlFile,
                                                                      excludesModel);
        excludes.getAddButton().setEnabled(false);
        excludes.getRemoveButton().setEnabled(false);

        final JTable depsTable = resources.getComponent();
        depsTable.getSelectionModel().addListSelectionListener(this);

        final SplitPanel<JPanel, JPanel> patternsSplit;
        patternsSplit = new SplitPanel<JPanel, JPanel>(includes,
                                                       excludes,
                                                       true,
                                                       false);

        final SplitPanel<JPanel, JPanel> split;
        split = new SplitPanel<JPanel, JPanel>(resources, patternsSplit, true);

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
    }

    public void valueChanged(ListSelectionEvent e) {
        final int row = resources.getSelectedRow();
        final PsiResources testResources = project.getTestResources();

        final PsiResourcePatterns psiIncludes = testResources.getIncludes(row);
        includes.setTableModel(new TestResourcePatternsTableModel(psiIncludes));
        includes.getAddButton().setEnabled(row >= 0);
        includes.getRemoveButton().setEnabled(row >= 0);

        final PsiResourcePatterns psiExcludes = testResources.getExcludes(row);
        excludes.setTableModel(new TestResourcePatternsTableModel(psiExcludes));
        excludes.getAddButton().setEnabled(row >= 0);
        excludes.getRemoveButton().setEnabled(row >= 0);
    }

}
