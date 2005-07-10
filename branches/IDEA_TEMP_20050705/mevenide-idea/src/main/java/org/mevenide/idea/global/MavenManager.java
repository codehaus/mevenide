/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.idea.global;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.log4j.Level;
import org.jdom.Element;
import org.mevenide.idea.util.FileUtils;
import org.mevenide.idea.util.components.AbstractApplicationComponent;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * This application component manages global Maven settings for IDEA.
 *
 * <p>Currently, only the Maven home settings is defined. In the future, we can define here the
 * amount of memory to allocate for Maven processes (e.g. -Xms and -Xmx) and others.</p>
 *
 * @author Arik
 * @todo allow setting custom Maven home per project
 */
public class MavenManager extends AbstractApplicationComponent implements JDOMExternalizable {
    /**
     * Jelly scripts extensions.
     */
    private static final String[] JELLY_EXTENSIONS = new String[]{"jelly"};

    /**
     * Used to synchronize calls to {@link #setMavenHome(String)} and
     * {@link #setMavenHome(com.intellij.openapi.vfs.VirtualFile)}.
     */
    private final Lock LOCK = new ReentrantLock();

    /**
     * Runnable that resets the maven home to null.
     */
    private final Runnable HOME_RESETTER = new HomeResetter();

    /**
     * Extra command line options to send to the Maven process.
     */
    private String mavenOptions;

    /**
     * Whether to activate Maven in offline mode.
     */
    private boolean offline = false;

    /**
     * The file watcher.
     */
    private LocalFileSystem.WatchRequest mavenHomeWatcher = null;

    /**
     * Detects external removal of maven home, and refreshes listeners.
     */
    private final VirtualFileAdapter FS_LISTENER = new VirtualFileAdapter() {
        @Override
        public void fileDeleted(VirtualFileEvent event) {
            LOCK.lock();
            try {
                if(mavenHomeWatcher == null)
                    return;

                final VirtualFile mavenHome = getMavenHome();
                if (mavenHome == null)
                    return;

                final String url = extractUrl(event);
                if(url == null || url.trim().length() == 0)
                    LOG.trace("Unknown file delete event - exiting.");
                else if(url.equalsIgnoreCase(mavenHome.getUrl()))
                    ApplicationManager.getApplication().invokeLater(HOME_RESETTER);
            }
            finally {
                LOCK.unlock();
            }
        }
    };

    /**
     * Returns the command line options to send to the Maven process when invoked.
     *
     * <p>This is the equivalent of <code>MAVEN_OPTS</code> environment variable.</p>
     *
     * @return string
     */
    public String getMavenOptions() {
        return mavenOptions;
    }

    /**
     * Sets the command line options to send to the Maven process invocations.
     *
     * <p>Invoking this method will cause a property-change event.</p>
     *
     * @param pMavenOptions the new maven command line options
     */
    public void setMavenOptions(final String pMavenOptions) {
        final String oldOptions = mavenOptions;

        if (pMavenOptions != null && pMavenOptions.trim().length() == 0)
            mavenOptions = null;
        else
            mavenOptions = pMavenOptions;

        changeSupport.firePropertyChange("mavenOptions", oldOptions, mavenOptions);
    }

    /**
     * Returns whether Maven will be executed in offline mode.
     *
     * @return boolean
     */
    public boolean isOffline() {
        return offline;
    }

    /**
     * Sets the offline/online mode for Maven executions.
     *
     * @param pOffline {@code true} for online, {@code false} for offline
     */
    public void setOffline(final boolean pOffline) {
        final boolean oldOffline = offline;
        offline = pOffline;
        changeSupport.firePropertyChange("offline", oldOffline, offline);
    }

    /**
     * Returns the selected Maven home, or <code>null</code> if not set.
     *
     * @return file pointing to the Maven directory, or <code>null</code>
     */
    public VirtualFile getMavenHome() {
        return mavenHomeWatcher == null ? null : mavenHomeWatcher.getRoot();
    }

