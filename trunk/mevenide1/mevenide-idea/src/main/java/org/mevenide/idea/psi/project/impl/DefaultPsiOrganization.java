package org.mevenide.idea.psi.project.impl;

import com.intellij.util.IncorrectOperationException;
import org.mevenide.idea.psi.project.PsiOrganization;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.support.AbstractPsiNamedPropertyObservable;

/**
 * @author Arik
 */
public class DefaultPsiOrganization extends AbstractPsiNamedPropertyObservable
        implements PsiOrganization {
    private final PsiProject project;

    public DefaultPsiOrganization(final PsiProject pProject) {
        super(pProject.getXmlFile(), "project/organization");
        project = pProject;
        registerTag("name", "name");
        registerTag("url", "url");
        registerTag("logoUri", "logo");
    }

    public PsiProject getParent() {
        return project;
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
