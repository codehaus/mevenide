package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.IncorrectOperationException;
import org.mevenide.idea.psi.support.AbstractPsiNamedPropertyObservable;

/**
 * @author Arik
 */
public class PsiOrganization extends AbstractPsiNamedPropertyObservable {
    public PsiOrganization(final XmlFile pXmlFile) {
        super(pXmlFile, "project/organization");

        registerTag("name", "name");
        registerTag("url", "url");
        registerTag("logoUri", "logo");
    }

    public final String getName() {
        return getValue("name");
    }

    public void setName(final String pValue) throws IncorrectOperationException {
        setValue("name", pValue);
    }

    public final String getUrl() {
        return getValue("url");
    }

    public void setUrl(final String pValue) throws IncorrectOperationException {
        setValue("url", pValue);
    }

    public final String getLogoUri() {
        return getValue("logoUri");
    }

    public void setLogoUri(final String pValue) throws IncorrectOperationException {
        setValue("logoUri", pValue);
    }
}
