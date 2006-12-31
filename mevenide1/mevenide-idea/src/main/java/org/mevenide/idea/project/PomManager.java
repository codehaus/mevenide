package org.mevenide.idea.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;
import org.jdom.Element;
import org.mevenide.idea.project.ui.PomManagerPanel;
import org.mevenide.idea.util.components.AbstractProjectComponent;
import org.mevenide.idea.util.ui.images.Icons;

/**
 * @author Arik
 */
public class PomManager extends AbstractProjectComponent
        implements JDOMExternalizable {
    /**
     * The map of file pointers to acquire their associated PSI projects.
     */
    protected final Map<CharSequence, PomInfo> entries = new HashMap<CharSequence, PomInfo>(10);

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

        final String path = pPomUrl.substring(7);
        LOG.debug("Adding POM from URL: " + pPomUrl);
        LOG.debug("\tPath of POM is: " + path);

        final File file = new File(path);
        if(!file.exists())
            return;

        final LocalFileSystem localFs = LocalFileSystem.getInstance();
        final VirtualFile virtualFile = localFs.findFileByIoFile(file);
        if(virtualFile == null)
            return;

        final PomInfo info = new PomInfo(file, localFs.addRootToWatch(virtualFile, false));
        entries.put(pPomUrl, info);

        firePomAddedEvent(pPomUrl);
    }

    public boolean contains(final CharSequence pPomUrl) {
        if (pPomUrl == null)
            return false;

        return entries.containsKey(pPomUrl);
    }

    public boolean isValid(final String pPomUrl) {
        return getIoFile(pPomUrl) != null;
    }

    public VirtualFile getVirtualFile(final String pPomUrl) {
        final File file = getIoFile(pPomUrl);
        if(file == null)
            return null;

        final VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        if(virtualFile == null || !virtualFile.isValid() || virtualFile.isDirectory())
            return null;

        return virtualFile;
    }

    public File getIoFile(final CharSequence pPomUrl) {
        if(!entries.containsKey(pPomUrl))
            return null;

        final File file = entries.get(pPomUrl).file;
        if(!file.exists())
            return null;

        return file;
    }

    public String[] getFileUrls() {
        final String[] buffer = new String[entries.size()];
        return entries.keySet().toArray(buffer);
    }

    public void remove(final CharSequence pPomUrl) {
        final File file = getIoFile(pPomUrl);
        if(file == null)
            return;

        LocalFileSystem.getInstance().removeWatchedRoot(entries.get(pPomUrl).watchRequest);
        entries.remove(pPomUrl);
        firePomRemovedEvent(pPomUrl);
    }

    public <T> void putData(final CharSequence pPomUrl, final Key<T> pKey, final T pValue) {
        if(!contains(pPomUrl))
            return;

        final PomInfo info = entries.get(pPomUrl);
        if(info == null)
            return;

        info.data.put(pKey, pValue);
    }

    public <T> T getData(final CharSequence pPomUrl, final Key<T> pKey) {
        if (!contains(pPomUrl))
            return null;

        final PomInfo info = entries.get(pPomUrl);
        if (info == null)
            return null;

        //noinspection unchecked
        return (T) info.data.get(pKey);
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

        LocalFileSystem.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
            @Override
            public void fileCreated(VirtualFileEvent event) {
                final String url = event.getFile().getUrl();
                for (Map.Entry<CharSequence,PomInfo> e : entries.entrySet()) {
                    if(!url.equals(e.getKey()))
                        continue;

                    firePomFileCreated(url);
                }
            }

            @Override
            public void fileDeleted(VirtualFileEvent event) {
                final String url = event.getFile().getUrl();
                for (Map.Entry<CharSequence, PomInfo> e : entries.entrySet()) {
                    if (!url.equals(e.getKey()))
                        continue;

                    firePomFileDeleted(url);
                }
            }

            @Override
            public void contentsChanged(VirtualFileEvent event) {
                final String url = event.getFile().getUrl();
                for (Map.Entry<CharSequence, PomInfo> e : entries.entrySet()) {
                    if (!url.equals(e.getKey()))
                        continue;

                    firePomFileChanged(url);
                }
            }

            @Override
            public void fileMoved(VirtualFileMoveEvent event) {
                final String url = event.getFile().getUrl();
                for (Map.Entry<CharSequence, PomInfo> e : entries.entrySet()) {
                    if (!url.equals(e.getKey()))
                        continue;

                    final StringBuilder oldUrl = new StringBuilder(event.getOldParent().getUrl());
                    if(oldUrl.charAt(oldUrl.length() - 1) != '/')
                        oldUrl.append('/');
                    oldUrl.append(event.getFile().getName());
                    remove(oldUrl);
                    add(event.getFile().getUrl());
                }
            }
        });

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
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomAdded(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomRemoved(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomRemovedEvent(final CharSequence pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomRemoved(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomFileCreated(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomFileCreated(final String pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomFileCreated(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomFileDeleted(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomFileDeleted(final String pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomFileDeleted(event);
        }
    }

    /**
     * Fires the {@link PomManagerListener#pomFileChanged(PomManagerEvent)} event.
     *
     * @param pPomUrl the modified POM
     */
    protected void firePomFileChanged(final String pPomUrl) {
        final PomManagerListener[] listeners = listenerList.getListeners(PomManagerListener.class);
        PomManagerEvent event = null;
        for (PomManagerListener listener : listeners) {
            if (event == null)
                event = new PomManagerEvent(this, pPomUrl);
            listener.pomFileChanged(event);
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
        final String[] urls = getFileUrls();
        for (String url : urls) {
            final Element pomElt = new Element("pom");
            pomElt.setAttribute("url", url);
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

    private class PomInfo {
        final File file;
        final LocalFileSystem.WatchRequest watchRequest;
        final Map<Key<?>,Object> data = new HashMap<Key<?>, Object>(5);

        public PomInfo(final File pFile, final LocalFileSystem.WatchRequest pWatchRequest) {
            file = pFile;
            watchRequest = pWatchRequest;
        }
    }
}
