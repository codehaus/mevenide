package org.mevenide.idea.util.ui.list;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.util.psi.PsiModifiable;
import org.mevenide.idea.util.psi.PsiSynchronizationListener;
import org.mevenide.idea.util.psi.PsiUtils;

import javax.swing.AbstractListModel;

/**
 * A list model backed by a PSI tree of an XML document.
 *
 * <p>The model refreshes itself when needed via the
 * {@link #refreshModel(org.mevenide.idea.util.psi.PsiEventType, com.intellij.psi.PsiTreeChangeEvent)}
 * method (called by the PSI listener).</p>
 *
 * @author Arik
 */
public abstract class AbstractXmlPsiListModel extends AbstractListModel implements PsiModifiable {
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
     * The project.
     */
    protected final Project project;

    /**
     * The POM file.
     */
    protected final XmlFile xmlFile;

    /**
     * Creates an instance using the given project and document.
     *
     * @param pProject      the project.
     * @param pIdeaDocument the document.
     */
    public AbstractXmlPsiListModel(final Project pProject,
                                   final Document pIdeaDocument) {
        project = pProject;
        xmlFile = PsiUtils.findXmlFile(project, pIdeaDocument);

        //
        //add this as a PSI listener, so that we can update this instance when the
        //PSI changes
        //
        PsiManager.getInstance(project).addPsiTreeChangeListener(
                new PsiSynchronizationListener(xmlFile, this, LOCK));
    }

    public PsiModifiable.ModificationSource getModificationSource() {
        synchronized (LOCK) {
            return modificationSource;
        }
    }

    public void setModificationSource(PsiModifiable.ModificationSource pSource) {
        synchronized (LOCK) {
            modificationSource = pSource;
        }
    }

    /**
     * Completely rebuilds the model from the PSI tree by calling the {@link
     * #refreshModel(org.mevenide.idea.util.psi.PsiEventType,com.intellij.psi.PsiTreeChangeEvent)}
     * method with a {@code null} argument.
     *
     * <p>This method is {@code final} so that only one place will contain synchronization logic
     * (the {@link #refreshModel(org.mevenide.idea.util.psi.PsiEventType,com.intellij.psi.PsiTreeChangeEvent)}
     * method). When implementing it, make sure you take into account that the event parameter might
     * be {@code null}.</p>
     */
    public final void refreshModel() {
        refreshModel(null, null);
    }
}
