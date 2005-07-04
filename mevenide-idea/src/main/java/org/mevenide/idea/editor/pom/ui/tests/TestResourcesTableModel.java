package org.mevenide.idea.editor.pom.ui.tests;

import org.mevenide.idea.editor.pom.ui.support.AbstractResourcesTableModel;
import org.mevenide.idea.psi.project.PsiResources;

/**
 * @author Arik
 */
public class TestResourcesTableModel
        extends AbstractResourcesTableModel {
    /**
     * Creates an instance for the given project and document. The model will use the specified
     * container tag name (e.g. {@code build/resources}) since this model can be used to represent
     * resource lists for more than one location (source code resources, test cases build, etc).
     */
    public TestResourcesTableModel(final PsiResources pModel) {
        super(pModel);
    }

}
