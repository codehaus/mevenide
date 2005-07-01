package org.mevenide.idea.editor.pom.ui.team;

import org.mevenide.idea.psi.project.PsiDevelopers;
import org.mevenide.idea.psi.project.PsiProject;

/**
 * @author Arik
 */
public class DevelopersPanel extends AbstractTeamPanel<PsiDevelopers> {

    public DevelopersPanel(final PsiProject pModel) {
        this(pModel.getDevelopers());
    }

    public DevelopersPanel(final PsiDevelopers pModel) {
        super(pModel);
    }
}
