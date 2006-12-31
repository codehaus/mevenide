package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiVersions extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiProject> {
    String getId(int pRow);

    void setId(int pRow, String pId);

    String getName(int pRow);

    void setName(int pRow, String pName);

    String getTag(int pRow);

    void setTag(int pRow, String pTag);
}
