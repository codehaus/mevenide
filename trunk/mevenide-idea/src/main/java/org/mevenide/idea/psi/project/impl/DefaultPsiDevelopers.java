package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiDevelopers;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.support.AbstractPsiTeamMembers;

/**
 * @author Arik
 */
public class DefaultPsiDevelopers extends AbstractPsiTeamMembers implements PsiDevelopers {
    private static final String CONTAINER_TAG_PATH = "project/developers";
    private static final String ROW_TAG_NAME = "developer";

    public DefaultPsiDevelopers(final PsiProject pProject) {
        super(pProject, CONTAINER_TAG_PATH, ROW_TAG_NAME);
    }

}
