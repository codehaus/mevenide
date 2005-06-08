package org.mevenide.idea.util.psi;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeEvent;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.table.AbstractTableModel;
import org.mevenide.idea.util.IDEUtils;

/**
 * A superclass for XML based (via PSI) table models.
 *
 * @author Arik
 */
public abstract class AbstractPsiTableModel<PsiFileType extends PsiFile> extends AbstractTableModel
                                                                                                    implements SimplePsiListener {
    /**
     * The POM file.
     */
    protected final PsiFileType psiFile;

    /**
     * The PSI agent that will notify this model of PSI events.
     */
    protected final PsiSimpleChangeNotifier psiSynchronizationListener;

    /**
     * The source of the current PSI or UI event. Used to prevent infinite loops between the PSI listener and the UI
     * model code.
     */
    private final AtomicReference<ModificationSource> modificationSource =
        new AtomicReference<ModificationSource>(null);

    /**
     * Creates an instance using the given parameters.
     *
     * @param pPsiFile the PSI file backing this model
     */
    protected AbstractPsiTableModel(final PsiFileType pPsiFile) {
        psiFile = pPsiFile;
        psiSynchronizationListener = new PsiSimpleChangeNotifier(psiFile,
                                                                 this);

        //register this instance as a PSI listener, so the model will be
        //able to update itself if the user/other-components makes changes
        //to the document
        final PsiManager psiMgr = PsiManager.getInstance(psiFile.getProject());
        psiMgr.addPsiTreeChangeListener(psiSynchronizationListener);
    }

    public final ModificationSource getModificationSource() {
        return modificationSource.get();
    }

    public final void setModificationSource(final ModificationSource pSource) {
        modificationSource.set(pSource);
    }

    /**
     * Called by the view component (the {@link javax.swing.JTable}) when the user sets a value on the ui.
     *
     * <p>This method will make sure the call did not originate from the PSI listener (as that would cause an infinite
     * loop), obtain a write lock, and call the abstract {@link #setValueAtInternal(Object, int, int)} method to
     * actually apply the new value in this model.</p>
     *
     * @param pValue  the new value
     * @param pRow    the row the value was set
     * @param pColumn the column the value was set
     */
    public final void setValueAt(final Object pValue,
                                 final int pRow,
                                 final int pColumn) {
        if (!modificationSource.compareAndSet(null, ModificationSource.EDITOR))
            return;

        try {
            IDEUtils.runCommand(psiFile.getProject(), new Runnable() {
                public void run() {
                    setValueAtInternal(pValue, pRow, pColumn);
                }
            });
        }
        finally {
            modificationSource.set(null);
        }
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    /**
     * This method must perform the actual update to the PSI tree, applying the new value.
     *
     * <p>This method is called by the {@link #setValueAt(Object, int, int)} method. Usually implementors would find the
     * appropriate PSI element to update and set its value.</p>
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
     * <p>This method is {@code final} so that only one place will contain synchronization logic (the {@link
     * #refreshModel(org.mevenide.idea.util.psi.PsiEventType,com.intellij.psi.PsiTreeChangeEvent)} method). When
     * implementing it, make sure you take into account that the event parameter might be {@code null}.</p>
     */
    public final void refreshModel() {
        refreshModel(null, null);
    }

    public void refreshModel(final PsiEventType pEventType,
                             final PsiTreeChangeEvent pEvent) {
        fireTableDataChanged();
    }

    /**
     * Returns the type of the specified column.
     *
     * <p>By default, this implementation always return {@code String.class}, but you can override and return other
     * classes instead.</p>
     *
     * @param pColumn the column to get the class for
     * @return class
     */
    @Override
    public Class<?> getColumnClass(final int pColumn) {
        return String.class;
    }
}
