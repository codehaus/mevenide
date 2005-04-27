package org.mevenide.idea;

import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.idea.global.MavenManager;

import java.io.File;

/**
 * @author Arik
 */
public class IDEALocationFinder implements ILocationFinder {

    private final ILocationFinder locationFinder;

    public IDEALocationFinder() {
        this(SysEnvLocationFinder.getInstance());
    }

    public IDEALocationFinder(final ILocationFinder pLocationFinder) {
        locationFinder = pLocationFinder;
    }

    public String getConfigurationFileLocation() {
        return locationFinder.getConfigurationFileLocation();
    }

    public String getJavaHome() {
        return locationFinder.getJavaHome();
    }

    public String getMavenHome() {
        final MavenManager mgr = MavenManager.getInstance();
        final File mavenHome = mgr.getMavenHome();
        if(mavenHome == null || !mavenHome.isDirectory())
            return locationFinder.getMavenHome();
        else
            return mavenHome.getAbsolutePath();
    }

    public String getMavenLocalHome() {
        return locationFinder.getMavenLocalHome();
    }

    public String getMavenLocalRepository() {
        return locationFinder.getMavenLocalRepository();
    }

    public String getMavenPluginsDir() {
        return locationFinder.getMavenPluginsDir();
    }

    public String getUserHome() {
        return locationFinder.getUserHome();
    }
}
