/* ==========================================================================
 * Copyright 2005 Mevenide Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.codehaus.mevenide.netbeans.j2ee.ear;

import java.io.File;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.j2ee.MavenDeploymentImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.ejbjar.Ear;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeApplicationProvider;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EarImplementation;
import org.netbeans.modules.j2ee.spi.ejbjar.EarProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * provider for ear specific functionality
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class EarModuleProviderImpl extends J2eeApplicationProvider implements EarProvider  {
    
    private EarImpl earimpl;
    private NbMavenProject project;
    private String serverInstanceID;
    private J2eeModule j2eemodule;

    
    /** Creates a new instance of MavenEarProvider */
    public EarModuleProviderImpl(NbMavenProject proj) {
        project = proj;
        earimpl = new EarImpl(project, this);
    }
    
    public EarImplementation getEarImplementation() {
        return earimpl;
    }
    
    public void loadPersistedServerId() {
        loadPersistedServerId(true);
    }
    
    private void loadPersistedServerId(boolean ensureReady) {
        String oldId = getServerInstanceID();
        String oldSer = getServerID();
        
        String val = project.getOriginalMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
        String server = project.getOriginalMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER);
        if (server == null) {
            //try checking for old values..
            server = project.getOriginalMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_OLD);
        }
        String instanceFound = null;
        if (server != null) {
            String[] instances = Deployment.getDefault().getInstancesOfServer(server);
            String inst = null;
            if (instances != null && instances.length > 0) {
                inst = instances[0];
                for (int i = 0; i < instances.length; i++) {
                    if (val != null && val.equals(instances[i])) {
                        inst = instances[i];
                        break;
                    }
                }
                instanceFound = inst;
            }
        }
        serverInstanceID = instanceFound;
        if (oldId != null) {
            fireServerChange(oldSer, getServerID());
        }
        if (ensureReady) {
            getConfigSupport().ensureConfigurationReady();
        }
    }
    
    public Ear findEar(FileObject file) {
        Project proj = FileOwnerQuery.getOwner(file);
        if (proj != null) {
            proj = proj.getLookup().lookup(Project.class);
        }
        if (proj != null && project == proj) {
            if (earimpl != null && earimpl.isValid()) {
                return EjbJarFactory.createEar(earimpl);
            }
        }
        return null;
    }

    /**
     * Returns the provider for the child module specified by given URI.
     * 
     * @param uri the child module URI within the J2EE application.
     * @return J2eeModuleProvider object
     */
    public J2eeModuleProvider getChildModuleProvider(String uri) {
//        System.out.println("!!!give me module with uri=" + uri);
        return null;
    }

    /**
     * Returns list of providers of every child J2EE module of this J2EE app.
     * 
     * @return array of J2eeModuleProvider objects.
     */
    public J2eeModuleProvider[] getChildModuleProviders() {
//        System.out.println("!!!give me child module providers..");
        return new J2eeModuleProvider[0];
    }

    public synchronized J2eeModule getJ2eeModule() {
        if (j2eemodule == null) {
            j2eemodule = J2eeModuleFactory.createJ2eeApplication(earimpl);
        }
        return j2eemodule; 
    }


    public ModuleChangeReporter getModuleChangeReporter() {
        return earimpl;
    }

    /**
     * Returns source deployment configuration file path for the given deployment 
     * configuration file name. 
     * 
     * @param name file name of the deployement configuration file.
     * @return non-null absolute path to the deployment configuration file.
     */
    public File getDeploymentConfigurationFile(String name) {
        if (name == null) {
            return null;
        }
        String path = getConfigSupport().getContentRelativePath(name);
        if (path == null) {
            path = name;
        }
//        System.out.println("EMPI: getDeploymentConfigFile=" + name);
        return earimpl.getDDFile(path);
    }



    /**
     * Finds source deployment configuration file object for the given deployment 
     * configuration file name.  
     * 
     * @param name file name of the deployement configuration file.
     * @return FileObject of the configuration descriptor file; null if the file does not exists.
     */
    public FileObject findDeploymentConfigurationFile(String name) {
        File fil = getDeploymentConfigurationFile(name);
        if (fil != null) {
            return FileUtil.toFileObject(fil);
        }
        return null;
    }

    public void setServerInstanceID(String string) {
       String oldone = null;
        if (serverInstanceID != null) {
            oldone = Deployment.getDefault().getServerID(serverInstanceID);
        }
        serverInstanceID = string;
        if (oldone != null) {
            fireServerChange(oldone, getServerID());            
        }
    }
    
    @Override
    public String getServerInstanceID() {
        if (serverInstanceID != null && Deployment.getDefault().getServerID(serverInstanceID) != null) {
            return serverInstanceID;
        }
        return MavenDeploymentImpl.DEV_NULL;
    }
    
    @Override
    public String getServerID() {
        if (serverInstanceID != null) {
            String tr = Deployment.getDefault().getServerID(serverInstanceID);
            if (tr != null) {
                return tr;
            }
        }
        return MavenDeploymentImpl.DEV_NULL;
    }
    
    @Override
    public boolean useDefaultServer() {
        return serverInstanceID == null;
    }
    
    @Override
    public FileObject[] getSourceRoots() {
        //TODO instead of src/test/java, drop the resources in..
        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        //#116215, 121147 just workaround the AIOOBE
        EjbJar[] jars = EjbJar.getEjbJars(project);
        int offset = (jars == null || jars.length == 0) ? 0 : 1;
        FileObject[] roots = new FileObject[groups.length + offset];
        if (jars != null && jars.length > 0) {
            roots[0] = jars[0].getMetaInf();
        }
        for (int i=0; i < groups.length; i++) {
            roots[i + offset] = groups[i].getRootFolder();
        }
        return roots;
    }
    
}
