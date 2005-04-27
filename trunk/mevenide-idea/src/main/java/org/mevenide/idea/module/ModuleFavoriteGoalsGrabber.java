package org.mevenide.idea.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.mevenide.context.IQueryContext;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.GoalGrabbingException;
import org.mevenide.idea.module.ModuleGoalsProvider;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Arik
 */
public class ModuleFavoriteGoalsGrabber extends ModuleGoalsGrabber {
    public ModuleFavoriteGoalsGrabber(final Module pModule,
                                      final ILocationFinder pLocationFinder) throws GoalGrabbingException {
        super(pModule, pLocationFinder);
    }

    public ModuleFavoriteGoalsGrabber(final Module pModule,
                                      final File pPomFile) throws GoalGrabbingException {
        super(pModule, pPomFile);
    }

    public ModuleFavoriteGoalsGrabber(final Module pModule,
                                      final IQueryContext pQueryContext) throws GoalGrabbingException {
        super(pModule, pQueryContext);
    }

    protected void refreshInternal() throws Exception {

        //
        //refresh the list of global plugins and goals
        //
        globalGoalsGrabber.refresh();

        //
        //get the favorite goals defined for the module, and register those that
        //exist in the global goals grabber.
        //
        final Collection favorites = getModuleGoals();
        for (Iterator iterator = favorites.iterator(); iterator.hasNext();) {
            final String goal = (String) iterator.next();

            //if goal does not exist in global grabber, skip it
            final String plugin = globalGoalsGrabber.getOrigin(goal);
            if (plugin == null || plugin.trim().length() == 0)
                continue;

            final String description = globalGoalsGrabber.getDescription(goal);
            final String[] prereqs = globalGoalsGrabber.getPrereqs(goal);
            final String prereqsString = StringUtils.join(prereqs, ',');

            //register goal
            final String props;
            if (description == null)
                props = ">" + prereqsString;
            else
                props = description + ">" + prereqsString;
            registerGoal(goal, props);
        }
    }

    /**
     * Interpolates available goals for the module by intersecting the available global goals with the goals
     * marked as favorite for the module, and common goals for the module, based on the module type.
     *
     * @return collection of fully qualified goal names
     */
    protected Collection getModuleGoals() {
        final String[] goals;

        final ModuleGoalsProvider moduleSettings = ModuleGoalsProvider.getInstance(module);
        final Collection favorites = new ArrayList(moduleSettings.getFavoriteGoals());

        final ModuleType moduleType = module.getModuleType();
        if (moduleType.equals(ModuleType.EJB))
            goals = globalGoalsGrabber.getGoals("ejb");
        else if (moduleType.equals(ModuleType.WEB))
            goals = globalGoalsGrabber.getGoals("war");
        else if (moduleType.equals(ModuleType.JAVA))
            goals = globalGoalsGrabber.getGoals("java");
        else if (moduleType.equals(ModuleType.J2EE_APPLICATION))
            goals = globalGoalsGrabber.getGoals("ear");
        else
            return favorites;

        for (int i = 0; i < goals.length; i++)
            favorites.add(goals[i]);

        return favorites;
    }
}
