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

import java.io.File;
import org.codehaus.mevenide.netbeans.NbMavenProject;
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
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarFactory;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */

public class EjbModuleProviderImpl extends J2eeModuleProvider implements EjbJarProvider  {
    
    static final String ATTRIBUTE_DEPLOYMENT_SERVER = "netbeans.deployment.server.type"; //NOI18N
    static final String ATTRIBUTE_DEPLOYMENT_SERVER_ID = "netbeans.deployment.server.id"; //NOI18N
    
    private EjbJarImpl ejbimpl;
    private NbMavenProject project;
    private String serverInstanceID;
    
    /** Creates a new instance of EjbModuleProviderImpl */
    public EjbModuleProviderImpl(NbMavenProject proj) {
        project = proj;
        ejbimpl = new EjbJarImpl(project);
        loadPersistedServerId(false);
    }
    
    public void loadPersistedServerId() {
        loadPersistedServerId(true);
    }
    
    public void loadPersistedServerId(boolean ensureReady) {
        String oldId = getServerInstanceID();
        String oldSer = getServerID();
        String val = project.getOriginalMavenProject().getProperties().getProperty(ATTRIBUTE_DEPLOYMENT_SERVER_ID);
        String server = project.getOriginalMavenProject().getProperties().getProperty(ATTRIBUTE_DEPLOYMENT_SERVER);
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
        if (instanceFound == null) {
            String[] ids = Deployment.getDefault().getServerInstanceIDs(new Object[] {J2eeModule.EJB});
            if (ids != null && ids.length > 0) {
                instanceFound = ids[0];
            }
        }
        serverInstanceID = instanceFound;
        if (oldSer != null && serverInstanceID != null && ensureReady) {
            getConfigSupport().ensureConfigurationReady();
        }
        if (oldId != null) {
            fireServerChange(oldSer, getServerID());
        }
    }
    

    public EjbJar findEjbJar(FileObject file) {
        Project proj = FileOwnerQuery.getOwner (file);
        if (proj != null) {
            proj = (Project)proj.getLookup().lookup(NbMavenProject.class);
        }
        if (proj != null && project == proj) {
            if (ejbimpl != null && ejbimpl.isValid()) {
                System.out.println("EjbMP: findEjbJar");
                return EjbJarFactory.createEjbJar(ejbimpl);
            }
        }
        return null;
    }

    public J2eeModule getJ2eeModule() {
        System.out.println("EjbMP:getJ2eeModule");
        return ejbimpl;
    }

    public ModuleChangeReporter getModuleChangeReporter() {
        return ejbimpl;
    }

    public File getDeploymentConfigurationFile(String name) {
        if (name == null) {
            return null;
        }
        String path = getConfigSupport().getContentRelativePath(name);
        if (path == null) {
            path = name;
        }
        return ejbimpl.getDDFile(path);
    }

    
    public FileObject findDeploymentConfigurationFile(String string) {
        File fil = getDeploymentConfigurationFile(string);
        if (fil != null) {
            return FileUtil.toFileObject(fil);
        }
        return null;
    }

    public void setServerInstanceID(String string) {
        // TODO implement when needed
    }
    
    public String getServerInstanceID() {
        if (serverInstanceID != null && Deployment.getDefault().getServerID(serverInstanceID) != null) {
            return serverInstanceID;
        }
        return MavenDeploymentImpl.DEV_NULL;
    }
    
    public String getServerID() {
        if (serverInstanceID != null) {
            String tr = Deployment.getDefault().getServerID(serverInstanceID);
            if (tr != null) {
                return tr;
            }
        }
        return MavenDeploymentImpl.DEV_NULL;
    }
    
    public File getEnterpriseResourceDirectory() {
        System.out.println("EjbMP: getEnterpriseResourceDirectory");
        return null;
    }
    
    public boolean useDefaultServer() {
        return serverInstanceID == null;
    }
    
    public FileObject[] getSourceRoots() {
        System.out.println("EjbMP: getsourceroots");

        Sources sources = ProjectUtils.getSources(project);
        SourceGroup[] groups = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        FileObject[] roots = new FileObject[groups.length+1];
        roots[0] = EjbJar.getEjbJars(project)[0].getMetaInf();
        for (int i=0; i < groups.length; i++) {
            roots[i+1] = groups[i].getRootFolder();
        }
        
        return roots;
    }
    
}
