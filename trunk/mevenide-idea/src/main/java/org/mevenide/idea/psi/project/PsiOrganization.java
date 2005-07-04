package org.mevenide.idea.psi.project;

import com.intellij.util.IncorrectOperationException;
import org.mevenide.idea.psi.support.XmlPsiObject;

/**
 * @author Arik
 */
public interface PsiOrganization extends XmlPsiObject, PsiChild<PsiProject> {
    String getName();

    void setName(String pValue) throws IncorrectOperationException;

    String getUrl();

    void setUrl(String pValue) throws IncorrectOperationException;

    String getLogoUri();

    void setLogoUri(String pValue) throws IncorrectOperationException;
}
