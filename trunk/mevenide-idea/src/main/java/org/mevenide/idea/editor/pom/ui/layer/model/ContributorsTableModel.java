package org.mevenide.idea.editor.pom.ui.layer.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.editor.Document;

/**
 * @author Arik
 */
public class ContributorsTableModel extends DevelopersTableModel {

    public ContributorsTableModel(final Project pProject, final Document pIdeaDocument) {
        super(pProject, pIdeaDocument, "contributors", "contributor");
    }
}
