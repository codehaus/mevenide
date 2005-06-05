package org.mevenide.idea.editor.pom.ui.layer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.Res;
import static org.mevenide.idea.editor.pom.ui.layer.TableModelConstants.DEPENDENCIES;
import org.mevenide.idea.repository.RepositoryUtils;
import org.mevenide.idea.repository.SelectRepositoryItemDialog;
import org.mevenide.idea.repository.model.RepoTreeNode;
import org.mevenide.idea.repository.model.NodeDescriptor;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.psi.PsiUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.repository.IRepositoryReader;
import org.mevenide.repository.RepoPathElement;

/**
 * @author Arik
 */
public class DependenciesPanel extends CRUDTablePanel {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(DependenciesPanel.class);

    /**
     * Resources
     */
    private static final Res RES = Res.getInstance(DependenciesPanel.class);

    private final JButton browseDependencyButton = new JButton(RES.get("browse.dep.label"));
    protected static final String BROWSE_REPO_DLG_TITLE = "Select artifact";

    public DependenciesPanel(final Project pProject, final Document pDocument) {
        super(pProject, pDocument, DEPENDENCIES);

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
                if(selectedElements != null) {
                    IDEUtils.runCommand(project, new Runnable() {
                        public void run() {
                            try {
                                for (RepoTreeNode elt : selectedElements) {
                                    final NodeDescriptor desc = elt.getNodeDescriptor();
                                    final XmlTag depRow = getModel().addRow();
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
                            catch (IncorrectOperationException e1) {
                                LOG.error(e1.getMessage(), e1);
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
