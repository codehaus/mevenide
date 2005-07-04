package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiScmBranches extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiProject> {
    String getTag(int pRow);

    void setTag(int pRow, Object pValue);
}
