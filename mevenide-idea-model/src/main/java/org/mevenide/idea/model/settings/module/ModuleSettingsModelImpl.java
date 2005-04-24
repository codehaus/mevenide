package org.mevenide.idea.model.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IProjectContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.common.settings.module.ModuleGoalsChangedEvent;
import org.mevenide.idea.common.settings.module.ModuleSettingsListener;
import org.mevenide.idea.common.settings.module.ModuleSettingsModel;
import org.mevenide.idea.common.settings.module.PomFileChangedEvent;
import org.mevenide.idea.common.ui.UI;
import org.mevenide.idea.common.util.Res;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Arik
 */
public class ModuleSettingsModelImpl implements ModuleSettingsModel, IGoalsGrabber {
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
    private static final String DESC_NOT_AVAIL = "description not available";

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
            fireModuleGoalsChangedEvent();
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
                try {
                    goalsGrabber = new ModuleGoalsGrabber(this, queryContext);
                }
                catch (Exception e) {
                    Messages.showErrorDialog(module.getProject(),
                                             e.getMessage(),
                                             UI.ERR_TITLE);
                    LOG.error(e.getMessage(), e);
                    goalsGrabber = new IGoalsGrabber() {
                        public String getDescription(String fullyQualifiedGoalName) {
                            return null;
                        }

                        public String[] getGoals(String plugin) {
                            return new String[0];
                        }

                        public String getName() {
                            return null;
                        }

                        public String getOrigin(String fullyQualifiedGoalName) {
                            return null;
                        }

                        public String[] getPlugins() {
                            return new String[0];
                        }

                        public String[] getPrereqs(String fullyQualifiedGoalName) {
                            return new String[0];
                        }

                        public void refresh() throws Exception {
                        }
                    };
                }
                refreshGoalsGrabber = true;
            }
            else {
                queryContext = null;
                projectContext = null;
                goalsGrabber = null;
                locationFinder = null;
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

    protected void fireModuleGoalsChangedEvent() {
        synchronized (LOCK) {
            final ModuleGoalsChangedEvent event = new ModuleGoalsChangedEvent(this);
            final EventListener[] listeners =
                    listenerList.getListeners(ModuleSettingsListener.class);
            for (int i = 0; i < listeners.length; i++) {
                ModuleSettingsListener listener = (ModuleSettingsListener) listeners[i];
                listener.moduleGoalsChanged(event);
            }
        }
    }

    public void initComponent() {
    }

    public String[] getPlugins() {
        if (goalsGrabber == null)
            return new String[0];

        ensureGrabberRefreshed();

        return goalsGrabber.getPlugins();
    }

    public String[] getGoals(final String pPlugin) {
        if (goalsGrabber == null)
            return new String[0];

        ensureGrabberRefreshed();

        return goalsGrabber.getGoals(pPlugin);
    }

    public SortedMap getPluginsMap() {
        try {
            final IGoalsGrabber grabber;
            if(locationFinder == null)
                grabber = new DefaultGoalsGrabber();
            else
                grabber = new DefaultGoalsGrabber(locationFinder);

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

    public String getDescription(String fullyQualifiedGoalName) {
        if (goalsGrabber == null)
            return DESC_NOT_AVAIL;

        ensureGrabberRefreshed();

        return goalsGrabber.getDescription(fullyQualifiedGoalName);
    }

    public String getName() {
        if (goalsGrabber == null)
            return "Name not available";

        ensureGrabberRefreshed();

        return goalsGrabber.getName();
    }

    public String getOrigin(String fullyQualifiedGoalName) {
        if (goalsGrabber == null)
            return "Origin not available";

        ensureGrabberRefreshed();

        return goalsGrabber.getOrigin(fullyQualifiedGoalName);
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        if (goalsGrabber == null)
            return new String[0];

        ensureGrabberRefreshed();

        return goalsGrabber.getPrereqs(fullyQualifiedGoalName);
    }

    public void refresh() throws Exception {
        ensureGrabberRefreshed(true);
    }

    private void ensureGrabberRefreshed() {
        ensureGrabberRefreshed(false);
    }

    private void ensureGrabberRefreshed(final boolean pForceRefresh) {
        if(pForceRefresh || (goalsGrabber != null && refreshGoalsGrabber)) {
            try {
                refreshGoalsGrabber = false;
                goalsGrabber.refresh();
                fireModuleGoalsChangedEvent();
            }
            catch (Exception e) {
                Messages.showErrorDialog(module.getProject(), e.getMessage(), UI.ERR_TITLE);
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
