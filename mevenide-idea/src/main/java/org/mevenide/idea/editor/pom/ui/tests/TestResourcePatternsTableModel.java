package org.mevenide.idea.editor.pom.ui.tests;

import org.mevenide.idea.editor.pom.ui.support.AbstractResourcePatternsTableModel;
import org.mevenide.idea.psi.project.PsiTestResourcePatterns;

/**
 * @author Arik
 */
public class TestResourcePatternsTableModel
    extends AbstractResourcePatternsTableModel<PsiTestResourcePatterns> {
    public TestResourcePatternsTableModel(final PsiTestResourcePatterns pModel) {
        super(pModel);
    }

}
