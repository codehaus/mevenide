package org.mevenide.idea.util.psi;

import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlFile;

/**
 * @author Arik
 */
public class PsiSynchronizationListener extends AbstractPsiXmlListener {
    protected final Object lock;

    private final PsiModifiable psiModifiable;

    public PsiSynchronizationListener(final XmlFile pXmlFile,
                                      final PsiModifiable pPsiModifiable,
                                      final Object pLock) {
        super(pXmlFile);
        lock = pLock;
        psiModifiable = pPsiModifiable;
    }

    private void doRefresh(final PsiEventType pEventType, final PsiTreeChangeEvent pEvent) {
        if (pEvent != null && !isEventRelevant(pEvent))
            return;

        synchronized (lock) {
            final PsiModifiable.ModificationSource source = psiModifiable.getModificationSource();
            if (source == PsiModifiable.ModificationSource.UI)
                return;

            psiModifiable.setModificationSource(PsiModifiable.ModificationSource.EDITOR);
            try {
                psiModifiable.refreshModel(pEventType, pEvent);
            }
            finally {
                psiModifiable.setModificationSource(null);
            }
        }
    }

    @Override public void childAdded(final PsiTreeChangeEvent pEvent) {
        doRefresh(PsiEventType.CHILD_ADDED, pEvent);
    }

    @Override public void childMoved(final PsiTreeChangeEvent pEvent) {
        doRefresh(PsiEventType.CHILD_MOVED, pEvent);
    }

    @Override public void childRemoved(final PsiTreeChangeEvent pEvent) {
        doRefresh(PsiEventType.CHILD_REMOVED, pEvent);
    }

    @Override public void childReplaced(final PsiTreeChangeEvent pEvent) {
        doRefresh(PsiEventType.CHILD_REPLACED, pEvent);
    }

    @Override public void childrenChanged(final PsiTreeChangeEvent pEvent) {
        doRefresh(PsiEventType.CHILDREN_CHANGED, pEvent);
    }

    @Override public void propertyChanged(final PsiTreeChangeEvent pEvent) {
        doRefresh(PsiEventType.PROPERTY_CHANGED, pEvent);
    }
}
