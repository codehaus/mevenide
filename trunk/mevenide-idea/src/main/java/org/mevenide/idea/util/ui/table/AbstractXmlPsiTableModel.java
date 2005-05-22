package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.psi.AbstractPsiXmlListener;
import org.mevenide.idea.util.psi.PsiUtils;

import javax.swing.table.AbstractTableModel;

/**
 * @author Arik
 */
public abstract class AbstractXmlPsiTableModel extends AbstractTableModel {
    /**
     * An enum specifying the types of events raised by the PSI tree. One of these enums is passed
     * to the {@link AbstractXmlPsiTableModel#refreshModel(PsiEventType,PsiTreeChangeEvent)} so that
     * it can distinguish which event triggered the synchronization (if desired).
     */
    protected static enum PsiEventType {
        CHILD_ADDED,
        CHILD_MOVED,
        CHILD_REPLACED,
        CHILD_REMOVED,
        CHILDREN_CHANGED,
        PROPERTY_CHANGED
    }

    /**
     * An enum for specifying what is the source of current event. When the modifies the UI (e.g.
     * not from the text editor), the {@link org.mevenide.idea.editor.pom.ui.layer.dependencies.PomDependenciesTableModel#modificationSource}
     * field will be set to {@link #UI}. If the user modifies using the text editor, the field will
     * be set to {@link #EDITOR}.
     *
     * <p>This is done because the code responding to UI modifications updates the PSI tree, which
     * invokes the code responding to PSI modifications, which updates the UI - this can cause an
     * infinite loop, so we need to know who started the loop to avoid it.</p>
     */
    protected static enum ModificationSource { UI, EDITOR }

    /**
     * Used to synchronize between the PSI and UI listeners.
     */
    protected final Object LOCK = new Object();

    /**
     * The source of the current PSI or UI event. Used to prevent infinite loops between the PSI
     * listener and the UI model code.
     */
    private ModificationSource modificationSource = null;

    /**
     * The POM file.
     */
    protected final XmlFile xmlFile;

    /**
     * The project.
     */
    protected final Project project;

    /**
     * The listener that synchronizes PSI changes with this model.
     */
    private final PsiSynchronizationListener psiListener;

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public AbstractXmlPsiTableModel(final Project pProject, final Document pIdeaDocument) {
        project = pProject;
        xmlFile = PsiUtils.findXmlFile(project, pIdeaDocument);
        psiListener = new PsiSynchronizationListener();

        //
        //add this as a PSI listener, so that we can update this instance when the
        //PSI changes
        //
        PsiManager.getInstance(project).addPsiTreeChangeListener(psiListener);
    }

    /**
     * Disposes this component.
     *
     * @throws Throwable
     * @todo currently we unregister this instance as a PSI listener, but this method will never be
     * called until we unregister, so this is a paradox. We must move this to a different place.
     */
    @Override protected void finalize() throws Throwable {
        super.finalize();
        PsiManager.getInstance(project).removePsiTreeChangeListener(psiListener);
    }

    /**
     * Called by the view component (the {@link javax.swing.JTable}) when the user sets a value on
     * the ui.
     *
     * <p>This method will make sure the call did not originate from the PSI listener (as that would
     * cause an infinite loop), obtain a write lock, and call the abstract {@link
     * #setValueAtInternal(Object, int, int)} method to actually apply the new value in this
     * model.</p>
     *
     * @param pValue  the new value
     * @param pRow    the row the value was set
     * @param pColumn the column the value was set
     */
    @Override public final void setValueAt(final Object pValue,
                                           final int pRow,
                                           final int pColumn) {
        synchronized (LOCK) {
            if (modificationSource == ModificationSource.EDITOR)
                return;

            modificationSource = ModificationSource.UI;
            try {
                setValueAtInternal(pValue, pRow, pColumn);
            }
            finally {
                modificationSource = null;
            }
        }
    }

    /**
     * This method must perform the actual update to the PSI tree, applying the new value.
     *
     * <p>This method is called by the {@link #setValueAt(Object, int, int)} method. Usually
     * implementors would find the appropriate PSI element to update and set its value.</p>
     *
     * @param pValue  the new value
     * @param pRow    the row the value was set
     * @param pColumn the column the value was set
     */
    protected abstract void setValueAtInternal(final Object pValue,
                                               final int pRow,
                                               final int pColumn);

    /**
     * Completely rebuilds the model from the PSI tree by calling the {@link #refreshModel(PsiEventType,com.intellij.psi.PsiTreeChangeEvent)}
     * method with a {@code null} argument.
     *
     * <p>This method is {@code final} so that only one place will contain synchronization logic
     * (the {@link #refreshModel(PsiEventType,com.intellij.psi.PsiTreeChangeEvent)} method). When implementing
     * it, make sure you take into account that the event parameter might be {@code null}.</p>
     */
    protected final void refreshModel() {
        refreshModel(null, null);
    }

    /**
     * This method must synchronize this model with the PSI model, where the PSI model acts as the
     * source and this model as the destination.
     *
     * <p>A possible approach is to completely build this model's values from the PSI tree, or the
     * incremently apply changes from the supplied PSI event object.</p>
     *
     * <p>If the {@code pEvent} parameter is {@code null}, then a complete rebuild of the model
     * from the PSI tree (accessible via the protected {@link #xmlFile} field) should be performed.
     * </p>
     *
     * @param pEvent the PSI event object. This <i>may</i> be {@code null}
     */
    protected abstract void refreshModel(PsiEventType pEventType, PsiTreeChangeEvent pEvent);

    /**
     * Listens to PSI changes and calls {@link AbstractXmlPsiTableModel#refreshModel(PsiEventType,com.intellij.psi.PsiTreeChangeEvent)}
     * to synchronize the model.
     */
    private class PsiSynchronizationListener extends AbstractPsiXmlListener {
        public PsiSynchronizationListener() {
            super(AbstractXmlPsiTableModel.this.xmlFile);
        }

        private void doRefresh(final PsiEventType pEventType, final PsiTreeChangeEvent pEvent) {
            if(pEvent != null && !isEventRelevant(pEvent))
                return;

            synchronized (LOCK) {
                if (modificationSource == ModificationSource.UI)
                    return;

                modificationSource = ModificationSource.EDITOR;
                try {
                    refreshModel(pEventType, pEvent);
                }
                finally {
                    modificationSource = null;
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
}
