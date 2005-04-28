package org.mevenide.idea.global;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mevenide.idea.Res;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.EventListener;

/**
 * This application component manages global Maven settings for IDEA.
 *
 * <p>Currently, only the Maven home settings is defined. In the future, we can define here the amount of
 * memory to allocate for Maven processes (e.g. -Xms and -Xmx) and others.</p>
 *
 * @author Arik
 */
public class MavenManager implements ApplicationComponent, JDOMExternalizable {
    /**
     * The component name.
     */
    private static final String NAME = MavenManager.class.getName();

    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(MavenManager.class);

    /**
     * The logger to use.
     */
    private static final Log LOG = LogFactory.getLog(MavenManager.class);

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The selected Maven home.
     */
    private File mavenHome;

    /**
     * Returns the selected Maven home, or <code>null</code> if not set.
     *
     * @return file pointing to the Maven directory, or <code>null</code>
     */
    public File getMavenHome() {
        return mavenHome;
    }

    /**
     * Sets the Maven home to the specified directory. Throws a {@link FileNotFoundException} if the specified
     * home points to a file or does not exist.
     *
     * @param pMavenHome the new Maven home - may be <code>null</code>
     *
     * @throws FileNotFoundException if the the specified home does not point to an existing directory
     */
    public void setMavenHome(final File pMavenHome) throws FileNotFoundException {
        if (pMavenHome != null && !pMavenHome.isDirectory())
            throw new FileNotFoundException(RES.get("maven.home.error"));

        mavenHome = pMavenHome;
        fireMavenHomeChangedEvent();
    }

    /**
     * Returns the ManagerManager class name as the component name.
     *
     * @return this class name
     */
    public String getComponentName() {
        return NAME;
    }

    /**
     * Does nothing.
     */
    public void initComponent() {
        if (LOG.isTraceEnabled())
            LOG.trace(NAME + " initialized.");

        if(mavenHome == null) {
            //TODO: guess maven home using a SysEnvLocationFinder
            //TODO: if not found, ask user to define it (with a 'never-ask-again' option)
        }
    }

    /**
     * Does nothing.
     */
    public void disposeComponent() {
        if (LOG.isTraceEnabled())
            LOG.trace(NAME + " disposed.");
    }

    public void readExternal(final Element pElement) throws InvalidDataException {
        if (LOG.isTraceEnabled())
            LOG.trace("Loading " + NAME + " from XML configuration.");

        final String mavenHomeValue = pElement.getAttributeValue("mavenHome");
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

        if (LOG.isTraceEnabled())
            LOG.trace("Finished loading " + NAME);
    }

    public void writeExternal(final Element pElement) throws WriteExternalException {
        if (LOG.isTraceEnabled())
            LOG.trace("Saving " + NAME + " to XML configuration.");

        final File mavenHome = getMavenHome();
        if (mavenHome != null)
            pElement.setAttribute("mavenHome", mavenHome.getAbsolutePath());

        if (LOG.isTraceEnabled())
            LOG.trace("Finished saving " + NAME);
    }

    /**
     * Adds a Maven manager listener.
     *
     * @param pListener the listener to notify on manager events
     */
    public void addGlobalSettingsListener(final MavenManagerListener pListener) {
        listenerList.add(MavenManagerListener.class, pListener);
    }

    /**
     * Remove the specified listener from the manager listeners list.
     *
     * @param pListener the listener to remove
     */
    public void removeGlobalSettingsListener(MavenManagerListener pListener) {
        listenerList.remove(MavenManagerListener.class, pListener);
    }

    /**
     * Fire the MavenHomeChanged event by notifiying all listeners.
     */
    protected void fireMavenHomeChangedEvent() {
        final MavenHomeChangedEvent event = new MavenHomeChangedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(MavenManagerListener.class);
        for (final EventListener listener : listeners)
            ((MavenManagerListener) listener).mavenHomeChanged(event);
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
