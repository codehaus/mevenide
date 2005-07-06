package org.mevenide.idea.project;

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.util.components.AbstractApplicationComponent;

/**
 * @author Arik
 */
public class PomPropertiesManager extends AbstractApplicationComponent
        implements PropertyChangeListener,
                   VirtualFilePointerListener {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PomPropertiesManager.class);

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
     * Internal.
     *
     * @param pPointers
     */
    public void beforeValidityChanged(final VirtualFilePointer[] pPointers) {
    }

    /**
     * Internal.
     *
     * @param pPointers
     */
    public void validityChanged(final VirtualFilePointer[] pPointers) {
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

    private void initializePointers(final VirtualFile pMavenHome) {
        final VirtualFilePointerManager pointerMgr = VirtualFilePointerManager.getInstance();

        //
        //create a pointer to maven installation property files
        //
        if (pMavenHome != null) {
            final String homePath = pMavenHome.getPath();
            String url;

            url = homePath + "/lib/maven.jar!/defaults.properties";
            mavenDefaultPropertiesPointer = pointerMgr.create(url, this);

            url = homePath + "/lib/maven.jar!/driver.properties";
            mavenDriverPropertiesPointer = pointerMgr.create(url, this);
        }
        else {
            mavenDefaultPropertiesPointer = null;
            mavenDriverPropertiesPointer = null;
        }
    }

    private String getFilePropertyValue(final VirtualFile pFile, final String pName)
            throws IOException {
        final Properties props = new Properties();
        props.load(new ByteArrayInputStream(pFile.contentsToByteArray()));
        return props.getProperty(pName);
    }

    private String getFilePropertyValue(final VirtualFile pFile,
                                        final String pChildFileName,
                                        final String pName) throws IOException {
        final VirtualFile child = pFile.findFileByRelativePath(pChildFileName);
        if (child != null && child.isValid() && !child.isDirectory())
            return getFilePropertyValue(child, pName);
        else
            return null;
    }

    private String getFilePointerPropertyValue(final VirtualFilePointer pPointer,
                                               final String pName) throws IOException {
        if (pPointer == null || !pPointer.isValid())
            return null;

        final VirtualFile file = pPointer.getFile();
        return getFilePropertyValue(file, pName);
    }

    public static PomPropertiesManager getInstance() {
        return ApplicationManager.getApplication().getComponent(PomPropertiesManager.class);
    }
}
