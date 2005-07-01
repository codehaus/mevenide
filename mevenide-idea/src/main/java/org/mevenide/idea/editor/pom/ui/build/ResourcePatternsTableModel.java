package org.mevenide.idea.editor.pom.ui.build;

import org.mevenide.idea.psi.project.PsiResourcePatterns;
import org.mevenide.idea.editor.pom.ui.support.AbstractResourcePatternsTableModel;

/**
 * @author Arik
 */
public class ResourcePatternsTableModel extends AbstractResourcePatternsTableModel<PsiResourcePatterns> {

    public ResourcePatternsTableModel(final PsiResourcePatterns pModel) {
        super(pModel);
    }

}
