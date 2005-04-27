package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mevenide.idea.Res;
import org.mevenide.idea.util.ui.UIUtils;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.EventListener;

/**
 * @author Arik
 */
public class ModuleSettings implements ModuleComponent, JDOMExternalizable {
    /**
     * Component name.
     */
    private static final String NAME = ModuleSettings.class.getName();

    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ModuleSettings.class);

    /**
     * Resources.
     */
    private static final Res RES = Res.getInstance(ModuleSettings.class);

    /**
     * The IDEA module.
     */
    private final Module module;

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The POM file attached to this module.
     */
    private File pomFile;

    /**
     * Creates a module settings manager for the specified module.
     *
     * @param pModule
     */
    public ModuleSettings(final Module pModule) {
        module = pModule;
    }

    public String getComponentName() {
        return NAME;
    }

    /**
     * Returns the module associated with this manager.
     *
     * @return
     */
    public Module getModule() {
        return module;
    }

    /**
     * Returns the JDK associated with this module. If the module uses the project-designated JDK, that JDK is
     * returned. If the JDK is invalid, <code>null</code> is returned.
     *
     * @return the JDK, or <code>null</code> if not defined/invalid
     */
    public ProjectJdk getJdk() {
        final ProjectJdk jdk = ModuleRootManager.getInstance(module).getJdk();
        if (jdk == null)
            return ProjectRootManager.getInstance(module.getProject()).getProjectJdk();
        else
            return jdk;
    }

    /**
     * Returns the POM file attached to this module.
     *
     * @return the POM file, or <code>null</code> if no pom is attached
     */
    public File getPomFile() {
        return pomFile;
    }

    /**
     * Sets the POM file for this module. The file must point to an existing file called "project.xml", or to
     * be <code>null</code>.
     *
     * @param pPomFile the POM file, can be <code>null</code>
     *
     * @throws FileNotFoundException if the file does not exist, points to a directory, or not named
     *                               "project.xml"
     */
    public void setPomFile(final File pPomFile) throws FileNotFoundException {
        if (pPomFile != null && !pPomFile.isFile())
            throw new FileNotFoundException(RES.get("pom.file.error"));

        if (!pPomFile.getName().equalsIgnoreCase("project.xml"))
            throw new FileNotFoundException(RES.get("pom.file.name.error"));

        if(pomFile == pPomFile)
            return;

        if(pomFile != null && pomFile.equals(pPomFile))
            return;
        else if(pPomFile.equals(pomFile))
            return;

        pomFile = pPomFile;
        firePomFileChangedEvent();
    }

    /**
     * Adds a module settings listener.
     *
     * @param pListener the listener to notify on settings changes
     */
    public void addModuleSettingsListener(final ModuleSettingsListener pListener) {
        listenerList.add(ModuleSettingsListener.class, pListener);
    }

    /**
     * Removes the specified listeners from the listener list.
     *
     * @param pListener the listener to remove
     */
    public void removeModuleSettingsListener(final ModuleSettingsListener pListener) {
        listenerList.remove(ModuleSettingsListener.class, pListener);
    }

    /**
     * Fires the POM file-selection-changed event to all registered listeners.
     */
    protected void firePomFileChangedEvent() {
        final PomSelectionChangedEvent event = new PomSelectionChangedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(ModuleSettingsListener.class);
        for (int i = 0; i < listeners.length; i++) {
            ModuleSettingsListener listener = (ModuleSettingsListener) listeners[i];
            listener.modulePomSelectionChanged(event);
        }
    }

    public void projectOpened() {
    }

    public void moduleAdded() {
    }

    public void projectClosed() {
    }

    /**
     * Initializes the manager. Specifically, if no POM is defined for this
     * module, this method tries to guess by searching for a POM file in the
     * module's directory named "project.xml", if uses it.
     */
    public void initComponent() {
        if(LOG.isTraceEnabled())
            LOG.trace(NAME + " initialized.");

        if(pomFile == null) {
            final File dir = new File(module.getModuleFile().getParent().getPath());
            final File pom = new File(dir, "project.xml");
            if(pom.isFile())
                try {
                    setPomFile(pom);
                }
                catch (FileNotFoundException e) {
                    //IGNORE - this is just a fallback, and if it fails, continue doing nothing
                    LOG.trace(e.getMessage(), e);
                }
        }
    }

    public void disposeComponent() {
        if (LOG.isTraceEnabled())
            LOG.trace(NAME + " disposed.");
    }

    public void readExternal(final Element pElement) throws InvalidDataException {
        if(LOG.isTraceEnabled())
            LOG.trace("Loading " + NAME + " from XML configuration");

        final String pomFilename = pElement.getAttributeValue("pomFile");
        if (pomFilename != null && pomFilename.trim().length() > 0)
            try {
                setPomFile(new File(pomFilename));
            }
            catch (FileNotFoundException e) {
                UIUtils.showError(e);
                LOG.error(e.getMessage(), e);
            }

        if (LOG.isTraceEnabled())
            LOG.trace("Finished loading " + NAME);
    }

    public void writeExternal(final Element pElement) throws WriteExternalException {
        if (LOG.isTraceEnabled())
            LOG.trace("Saving " + NAME + " from XML configuration");

        final File pomFile = getPomFile();
        if (pomFile != null && pomFile.isFile())
            pElement.setAttribute("pomFile", pomFile.getAbsolutePath());

        if (LOG.isTraceEnabled())
            LOG.trace("Finished saving " + NAME);
    }

    /**
     * Returns the module settings instance for the specified module.
     *
     * @param pModule the module to retrieve the settings manager for
     * @return a ModuleSettings instance
     */
    public static ModuleSettings getInstance(final Module pModule) {
        return (ModuleSettings) pModule.getComponent(ModuleSettings.class);
    }
}
