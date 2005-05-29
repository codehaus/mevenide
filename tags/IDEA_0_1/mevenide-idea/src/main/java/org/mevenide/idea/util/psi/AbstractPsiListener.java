package org.mevenide.idea.util.psi;

import com.intellij.psi.*;
import com.intellij.openapi.project.Project;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Arik
 */
public abstract class AbstractPsiListener extends PsiTreeChangeAdapter {
    private static final Log LOG = LogFactory.getLog(AbstractPsiListener.class);

    protected final PsiFile psiFile;

    protected AbstractPsiListener(final PsiFile pPsiFile) {
        psiFile = pPsiFile;
    }

    protected boolean isEventRelevant(final PsiTreeChangeEvent pEvent) {
        final PsiManager mgr = pEvent.getManager();
        if(mgr == null)
            return false;

        final Project project = mgr.getProject();
        if(!project.equals(psiFile.getProject()))
            return false;

        final PsiFile eventFile = pEvent.getFile();
        return eventFile != null && eventFile.equals(psiFile);
    }

    protected CharSequence toDebugString(final Object pElt) {
        return toDebugString(pElt, true);
    }

    protected CharSequence toDebugString(final Object pElt, final boolean pDeep) {
        if(pElt == null)
            return null;

        final StringBuilder buf = new StringBuilder(pElt.toString());
        buf.append(" [");
        if(pElt instanceof PsiElement)
            appendProperties(buf, ((PsiElement) pElt), pDeep);
        buf.append(']');

        return buf;
    }

    protected final void appendProperties(final StringBuilder buf, final PsiElement pElt) {
        appendProperties(buf, pElt, false);
    }

    protected void appendProperties(final StringBuilder buf,
                                    final PsiElement pElt,
                                    final boolean pDeep) {
        if(pDeep)
            buf.append("parent=").append(toDebugString(pElt.getParent())).append(",");
        else
            buf.append("parent=").append(pElt.getParent()).append(",");

        buf.append("startOffsetInParent=").append(pElt.getStartOffsetInParent()).append(",");
        buf.append("text=").append(pElt.getText()).append(",");
        buf.append("textOffset=").append(pElt.getTextOffset());
    }

    protected void dumpEventToLog(final String pEvent, final PsiTreeChangeEvent event) {
        LOG.trace(pEvent + " =============================================================================================");
        LOG.trace(pEvent + " Element: " + toDebugString(event.getElement()));
        LOG.trace(pEvent + " Child: " + toDebugString(event.getChild()));
        LOG.trace(pEvent + " Old child: " + toDebugString(event.getOldChild()));
        LOG.trace(pEvent + " New child: " + toDebugString(event.getNewChild()));
        LOG.trace(pEvent + " Parent: " + toDebugString(event.getParent()));
        LOG.trace(pEvent + " Old parent: " + toDebugString(event.getOldParent()));
        LOG.trace(pEvent + " New parent: " + toDebugString(event.getNewParent()));
        LOG.trace(pEvent + " Old value: " + toDebugString(event.getOldValue()));
        LOG.trace(pEvent + " New value: " + toDebugString(event.getNewValue()));
        LOG.trace(pEvent + " =============================================================================================");
    }
}
