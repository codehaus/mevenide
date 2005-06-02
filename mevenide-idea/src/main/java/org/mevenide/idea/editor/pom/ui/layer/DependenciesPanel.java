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
import org.mevenide.context.IQueryContext;
import org.mevenide.idea.Res;
import static org.mevenide.idea.editor.pom.ui.layer.TableModelConstants.DEPENDENCIES;
import org.mevenide.idea.module.ModuleSettings;
import org.mevenide.idea.repository.AggregatingRepositoryReader;
import org.mevenide.idea.repository.SelectRepositoryItemDialog;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.psi.PsiUtils;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;
import org.mevenide.repository.RepoPathElement;
import static org.mevenide.repository.RepositoryReaderFactory.createRemoteRepositoryReader;

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
                final IQueryContext queryContext = ModuleSettings.getInstance(module).getQueryContext();
                dlg.setRepositoryReader(new AggregatingRepositoryReader(queryContext));

                final RepoPathElement[] selectedElements = dlg.show(project);
                if(selectedElements != null) {
                    IDEUtils.runCommand(project, new Runnable() {
                        public void run() {
                            try {
                                for (RepoPathElement elt : selectedElements) {
                                    final XmlTag depRow = getModel().addRow();
                                    PsiUtils.setTagValue(project,
                                                         depRow,
                                                         "groupId",
                                                         elt.getGroupId());
                                    PsiUtils.setTagValue(project,
                                                         depRow,
                                                         "artifactId",
                                                         elt.getArtifactId());
                                    PsiUtils.setTagValue(project, depRow, "type", elt.getType());
                                    PsiUtils.setTagValue(project,
                                                         depRow,
                                                         "version",
                                                         elt.getVersion());
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
