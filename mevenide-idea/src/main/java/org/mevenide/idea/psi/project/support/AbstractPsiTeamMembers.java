package org.mevenide.idea.psi.project.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiTeamMemberRoles;
import org.mevenide.idea.psi.project.PsiTeamMembers;
import org.mevenide.idea.psi.project.impl.DefaultPsiTeamMemberRoles;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public abstract class AbstractPsiTeamMembers extends AbstractPsiBeanRowsObservable
        implements PsiTeamMembers {
    private final Map<Integer, PsiTeamMemberRoles> rolesCache = Collections.synchronizedMap(
            new HashMap<Integer, PsiTeamMemberRoles>(10));
    private final PsiProject project;

    protected AbstractPsiTeamMembers(final PsiProject pProject,
                                     final String pContainerTagPath,
                                     final String pRowTagName) {
        super(pProject.getXmlFile(), pContainerTagPath, pRowTagName);
        project = pProject;
        registerTag("name", "name");
        registerTag("id", "id");
        registerTag("email", "email");
        registerTag("organization", "organization");
        registerTag("url", "url");
        registerTag("timezone", "timezone");
    }

    public PsiProject getParent() {
        return project;
    }

    public String getName(final int pRow) {
        return getValue(pRow, "name");
    }

    public void setName(final int pRow, final String pName) {
        setValue(pRow, "name", pName);
    }

    public String getId(final int pRow) {
        return getValue(pRow, "id");
    }

    public void setId(final int pRow, final String pId) {
        setValue(pRow, "id", pId);
    }

    public String getEmail(final int pRow) {
        return getValue(pRow, "email");
    }

    public void setEmail(final int pRow, final String pEmail) {
        setValue(pRow, "email", pEmail);
    }

    public String getOrganization(final int pRow) {
        return getValue(pRow, "organization");
    }

    public void setOrganization(final int pRow, final String pOrganization) {
        setValue(pRow, "organization", pOrganization);
    }

    public String getUrl(final int pRow) {
        return getValue(pRow, "url");
    }

    public void setUrl(final int pRow, final String pUrl) {
        setValue(pRow, "url", pUrl);
    }

    public String getTimezone(final int pRow) {
        return getValue(pRow, "timezone");
    }

    public void setTimezone(final int pRow, final String pTimezone) {
        setValue(pRow, "timezone", pTimezone);
    }

    public final PsiTeamMemberRoles getRoles(final int pRow) {
        PsiTeamMemberRoles props = rolesCache.get(pRow);
        if (props == null) {
            StringBuilder buf = new StringBuilder(psi.getContainerPath().getPath());
            buf.append('/').append(psi.getRowTagName());
            buf.append('[').append(pRow).append(']');
            buf.append('/').append("roles");

            props = new DefaultPsiTeamMemberRoles(this, buf.toString());
            rolesCache.put(pRow, props);
        }

        return props;
    }
}
