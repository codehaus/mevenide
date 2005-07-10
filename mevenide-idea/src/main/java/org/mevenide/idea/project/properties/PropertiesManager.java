package org.mevenide.idea.project.properties;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.idea.global.MavenManager;
import org.mevenide.idea.project.PomManager;
import org.mevenide.idea.util.components.AbstractProjectComponent;

/**
 * Resolves Maven properties by looking in all possible places - POM property files, user files,
 * system properties and in the Maven installation itself.
 *
 * <p>Resolves property values by supporting the "${property}" property expression convention (which
 * is used by Maven).</p>
 *
 * @author Arik
 */
public class PropertiesManager extends AbstractProjectComponent {
    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(PropertiesManager.class);

    /**
     * Creates an instance for the given project.
     *
     * @param pProject the project
     */
    public PropertiesManager(final Project pProject) {
        super(pProject);
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
         * @param pPomUrl the POM file url for which we resolve the property. The POM is needed, since
         *                each POM defines its own set of properties
         * @param pName   name of the property to resolve
         *
         * @return property value, or {@code null} if it could not be found
         */
    public String getProperty(final String pPomUrl, final String pName) {
        final VirtualFile pPomFile = PomManager.getInstance(project).getFile(pPomUrl);

        //
        //check some preconfigured properties that are not present in
        //configuration files, but Maven expects them because they are
        //passed by the maven shell scripts
        //
        final MavenManager mavenMgr = MavenManager.getInstance();
        final VirtualFile mavenHome = mavenMgr.getMavenHome();
        if (pName.equals("maven.home"))
            return mavenHome == null ? null : mavenHome.getPath();

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
            final VirtualFile home = getUserHome();
            if(home != null && home.isValid() && home.isDirectory()) {
                final VirtualFile propsFile = home.findChild("build.properties");
                if(propsFile != null && propsFile.isValid() && !propsFile.isDirectory()) {
                    value = getFilePropertyValue(propsFile, pName);
                    if (value != null)
                        return resolveProperty(pPomFile, value);
                }
            }
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

        if(mavenHome == null || !mavenHome.isValid() || !mavenHome.isDirectory())
            return null;
        final String baseJarUrl = "jar://" + mavenHome.getPath() + "/lib/maven.jar!/";

        //
        //maven defaults properties
        //
        try {
            final String url = baseJarUrl + "defaults.properties";
            final VirtualFile propsFile = VirtualFileManager.getInstance().findFileByUrl(url);
            if(propsFile != null) {
                value = getFilePropertyValue(propsFile, pName);
                if (value != null)
                    return resolveProperty(pPomFile, value);
            }
        }
        catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }

        //
        //maven driver properties
        //
        try {
            final String url = baseJarUrl + "driver.properties";
            final VirtualFile propsFile = VirtualFileManager.getInstance().findFileByUrl(url);
            if (propsFile != null) {
                value = getFilePropertyValue(propsFile, pName);
                if (value != null)
                    return resolveProperty(pPomFile, value);
            }
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
     * @param pPomUrl the POM by which property values will be fetched if needed
     * @param pValue   the string containing expressions
     *
     * @return expanded string with property values
     */
    public String resolveProperty(final String pPomUrl, final String pValue) {
        final VirtualFile pomFile = PomManager.getInstance(project).getFile(pPomUrl);
        return resolveProperty(pomFile, pValue);
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
                        final String url = pPomFile == null ? null : pPomFile.getUrl();
                        final String value = getProperty(url, expr);
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
     * Returns an instance of the component.
     *
     * @return instance
     */
    public static PropertiesManager getInstance(final Project pProject) {
        return pProject.getComponent(PropertiesManager.class);
    }
}
