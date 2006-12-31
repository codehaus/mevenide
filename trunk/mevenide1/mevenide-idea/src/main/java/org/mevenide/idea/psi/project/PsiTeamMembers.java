package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiTeamMembers extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiProject> {
    String getName(int pRow);

    void setName(int pRow, String pName);

    String getId(int pRow);

    void setId(int pRow, String pId);

    String getEmail(int pRow);

    void setEmail(int pRow, String pEmail);

    String getOrganization(int pRow);

    void setOrganization(int pRow, String pOrganization);

    String getUrl(int pRow);

    void setUrl(int pRow, String pUrl);

    String getTimezone(int pRow);

    void setTimezone(int pRow, String pTimezone);

    PsiTeamMemberRoles getRoles(int pRow);
}
