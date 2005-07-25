package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiMailingLists extends XmlPsiObject, BeanRowsObservable, PsiChild<PsiProject> {
    String getName(int pRow);

    void setName(int pRow, String pName);

    String getSubscribe(int pRow);

    void setSubscribe(int pRow, String pSubscribe);

    String getUnsubscribe(int pRow);

    void setUnsubscribe(int pRow, String pUnsubscribe);

    String getArchive(int pRow);

    void setArchive(int pRow, String pArchive);
}
