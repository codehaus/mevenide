package org.mevenide.idea.psi.support;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.util.PsiNamedPropertyChangeListener;

/**
 * @author Arik
 */
public abstract class AbstractPsiNamedPropertyObservable
        extends AbstractPsiPropertyObservable<PsiNamedPropertyChangeListener> {
    public AbstractPsiNamedPropertyObservable(final XmlFile pXmlFile) {
        this(pXmlFile, null);
    }

    public AbstractPsiNamedPropertyObservable(final XmlFile pXmlFile,
                                              final String pPrefix) {
        super(pXmlFile, new PsiNamedPropertyChangeListener(pPrefix));
    }

    protected void registerTag(final String pPropertyName, final String pPath) {
        psi.registerTag(pPropertyName, getXmlFile(), pPath);
    }
}
