package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public interface PsiDependencies extends BeanRowsObservable, XmlPsiObject, PsiChild<PsiProject> {
    String getGroupId(int pRow);

    void setGroupId(int pRow, String pGroupId);

    String getArtifactId(int pRow);

    void setArtifactId(int pRow, String pArtifactId);

    String getVersion(int pRow);

    void setVersion(int pRow, String pVersion);

    String getType(int pRow);

    void setType(int pRow, String pType);

    String getUrl(int pRow);

    void setUrl(int pRow, String pUrl);

    PsiDependencyProperties getProperties(int pRow);
}
