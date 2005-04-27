package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.JDOMExternalizableStringList;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.mevenide.idea.GoalGrabbingException;
import org.mevenide.idea.GoalsChangedEvent;
import org.mevenide.idea.GoalsProvider;
import org.mevenide.idea.GoalsProviderListener;
import org.mevenide.idea.util.ui.UIUtils;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;

/**
 * @author Arik
 */
public class ModuleGoalsProvider implements ModuleComponent,
                                            JDOMExternalizable,
                                            GoalsProvider,
                                            GoalsProviderListener {
    /**
     * The class name of the goals provider this component will use. This is specified via a string, since it
     * is defined lower in the classloader hierarchy, so we cannot reference the actual class here. The
     * communication between this component and the provider is done via the {@link GoalsProvider} interface.
     */
    private static final String MODULE_GOALS_PROVIDER_CLASS_NAME =
            "org.mevenide.idea.model.goals.grabber.ModuleGoalsGrabber";

    /**
     * The class name of the goals provider this component will use. This is specified via a string, since it
     * is defined lower in the classloader hierarchy, so we cannot reference the actual class here. The
     * communication between this component and the provider is done via the {@link GoalsProvider} interface.
     */
    private static final String FAVORITE_MODULE_GOALS_PROVIDER_CLASS_NAME =
            "org.mevenide.idea.model.goals.grabber.ModuleFavoriteGoalsGrabber";

    /**
     * The parameter signature for the constructor of the goals provider we will use.
     */
    private static final Class[] GOALS_PROVIDER_CTOR_PARAM_TYPES = new Class[]{Module.class,
                                                                               File.class};

    /**
     * Component name.
     */
    private static final String NAME = ModuleGoalsProvider.class.getName();

    /**
     * Logging.
     */
    private static final Log LOG = LogFactory.getLog(ModuleGoalsProvider.class);

    /**
     * The IDEA module.
     */
    private final Module module;

    /**
     * Event listener support.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * The list of favorite goals attached to this module.
     */
    private final Collection favoriteGoals = new HashSet(5);

    /**
     * The goals provider that retrieves the selected goals for this module.
     */
    private GoalsProvider goalsProvider;

    /**
     * The goals provider that retrieves the all available goals for this module.
     */
    private GoalsProvider fullGoalsProvider;

    /**
     * Creates an instance of this manager for the specified module.
     *
     * @param pModule the module to provide goals for
     */
    public ModuleGoalsProvider(final Module pModule) {
        module = pModule;
    }

    /**
     * Returns the module for this component.
     *
     * @return IDEA module instance
     */
    public Module getModule() {
        return module;
    }

    /**
     * Returns the favorite goals collection for this module.
     *
     * @return an unmodifiable collection
     */
    public Collection getFavoriteGoals() {
        return Collections.unmodifiableCollection(favoriteGoals);
    }

    public void setFavoriteGoals(final Collection pGoals) throws GoalGrabbingException {
        favoriteGoals.clear();
        if (pGoals != null)
            favoriteGoals.addAll(pGoals);
        goalsProvider.refreshGoals();
    }

    public String getComponentName() {
        return NAME;
    }

    public void initComponent() {
        if (LOG.isTraceEnabled())
            LOG.trace(NAME + " initialized.");
    }

    public void disposeComponent() {
        if (LOG.isTraceEnabled())
            LOG.trace(NAME + " disposed.");
    }

    public void projectOpened() {
    }

    public void moduleAdded() {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        moduleSettings.addModuleSettingsListener(new GoalsModuleSettingsListener());

        final File pomFile = moduleSettings.getPomFile();
        initializeFromPom(pomFile);
    }

    protected void initializeFromPom(final File pPomFile) {
        if(goalsProvider != null)
            goalsProvider.removeGoalsProviderListener(this);
        if(fullGoalsProvider != null)
            fullGoalsProvider.removeGoalsProviderListener(this);

        /*
                      if (goalsProvider != null)
                          goalsProvider.removeGoalsProviderListener(this);

                      //
                      //if we reset the goals provider (or if an exception occured) we
                      //should manually fire the goals changed event, since no refresh was
                      //called on the delegated provider
                      //
                      if (goalsProvider == null)
                          fireGoalsChangedEvent();
          */

        goalsProvider = null;
        fullGoalsProvider = null;

        if (pPomFile != null && pPomFile.exists()) {
            try {
                goalsProvider = new ModuleFavoriteGoalsGrabber(module, pPomFile);
                goalsProvider.refreshGoals();
                goalsProvider.addGoalsProviderListener(this);

                fullGoalsProvider = new ModuleGoalsGrabber(module, pPomFile);
                fullGoalsProvider.refreshGoals();
                fullGoalsProvider.addGoalsProviderListener(this);
            }
            catch (GoalGrabbingException e) {
                UIUtils.showError(e);
                LOG.error(e.getMessage(), e);
                goalsProvider = null;
                fullGoalsProvider = null;
            }
        }
    }

    public void projectClosed() {
    }

    public void readExternal(final Element pElement) throws InvalidDataException {
        if (LOG.isTraceEnabled())
            LOG.trace("Loading " + NAME + " from XML configuration");

        //
        //load favorite goals list
        //
        final Collection favoriteGoals = new HashSet(10);
        final Element favoritesElt = pElement.getChild("favoriteGoals");
        if (favoritesElt != null) {
            JDOMExternalizableStringList list = new JDOMExternalizableStringList();
            list.readExternal(favoritesElt);
            favoriteGoals.addAll(list);
        }
        this.favoriteGoals.addAll(favoriteGoals);

        if (LOG.isTraceEnabled())
            LOG.trace("Finished loading " + NAME);
    }

    public void writeExternal(final Element pElement) throws WriteExternalException {
        if (LOG.isTraceEnabled())
            LOG.trace("Writing " + NAME + " to XML configuration");

        //
        //save favorite goals list
        //
        final Collection favoriteGoals = getFavoriteGoals();
        if (favoriteGoals.size() > 0) {
            final Element favoritesElt = new Element("favoriteGoals");
            JDOMExternalizableStringList list = new JDOMExternalizableStringList();
            list.addAll(favoriteGoals);
            list.writeExternal(favoritesElt);
            pElement.addContent(favoritesElt);
        }

        if (LOG.isTraceEnabled())
            LOG.trace("Finished writing " + NAME);
    }

    public String getDescription(String fullyQualifiedGoalName) {
        if (goalsProvider == null)
            return null;

        return goalsProvider.getDescription(fullyQualifiedGoalName);
    }

    public String[] getGoals(String plugin) {
        if (goalsProvider == null)
            return new String[0];

        return goalsProvider.getGoals(plugin);
    }

    public String getOrigin(String fullyQualifiedGoalName) {
        if (goalsProvider == null)
            return null;

        return goalsProvider.getOrigin(fullyQualifiedGoalName);
    }

    public String[] getPlugins() {
        if (goalsProvider == null)
            return new String[0];

        return goalsProvider.getPlugins();
    }

    public String[] getPrereqs(String fullyQualifiedGoalName) {
        if (goalsProvider == null)
            return new String[0];

        return goalsProvider.getPrereqs(fullyQualifiedGoalName);
    }

    public GoalsProvider getFullGoalsProvider() {
        return fullGoalsProvider;
    }

    public void refreshGoals() throws GoalGrabbingException {
        if (goalsProvider != null)
            goalsProvider.refreshGoals();
    }

    /**
     * Adds a module goals listener.
     *
     * @param pListener the listener to notify on settings changes
     */
    public void addGoalsProviderListener(final GoalsProviderListener pListener) {
        listenerList.add(GoalsProviderListener.class, pListener);
    }

    /**
     * Removes the specified listener from the listener list.
     *
     * @param pListener the listener to remove
     */
    public void removeGoalsProviderListener(final GoalsProviderListener pListener) {
        listenerList.remove(GoalsProviderListener.class, pListener);
    }

    /**
     * Fires the POM file-selection-changed event to all registered listeners.
     */
    protected void fireGoalsChangedEvent() {
        final GoalsChangedEvent event = new GoalsChangedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(GoalsProviderListener.class);
        for (int i = 0; i < listeners.length; i++) {
            GoalsProviderListener listener = (GoalsProviderListener) listeners[i];
            listener.goalsChanged(event);
        }
    }

    public void goalsChanged(GoalsChangedEvent pEvent) {
        fireGoalsChangedEvent();
    }

    /**
     * Returns the module goals provider instance for the specified module.
     *
     * @param pModule the module to retrieve the goals provider for
     *
     * @return a ModuleGoalsProvider instance
     */
    public static ModuleGoalsProvider getInstance(final Module pModule) {
        return (ModuleGoalsProvider) pModule.getComponent(ModuleGoalsProvider.class);
    }

    private class GoalsModuleSettingsListener implements ModuleSettingsListener {
        public void modulePomSelectionChanged(final PomSelectionChangedEvent pEvent) {
            final ModuleSettings moduleSettings = pEvent.getModuleSettings();
            final File pomFile = moduleSettings.getPomFile();
            initializeFromPom(pomFile);
        }
    }
}
