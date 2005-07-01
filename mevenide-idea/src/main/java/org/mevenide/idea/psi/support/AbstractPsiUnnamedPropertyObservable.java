package org.mevenide.idea.psi.support;

import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.util.PsiUnnamedPropertyChangeListener;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.event.BeanRowsListener;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public abstract class AbstractPsiUnnamedPropertyObservable extends AbstractPsiPropertyObservable<PsiUnnamedPropertyChangeListener> implements BeanRowsObservable {

    public AbstractPsiUnnamedPropertyObservable(final XmlFile pXmlFile,
                                                final String pContainerPath) {
        this(new XmlTagPath(pXmlFile, pContainerPath));
    }

    public AbstractPsiUnnamedPropertyObservable(final XmlTagPath pContainerPath) {
        super(pContainerPath.getFile(),
              new PsiUnnamedPropertyChangeListener(pContainerPath));
    }

    public void addBeanRowsListener(BeanRowsListener pListener) {
        psi.addBeanRowsListener(pListener);
    }

    public void removeBeanRowsListener(BeanRowsListener pListener) {
        psi.removeBeanRowsListener(pListener);
    }

    public int getRowCount() {
        return psi.getRowCount();
    }

    public int appendRow() {
        return psi.appendRow();
    }

    public void deleteRows(int... pRowIndices) {
        psi.deleteRows(pRowIndices);
    }

    public final String[] getPropertyNames() {
        return psi.getPropertyNames();
    }

    public final void renameProperty(final String pPropertyName,
                                     final String pNewPropertyName) {
        psi.renameProperty(pPropertyName, pNewPropertyName);
    }

    public final String getUnknownPropertyName() {
        return psi.getUnknownPropertyName();
    }
}
