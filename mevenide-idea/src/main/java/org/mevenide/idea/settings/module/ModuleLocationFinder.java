package org.mevenide.idea.settings.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.idea.settings.global.GlobalSettings;

import java.io.File;

/**
 * @author Arik
 */
public class ModuleLocationFinder extends LocationFinderAggregator {

    private final Module module;

    public ModuleLocationFinder(final Module pModule) {
        //TODO: if module has no POM, this will cause an NPE
        super(ModuleSettings.getInstance(pModule).getQueryContext());
        module = pModule;
    }

    public String getJavaHome() {
        ProjectJdk jdk = ModuleSettings.getInstance(module).getJdk();
        if(jdk == null)
            return super.getJavaHome();
        else
            return jdk.getHomeDirectory().getPath();
    }

    public String getMavenHome() {
        final File mavenHome = GlobalSettings.getInstance().getMavenHome();
        if(mavenHome == null)
            return super.getMavenHome();
        else
            return mavenHome.getAbsolutePath();
    }
}
