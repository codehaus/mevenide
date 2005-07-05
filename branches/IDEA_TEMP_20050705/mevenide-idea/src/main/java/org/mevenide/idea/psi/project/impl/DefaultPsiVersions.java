package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiVersions;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class DefaultPsiVersions extends AbstractPsiBeanRowsObservable implements PsiVersions {
    private final PsiProject project;

    public DefaultPsiVersions(final PsiProject pProject) {
        super(pProject.getXmlFile(), "project/versions", "version");
        project = pProject;
        registerTag("id", "id");
        registerTag("name", "name");
        registerTag("tag", "tag");
    }

    public PsiProject getParent() {
        return project;
    }

    public String getId(final int pRow) {
        return getValue(pRow, "id");
    }

    public void setId(final int pRow, final String pId) {
        setValue(pRow, "id", pId);
    }

    public String getName(final int pRow) {
        return getValue(pRow, "name");
    }

    public void setName(final int pRow, final String pName) {
        setValue(pRow, "name", pName);
    }

    public String getTag(final int pRow) {
        return getValue(pRow, "tag");
    }

    public void setTag(final int pRow, final String pTag) {
        setValue(pRow, "tag", pTag);
    }
}