    /**
     * Sets the Maven home to the specified directory. Throws a {@link FileNotFoundException} if the
     * specified home points to a file or does not exist.
     *
     * <p>Invoking this method will cause a property-change event.</p>
     *
     * @param pMavenHome the new Maven home - may be <code>null</code>
     */
    public void setMavenHome(final String pMavenHome) throws IllegalMavenHomeException {
        LOCK.lock();
        try {
            final VirtualFile home;
            if (pMavenHome == null || pMavenHome.trim().length() == 0)
                home = null;
            else {
                final String path = pMavenHome.replace(File.separatorChar, '/');
                final String url = "file://" + path;

                //
                //find a virtual-file for the given path. We disable our file-
                //system listener here, because the refreshAndFindFileByUrl
                //method causes the listener to be invoked, which will call this
                //method, hence causing an infinite loop.
                //
                LOG.trace("setMavenHome(String): validating existance of " + pMavenHome);
                final VirtualFileManager vfm = VirtualFileManager.getInstance();
                vfm.removeVirtualFileListener(FS_LISTENER);
                home = FileUtils.find(url);
                vfm.addVirtualFileListener(FS_LISTENER);

                //
                //if the file could not be found, throw an exception
                //
                LOG.trace("setMavenHome(String): found " + home);
                if(home == null || !FileUtils.exists(home))
                    throw new IllegalMavenHomeException(RES.get("illegal.maven.home", pMavenHome));
            }

            setMavenHome(home);
        }
        finally {
            LOCK.unlock();
        }
    }

    /**
     * Sets the Maven home to the specified directory. Throws a {@link FileNotFoundException} if the
     * specified home points to a file or does not exist.
     *
     * <p>Invoking this method will cause a property-change event.</p>
     *
     * @param pMavenHome the new Maven home - may be <code>null</code>
     */
    public void setMavenHome(final VirtualFile pMavenHome) throws IllegalMavenHomeException {
        LOCK.lock();
        try {
            LOG.trace("setMavenHome(VirtualFile): validating " + pMavenHome);
            validateMavenHome(pMavenHome);
            LOG.trace("setMavenHome(VirtualFile): validation passed for " + pMavenHome);

            final VirtualFile oldMavenHome = getMavenHome();
            if(mavenHomeWatcher != null)
                LocalFileSystem.getInstance().removeWatchedRoot(mavenHomeWatcher);
            if(pMavenHome != null)
                mavenHomeWatcher = LocalFileSystem.getInstance().addRootToWatch(pMavenHome, false);
            else
                mavenHomeWatcher = null;
            changeSupport.firePropertyChange("mavenHome", oldMavenHome, getMavenHome());
        }
        finally {
            LOCK.unlock();
        }
    }

    /**
     * Tries to guess the Maven home installation from the system environment. Since environment
     * entries on Windows are case-insensitive, and on UNIX system are case-sensitive, the check is
     * done in a case-insensitive manner.
     *
     * @return the maven home, or {@code null} if could not be detected
     */
    public VirtualFile guessMavenHome() {
        final Map<String, String> env = System.getenv();
        for (String key : env.keySet()) {
            if (key.equalsIgnoreCase("MAVEN_HOME")) {
                final String value = System.getenv(key);
                final String path = value.replace(File.separatorChar, '/');
                final VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(path);
                if (dir == null || !dir.isValid() || !FileUtils.exists(dir) || !dir.isDirectory())
                    return null;

                return dir;
            }
        }

        return null;
    }

    public void initComponent() {
        VirtualFileManager.getInstance().addVirtualFileListener(FS_LISTENER);

        //
        //disable http-client logger as it is quite verbose
        //
        final Logger logger = Logger.getInstance(HttpMethodBase.class.getName());
        logger.setLevel(Level.ERROR);

        //
        //register the ".jelly" file extension as an XML file
        //
        FileTypeManager.getInstance().registerFileType(StdFileTypes.XML, JELLY_EXTENSIONS);
    }

