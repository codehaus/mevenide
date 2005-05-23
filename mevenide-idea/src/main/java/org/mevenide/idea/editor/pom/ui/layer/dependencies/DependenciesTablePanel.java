package org.mevenide.idea.editor.pom.ui.layer.dependencies;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ui.Table;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.IDEUtils;
import org.mevenide.idea.util.psi.PsiUtils;
import org.mevenide.idea.util.ui.UIUtils;
import org.mevenide.idea.util.ui.table.AbstractDocumentCRUDPanel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Arik
 */
public class DependenciesTablePanel extends AbstractDocumentCRUDPanel<JTable> {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(DependenciesTablePanel.class);

    /**
     * A runnable that adds a new dependency. Called by the add button.
     */
    private final Runnable addDependencyRunnable = new Runnable() {
        public void run() {
            try {
                addDependency();
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    };

    /**
     * A runnable that removes the selected dependency(ies). Called by the
     * remove button.
     */
    private final Runnable removeDependenciesRunnable = new Runnable() {
        public void run() {
            try {
                removeDependencies();
            }
            catch (IncorrectOperationException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    };

    /**
     * Creates a new dependencies panel for the given project and document.
     *
     * @param pProject the project we belong to
     * @param pEditorDocument the document serving as the backing model
     */
    public DependenciesTablePanel(final Project pProject,
                                  final Document pEditorDocument) {
        super(new Table(), true, false, true, true, pProject, pEditorDocument);

        final PomDependenciesTableModel model = new PomDependenciesTableModel(
                project, editorDocument);

        component.setModel(model);
        component.setCellSelectionEnabled(false);
        component.setColumnSelectionAllowed(false);
        component.setRowSelectionAllowed(true);
        component.getSelectionModel().setSelectionMode(
                ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        setAddAction(new ActionListener() {
            public void actionPerformed(ActionEvent pEvent) {
                IDEUtils.runCommand(project, addDependencyRunnable);
            }
        });

        setRemoveAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IDEUtils.runCommand(project, removeDependenciesRunnable);
            }
        });
    }

    /**
     * Adds a new dependency to the backing document.
     *
     * @throws IncorrectOperationException if errors occur
     */
    private void addDependency() throws IncorrectOperationException {
        final XmlFile file = PsiUtils.findXmlFile(project, editorDocument);
        final XmlDocument xmlDocument = file.getDocument();
        if(xmlDocument == null) {
            Messages.showErrorDialog(project, "The POM does not contain a valid document. Unable to create a dependency.", UIUtils.ERROR_TITLE);
            return;
        }

        final XmlTag projectElt = xmlDocument.getRootTag();
        if(projectElt == null) {
            Messages.showErrorDialog(project, "The POM does not contain a valid document. Unable to create a dependency.", UIUtils.ERROR_TITLE);
            return;
        }

        XmlTag depsTag = projectElt.findFirstSubTag("dependencies");
        if(depsTag == null) {
            depsTag = projectElt.createChildTag("dependencies",
                                                projectElt.getNamespace(),
                                                null,
                                                false);
            projectElt.add(depsTag);
        }

        final XmlTag depTag = depsTag.createChildTag(
                "dependency",
                depsTag.getNamespace(),
                " ",
                false);
        depsTag.add(depTag);
    }

    /**
     * Removes the selected dependencies from the document and table.
     *
     * @throws IncorrectOperationException if an error occurs
     */
    private void removeDependencies() throws IncorrectOperationException {
        final XmlFile file = PsiUtils.findXmlFile(project, editorDocument);
        final XmlDocument xmlDocument = file.getDocument();
        if(xmlDocument == null) {
            Messages.showErrorDialog(project, "The POM does not contain a valid document. Unable to create a dependency.", UIUtils.ERROR_TITLE);
            return;
        }

        final XmlTag projectElt = xmlDocument.getRootTag();
        if(projectElt == null) {
            Messages.showErrorDialog(project, "The POM does not contain a valid document. Unable to create a dependency.", UIUtils.ERROR_TITLE);
            return;
        }

        XmlTag depsTag = projectElt.findFirstSubTag("dependencies");
        if(depsTag == null)
            return;

        final int[] rows = component.getSelectedRows();
        final XmlTag[] depTagsToRemove = new XmlTag[rows.length];

        final XmlTag[] depTags = depsTag.findSubTags("dependency");
        for(int i = 0; i < rows.length; i++)
            depTagsToRemove[i] = depTags[rows[i]];

        for(XmlTag depTagToRemove : depTagsToRemove)
            depTagToRemove.delete();
    }
}
