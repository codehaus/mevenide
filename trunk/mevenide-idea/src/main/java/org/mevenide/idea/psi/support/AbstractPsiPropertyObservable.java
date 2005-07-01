package org.mevenide.idea.psi.support;

import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import java.beans.PropertyChangeListener;
import org.mevenide.idea.psi.util.PsiPropertyChangeListener;
import org.mevenide.idea.util.event.PropertyObservable;

/**
 * @author Arik
 */
public abstract class AbstractPsiPropertyObservable<Psi extends PsiPropertyChangeListener>
    extends AbstractPsiObject
    implements PropertyObservable {
    /**
     * The PSI listener used to trigger property change events.
     */
    protected final Psi psi;

    /**
     * Creates a new instance for the given XML file, using the given PSI handler.
     *
     * @param pXmlFile the XML file to track
     */
    protected AbstractPsiPropertyObservable(final XmlFile pXmlFile,
                                            final Psi pPsi) {
        super(pXmlFile);
        psi = pPsi;
        PsiManager.getInstance(xmlFile.getProject()).addPsiTreeChangeListener(psi);
    }

    public void addPropertyChangeListener(final String pPropertyName,
                                          final PropertyChangeListener listener) {
        psi.addPropertyChangeListener(pPropertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        psi.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final String pPropertyName,
                                             final PropertyChangeListener listener) {
        psi.removePropertyChangeListener(pPropertyName, listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        psi.removePropertyChangeListener(listener);
    }

    protected final String getValue(final String pPropertyName) {
        return psi.getValue(pPropertyName);
    }

    protected final void setValue(final String pPropertyName, final Object pValue) {
        psi.setValue(pPropertyName, pValue);
    }
}
