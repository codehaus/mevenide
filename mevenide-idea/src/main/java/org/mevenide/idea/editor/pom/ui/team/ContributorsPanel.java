package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.psi.project.PsiContributors;
import org.mevenide.idea.psi.project.PsiProject;

/**
 * @author Arik
 */
public class ContributorsPanel extends AbstractTeamPanel<PsiContributors> {

    public ContributorsPanel(final PsiProject pModel) {
        this(pModel.getContributors());
    }

    public ContributorsPanel(final PsiContributors pModel) {
        super(pModel);
    }
}
