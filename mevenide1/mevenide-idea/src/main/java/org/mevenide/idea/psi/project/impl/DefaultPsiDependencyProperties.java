package org.mevenide.idea.psi.project.impl;

import org.mevenide.idea.psi.project.PsiDependencies;
import org.mevenide.idea.psi.project.PsiDependencyProperties;
import org.mevenide.idea.psi.support.AbstractPsiUnnamedPropertyObservable;

/**
 * @author Arik
 */
public class DefaultPsiDependencyProperties extends AbstractPsiUnnamedPropertyObservable
        implements PsiDependencyProperties {
    private final PsiDependencies dependencies;

    /**
     * Creates an instance for the given POM xml file.
     *
     * @param pXmlFile the POM file
     */
    public DefaultPsiDependencyProperties(final PsiDependencies pDependencies,
                                          final int pDependencyRow) {
        super(pDependencies.getXmlFile(),
              "project/dependencies/dependency[" + pDependencyRow + "]/properties");
        dependencies = pDependencies;
    }

    public PsiDependencies getParent() {
        return dependencies;
    }

    public final String getProperty(final String pPropertyName) {
        return getValue(pPropertyName);
    }

    public final void setProperty(final String pPropertyName, final Object pValue) {
        setValue(pPropertyName, pValue);
    }
}