    @Override
    public void disposeComponent() {
        VirtualFileManager.getInstance().removeVirtualFileListener(FS_LISTENER);
    }

    /**
     * Reads configuration from external storage.
     *
     * @param pElement XML element to read configuration from
     *
     * @throws InvalidDataException if an error occurs
     */
    public void readExternal(final Element pElement) throws InvalidDataException {
        //
        //read maven home
        //
        String mavenHomeValue = JDOMExternalizer.readString(pElement, "mavenHome");
        try {
            setMavenHome(mavenHomeValue);
        }
        catch (IllegalMavenHomeException e) {
            UIUtils.showError(e);
        }

        //
        //read maven options
        //
        final String mavenOptionsValue = JDOMExternalizer.readString(pElement, "mavenOptions");
        setMavenOptions(mavenOptionsValue);

        //
        //read maven offline mode
        //
        final boolean offline;
        String offlineValue = JDOMExternalizer.readString(pElement, "offline");
        if (offlineValue != null)
            offline = JDOMExternalizer.readBoolean(pElement, "offline");
        else {
            offlineValue = System.getProperty("maven.online.mode");
            offline = offlineValue != null && offlineValue.equalsIgnoreCase("true");
        }
        setOffline(offline);
    }

    /**
     * Writes configuration to external storage.
     *
     * @param pElement XML element to write configuration to
     *
     * @throws WriteExternalException if an error occurs
     */
    public void writeExternal(final Element pElement) throws WriteExternalException {
        //
        //write maven home
        //
        final VirtualFile mavenHome = getMavenHome();
        if (mavenHome != null)
            JDOMExternalizer.write(pElement, "mavenHome", mavenHome.getPath());
        else
            JDOMExternalizer.write(pElement, "mavenHome", null);

        //
        //write maven options
        //
        JDOMExternalizer.write(pElement, "mavenOptions", mavenOptions);

        //
        //write offline mode
        //
        JDOMExternalizer.write(pElement, "offline", offline);
    }

    private void validateMavenHome(VirtualFile pMavenHome) throws IllegalMavenHomeException {
        if (pMavenHome == null)
            return;

        final String url = pMavenHome.getPresentableUrl();
        if (!pMavenHome.isValid() || !FileUtils.exists(pMavenHome))
            throw new IllegalMavenHomeException(RES.get("illegal.maven.home", url));

        if (!pMavenHome.isDirectory())
            throw new IllegalMavenHomeException(RES.get("file.must.be.dir", url));

        final VirtualFile mavenLib = pMavenHome.findFileByRelativePath("lib/maven.jar");
        if (mavenLib == null || !mavenLib.isValid() || !FileUtils.exists(mavenLib) || mavenLib.isDirectory())
            throw new IllegalMavenHomeException(RES.get("illegal.maven.home", url));
    }

    /**
     * Returns an instance of this component.
     *
     * @return Maven manager instance
     */
    public static MavenManager getInstance() {
        return ApplicationManager.getApplication().getComponent(MavenManager.class);
    }

    private String extractUrl(final VirtualFileEvent event) {
        final VirtualFile parent = event.getParent();
        if (parent != null) {
            final StringBuilder buf = new StringBuilder(parent.getUrl());
            if (buf.charAt(buf.length() - 1) != '/')
                buf.append('/');
            buf.append(event.getFileName());
            return buf.toString();
        }
        else if (event.getFile() != null)
            return event.getFile().getUrl();
        else {
            LOG.trace("Could not extract url from event.");
            return null;
        }
    }

    private class HomeResetter implements Runnable {
        public void run() {
            try {
                LOG.trace("Detected Maven home deletion - setting Maven home to null");
                setMavenHome((VirtualFile) null);
            }
            catch (IllegalMavenHomeException e) {
                LOG.error(e, e);
            }
        }
    }
}
