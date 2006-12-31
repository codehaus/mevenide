package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiTeamMemberRoles
        extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiTeamMembers> {
    String getRole(final int pRow);

    void setRole(int pRow, Object pValue);

    String[] getRoles();
}
