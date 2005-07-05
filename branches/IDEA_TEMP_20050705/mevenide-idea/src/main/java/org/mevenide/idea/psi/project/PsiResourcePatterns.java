package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiResourcePatterns
        extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiResources> {
    String getPattern(int pRow);

    void setPattern(int pRow, Object pValue);

    String[] getPatterns();

    PatternType getType();
}
