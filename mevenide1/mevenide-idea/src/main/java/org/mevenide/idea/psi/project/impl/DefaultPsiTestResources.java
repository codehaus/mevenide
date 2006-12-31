package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PatternType;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiResourcePatterns;
import org.mevenide.idea.psi.project.support.AbstractPsiResources;

/**
 * @author Arik
 */
public class DefaultPsiTestResources extends AbstractPsiResources {
    private static final String CONTAINER_TAG_PATH = "project/build/unitTest";

    public DefaultPsiTestResources(final PsiProject pProject) {
        super(pProject, CONTAINER_TAG_PATH);
    }

    protected PsiResourcePatterns createPsiResourcePatterns(final int pRow,
                                                            final PatternType pType) {
        return new DefaultPsiTestResourcePatterns(this, pRow, pType);
    }
}
