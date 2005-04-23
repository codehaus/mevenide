package org.mevenide.idea.model.settings.module;

import org.mevenide.goals.grabber.GoalsGrabbersAggregator;
import org.mevenide.goals.grabber.IGoalsGrabber;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.goals.grabber.DefaultGoalsGrabber;
import org.mevenide.idea.common.settings.module.ModuleSettingsModel;
import org.mevenide.idea.common.util.GoalsHelper;
import org.mevenide.context.IQueryContext;

import java.io.File;
import java.util.*;

/**
 * @author Arik
 */
public class ModuleGoalsGrabber extends GoalsGrabbersAggregator {
    private static final String MAVEN_XML_FILE_NAME = "maven.xml";

    private final ModuleSettingsModel module;

    public ModuleGoalsGrabber(final ModuleSettingsModel pModule,
                              final IQueryContext pContext) throws Exception {
        module = pModule;

        //register the favorite goals grabber
        final ModuleLocationFinder locationFinder = new ModuleLocationFinder(pContext, module.getModule());
        final IGoalsGrabber grabber = new DefaultGoalsGrabber(locationFinder);
        addGoalsGrabber(new FavoriteModuleGoalsGrabberFilter(grabber));

        //register a maven.xml parsing goals grabber
        final File pomFile = module.getPomFile();
        if (pomFile != null) {
            final File mavenXmlFile = new File(pomFile.getParentFile(), MAVEN_XML_FILE_NAME);
            if (mavenXmlFile.exists()) {
                final ProjectGoalsGrabber projectGoalsGrabber = new ProjectGoalsGrabber();
                projectGoalsGrabber.setMavenXmlFile(mavenXmlFile.getAbsolutePath());
                addGoalsGrabber(projectGoalsGrabber);
            }
        }
    }

    public String getDescription(String fullyQualifiedGoalName) {
        final String description = super.getDescription(fullyQualifiedGoalName);
        if(description != null && description.equals("null"))
            return null;
        
        return description;
    }
    private class FavoriteModuleGoalsGrabberFilter implements IGoalsGrabber {
        private final IGoalsGrabber goalsGrabber;

        public FavoriteModuleGoalsGrabberFilter(final IGoalsGrabber pGrabber) throws Exception {
            goalsGrabber = pGrabber;
        }

        public String getName() {
            return "Favorites";
        }

        public String getDescription(String fullyQualifiedGoalName) {
            final Collection favoriteGoals = module.getFavoriteGoals();
            if (!favoriteGoals.contains(fullyQualifiedGoalName))
                return null;

            return goalsGrabber.getDescription(fullyQualifiedGoalName);
        }

        public String[] getGoals(String plugin) {
            final Collection favoriteGoals = module.getFavoriteGoals();
            final List goals = new ArrayList(10);
            final String[] goalNames = goalsGrabber.getGoals(plugin);
            if(goalNames == null)
                return null;

            for (int i = 0; i < goalNames.length; i++) {
                final String goal = goalNames[i];
                final String fqName = GoalsHelper.buildFullyQualifiedName(plugin, goal);
                if (favoriteGoals.contains(fqName))
                    goals.add(goal);
            }
            return (String[]) goals.toArray(new String[goals.size()]);
        }

        public void refresh() throws Exception {
            goalsGrabber.refresh();
        }

        public String getOrigin(String fullyQualifiedGoalName) {
            final Collection favoriteGoals = module.getFavoriteGoals();
            if (!favoriteGoals.contains(fullyQualifiedGoalName))
                return null;

            return goalsGrabber.getOrigin(fullyQualifiedGoalName);
        }

        public String[] getPlugins() {
            final String[] availablePlugins = goalsGrabber.getPlugins();
            final List availPluginsList = Arrays.asList(availablePlugins);

            final Collection favoriteGoals = module.getFavoriteGoals();
            final Collection favoritePlugins = new HashSet(favoriteGoals.size());
            final Iterator i = favoriteGoals.iterator();
            while (i.hasNext()) {
                final String fqGoalName = (String) i.next();
                final String pluginName = GoalsHelper.getPluginName(fqGoalName);
                if(availPluginsList.contains(pluginName))
                    favoritePlugins.add(pluginName);
            }

            return (String[]) favoritePlugins.toArray(new String[favoritePlugins.size()]);
        }

        public String[] getPrereqs(String fullyQualifiedGoalName) {
            final Collection favoriteGoals = module.getFavoriteGoals();
            if (!favoriteGoals.contains(fullyQualifiedGoalName))
                return null;

            return goalsGrabber.getPrereqs(fullyQualifiedGoalName);
        }
    }
}
