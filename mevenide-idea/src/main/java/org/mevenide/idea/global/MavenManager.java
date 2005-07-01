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
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.WriteExternalException;
import java.io.File;
import java.io.FileNotFoundException;
import org.jdom.Element;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.idea.util.components.AbstractApplicationComponent;
import org.mevenide.idea.util.ui.UIUtils;

/**
 * This application component manages global Maven settings for IDEA.
 *
 * <p>Currently, only the Maven home settings is defined. In the future, we can define
 * here the amount of memory to allocate for Maven processes (e.g. -Xms and -Xmx) and
 * others.</p>
 *
 * @author Arik
 */
public class MavenManager extends AbstractApplicationComponent
    implements JDOMExternalizable {
    /**
     * The selected Maven home.
     */
    private File mavenHome;

    /**
     * Extra command line options to send to the Maven process.
     */
    private String mavenOptions;

    /**
     * Whether to activate Maven in offline mode.
     */
    private boolean offline = false;

    /**
     * Returns the selected Maven home, or <code>null</code> if not set.
     *
     * @return file pointing to the Maven directory, or <code>null</code>
     */
    public File getMavenHome() {
        return mavenHome;
    }

    /**
     * Sets the Maven home to the specified directory. Throws a {@link
     * FileNotFoundException} if the specified home points to a file or does not exist.
     *
     * <p>Invoking this method will cause a property-change event.</p>
     *
     * @param pMavenHome the new Maven home - may be <code>null</code>
     *
     * @throws FileNotFoundException if the the specified home does not point to an
     *                               existing directory
     */
    public void setMavenHome(final File pMavenHome) throws FileNotFoundException {
        if (pMavenHome != null && !pMavenHome.isDirectory())
            throw new FileNotFoundException(RES.get("maven.home.error"));

        final File oldMavenHome = mavenHome;
        mavenHome = pMavenHome;
        changeSupport.firePropertyChange("mavenHome", oldMavenHome, mavenHome);
    }

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
     * This method is called after the {@link #readExternal(org.jdom.Element)} method is
     * called. If the {@link #mavenHome} field is still <code>null</code>, then this
     * method tries to guess it by invoking the {@link SysEnvLocationFinder}'s default
     * instance's {@link org.mevenide.environment.SysEnvLocationFinder#getMavenHome()}
     * method.
     *
     * <p>If it still cannot find the Maven home, or if it finds it invalid, an error
     * message is shown.</p>
     *
     * @todo Allow the user to specify never to both him/her again with the error message
     */
    public void initComponent() {
        if (mavenHome == null) {
            //
            //maven home is null - try to guess
            //
            final ILocationFinder finder = SysEnvLocationFinder.getInstance();
            final String mavenHomePath = finder.getMavenHome();
            if (mavenHomePath != null && mavenHomePath.trim().length() > 0)
                try {
                    setMavenHome(new File(mavenHomePath).getAbsoluteFile());
                }
                catch (FileNotFoundException e) {
                    UIUtils.showError(RES.get("maven.home.misconfigured", mavenHomePath));
                }
            else
                Messages.showInfoMessage(RES.get("maven.home.undefined"), "Maven");
        }

        FileTypeManager.getInstance().registerFileType(StdFileTypes.XML,
                                                       new String[]{"jelly"});
    }

    public void readExternal(final Element pElement) throws InvalidDataException {
        //
        //read maven home
        //
        final String mavenHomeValue = JDOMExternalizer.readString(pElement, "mavenHome");
        if (mavenHomeValue != null && mavenHomeValue.trim().length() > 0)
            try {
                setMavenHome(new File(mavenHomeValue));
            }
            catch (FileNotFoundException e) {
                //
                //ignoring exception - in 'initComponent', if the maven home is
                //still null, and MAVEN_HOME is not defined in the environment,
                //we ask the user to supply one
                //
                LOG.trace(e.getMessage(), e);
            }

        //
        //read maven options
        //
        final String mavenOptionsValue = JDOMExternalizer.readString(pElement,
                                                                     "mavenOptions");
        setMavenOptions(mavenOptionsValue);

        //
        //read maven offline mode
        //TODO: read maven property "maven.online.mode"
        //
        setOffline(JDOMExternalizer.readBoolean(pElement, "offline"));
    }

    public void writeExternal(final Element pElement) throws WriteExternalException {
        //
        //write maven home
        //
        final File mavenHome = getMavenHome();
        if (mavenHome != null)
            JDOMExternalizer.write(pElement, "mavenHome", mavenHome.getAbsolutePath());

        //
        //write maven options
        //
        JDOMExternalizer.write(pElement, "mavenOptions", mavenOptions);

        //
        //write offline mode
        //
        JDOMExternalizer.write(pElement, "offline", offline);
    }

    /**
     * Returns an instance of this component.
     *
     * @return Maven manager instance
     */
    public static MavenManager getInstance() {
        return ApplicationManager.getApplication().getComponent(MavenManager.class);
    }
}
