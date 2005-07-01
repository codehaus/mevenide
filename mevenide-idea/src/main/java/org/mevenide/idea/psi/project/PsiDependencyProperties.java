package org.mevenide.idea.psi.project;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.support.AbstractPsiUnnamedPropertyObservable;

/**
 * @author Arik
 */
public class PsiDependencyProperties extends AbstractPsiUnnamedPropertyObservable {

    /**
     * Creates an instance for the given POM xml file.
     *
     * @param pXmlFile the POM file
     */
    public PsiDependencyProperties(final XmlFile pXmlFile, final int pDependencyRow) {
        super(pXmlFile, "project/dependencies/dependency[" + pDependencyRow + "]/properties");
    }

    public final String getProperty(final String pPropertyName) {
        return getValue(pPropertyName);
    }

    public final void setProperty(final String pPropertyName, final Object pValue) {
        setValue(pPropertyName, pValue);
    }
}
