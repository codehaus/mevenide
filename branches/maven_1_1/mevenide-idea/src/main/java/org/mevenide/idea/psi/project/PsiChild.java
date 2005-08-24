package org.mevenide.idea.psi.project;

/**
 * @author Arik
 */
public interface PsiChild<ParentType> {
    ParentType getParent();

}
