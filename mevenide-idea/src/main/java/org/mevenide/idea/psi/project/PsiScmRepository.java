package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.support.AbstractPsiNamedPropertyObservable;

/**
 * @author Arik
 */
public class PsiScmRepository extends AbstractPsiNamedPropertyObservable {
    public PsiScmRepository(final XmlFile pXmlFile) {
        super(pXmlFile, "project/repository");

        registerTag("anonymousConnection", "connection");
        registerTag("developerConnection", "developerConnection");
        registerTag("url", "url");
    }

    public String getAnonymousConnection() {
        return getValue("anonymousConnection");
    }

    public void setAnonymousConnection(final String pAnonymousConnection) {
        setValue("anonymousConnection", pAnonymousConnection);
    }

    public String getDeveloperConnection() {
        return getValue("developerConnection");
    }

    public void setDeveloperConnection(final String pDeveloperConnection) {
        setValue("developerConnection", pDeveloperConnection);
    }

    public String getUrl() {
        return getValue("url");
    }

    public void setUrl(final String pUrl) {
        setValue("url", pUrl);
    }
}
