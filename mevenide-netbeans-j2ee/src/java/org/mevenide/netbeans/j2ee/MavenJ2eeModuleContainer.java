/*
 * MavenJ2eeModuleContainer.java
 *
 * Created on July 13, 2005, 8:48 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.mevenide.netbeans.j2ee;

import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModuleContainer;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleListener;

/**
 *
 * @author Administrator
 */
public class MavenJ2eeModuleContainer extends MavenJ2eeModule implements J2eeModuleContainer {
    private MavenProject project;
    /** Creates a new instance of MavenJ2eeModuleContainer */
    public MavenJ2eeModuleContainer(MavenProject proj) {
        super(proj);
        project = proj;
    }

    public void addModuleListener(ModuleListener ml) {
    }

    public J2eeModule[] getModules(ModuleListener ml) {
        return new J2eeModule[0];
    }

    public void removeModuleListener(ModuleListener ml) {
    }
    
}
