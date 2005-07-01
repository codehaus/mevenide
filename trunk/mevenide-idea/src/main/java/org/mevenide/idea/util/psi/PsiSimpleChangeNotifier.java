package org.mevenide.idea.util.psi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.PsiTreeChangeListener;
import org.mevenide.idea.psi.util.PsiUtils;

/**
 * A generic PSI listener that will notify a {@link SimplePsiListener} instance when PSI
 * events occur.
 *
 * <p>Basically, this listener is just a pipeline for PSI events onto a different kind of
 * PSI listener (a-ka {@code SimplePsiListener}).</p>
 *
 * <p>The purpose for creating this "redundant" listener preventing recursive event loops:
 * if the user makes a change to the UI components, than Swing notifies the Swing models,
 * which usually update the PSI model. This generates a PSI event which is propagated back
 * to the Swing model (since the Swing model will usually also listen to the PSI) which
 * can result in a recursive behavior. This listener, therefor, supports marking the
 * source of the PSI event (either the UI or the model) and stop the recursive loop.</p>
 *
 * @author Arik
 */
public class PsiSimpleChangeNotifier implements PsiTreeChangeListener {
    /**
     * The XML (PSI) file this listener listens to.
     */
    protected final PsiFile psiFile;

    /**
     * The PSI listener to notify.
     */
    protected final SimplePsiListener listener;

    /**
     * Creates an instance.
     *
     * @param pXmlFile       the XML file we listen to
     * @param pPsiModifiable the PSI listener to notify on events
     */
    public PsiSimpleChangeNotifier(final PsiFile pXmlFile,
                                   final SimplePsiListener pPsiModifiable) {
        psiFile = pXmlFile;
        listener = pPsiModifiable;
    }

    /**
     * Creates an instance.
     *
     * @param pProject       the project context
     * @param pDocument      the document this listener listens to
     * @param pPsiModifiable the PSI listener to notify on events
     */
    public PsiSimpleChangeNotifier(final Project pProject,
                                   final Document pDocument,
                                   final SimplePsiListener pPsiModifiable) {
        this(PsiUtils.findXmlFile(pProject, pDocument), pPsiModifiable);
    }

    /**
     * Notifies, if necessary, the PSI listener about the event.
     *
     * <p>If the event is not {@link #isEventRelevant(com.intellij.psi.PsiTreeChangeEvent)}
     * relevant}, it will be ignored. Otherwise, the {@link SimplePsiListener#refreshModel(PsiEventType,
     * com.intellij.psi.PsiTreeChangeEvent)} method will be called.</p.
     *
     * @param pEventType the event type
     * @param pEvent     the original event object
     */
    private void notifyListener(final PsiEventType pEventType,
                                final PsiTreeChangeEvent pEvent) {

        if (pEvent != null && !isEventRelevant(pEvent))
            return;

        synchronized (psiFile) {
            if (listener.getModificationSource() == ModificationSource.UI)
                return;

            listener.setModificationSource(ModificationSource.EDITOR);
            try {
                listener.refreshModel(pEventType, pEvent);
            }
            finally {
                listener.setModificationSource(null);
            }
        }
    }

    public void childAdded(final PsiTreeChangeEvent pEvent) {
        notifyListener(PsiEventType.CHILD_ADDED, pEvent);
    }

    public void childMoved(final PsiTreeChangeEvent pEvent) {
        notifyListener(PsiEventType.CHILD_MOVED, pEvent);
    }

    public void childRemoved(final PsiTreeChangeEvent pEvent) {
        notifyListener(PsiEventType.CHILD_REMOVED, pEvent);
    }

    public void childReplaced(final PsiTreeChangeEvent pEvent) {
        notifyListener(PsiEventType.CHILD_REPLACED, pEvent);
    }

    public void childrenChanged(final PsiTreeChangeEvent pEvent) {
        notifyListener(PsiEventType.CHILDREN_CHANGED, pEvent);
    }

    public void propertyChanged(final PsiTreeChangeEvent pEvent) {
        notifyListener(PsiEventType.PROPERTY_CHANGED, pEvent);
    }

    public void beforeChildAddition(PsiTreeChangeEvent pEvent) {
    }

    public void beforeChildRemoval(PsiTreeChangeEvent pEvent) {
    }

    public void beforeChildReplacement(PsiTreeChangeEvent pEvent) {
    }

    public void beforeChildMovement(PsiTreeChangeEvent pEvent) {
    }

    public void beforeChildrenChange(PsiTreeChangeEvent pEvent) {
    }

    public void beforePropertyChange(PsiTreeChangeEvent pEvent) {
    }

    /**
     * Used to filter events that are not related to this listener.
     *
     * <p>Unfortunately, PSI listeners are global PSI listeners - they receive ALL psi
     * events in ALL files. Since we are only interested in events pertaining to our
     * {@link #psiFile}, this method can be used to filter such events out.</p>
     *
     * <p>This method can be overriden to further eliminate more events. </p>
     *
     * @param pEvent the event object
     *
     * @return {@code true} if the event is relevant, {@code false} otherwise.
     */
    protected boolean isEventRelevant(final PsiTreeChangeEvent pEvent) {
        final PsiManager mgr = pEvent.getManager();
        if (mgr == null)
            return false;

        final Project project = mgr.getProject();
        if (!project.equals(psiFile.getProject()))
            return false;

        final PsiFile eventFile = pEvent.getFile();
        return eventFile != null && eventFile.equals(psiFile);
    }
}
