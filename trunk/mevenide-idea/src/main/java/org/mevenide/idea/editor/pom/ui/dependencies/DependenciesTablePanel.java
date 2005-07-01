package org.mevenide.idea.editor.pom.ui.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.xml.XmlTag;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.mevenide.idea.Res;
import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.repository.SelectRepositoryItemDialog;
import org.mevenide.idea.repository.model.NodeDescriptor;
import org.mevenide.idea.repository.model.RepoTreeNode;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.repository.IRepositoryReader;

/**
 * @author Arik
 */
public class DependenciesTablePanel extends CRUDTablePanel<DependenciesTableModel> {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependenciesTablePanel.class);

    protected static final String BROWSE_REPO_DLG_TITLE = "Select artifact";

    private final JButton browseDependencyButton = new JButton(RES.get("browse.dep.label"));

    public DependenciesTablePanel(final PsiDependencies pModel) {
        super(pModel.getXmlFile(), new DependenciesTableModel(pModel));

        component.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        browseDependencyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final DependencyBrowseDialog dlg = new DependencyBrowseDialog();
                final RepoTreeNode[] selectedElements = dlg.show(project);

                if (selectedElements != null) {
                    IDEUtils.runCommand(
                        project,
                        new AddDependenciesRunnable(selectedElements));
                }
            }
        });

        buttonsBar.addRelatedGap();
        buttonsBar.addFixed(browseDependencyButton);
    }

    private class DependencyBrowseDialog extends SelectRepositoryItemDialog {
        public DependencyBrowseDialog() {
            setAllowingArtifacts(false);
            setAllowingGroups(false);
            setAllowingRoot(false);
            setAllowingTypes(false);
            setAllowingVersions(true);
            setTitle(BROWSE_REPO_DLG_TITLE);

            final Module module = VfsUtil.getModuleForFile(project, getFile());
            final IRepositoryReader[] readers = RepositoryUtils.createRepoReaders(module);
            setRepositoryReaders(readers);
        }
    }

    private class AddDependenciesRunnable implements Runnable {
        private final RepoTreeNode[] items;

        public AddDependenciesRunnable(final RepoTreeNode[] pPathElements) {
            items = pPathElements;
        }

        public void run() {
            //TODO: need rewrite to use PSI bean api
            for (RepoTreeNode elt : items) {
                final NodeDescriptor desc = elt.getNodeDescriptor();
                final Object result = getTableModel().appendRow();
                if (!(result instanceof XmlTag))
                    return;

                final XmlTag depRow = (XmlTag) result;
                PsiUtils.setTagValue(project,
                                     depRow,
                                     "groupId",
                                     desc.getGroupId());
                PsiUtils.setTagValue(project,
                                     depRow,
                                     "artifactId",
                                     desc.getArtifactId());
                PsiUtils.setTagValue(project, depRow, "type", desc.getType());
                PsiUtils.setTagValue(project,
                                     depRow,
                                     "version",
                                     desc.getVersion());
            }
        }
    }
}
