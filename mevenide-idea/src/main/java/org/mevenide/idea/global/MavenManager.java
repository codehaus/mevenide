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
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.log4j.Level;
import org.jdom.Element;
import org.mevenide.idea.util.IDEUtils;
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
     * Flag used to synchronize maven home set requests and the file refresher.
     */
    private boolean inSetMavenHomeFlag = false;

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
        final VirtualFile home;
        if (pMavenHome == null)
            home = null;
        else if (pMavenHome.trim().length() == 0)
            home = null;
        else {
            final String path = pMavenHome.replace(File.separatorChar, '/');
            final LocalFileSystem fs = LocalFileSystem.getInstance();
            home = fs.findFileByPath(path);
            if (home == null)
                throw new IllegalMavenHomeException(RES.get("file.must.be.dir", pMavenHome));
        }

        setMavenHome(home);
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
        validateMavenHome(pMavenHome);

        final VirtualFile oldMavenHome = getMavenHome();
        if(mavenHomeWatcher != null)
            LocalFileSystem.getInstance().removeWatchedRoot(mavenHomeWatcher);
        if(pMavenHome != null)
            mavenHomeWatcher = LocalFileSystem.getInstance().addRootToWatch(pMavenHome, false);
        else
            mavenHomeWatcher = null;
        changeSupport.firePropertyChange("mavenHome", oldMavenHome, getMavenHome());
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
                if (dir == null || !dir.isValid() || !dir.isDirectory())
                    return null;

                return dir;
            }
        }

        return null;
    }

    public void initComponent() {
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
            @Override
            public void fileDeleted(VirtualFileEvent event) {
                if(mavenHomeWatcher == null || inSetMavenHomeFlag)
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
        });

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

        final String uiUrl = pMavenHome.getPresentableUrl();

        final String url = pMavenHome.getUrl();
        final FileRefresher finder = new FileRefresher(url);
        inSetMavenHomeFlag = true;
        try {
            IDEUtils.runWriteAction(finder);
        }
        finally {
            inSetMavenHomeFlag = false;
        }

        pMavenHome = finder.getFile();
        if (pMavenHome == null || !pMavenHome.isValid())
            throw new IllegalMavenHomeException(RES.get("illegal.maven.home", uiUrl));

        if (!pMavenHome.isDirectory())
            throw new IllegalMavenHomeException(RES.get("file.must.be.dir", uiUrl));

        final VirtualFile mavenLib = pMavenHome.findFileByRelativePath("lib/maven.jar");
        if (mavenLib == null || !mavenLib.isValid() || mavenLib.isDirectory())
            throw new IllegalMavenHomeException(RES.get("illegal.maven.home",
                                                        pMavenHome.getPresentableUrl()));
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

    private class FileRefresher implements Runnable {
        private final String url;
        private VirtualFile file;

        public FileRefresher(final String pUrl) {
            url = pUrl;
        }

        public void run() {
            final VirtualFileManager vfm = VirtualFileManager.getInstance();
            file = vfm.refreshAndFindFileByUrl(url);
        }

        public VirtualFile getFile() {
            return file;
        }
    }

    private class HomeResetter implements Runnable {
        public void run() {
            try {
                setMavenHome((VirtualFile) null);
            }
            catch (IllegalMavenHomeException e) {
                LOG.error(e, e);
            }
        }
    }
}
