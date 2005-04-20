package org.mevenide.idea.model.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.common.settings.module.ModuleSettingsModel;
import org.mevenide.idea.common.settings.module.ModuleSettingsListener;
import org.mevenide.idea.common.settings.module.PomFileChangedEvent;
import org.mevenide.idea.common.settings.module.FavoriteGoalsChangedEvent;
import org.mevenide.idea.common.util.Res;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Arik
 */
public class ModuleSettingsModelImpl implements ModuleSettingsModel {
    private static final Log LOG = LogFactory.getLog(ModuleSettingsModelImpl.class);
    private static final Res RES = Res.getInstance(ModuleSettingsModelImpl.class);

    /**
     * Used to synchronized parts of the code that are not re-entrant.
     */
    private final Object LOCK = new Object();

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The module this component manages.
     */
    private Module module;

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

    /**
     * Used to locate Maven paths and POMs.
     */
    private ILocationFinder locationFinder;

    /**
     * The goals grabber that retrieves available plugins and goals for this
     * module.
     */
    private IGoalsGrabber goalsGrabber;
    private boolean refreshGoalsGrabber = true;

    public void addModuleSettingsListener(ModuleSettingsListener pListener) {
        synchronized (LOCK) {
            listenerList.add(ModuleSettingsListener.class, pListener);
        }
    }

    public Collection getFavoriteGoals() {
        synchronized (LOCK) {
            return favoriteGoals;
        }
    }

    public ProjectJdk getJdk() {
        synchronized (LOCK) {
            ProjectJdk jdk = ModuleRootManager.getInstance(module).getJdk();
            if (jdk == null)
                jdk = ProjectRootManager.getInstance(module.getProject()).getProjectJdk();

            return jdk;
        }
    }

    public Module getModule() {
        synchronized (LOCK) {
            return module;
        }
    }

    public File getPomFile() {
        synchronized (LOCK) {
            return pomFile;
        }
    }

    public void removeModuleSettingsListener(final ModuleSettingsListener pListener) {
        synchronized (LOCK) {
            listenerList.remove(ModuleSettingsListener.class, pListener);
        }
    }

    public void setFavoriteGoals(final Collection pFavoriteGoals) {
        synchronized (LOCK) {
            final Collection oldFavorites = new HashSet(favoriteGoals);
            favoriteGoals.clear();
            favoriteGoals.addAll(pFavoriteGoals);
            refreshGoalsGrabber = true;
            fireFavoriteGoalsChangedEvent(oldFavorites);
        }
    }

    public void setModule(Module pModule) {
        synchronized (LOCK) {
            if(module != null)
                throw new IllegalStateException(RES.get("module.has.been.set"));

            module = pModule;
        }
    }

    public void setPomFile(File pPomFile) throws FileNotFoundException {
        synchronized (LOCK) {
            if (pPomFile != null) {
                if (!pPomFile.exists())
                    throw new FileNotFoundException(RES.get("file.not.exist",
                                                            new Object[]{pPomFile.getAbsolutePath()}));

                if (!pPomFile.getName().equals("project.xml"))
                    throw new FileNotFoundException(RES.get("not.project.xml.file"));
            }

            final File oldPomFile = pomFile;
            pomFile = pPomFile;
            if (pomFile != null) {
                queryContext = new DefaultQueryContext(pomFile.getParentFile());
                projectContext = queryContext.getPOMContext();
                projectContext.getFinalProject().setFile(pomFile);
                locationFinder = new ModuleLocationFinder(queryContext, module);
                goalsGrabber = new ModuleGoalsGrabber(this);
                refreshGoalsGrabber = true;
            }
            else {
                queryContext = null;
                projectContext = null;
                goalsGrabber = null;
            }
            firePomFileChangedEvent(oldPomFile, pomFile);
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
        synchronized (LOCK) {
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

    public void initComponent() {
    }

    public String[] getPlugins() {
        if (goalsGrabber == null)
            return new String[0];

        if(refreshGoalsGrabber)
            try {
                refreshGoalsGrabber = false;
                goalsGrabber.refresh();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }

        return goalsGrabber.getPlugins();
    }

    public String[] getGoals(final String pPlugin) {
        if (goalsGrabber == null)
            return new String[0];

        if (refreshGoalsGrabber)
            try {
                refreshGoalsGrabber = false;
                goalsGrabber.refresh();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }

        return goalsGrabber.getGoals(pPlugin);
    }

    public SortedMap getPluginsMap() {
        try {
            final IGoalsGrabber grabber = new DefaultGoalsGrabber(locationFinder);
            grabber.refresh();

            final String[] plugins = grabber.getPlugins();
            final SortedMap map = new TreeMap();
            for (int i = 0; i < plugins.length; i++) {
                final String plugin = plugins[i];
                final String[] goals = grabber.getGoals(plugin);
                map.put(plugin, goals);
            }

            return map;
        }
        catch (Exception e) {
            Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
            LOG.error(e.getMessage(), e);
            return new TreeMap();
        }
    }
}
