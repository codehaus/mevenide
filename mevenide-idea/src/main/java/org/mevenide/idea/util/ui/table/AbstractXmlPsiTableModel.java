package org.mevenide.idea.util.ui.table;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.psi.*;

import javax.swing.table.AbstractTableModel;

/**
 * @author Arik
 */
public abstract class AbstractXmlPsiTableModel extends AbstractTableModel implements PsiModifiable {

    /**
     * Used to synchronize between the PSI and UI listeners.
     */
    protected final Object LOCK = new Object();

    /**
     * The source of the current PSI or UI event. Used to prevent infinite loops between the PSI
     * listener and the UI model code.
     */
    private PsiModifiable.ModificationSource modificationSource = null;

    /**
     * The POM file.
     */
    protected final XmlFile xmlFile;

    /**
     * The project.
     */
    protected final Project project;

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public AbstractXmlPsiTableModel(final Project pProject, final Document pIdeaDocument) {
        project = pProject;
        xmlFile = PsiUtils.findXmlFile(project, pIdeaDocument);

        //
        //add this as a PSI listener, so that we can update this instance when the
        //PSI changes
        //
        PsiManager.getInstance(project).addPsiTreeChangeListener(
                new PsiSynchronizationListener(xmlFile, this, LOCK));
    }

    public ModificationSource getModificationSource() {
        synchronized (LOCK) {
            return modificationSource;
        }
    }

    public void setModificationSource(ModificationSource pSource) {
        synchronized (LOCK) {
            modificationSource = pSource;
        }
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
     * Completely rebuilds the model from the PSI tree by calling the {@link #refreshModel(org.mevenide.idea.util.psi.PsiEventType,com.intellij.psi.PsiTreeChangeEvent)}
     * method with a {@code null} argument.
     *
     * <p>This method is {@code final} so that only one place will contain synchronization logic
     * (the {@link #refreshModel(PsiEventType,PsiTreeChangeEvent)} method). When implementing
     * it, make sure you take into account that the event parameter might be {@code null}.</p>
     */
    public final void refreshModel() {
        refreshModel(null, null);
    }
}
