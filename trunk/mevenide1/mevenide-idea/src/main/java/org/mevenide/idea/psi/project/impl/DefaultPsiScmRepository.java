package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.PsiScmRepository;
import org.mevenide.idea.psi.support.AbstractPsiNamedPropertyObservable;

/**
 * @author Arik
 */
public class DefaultPsiScmRepository extends AbstractPsiNamedPropertyObservable
        implements PsiScmRepository {
    private final PsiProject project;

    public DefaultPsiScmRepository(final PsiProject pProject) {
        super(pProject.getXmlFile(), "project/repository");

        project = pProject;
        registerTag("anonymousConnection", "connection");
        registerTag("developerConnection", "developerConnection");
        registerTag("url", "url");
    }

    public PsiProject getParent() {
        return project;
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
