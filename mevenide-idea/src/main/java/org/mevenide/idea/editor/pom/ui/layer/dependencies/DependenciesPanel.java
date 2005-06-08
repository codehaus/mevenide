package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.mevenide.idea.Res;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.repository.SelectRepositoryItemDialog;
import org.mevenide.idea.repository.model.NodeDescriptor;
import org.mevenide.idea.repository.model.RepoTreeNode;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.psi.PsiUtils;
import org.mevenide.idea.util.ui.table.SimpleCRUDTablePanel;
import org.mevenide.repository.IRepositoryReader;

/**
 * @author Arik
 */
public class DependenciesPanel extends SimpleCRUDTablePanel {
    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependenciesPanel.class);

    private static final String TAG_PATH = "project/dependencies";
    private static final String ROW_TAG_NAME = "dependency";
    private static final String[] VALUE_TAG_NAMES = new String[]{
        "groupId",
        "artifactId",
        "version",
        "type"
    };

    private static final String[] COLUMN_TITLES = new String[]{
        "Group ID",
        "Artifact ID",
        "Version",
        "Type"
    };

    private final JButton browseDependencyButton = new JButton(RES.get("browse.dep.label"));
    protected static final String BROWSE_REPO_DLG_TITLE = "Select artifact";

    public DependenciesPanel(final XmlFile pXmlFile) {
        super(pXmlFile,
              TAG_PATH,
              ROW_TAG_NAME,
              VALUE_TAG_NAMES,
              COLUMN_TITLES);

        browseDependencyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final SelectRepositoryItemDialog dlg = new SelectRepositoryItemDialog();
                dlg.setAllowingArtifacts(false);
                dlg.setAllowingGroups(false);
                dlg.setAllowingRoot(false);
                dlg.setAllowingTypes(false);
                dlg.setAllowingVersions(true);
                dlg.setTitle(BROWSE_REPO_DLG_TITLE);

                final Module module = VfsUtil.getModuleForFile(project, getFile());
                final IRepositoryReader[] readers = RepositoryUtils.createRepoReaders(module);
                dlg.setRepositoryReaders(readers);

                final RepoTreeNode[] selectedElements = dlg.show(project);
                if (selectedElements != null) {
                    IDEUtils.runCommand(project, new Runnable() {
                        public void run() {
                            for (RepoTreeNode elt : selectedElements) {
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
                    });
                }
            }
        });

        buttonsBar.addRelatedGap();
        buttonsBar.addFixed(browseDependencyButton);
    }
}
