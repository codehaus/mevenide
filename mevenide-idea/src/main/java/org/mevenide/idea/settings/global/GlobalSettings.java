package org.mevenide.idea.settings.global;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.mevenide.idea.util.Res;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.settings.XMLConstants;
import org.mevenide.environment.SysEnvLocationFinder;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.EventListener;

/**
 * Global IDEA settings component - mainly manages the Maven home setting.
 *
 * <p>Clients can register as listeners for settings changes via a standard
 * JavaBean listener interface ({@link GlobalSettingsListener} using the
 * {@link #addGlobalSettingsListener(GlobalSettingsListener)} method.</p>
 *
 * <p>This component is serialized via JDOM by implementing the
 * {@link JDOMExternalizable} interface.</p>
 *
 * @author Arik
 */
public class GlobalSettings implements ApplicationComponent,
                                       JDOMExternalizable {
    /**
     * The IDEA component name.
     */
    public static final String NAME = GlobalSettings.class.getName();

    //--- utility classes -----------------------------------------------------

    /**
     * The resource bundle.
     */
    private static final Res RES = Res.getInstance(GlobalSettings.class);

    /**
     * A mutex to synchronize access (since IDEA is multithreaded by nature).
     */
    private final Object LOCK = new Object();

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The currently selected maven home.
     */
    private File mavenHome;

    /**
     * Returns the maven home.
     *
     * @return File instance
     */
    public File getMavenHome() {
        synchronized (LOCK) {
            return mavenHome;
        }
    }

    /**
     * Sets the maven home to be used by the plugin.
     *
     * <p>Makes sure the maven home exists, if it is not null. Otherwise, fires a MavenHomeChanged
     * event.</p>
     *
     * @param pMavenHome the maven home
     *
     * @throws IllegalArgumentException if not null, and doesn't exist
     */
    public void setMavenHome(final File pMavenHome) throws FileNotFoundException {
        synchronized (LOCK) {
            if (pMavenHome != null && !pMavenHome.exists())
                throw new FileNotFoundException(RES.get("dir.not.exist", new Object[]{pMavenHome.getAbsolutePath()}));

            final File oldHome = mavenHome;
            mavenHome = pMavenHome;
            fireMavenHomeChangedEvent(oldHome, mavenHome);
        }
    }

    public void disposeComponent() {
    }

    public String getComponentName() {
        return NAME;
    }

    public void initComponent() {
        if(mavenHome == null) {
            final String mavenHomePath = SysEnvLocationFinder.getInstance().getMavenHome();
            if(mavenHomePath != null && mavenHomePath.trim().length() > 0) {
                final File mavenHomeFile = new File(mavenHomePath);
                if(mavenHomeFile.exists())
                    mavenHome = mavenHomeFile;
            }
        }
    }

    public void readExternal(Element pMavenSettingsElt) throws InvalidDataException {
        synchronized (LOCK) {
            final Element mavenHomeElt = pMavenSettingsElt.getChild(XMLConstants.MAVEN_HOME_ELT_NAME);
            File lMavenHome = null;
            if (mavenHomeElt != null) {
                final String mavenHomeText = mavenHomeElt.getTextTrim();
                if (mavenHomeText != null && mavenHomeText.trim().length() > 0)
                    lMavenHome = new File(mavenHomeText);
            }

            try {
                setMavenHome(lMavenHome);
            }
            catch (FileNotFoundException e) {
                Messages.showErrorDialog(e.getMessage(), UIConstants.ERROR_TITLE);
            }
        }
    }

    public void writeExternal(Element pGlobalSettingsElt) throws WriteExternalException {
        synchronized (LOCK) {
            final Element mavenHomeElt = new Element(XMLConstants.MAVEN_HOME_ELT_NAME);
            final File mavenHome = getMavenHome();
            if (mavenHome != null)
                mavenHomeElt.setText(mavenHome.getAbsolutePath());

            pGlobalSettingsElt.addContent(mavenHomeElt);
        }
    }

    /**
     * Adds a global settings listener.
     *
     * @param pListener the listener to add
     */
    public void addGlobalSettingsListener(final GlobalSettingsListener pListener) {
        synchronized (LOCK) {
            listenerList.add(GlobalSettingsListener.class, pListener);
        }
    }

    /**
     * Removes the specified listener from the listener list.
     *
     * @param pListener the listener to remove
     */
    public void removeGlobalSettingsListener(final GlobalSettingsListener pListener) {
        synchronized (LOCK) {
            listenerList.remove(GlobalSettingsListener.class, pListener);
        }
    }

    /**
     * Fires the Maven Home Changed event to all registered listeners.
     *
     * @param pOldHome the old home (before the change)
     * @param pNewHome the new home set (after the change)
     */
    protected void fireMavenHomeChangedEvent(final File pOldHome,
                                             final File pNewHome) {
        synchronized (LOCK) {
            final MavenHomeChangedEvent event = new MavenHomeChangedEvent(this,
                                                                          pOldHome,
                                                                          pNewHome);
            final EventListener[] listeners =
                    listenerList.getListeners(GlobalSettingsListener.class);
            for (int i = 0; i < listeners.length; i++) {
                GlobalSettingsListener listener = (GlobalSettingsListener) listeners[i];
                listener.mavenHomeChanged(event);
            }
        }
    }

    public static GlobalSettings getInstance() {
        return (GlobalSettings) ApplicationManager.getApplication().getComponent(NAME);
    }
}
