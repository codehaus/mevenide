package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiContributors;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.support.AbstractPsiTeamMembers;

/**
 * @author Arik
 */
public class DefaultPsiContributors extends AbstractPsiTeamMembers implements PsiContributors {
    private static final String CONTAINER_TAG_PATH = "project/contributors";
    private static final String ROW_TAG_NAME = "contributor";

    public DefaultPsiContributors(final PsiProject pProject) {
        super(pProject, CONTAINER_TAG_PATH, ROW_TAG_NAME);
    }
}
