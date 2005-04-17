package org.mevenide.idea.goalstoolwindow;

import com.intellij.openapi.module.Module;
import org.mevenide.goals.grabber.AbstractGoalsGrabber;
import org.mevenide.goals.grabber.GoalsGrabbersAggregator;
import org.mevenide.goals.grabber.ProjectGoalsGrabber;
import org.mevenide.idea.settings.module.ModuleSettings;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Arik
 */
public class ModuleGoalsGrabber extends GoalsGrabbersAggregator {
    private static final String MAVEN_XML_FILE_NAME = "maven.xml";

    private final Module module;

    public ModuleGoalsGrabber(final Module pModule) {
        module = pModule;

        //register the favorite goals grabber
        addGoalsGrabber(new FavoriteModuleGoalsGrabber());

        //register a maven.xml parsing goals grabber
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        final File pomFile = moduleSettings.getPomFile();
        if (pomFile != null) {
            final File mavenXmlFile = new File(pomFile.getParentFile(), MAVEN_XML_FILE_NAME);
            if (mavenXmlFile.exists()) {
                final ProjectGoalsGrabber projectGoalsGrabber = new ProjectGoalsGrabber();
                projectGoalsGrabber.setMavenXmlFile(mavenXmlFile.getAbsolutePath());
                addGoalsGrabber(projectGoalsGrabber);
            }
        }
    }

    private class FavoriteModuleGoalsGrabber extends AbstractGoalsGrabber {

        public void refresh() throws Exception {
            super.refresh();

            final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
            final Collection favoriteGoals = moduleSettings.getFavoriteGoals();
            final Iterator favIterator = favoriteGoals.iterator();
            while (favIterator.hasNext()) {
                final String fqName = (String) favIterator.next();
                registerGoalName(fqName);
            }
        }

        public String getName() {
            return "Favorites";
        }
    }
}
