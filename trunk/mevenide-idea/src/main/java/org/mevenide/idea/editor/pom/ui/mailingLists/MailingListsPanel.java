package org.mevenide.idea.editor.pom.ui.mailingLists;

import org.mevenide.idea.psi.project.PsiMailingLists;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.util.ui.table.CRUDTablePanel;

/**
 * @author Arik
 */
public class MailingListsPanel extends CRUDTablePanel {

    public MailingListsPanel(final PsiProject pModel) {
        this(pModel.getMailingLists());
    }

    public MailingListsPanel(final PsiMailingLists pModel) {
        super(pModel.getXmlFile(), new MailingListsTableModel(pModel));
    }
}
