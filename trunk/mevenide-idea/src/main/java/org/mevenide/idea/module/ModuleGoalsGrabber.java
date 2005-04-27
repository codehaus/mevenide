package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import org.apache.commons.lang.StringUtils;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.goals.grabber.AbstractGoalsGrabber;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.idea.GoalGrabbingException;
import org.mevenide.idea.GoalsChangedEvent;
import org.mevenide.idea.GoalsProvider;
import org.mevenide.idea.GoalsProviderListener;
import org.mevenide.idea.util.goals.GoalsHelper;
import org.mevenide.idea.module.ModuleLocationFinder;

import javax.swing.event.EventListenerList;
import java.io.File;
import java.util.EventListener;

/**
 * A module-aware goals grabber, which returns only the module's favorite goals, that do exist in the global
 * goals list.
 *
 * @author Arik
 * @todo guess common goals according to module type (ejb, war, etc).
 * @todo create a synchronized, generic, goals grabber (to wrap this impl)
 */
public class ModuleGoalsGrabber extends AbstractGoalsGrabber implements GoalsProvider {
    /**
     * The module for which this instance will find goals for.
     */
    protected final Module module;

    /**
     * The goals grabber that fetches the global plugins goals (not module specific). This is merged with
     * default and favorite module goals.
     */
    protected final IGoalsGrabber globalGoalsGrabber;

    /**
     * The listener management support, for notifying when refreshed.
     */
    protected final EventListenerList listenerList = new EventListenerList();

    /**
     * Creates an instance for the given module, using the given POM file.
     *
     * @param pModule  the module for which to "grab" goals.
     * @param pPomFile the POM file used for introspection of goals
     *
     * @throws GoalGrabbingException if errors occur
     */
    public ModuleGoalsGrabber(final Module pModule, final File pPomFile) throws GoalGrabbingException {
        this(pModule, new DefaultQueryContext(pPomFile.getParentFile()));
    }

    /**
     * Creates an instance for the given module, using the given query context for goal introspection.
     *
     * @param pModule       the module for which to "grab" goals.
     * @param pQueryContext the query context to use for creating a location finder
     *
     * @throws GoalGrabbingException if errors occur
     */
    public ModuleGoalsGrabber(final Module pModule, final IQueryContext pQueryContext)
            throws GoalGrabbingException {
        this(pModule, new ModuleLocationFinder(pQueryContext, pModule));
    }

    /**
     * Creates an instance for the given module, using the given location finder for goal introspection.
     *
     * @param pModule         the module for which to "grab" goals.
     * @param pLocationFinder the location finder that locate important locations for introspection
     *
     * @throws GoalGrabbingException if errors occur
     */
    public ModuleGoalsGrabber(final Module pModule, final ILocationFinder pLocationFinder)
            throws GoalGrabbingException {
        module = pModule;
        try {
            globalGoalsGrabber = new DefaultGoalsGrabber(pLocationFinder);
        }
        catch (Exception e) {
            throw new GoalGrabbingException(e);
        }
    }

    public String getName() {
        return "Module Goals Grabber";
    }

    public final void refresh() throws Exception {
        super.refresh();
        refreshInternal();
        fireGoalsChangedEvent();
    }

    protected void refreshInternal() throws Exception {
        globalGoalsGrabber.refresh();
        final String[] plugins = globalGoalsGrabber.getPlugins();
        for (int i = 0; i < plugins.length; i++) {
            final String plugin = plugins[i];
            final String[] goals = globalGoalsGrabber.getGoals(plugin);
            for (int j = 0; j < goals.length; j++) {
                final String goal = goals[j];
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                final String desc = globalGoalsGrabber.getDescription(fqName);
                final String[] prereqs = globalGoalsGrabber.getPrereqs(fqName);
                final String prereqsString = StringUtils.join(prereqs, ',');
                final String props;
                if(desc == null)
                    props = ">" + prereqsString;
                else
                    props = desc + ">" + prereqsString;
                registerGoal(fqName, desc + ">" + props);
            }
        }
    }

    public void refreshGoals() throws GoalGrabbingException {
        try {
            refresh();
        }
        catch (Exception e) {
            if (e instanceof GoalGrabbingException)
                throw (GoalGrabbingException) e;
            else
                throw new GoalGrabbingException(e);
        }
    }

    public void addGoalsProviderListener(GoalsProviderListener pListener) {
        listenerList.add(GoalsProviderListener.class, pListener);
    }

    public void removeGoalsProviderListener(GoalsProviderListener pListener) {
        listenerList.remove(GoalsProviderListener.class, pListener);
    }

    protected void fireGoalsChangedEvent() {
        final GoalsChangedEvent event = new GoalsChangedEvent(this);
        final EventListener[] listeners = listenerList.getListeners(GoalsProviderListener.class);
        for (int i = 0; i < listeners.length; i++) {
            final GoalsProviderListener listener = (GoalsProviderListener) listeners[i];
            listener.goalsChanged(event);
        }
    }
}
