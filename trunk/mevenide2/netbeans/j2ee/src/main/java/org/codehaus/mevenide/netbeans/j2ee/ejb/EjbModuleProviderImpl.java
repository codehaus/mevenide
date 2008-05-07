/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.codehaus.mevenide.netbeans.j2ee.ejb;

import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.j2ee.MavenDeploymentImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarFactory;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarsInProject;
import org.openide.filesystems.FileObject;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class EjbModuleProviderImpl extends J2eeModuleProvider implements EjbJarProvider, EjbJarsInProject  {
    
    private EjbJarImpl ejbimpl;
    private Project project;
    private String serverInstanceID;
    private J2eeModule j2eemodule;    
    private EjbJar apiEjbJar;
    private ProjectURLWatcher mavenproject;
    
    /** Creates a new instance of EjbModuleProviderImpl */
    public EjbModuleProviderImpl(Project proj) {
        project = proj;
        ejbimpl = new EjbJarImpl(project, this);
        mavenproject = project.getLookup().lookup(ProjectURLWatcher.class);
    }
    
    /**
     * 
     */
    public void loadPersistedServerId() {
        loadPersistedServerId(true);
    }
    
    /**
     * 
     * @param ensureReady 
     */
    private void loadPersistedServerId(boolean ensureReady) {
        String oldId = getServerInstanceID();
        String oldSer = getServerID();
        String val = mavenproject.getMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_ID);
        String server = mavenproject.getMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER);
        if (server == null) {
            //try checking for old values..
            server = mavenproject.getMavenProject().getProperties().getProperty(Constants.HINT_DEPLOY_J2EE_SERVER_OLD);
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
//        if (instanceFound == null) {
//            String[] ids = Deployment.getDefault().getServerInstanceIDs(new Object[] {J2eeModule.EJB});
//            if (ids != null && ids.length > 0) {
//                instanceFound = ids[0];
//            }
//        }
        serverInstanceID = instanceFound;
        if (oldId != null) {
            fireServerChange(oldSer, getServerID());
        }
        if (oldSer != null && serverInstanceID != null && ensureReady) {
            getConfigSupport().ensureConfigurationReady();
        }
    }
    

    /**
     * 
     * @param file 
     * @return 
     */
    public EjbJar findEjbJar(FileObject file) {
        Project proj = FileOwnerQuery.getOwner (file);
        if (proj != null) {
            proj = proj.getLookup().lookup(Project.class);
        }
        if (proj != null && project == proj) {
            if (ejbimpl.isValid()) {
                if (apiEjbJar == null) {
                    apiEjbJar =  EjbJarFactory.createEjbJar(ejbimpl);
                }
                return apiEjbJar;
            }
        }
        return null;
    }

    /**
     * 
     * @return 
     */
    public synchronized J2eeModule getJ2eeModule() {
        if (j2eemodule == null) {
            j2eemodule = J2eeModuleFactory.createJ2eeModule(ejbimpl);
        }
        return j2eemodule; 
    }
    
    /**
     * 
     * @return 
     */
    public ModuleChangeReporter getModuleChangeReporter() {
        return ejbimpl;
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
//        System.out.println("EjbMP: getsourceroots");

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        //#116215,121147 just workaround the AIOOBE
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

    public EjbJar[] getEjbJars() {
        if (ejbimpl.isValid()) {
            if (apiEjbJar == null) {
                apiEjbJar =  EjbJarFactory.createEjbJar(ejbimpl);
            }
            return new EjbJar[] {apiEjbJar};
        }
        return new EjbJar[0];
    }
    
}
