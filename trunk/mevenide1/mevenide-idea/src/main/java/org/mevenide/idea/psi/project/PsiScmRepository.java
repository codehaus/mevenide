package org.mevenide.idea.psi.project;

import org.mevenide.idea.psi.support.XmlPsiObject;

/**
 * @author Arik
 */
public interface PsiScmRepository extends XmlPsiObject, PsiChild<PsiProject> {
    String getAnonymousConnection();

    void setAnonymousConnection(String pAnonymousConnection);

    String getDeveloperConnection();

    void setDeveloperConnection(String pDeveloperConnection);

    String getUrl();

    void setUrl(String pUrl);
}
