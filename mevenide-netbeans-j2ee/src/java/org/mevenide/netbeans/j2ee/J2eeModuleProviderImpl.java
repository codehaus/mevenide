/*
 * J2eeModuleProviderImpl.java
 *
 * Created on July 11, 2005, 10:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.mevenide.netbeans.j2ee;

import java.io.File;
import org.mevenide.netbeans.j2ee.web.WebModuleImpl;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class J2eeModuleProviderImpl extends J2eeModuleProvider {
    private String serverId;
    private MavenProject project;
    private MavenJ2eeModule j2eeModule;
    /** Creates a new instance of J2eeModuleProviderImpl */
    public J2eeModuleProviderImpl(MavenProject proj) {
        project = proj;
        j2eeModule = new MavenJ2eeModule(project);
    }

    /**
     * Finds source deployment configuration file object for the given deployment 
     * configuration file name.  
     *
     * @param name file name of the deployement configuration file.
     * @return FileObject of the configuration descriptor file; null if the file does not exists.
     * 
     */
    
    public FileObject findDeploymentConfigurationFile(String str) {
        File fil = getDeploymentConfigurationFile(str);
        if (fil != null && fil.exists()) {
            return FileUtil.toFileObject(fil);
        }
        return null;
    }
    
    /**
     * Returns source deployment configuration file path for the given deployment 
     * configuration file name. 
     *
     * @param name file name of the deployement configuration file.
     * @return non-null absolute path to the deployment configuration file.
     */

    public File getDeploymentConfigurationFile(String str) {
        if (J2eeModule.WEB_XML.equals(str)) {
            return WebModuleImpl.guessWebDescriptor(project);
        }
        if (J2eeModule.EJBJAR_XML.equals(str)) {
            return MavenEjbJarImpl.guessEjbJarDescriptor(project);
        }
        if (J2eeModule.CONNECTOR_XML.equals(str)) {
            File connFile = FileUtilities.getFileForProperty("maven.rar.raxml", project.getPropertyResolver());
            if (connFile != null) {
                return connFile;
            }
        }
        if (J2eeModule.EAR.equals(str)) {
            return MavenEarImpl.guessEarDescriptor(project);
        }
        // mkleint: what do do here.. I don't get the concept of teh method at all.
        
        return null;
    }

    public J2eeModule getJ2eeModule() {
        if (j2eeModule.getModuleType() != null) {
            return j2eeModule;
        }
        return null;
    }

    public ModuleChangeReporter getModuleChangeReporter() {
        return null;
    }

    public void setServerInstanceID(String str) {
        serverId = str;
    }
    
    /**
     *  Returns list of root directories for source files including configuration files.
     *  Examples: file objects for src/java, src/conf.  
     *  Note: 
     *  If there is a standard configuration root, it should be the first one in
     *  the returned list.
     */
    public FileObject[] getSourceRoots() {
        return new FileObject[0];
    }
    
    
}
