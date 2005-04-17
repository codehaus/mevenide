package org.mevenide.idea.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizableStringList;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jdom.Element;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.settings.XMLConstants;
import org.mevenide.idea.support.ui.UIConstants;
import org.mevenide.idea.util.Res;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Module-level component for managing the module settings.
 *
 * @author Arik
 */
public class ModuleSettings implements ModuleComponent,
                                       JDOMExternalizable {

    /**
     * The component name.
     */
    private static final String NAME = ModuleSettings.class.getName();

    /**
     * Used for resource loading.
     */
    private static final Res RES = Res.getInstance(ModuleSettings.class);

    /**
     * Used to synchronize access to this component (since IDEA is multithreaded by nature).
     */
    private final Object LOCK = new Object();

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The module this component manages.
     */
    private final Module module;

    /**
     * The list of favorite goals attached to this module.
     */
    private final Collection favoriteGoals = Collections.synchronizedSet(new HashSet(5));

    /**
     * The Maven POM file attached to this module.
     */
    private File pomFile;

    /**
     * The project context used by mevenide.
     */
    private IProjectContext projectContext;

    /**
     * The query context used by mevenide.
     */
    private IQueryContext queryContext;

    private ILocationFinder locationFinder;

    /**
     * Creates an instance for the specified module.
     *
     * @param pModule the module to manage
     */
    public ModuleSettings(final Module pModule) {
        module = pModule;
    }

    public String getComponentName() {
        return NAME;
    }

    public void projectOpened() {
    }

    public void initComponent() {
    }

    public Collection getFavoriteGoals() {
        return favoriteGoals;
    }

    public Module getModule() {
        return module;
    }

    public ProjectJdk getJdk() {
        ProjectJdk jdk = ModuleRootManager.getInstance(module).getJdk();
        if (jdk == null)
            jdk = ProjectRootManager.getInstance(module.getProject()).getProjectJdk();

        return jdk;
    }

    public IProjectContext getProjectContext() {
        synchronized (LOCK) {
            return projectContext;
        }
    }

    public IQueryContext getQueryContext() {
        synchronized (LOCK) {
            return queryContext;
        }
    }

    public ILocationFinder getLocationFinder() {
        synchronized (LOCK) {
            if(pomFile == null)
                return null;
            else if(locationFinder == null)
                locationFinder = new ModuleLocationFinder(module);

            return locationFinder;
        }
    }

    public void moduleAdded() {
    }

    public void projectClosed() {
    }

    public void disposeComponent() {
    }

    /**
     * Returns the POM file attached to this module.
     *
     * @return the POM file, or <code>null</code> if this module has no POM file attached.
     */
    public File getPomFile() {
        synchronized (LOCK) {
            return pomFile;
        }
    }

    /**
     * Sets the POM file for this module. A module can have no POM file (by passing
     * <code>null</code> to this method).
     *
     * @param pPomFile the new POM file (or <code>null</code>)
     */
    public void setPomFile(final File pPomFile) throws FileNotFoundException {
        synchronized (LOCK) {
            if(pPomFile != null) {
                if (!pPomFile.exists())
                    throw new FileNotFoundException(RES.get("file.not.exist",
                                                        new Object[]{pPomFile.getAbsolutePath()}));

                if(!pPomFile.getName().equals("project.xml"))
                    throw new FileNotFoundException(RES.get("not.project.xml.file"));
            }

            final File oldPomFile = pomFile;
            pomFile = pPomFile;
            if(pomFile != null) {
                queryContext = new DefaultQueryContext(pomFile.getParentFile());
                projectContext = queryContext.getPOMContext();
                projectContext.getFinalProject().setFile(pomFile);
            }
            else {
                queryContext = null;
                projectContext = null;
            }
            firePomFileChangedEvent(oldPomFile, pomFile);
        }
    }

    public void readExternal(Element element) throws InvalidDataException {
        synchronized (LOCK) {
            favoriteGoals.clear();

            final Element favoritesElt = element.getChild(XMLConstants.FAVORITE_GOALS_ELT_NAME);
            if (favoritesElt != null) {
                JDOMExternalizableStringList list = new JDOMExternalizableStringList();
                list.readExternal(favoritesElt);
                favoriteGoals.addAll(list);
            }

            final String mavenProjectFilename =
                    element.getAttributeValue(XMLConstants.POM_FILE_ATTR_NAME);
            if (mavenProjectFilename != null && mavenProjectFilename.trim().length() > 0)
                try {
                    setPomFile(new File(mavenProjectFilename));
                }
                catch (FileNotFoundException e) {
                    Messages.showErrorDialog(e.getMessage(), UIConstants.ERROR_TITLE);
                }
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        synchronized (LOCK) {
            if (favoriteGoals.size() > 0) {
                final Element favoritesElt = new Element(XMLConstants.FAVORITE_GOALS_ELT_NAME);
                JDOMExternalizableStringList list = new JDOMExternalizableStringList();
                list.addAll(favoriteGoals);
                list.writeExternal(favoritesElt);
                element.addContent(favoritesElt);
            }

            if (pomFile != null)
                element.setAttribute(XMLConstants.POM_FILE_ATTR_NAME, pomFile.getAbsolutePath());
        }
    }

    /**
     * Adds a global settings listener.
     *
     * @param pListener the listener to add
     */
    public void addModuleSettingsListener(final ModuleSettingsListener pListener) {
        synchronized (LOCK) {
            listenerList.add(ModuleSettingsListener.class, pListener);
        }
    }

    /**
     * Removes the specified listener from the listener list.
     *
     * @param pListener the listener to remove
     */
    public void removeModuleSettingsListener(final ModuleSettingsListener pListener) {
        synchronized (LOCK) {
            listenerList.remove(ModuleSettingsListener.class, pListener);
        }
    }

    /**
     * Fires the POM file changed event to all registered listeners.
     *
     * @param pOldPomFile the old pom (before the change)
     * @param pNewPomFile the new pom set (after the change)
     */
    protected void firePomFileChangedEvent(final File pOldPomFile,
                                           final File pNewPomFile) {
        synchronized (LOCK) {
            final PomFileChangedEvent event = new PomFileChangedEvent(this,
                                                                      pOldPomFile,
                                                                      pNewPomFile);
            final EventListener[] listeners =
                    listenerList.getListeners(ModuleSettingsListener.class);
            for (int i = 0; i < listeners.length; i++) {
                ModuleSettingsListener listener = (ModuleSettingsListener) listeners[i];
                listener.pomFileChanged(event);
            }
        }
    }

    protected void fireFavoriteGoalsChangedEvent(final Collection pOldFavorites) {
        synchronized(LOCK) {
            final FavoriteGoalsChangedEvent event = new FavoriteGoalsChangedEvent(this,
                                                                                  favoriteGoals,
                                                                                  pOldFavorites);
            final EventListener[] listeners =
                    listenerList.getListeners(ModuleSettingsListener.class);
            for (int i = 0; i < listeners.length; i++) {
                ModuleSettingsListener listener = (ModuleSettingsListener) listeners[i];
                listener.favoriteGoalsChanged(event);
            }
        }
    }

    public static ModuleSettings getInstance(final Module pModule) {
        return (ModuleSettings) pModule.getComponent(NAME);
    }

    public void setFavoriteGoals(final Collection pFavoriteGoals) {
        synchronized (LOCK) {
            final Collection oldFavorites = new HashSet(favoriteGoals);
            favoriteGoals.clear();
            favoriteGoals.addAll(pFavoriteGoals);
            fireFavoriteGoalsChangedEvent(oldFavorites);
        }
    }
}
