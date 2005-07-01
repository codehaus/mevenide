package org.mevenide.idea.editor.pom.ui.build;

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
import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class ResourcesPanel extends AbstractPomLayerPanel
    implements ListSelectionListener {
    private final CRUDTablePanel<ResourcesTableModel> resources;
    private final CRUDTablePanel<ResourcePatternsTableModel> includes;
    private final CRUDTablePanel<ResourcePatternsTableModel> excludes;

    private final PsiProject project;

    public ResourcesPanel(final PsiProject pProject) {
        project = pProject;
        final XmlFile xmlFile = project.getXmlFile();

        final PsiResources psiResources = project.getResources();
        final ResourcesTableModel resourcesModel = new ResourcesTableModel(psiResources);
        resources = new CRUDTablePanel<ResourcesTableModel>(xmlFile, resourcesModel);

        final PsiResourcePatterns psiIncludes = psiResources.getIncludes(-1);
        final ResourcePatternsTableModel includesModel;
        includesModel = new ResourcePatternsTableModel(psiIncludes);
        includes = new CRUDTablePanel<ResourcePatternsTableModel>(xmlFile, includesModel);
        includes.getAddButton().setEnabled(false);
        includes.getRemoveButton().setEnabled(false);

        final ResourcePatternsTableModel excludesModel;
        excludesModel = new ResourcePatternsTableModel(psiResources.getExcludes(-1));
        excludes = new CRUDTablePanel<ResourcePatternsTableModel>(xmlFile, excludesModel);
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

        final PsiResourcePatterns psiIncludes = project.getResources().getIncludes(row);
        includes.setTableModel(new ResourcePatternsTableModel(psiIncludes));

        final PsiResourcePatterns psiExcludes = project.getResources().getExcludes(row);
        excludes.setTableModel(new ResourcePatternsTableModel(psiExcludes));

        includes.getAddButton().setEnabled(row >= 0);
        includes.getRemoveButton().setEnabled(row >= 0);

        excludes.getAddButton().setEnabled(row >= 0);
        excludes.getRemoveButton().setEnabled(row >= 0);
    }
}
