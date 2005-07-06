package org.mevenide.idea.global;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.swing.event.EventListenerList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.util.components.AbstractApplicationComponent;

/**
 * Resolves Maven properties by looking in all possible places - POM property files, user files,
 * system properties and in the Maven installation itself.
 *
 * <p>Resolves property values by supporting the "${property}" property expression convention (which
 * is used by Maven).</p>
 *
 * @author Arik
 */
public class PropertiesManager extends AbstractApplicationComponent
        implements PropertyChangeListener,
                   VirtualFilePointerListener {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PropertiesManager.class);

    /**
     * Manages listeners.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The user's home directory.
     */
    private final VirtualFile userHome = getUserHome();

    /**
     * A file pointer to the user's 'build.properties' file.
     */
    private VirtualFilePointer userPropertiesPointer;

    /**
     * A file pointer to the Maven installation's default properties file.
     */
    private VirtualFilePointer mavenDefaultPropertiesPointer;

    /**
     * A file pointer to the Maven installation's default properties file.
     */
    private VirtualFilePointer mavenDriverPropertiesPointer;

    /**
     * Registers the given properties listener.
     *
     * @param pListener the listener to register
     */
    public void addPropertiesListener(final PropertiesListener pListener) {
        listenerList.add(PropertiesListener.class, pListener);
    }

    /**
     * Unregisters the given properties listener.
     *
     * @param pListener the listener to unregister
     */
    public void removePropertiesListener(final PropertiesListener pListener) {
        listenerList.remove(PropertiesListener.class, pListener);
    }

    /**
     * Internal. Initializes the component and file pointers. Registers as a listener to {@link
     * MavenManager}'s properties.
     */
    @Override
    public void initComponent() {
        final MavenManager mavenMgr = MavenManager.getInstance();
        mavenMgr.addPropertyChangeListener("mavenHome", this);

        //
        //create a file pointer to the user's 'build.properties'
        //
        final String userPropsUrl = userHome.getUrl() + "/build.properties";
        final VirtualFilePointerManager pntrMgr = VirtualFilePointerManager.getInstance();
        userPropertiesPointer = pntrMgr.create(userPropsUrl, this);

        //
        //initialize file pointers to match the maven home
        //
        initializePointers(mavenMgr.getMavenHome());
    }

    public void propertyChange(final PropertyChangeEvent pEvent) {
        final Object source = pEvent.getSource();
        final String propertyName = pEvent.getPropertyName();
        if (source instanceof MavenManager && "mavenHome".equals(propertyName)) {
            final MavenManager mgr = (MavenManager) source;
            initializePointers(mgr.getMavenHome());
        }
    }

    /**
     * Returns the value of the given property. The value is located the same way Maven resolves
     * properties - in the following order:
     *
     * <p><ol> <li>system properties ({@code -Dx=y})</li> <li>user's properties ({@code
     * build.properties} under the user's home)</li> <li>maven properties (maven-provided
     * defaults)</li> </ol></p>
     *
     * <p><b>Note:</b> that this method does NOT evaluate project properties, as it is intended to
     * be used for global properties only.</p>
     *
     * @param pName name of the property to resolve
     *
     * @return property value, or {@code null} if it could not be found
     */
    public String getProperty(final String pName) {
        return getProperty(null, pName);
    }

    /**
     * Returns the value of the given property. The value is located the same way Maven resolves
     * properties - in the following order:
     *
     * <p><ol> <li>system properties ({@code -Dx=y})</li> <li>user's properties ({@code
     * build.properties} under the user's home)</li> <li>build properties ({@code build.properties}
     * in the POM directory)</li> <li>project properties ({@code project.properties} in the POM
     * directory</li> <li>maven properties (maven-provided defaults)</li> </ol></p>
     *
     * @param pPomFile the POM for which we resolve the property. The POM is needed, since each POM
     *                 defines its own set of properties
     * @param pName    name of the property to resolve
     *
     * @return property value, or {@code null} if it could not be found
     */
    public String getProperty(final VirtualFile pPomFile, final String pName) {

        //
        //check some preconfigured properties that are not present in
        //configuration files, but Maven expects them because they are
        //passed by the maven shell scripts
        //
        if (pName.equals("maven.home"))
            return MavenManager.getInstance().getMavenHome().getPath();

        //
        //system properties
        //
        String value = System.getProperty(pName);
        if (value != null && value.trim().length() > 0)
            return resolveProperty(pPomFile, value);

        //
        //user properties
        //
        try {
            value = getFilePointerPropertyValue(userPropertiesPointer, pName);
            if (value != null)
                return resolveProperty(pPomFile, value);
        }
        catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }

        //
        //starting to use POM - get POM directory
        //
        if (pPomFile != null) {
            final VirtualFile dir = pPomFile.getParent();
            assert dir != null;

            //
            //build properties
            //
            try {
                value = getFilePropertyValue(dir, "build.properties", pName);
                if (value != null)
                    return resolveProperty(pPomFile, value);
            }
            catch (IOException e) {
                LOG.warn(e.getMessage(), e);
            }

            //
            //project properties
            //
            try {
                value = getFilePropertyValue(dir, "project.properties", pName);
                if (value != null)
                    return resolveProperty(pPomFile, value);
            }
            catch (IOException e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        //
        //maven defaults properties
        //
        try {
            value = getFilePointerPropertyValue(mavenDefaultPropertiesPointer, pName);
            if (value != null)
                return resolveProperty(pPomFile, value);
        }
        catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }

        //
        //maven driver properties
        //
        try {
            value = getFilePointerPropertyValue(mavenDriverPropertiesPointer, pName);
            if (value != null)
                return resolveProperty(pPomFile, value);
        }
        catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }

        return null;
    }

    /**
     * Resolves all property expressions in the given string to their values. Property expressions
     * are written in the familiar format of {@code ${propertyname}}.
     *
     * @param pPomFile the POM by which property values will be fetched if needed
     * @param pValue   the string containing expressions
     *
     * @return expanded string with property values
     */
    public String resolveProperty(final VirtualFile pPomFile, final String pValue) {
        final StringBuilder buf = new StringBuilder(pValue);

        int exprStart = buf.indexOf("$");
        while (exprStart >= 0) {
            if (exprStart < buf.length() - 1) {
                final char next = buf.charAt(exprStart + 1);
                if (next == '{') {
                    final int exprEnd = buf.indexOf("}", exprStart);
                    if (exprEnd >= 0) {
                        final String expr = buf.substring(exprStart + 2, exprEnd);
                        final String value = getProperty(pPomFile, expr);
                        buf.replace(exprStart, exprEnd + 1, value == null ? "" : value);
                        exprStart--;
                    }
                }
            }

            exprStart++;
            exprStart = buf.indexOf("$", exprStart);
        }

        return buf.toString();
    }

    /**
     * Internal. Does nothing.
     *
     * @param pPointers
     */
    public void beforeValidityChanged(final VirtualFilePointer[] pPointers) {
    }

    /**
     * Internal. Called by the file pointers if the files change validity, and notifies all
     * registered {@link PropertiesListener}s that the properties files changed.
     *
     * @param pPointers
     */
    public void validityChanged(final VirtualFilePointer[] pPointers) {
        for (VirtualFilePointer pointer : pPointers) {
            if (pointer == userPropertiesPointer ||
                    pointer == mavenDefaultPropertiesPointer ||
                    pointer == mavenDriverPropertiesPointer)
                firePropertiesChangedEvent();
        }
    }

    /**
     * Fires the propertiesChanged event to all listeners.
     */
    private void firePropertiesChangedEvent() {
        final PropertiesListener[] listeners = listenerList.getListeners(PropertiesListener.class);
        PropertiesEvent e = null;
        for (PropertiesListener listener : listeners) {
            if (e == null)
                e = new PropertiesEvent(this);
            listener.propertiesChanged(e);
        }
    }

    /**
     * Returns the user's home directory, or {@code null} if it can't be found.
     *
     * @return file
     */
    private VirtualFile getUserHome() {
        final String homePath = System.getProperty("user.home");
        final String fixedPath = homePath.replace(File.separatorChar, '/');
        final String homeUrl = VirtualFileManager.constructUrl("file", fixedPath);
        return VirtualFileManager.getInstance().findFileByUrl(homeUrl);
    }

    /**
     * Initializes the file pointers (to properties files) based on the given maven home.
     *
     * @param pMavenHome the new maven home
     */
    private void initializePointers(final VirtualFile pMavenHome) {
        final VirtualFilePointerManager pointerMgr = VirtualFilePointerManager.getInstance();

        if (pMavenHome != null) {
            final String homePath = pMavenHome.getPath();
            String url;

            url = "jar://" + homePath + "/lib/maven.jar!/defaults.properties";
            mavenDefaultPropertiesPointer = pointerMgr.create(url, this);

            url = "jar://" + homePath + "/lib/maven.jar!/driver.properties";
            mavenDriverPropertiesPointer = pointerMgr.create(url, this);
        }
        else {
            mavenDefaultPropertiesPointer = null;
            mavenDriverPropertiesPointer = null;
        }
    }

    /**
     * Searches for the given property in the specified file, and if found, returns its value. If
     * the property is not found, {@code null} is returned.
     *
     * <p><b>Note:</b> this method does not resolve the value - it is returned "raw".</p>
     *
     * @param pFile the file to search in
     * @param pName the name of the property to search for
     *
     * @return property value as a string, or {@code null} if not found
     * @throws IOException if an error occurs while reading the file
     */
    private String getFilePropertyValue(final VirtualFile pFile, final String pName)
            throws IOException {
        final Properties props = new Properties();
        props.load(new ByteArrayInputStream(pFile.contentsToByteArray()));
        return props.getProperty(pName);
    }

    /**
     * Searches for the given property in a file, and if found, returns its value. If the property
     * or file is not found, {@code null} is returned.
     *
     * <p>The file that is searched is the file that corresponds to the relative path specified. The
     * path is resolved against the {@code pFile} argument.</p>
     *
     * <p><b>Note:</b> this method does not resolve the value - it is returned "raw".</p>
     *
     * @param pFile the file to search in
     * @param pName the name of the property to search for
     *
     * @return property value as a string, or {@code null} if not found
     * @throws IOException if an error occurs while reading the file
     */
    private String getFilePropertyValue(final VirtualFile pFile,
                                        final String pChildFileName,
                                        final String pName) throws IOException {
        final VirtualFile child = pFile.findFileByRelativePath(pChildFileName);
        if (child != null && child.isValid() && !child.isDirectory())
            return getFilePropertyValue(child, pName);
        else
            return null;
    }

    /**
     * Searches for the given property in a file, and if found, returns its value. If the property
     * or file is not found, {@code null} is returned.
     *
     * <p>The file is found using the given file pointer. If the pointer reports that the file does
     * not exist, {@code null} is returned.</p>
     *
     * @param pPointer the pointer to the file to search in
     * @param pName    the name of the property to search for
     *
     * @return the property value, or {@code null} if the file or property are not found
     * @throws IOException if an error occurs while reading the file
     */
    private String getFilePointerPropertyValue(final VirtualFilePointer pPointer,
                                               final String pName) throws IOException {
        if (pPointer == null || !pPointer.isValid())
            return null;

        final VirtualFile file = pPointer.getFile();
        return getFilePropertyValue(file, pName);
    }

    /**
     * Returns an instance of the component.
     *
     * @return instance
     */
    public static PropertiesManager getInstance() {
        return ApplicationManager.getApplication().getComponent(PropertiesManager.class);
    }
}
