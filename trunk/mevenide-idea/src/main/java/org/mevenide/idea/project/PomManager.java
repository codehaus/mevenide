package org.mevenide.idea.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerContainer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import java.util.List;
import javax.swing.event.EventListenerList;
import org.jdom.Element;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class PomManager extends AbstractProjectComponent
        implements JDOMExternalizable, VirtualFilePointerListener {
    /**
     * The map of file pointers to acquire their associated PSI projects.
     */
    protected final VirtualFilePointerContainer entries = VirtualFilePointerManager.getInstance().createContainer(
            this);

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

    public void add(final String pPomUrl) {
        if (pPomUrl == null || pPomUrl.trim().length() == 0 || contains(pPomUrl))
            return;

        entries.add(pPomUrl);
        firePomAddedEvent(pPomUrl);
    }

    public boolean contains(final String pPomUrl) {
        if (pPomUrl == null || pPomUrl.trim().length() == 0)
            return false;

        return entries.findByUrl(pPomUrl) != null;
    }

    public boolean isValid(final String pPomUrl) {
        if (pPomUrl == null || pPomUrl.trim().length() == 0)
            return false;

        if (!contains(pPomUrl))
            return false;

        final VirtualFilePointer pointer = getPointer(pPomUrl);
        return pointer != null && pointer.isValid() && pointer.getFile() != null;
    }

    public VirtualFile getFile(final String pPomUrl) {
        if (pPomUrl == null || pPomUrl.trim().length() == 0)
            return null;

        final VirtualFilePointer pointer = getPointer(pPomUrl);
        if (pointer == null || !pointer.isValid())
            return null;

        return pointer.getFile();
    }

    public VirtualFilePointer getPointer(final String pPomUrl) {
        if (pPomUrl == null || pPomUrl.trim().length() == 0)
            return null;

        return entries.findByUrl(pPomUrl);
    }

    public String[] getFileUrls() {
        return entries.getUrls();
    }

    public VirtualFilePointer[] getFilePointers() {
        final List<VirtualFilePointer> list = entries.getList();
        return list.toArray(new VirtualFilePointer[list.size()]);
    }

    public void remove(final String pPomUrl) {
        if (pPomUrl == null || pPomUrl.trim().length() == 0 || !contains(pPomUrl))
            return;

        final VirtualFilePointer pointer = getPointer(pPomUrl);
        entries.remove(pointer);
        firePomRemovedEvent(pPomUrl);
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

    /**
     * Internal. Does nothing.
     *
     * @param pointers ignored
     */
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
            firePomValidityChangedEvent(pointer.getUrl());
    }

    @Override
    public void projectOpened() {
        final Runnable regToolWinRunnable = new Runnable() {
            public void run() {

                //
                //register tool window - this is done in a future runnable
                //because if we create the pom manager panel now, it will
                //try to load available goals from maven. this is done via
                //PSI, but unfortunately, PSI is not available at this stage.
                //
                final ToolWindowManager twMgr = ToolWindowManager.getInstance(project);
                final PomManagerPanel ui = new PomManagerPanel(project);
                final ToolWindow tw = twMgr.registerToolWindow(PomManagerPanel.TITLE,
                                                               ui,
                                                               ToolWindowAnchor.RIGHT);
                tw.setIcon(Icons.MAVEN);
            }
        };

        final StartupManager mgr = StartupManager.getInstance(project);
        mgr.registerPostStartupActivity(regToolWinRunnable);
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

    public PomManagerPanel getToolWindowComponent() {
        final ToolWindow tw = getToolWindow();
        if (tw != null)
            return (PomManagerPanel) tw.getComponent();

        return null;
    }

    /**
     * Fires the {@link PomManagerListener#pomAdded(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomAddedEvent(final String pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null) {
                event = new PomManagerEvent(this, pPomUrl);
            }
            listener.pomAdded(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomRemoved(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomRemovedEvent(final String pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomRemoved(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomValidityChanged(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomValidityChangedEvent(final String pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomValidityChanged(event);
        }
    }

    public void readExternal(final Element pElt) throws InvalidDataException {

        //noinspection unchecked
        final List<Element> pomElts = pElt.getChildren("pom");
        for (Element pomElt : pomElts) {
            final String url = pomElt.getAttributeValue("url");
            if (url == null || url.trim().length() == 0)
                continue;

            add(url);
        }
    }

    public void writeExternal(final Element pElt) throws WriteExternalException {
        final VirtualFilePointer[] filePointers = getFilePointers();
        for (VirtualFilePointer pointer : filePointers) {
            final Element pomElt = new Element("pom");
            pomElt.setAttribute("url", pointer.getUrl());
            pElt.addContent(pomElt);
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
