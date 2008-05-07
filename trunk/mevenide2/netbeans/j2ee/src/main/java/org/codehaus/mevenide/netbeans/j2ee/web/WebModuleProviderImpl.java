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

package org.codehaus.mevenide.netbeans.j2ee.web;

import java.util.ArrayList;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.j2ee.J2eeMavenSourcesImpl;
import org.codehaus.mevenide.netbeans.j2ee.MavenDeploymentImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.openide.filesystems.FileObject;


/**
 * web module provider implementation for maven2 project type.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleProviderImpl extends J2eeModuleProvider implements WebModuleProvider {
    
    
    private Project project;
    private WebModuleImpl implementation;
    private WebModule module;
    private J2eeModule j2eemodule;
    
    private ModuleChangeReporter moduleChange;
    
    private String serverInstanceID;

    static final String ATTRIBUTE_CONTEXT_PATH = "WebappContextPath"; //NOI18N
    private ProjectURLWatcher mavenproject;
    
    public WebModuleProviderImpl(Project proj) {
        project = proj;
        mavenproject = project.getLookup().lookup(ProjectURLWatcher.class);
        implementation = new WebModuleImpl(project, this);
        moduleChange = new ModuleChangeReporterImpl();
    }
    
    public void loadPersistedServerId() {
        loadPersistedServerId(true);
    }
    
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
//            System.out.println("have instances of=" + server + " " + instances);
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
//            String[] ids = Deployment.getDefault().getServerInstanceIDs(new Object[] {J2eeModule.WAR});
//            if (ids != null && ids.length > 0) {
//                instanceFound = ids[0];
//            }
//        }
        serverInstanceID = instanceFound;
        if (oldId != null) {
            fireServerChange(oldSer, getServerID());
        }
        if (ensureReady) {
            getConfigSupport().ensureConfigurationReady();
        }
    }
    
    public WebModule findWebModule(FileObject fileObject) {
        if (implementation != null && implementation.isValid()) {
            if (module == null) {
                module = WebModuleFactory.createWebModule(implementation);
            }
            return module;
        }
        return null;
    }
    
    public synchronized J2eeModule getJ2eeModule() {
        if (j2eemodule == null) {
            j2eemodule = J2eeModuleFactory.createJ2eeModule(implementation);
        }
        return j2eemodule; 
    }
    
    /**
     * 
     * @return 
     */
    public WebModuleImpl getWebModuleImplementation() {
        return implementation;
    }
    
    public ModuleChangeReporter getModuleChangeReporter() {
        return moduleChange;
    }
    
    public void setServerInstanceID(String str) {
        String oldone = null;
        if (serverInstanceID != null) {
            oldone = Deployment.getDefault().getServerID(serverInstanceID);
        }
        serverInstanceID = str;
        if (oldone != null) {
            fireServerChange(oldone, getServerID());            
        }
        // TODO write into the private/public profile..

    }
    
    /** If the module wants to specify a target server instance for deployment
     * it needs to override this method to return false.
     */
    @Override
    public boolean useDefaultServer() {
        return serverInstanceID == null;
    }
    
    /** Id of server isntance for deployment. The default implementation returns
     * the default server instance selected in Server Registry.
     * The return value may not be null.
     * If modules override this method they also need to override {@link useDefaultServer}.
     */
    @Override
    public String getServerInstanceID() {
        if (serverInstanceID != null && Deployment.getDefault().getServerID(serverInstanceID) != null) {
            return serverInstanceID;
        }
        return MavenDeploymentImpl.DEV_NULL;
    }
    
    /** This method is used to determin type of target server.
     * The return value must correspond to value returned from {@link getServerInstanceID}.
     */
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

    /**
     *  Returns list of root directories for source files including configuration files.
     *  Examples: file objects for src/java, src/conf.  
     *  Note: 
     *  If there is a standard configuration root, it should be the first one in
     *  the returned list.
     */
    
    @Override
    public FileObject[] getSourceRoots() {
        ArrayList<FileObject> toRet = new ArrayList<FileObject>();
        Sources srcs = ProjectUtils.getSources(project);
        SourceGroup[] webs = srcs.getSourceGroups(J2eeMavenSourcesImpl.TYPE_DOC_ROOT);
        if (webs != null) {
            for (int i = 0; i < webs.length; i++) {
                toRet.add(webs[i].getRootFolder());
            }
        }
        SourceGroup[] grps = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (grps != null) {
            for (int i = 0; i < grps.length; i++) {
                toRet.add(grps[i].getRootFolder());
            }
        }
        return toRet.toArray(new org.openide.filesystems.FileObject[toRet.size()]);
    }
    

    // TODO
    private class ModuleChangeReporterImpl implements ModuleChangeReporter {
        public EjbChangeDescriptor getEjbChanges(long param) {
            return null;
        }
        
        public boolean isManifestChanged(long param) {
            return false;
        }
        
    }
    
}
