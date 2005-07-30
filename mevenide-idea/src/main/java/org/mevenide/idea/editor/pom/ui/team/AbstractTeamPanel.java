package org.mevenide.idea.editor.pom.ui.team;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.mevenide.idea.editor.pom.ui.AbstractPomLayerPanel;
import org.mevenide.idea.psi.project.PsiTeamMemberRoles;
import org.mevenide.idea.psi.project.PsiTeamMembers;
import org.mevenide.idea.util.ui.SplitPanel;

/**
 * @author Arik
 */
public abstract class AbstractTeamPanel<Psi extends PsiTeamMembers>
        extends AbstractPomLayerPanel
        implements ListSelectionListener {
    private final Psi model;
    private final MembersPanel members;
    private final RolesPanel roles;

    public AbstractTeamPanel(final Psi pPsi) {
        model = pPsi;

        members = new MembersPanel(pPsi);
        roles = new RolesPanel(model.getRoles(-1));

        members.getTable().getSelectionModel().addListSelectionListener(this);

        layoutComponents();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        final SplitPanel<JPanel, JPanel> splitPanel = new SplitPanel<JPanel, JPanel>(members,
                                                                                     roles,
                                                                                     true,
                                                                                     true);
        add(splitPanel,
            BorderLayout.CENTER);
    }

    public void valueChanged(final ListSelectionEvent pEvent) {
        final JTable membersTable = members.getTable();
        final int row = membersTable.getSelectedRow();

        final PsiTeamMemberRoles psiRoles = model.getRoles(row);
        roles.setTableModel(new RolesTableModel(psiRoles));

        roles.getAddButton().setEnabled(row >= 0);
        roles.getRemoveButton().setEnabled(row >= 0);
    }
}
