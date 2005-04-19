package org.mevenide.idea.model.settings.module;

import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.idea.main.settings.global.GlobalSettings;
import org.mevenide.idea.main.settings.module.ModuleSettings;
import org.mevenide.context.IQueryContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;

import java.io.File;

/**
 * @author Arik
 */
public class ModuleLocationFinder extends LocationFinderAggregator {
    private final Module module;

    public ModuleLocationFinder(final IQueryContext pQueryContext,
                                final Module pModule) {
        //TODO: if module has no query context, this will cause an NPE
        super(pQueryContext);
        module = pModule;
    }

    public String getJavaHome() {
        ProjectJdk jdk = ModuleSettings.getInstance(module).getJdk();
        if (jdk == null)
            return super.getJavaHome();
        else
            return jdk.getHomeDirectory().getPath();
    }

    public String getMavenHome() {
        final File mavenHome = GlobalSettings.getInstance().getMavenHome();
        if (mavenHome == null)
            return super.getMavenHome();
        else
            return mavenHome.getAbsolutePath();
    }
}
