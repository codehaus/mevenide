package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiResources extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiProject> {
    String getDirectory(int pRow);

    void setDirectory(int pRow, Object pValue);

    String getTargetPath(int pRow);

    void setTargetPath(int pRow, Object pValue);

    PsiResourcePatterns getPatterns(int pRow, PatternType pType);

    PsiResourcePatterns getIncludes(int pRow);

    PsiResourcePatterns getExcludes(int pRow);
}
