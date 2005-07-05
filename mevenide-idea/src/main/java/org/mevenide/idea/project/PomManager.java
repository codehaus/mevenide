package org.mevenide.idea.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.xml.XmlFile;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.psi.project.PsiProject;
import org.mevenide.idea.psi.project.impl.DefaultPsiProject;
import org.mevenide.idea.psi.util.PsiUtils;
import org.mevenide.idea.util.ui.images.Icons;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import javax.swing.event.EventListenerList;

/**
 * @author Arik
 */
public class PomManager extends AbstractProjectComponent implements VirtualFilePointerListener {
    /**
     * The key under which the POM entries are stored in the file pointers.
     */
    private static final Key<PsiProject> POM_KEY = Key.create(PsiProject.class.getName());

    /**
     * Tracks file pointers.
     */
    private final VirtualFilePointerHelper helper = new VirtualFilePointerHelper(this);

    /**
     * Event listeners support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Creates an instance for the given project. Called by IDEA on project load.
     *
     * @param pProject the project this component will serve
     */
    public PomManager(final Project pProject) {
        super(pProject);
    }

    public void add(final VirtualFile pFile) {
        add(pFile.getUrl());
    }

    public void add(final String pUrl) {
        final VirtualFilePointer pointer = helper.add(pUrl);
        firePomAddedEvent(pointer);
    }

    public PsiProject getPsiProject(final VirtualFile pFile) {
        return getPsiProject(pFile.getUrl());
    }

    public PsiProject getPsiProject(final String pUrl) {
        final VirtualFilePointer filePointer = helper.get(pUrl);
        if (filePointer == null || !filePointer.isValid())
            return null;

        final VirtualFile file = filePointer.getFile();
        if (file == null || !file.isValid() || file.isDirectory())
            return null;

        PsiProject psi = filePointer.getUserData(POM_KEY);
        if (psi == null || !file.equals(psi.getXmlFile().getVirtualFile())) {
            final XmlFile xmlFile = PsiUtils.findXmlFile(project, file);
            assert xmlFile != null;
            psi = new DefaultPsiProject(xmlFile);
        }

        return psi;
    }

    public void remove(final VirtualFile pFile) {
        remove(pFile.getUrl());
    }

    public void remove(final String pUrl) {
        final VirtualFilePointer pointer = helper.remove(pUrl);
        firePomRemovedEvent(pointer);
    }

    /**
     * Registers the given listener for POM events.
     *
     * @param pListener the listener
     */
    public void addPomManagerListener(final PomManagerListener pListener) {
        listenerList.add(PomManagerListener.class, pListener);
    }

    /**
     * Unregisters the given listener from POM events.
     *
     * @param pListener the listener
     */
    public void removePomManagerListener(final PomManagerListener pListener) {
        listenerList.remove(PomManagerListener.class, pListener);
    }

    public void beforeValidityChanged(VirtualFilePointer[] pointers) {
    }

    /**
     * Activated by IDEA when one or more of the registered POM files changed validity.
     *
     * <p>This method will notify all registered {@link PomManagerListener POM listeners} that the
     * specified files have changed validitiy.</p>
     *
     * @param pPointers
     */
    public void validityChanged(final VirtualFilePointer[] pPointers) {
        for (VirtualFilePointer pointer : pPointers)
            firePomValidityChangedEvent(pointer);
    }

    @Override
    public void projectOpened() {
        final ToolWindowManager twMgr = ToolWindowManager.getInstance(project);
        final PomManagerPanel ui = new PomManagerPanel(project);
        final ToolWindow tw = twMgr.registerToolWindow(PomManagerPanel.TITLE,
                                                       ui,
                                                       ToolWindowAnchor.RIGHT);
        tw.setIcon(Icons.MAVEN);
    }

    @Override
    public void projectClosed() {
        final ToolWindowManager twMgr = ToolWindowManager.getInstance(project);
        twMgr.unregisterToolWindow(PomManagerPanel.TITLE);
    }

    /**
     * Returns the POM manager's tool window.
     *
     * @return the tool window
     */
    public ToolWindow getToolWindow() {
        final ToolWindowManager mgr = ToolWindowManager.getInstance(project);
        return mgr.getToolWindow(PomManagerPanel.TITLE);
    }

    /**
     * Fires the {@link PomManagerListener#pomAdded(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomAddedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null) {
                event = new PomManagerEvent(this, pPointer);
            }
            listener.pomAdded(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomRemoved(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomRemovedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPointer);
            listener.pomRemoved(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomValidityChanged(PomManagerEvent)} event.
     *
     * @param pPointer the file pointer of the modified POM
     */
    protected void firePomValidityChangedEvent(final VirtualFilePointer pPointer) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPointer);
            listener.pomValidityChanged(event);
        }
    }

    /**
     * Returns the PSI-POM manager for the specified project.
     *
     * @param pProject the project to retrieve the component for
     *
     * @return instance
     */
    public static PomManager getInstance(final Project pProject) {
        return pProject.getComponent(PomManager.class);
    }
}
