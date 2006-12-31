package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiScmBranches;
import org.mevenide.idea.psi.support.AbstractPsiBeanRowsObservable;

/**
 * @author Arik
 */
public class DefaultPsiScmBranches extends AbstractPsiBeanRowsObservable implements PsiScmBranches {
    private static final String CONTAINER_TAG_PATH = "project/branches";
    private static final String ROW_TAG_NAME = "branch";

    private final PsiProject project;

    public DefaultPsiScmBranches(final PsiProject pProject) {
        super(pProject.getXmlFile(), CONTAINER_TAG_PATH, ROW_TAG_NAME);
        project = pProject;
        registerTag("tag", "tag");
    }

    public PsiProject getParent() {
        return project;
    }

    public final String getTag(final int pRow) {
        return getValue(pRow, "tag");
    }

    public final void setTag(final int pRow, final Object pValue) {
        setValue(pRow, "tag", pValue);
    }
}
