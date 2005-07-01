package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;
import com.intellij.psi.xml.XmlFile;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author Arik
 */
public abstract class AbstractPsiTeamMembers extends AbstractPsiBeanRowsObservable {

    private final Map<Integer, PsiTeamMemberRoles> rolesCache = Collections.synchronizedMap(
        new HashMap<Integer, PsiTeamMemberRoles>(10));

    protected AbstractPsiTeamMembers(final XmlFile pXmlFile,
                                     final String pContainerTagPath,
                                     final String pRowTagName) {
        super(pXmlFile, pContainerTagPath, pRowTagName);
        registerTag("name", "name");
        registerTag("id", "id");
        registerTag("email", "email");
        registerTag("organization", "organization");
        registerTag("url", "url");
        registerTag("timezone", "timezone");
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

            props = new PsiTeamMemberRoles(getXmlFile(), buf.toString());
            rolesCache.put(pRow, props);
        }

        return props;
    }
}
