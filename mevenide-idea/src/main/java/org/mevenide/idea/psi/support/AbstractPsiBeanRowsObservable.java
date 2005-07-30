package org.mevenide.idea.psi.support;

import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.psi.util.PsiIndexedPropertyChangeListener;
import org.mevenide.idea.psi.util.XmlTagPath;
import org.mevenide.idea.util.event.BeanRowsListener;
import org.mevenide.idea.util.event.BeanRowsObservable;

/**
 * @author Arik
 */
public abstract class AbstractPsiBeanRowsObservable extends AbstractPsiObject
        implements BeanRowsObservable {
    /**
     * The PSI listener used to trigger property change events.
     */
    protected final PsiIndexedPropertyChangeListener psi;

    protected AbstractPsiBeanRowsObservable(final XmlFile pXmlFile,
                                            final String pContainerTagPath,
                                            final String pRowTagName) {
        this(new XmlTagPath(pXmlFile, pContainerTagPath), pRowTagName);
    }

    protected AbstractPsiBeanRowsObservable(final XmlTagPath pContainerPath,
                                            final String pRowTagName) {
        super(pContainerPath.getFile());
        psi = new PsiIndexedPropertyChangeListener(pContainerPath, pRowTagName);
        PsiManager.getInstance(xmlFile.getProject()).addPsiTreeChangeListener(psi);
    }

    public void addBeanRowsListener(final BeanRowsListener pListener) {
        psi.addBeanRowsListener(pListener);
    }

    public void removeBeanRowsListener(final BeanRowsListener pListener) {
        psi.removeBeanRowsListener(pListener);
    }

    protected final void registerTag(final String pPropertyName,
                                     final String pTagPath) {
        psi.registerTag(pPropertyName, xmlFile, pTagPath);
    }

    protected final String[] getValues() {
        return psi.getValues();
    }

    protected final String getValue(final int pRow) {
        return getValue(pRow, null);
    }

    protected final String getValue(final int pRow, final String pPropertyName) {
        return psi.getValue(pRow, pPropertyName);
    }

    protected final void setValue(final int pRow,
                                  final Object pValue) {
        psi.setValue(pRow, pValue);
    }

    protected final void setValue(final int pRow,
                                  final String pPropertyName,
                                  final Object pValue) {
        psi.setValue(pRow, pPropertyName, pValue);
    }

    public final int getRowCount() {
        return psi.getRowCount();
    }

    public final int appendRow() {
        return psi.appendRow();
    }

    public final void deleteRows(final int... pRowIndices) {
        psi.deleteRows(pRowIndices);
    }
}
