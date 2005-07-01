package org.mevenide.idea.editor.pom.ui.build;

import org.mevenide.idea.editor.pom.ui.support.AbstractResourcesTableModel;
import org.mevenide.idea.psi.project.PsiResources;
import org.mevenide.idea.util.psi.MultiValuedXmlTagRowsTableModel;

/**
 * The table model for resources table.
 *
 * <p>This class extends the standard {@link MultiValuedXmlTagRowsTableModel simple
 * tag-based} model to override the mechanism for extracting the values for the {@code
 * <includes>} and {@code <excludes>} columns.</p>
 *
 * <p>The includes/excludes columns are not editable directly (the text field is not
 * editable) but have a small "Browse" button which pops up a dialog with the {@link
 * org.mevenide.idea.util.ui.StringListEditPanel} which allow the actual editing. When the
 * dialog is closed (with the OK button), the selected patterns are pushed back into the
 * text field. Once the user exits the text field, the values are pushed back into this
 * model.</p>
 *
 * <p>This is required since the includes/excludes fields are not simple valued - they
 * contain themselves a list of {@code <include>} or {@code <exclude>} tags and therefor
 * we need to manually extract these into a string array.</p>
 *
 * @author Arik
 */
public class ResourcesTableModel extends AbstractResourcesTableModel<PsiResources> {
    /**
     * Creates an instance for the given project and document. The model will use the
     * specified container tag name (e.g. {@code build/resources}) since this model can be
     * used to represent resource lists for more than one location (source code resources,
     * test cases build, etc).
     */
    public ResourcesTableModel(final PsiResources pModel) {
        super(pModel);
    }
}
