package org.mevenide.netbeans.devel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.ConfigUtils;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.properties.resolver.PropertyResolverFactory;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInstall;
/*
 * Install.java
 *
 * Created on March 12, 2005, 11:02 AM
 */

/**
 *
 * @author cenda
 */
public class Install extends ModuleInstall {
    private static final long serialVersionUID = -485754848837352247L;
    
    /** Creates a new instance of Install */
    public Install() {
    }
    
    public void updated(int release, String specVersion) {
        super.updated(release, specVersion);
    }
    
    public void installed() {
        super.installed();
    }
    

    
}
