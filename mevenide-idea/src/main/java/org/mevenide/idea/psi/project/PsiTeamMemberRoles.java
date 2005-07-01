package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class PsiTeamMemberRoles extends AbstractPsiBeanRowsObservable {
    private static final String ROW_TAG_NAME = "role";

    public PsiTeamMemberRoles(final XmlFile pXmlFile,
                              final String pRolesContainerTagPath) {
        super(pXmlFile, pRolesContainerTagPath, ROW_TAG_NAME);
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
