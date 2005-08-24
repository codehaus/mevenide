package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiTeamMemberRoles;
import org.mevenide.idea.psi.project.PsiTeamMembers;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class DefaultPsiTeamMemberRoles extends AbstractPsiBeanRowsObservable
        implements PsiTeamMemberRoles {
    private static final String ROW_TAG_NAME = "role";

    private final PsiTeamMembers teamMembers;

    public DefaultPsiTeamMemberRoles(final PsiTeamMembers pTeamMembers,
                                     final String pRolesContainerTagPath) {
        super(pTeamMembers.getXmlFile(), pRolesContainerTagPath, ROW_TAG_NAME);
        teamMembers = pTeamMembers;
    }

    public PsiTeamMembers getParent() {
        return teamMembers;
    }

    public final String getRole(final int pRow) {
        return getValue(pRow);
    }

    public final void setRole(final int pRow, final Object pValue) {
        setValue(pRow, pValue);
    }

    public final String[] getRoles() {
        return getValues();
    }
}
