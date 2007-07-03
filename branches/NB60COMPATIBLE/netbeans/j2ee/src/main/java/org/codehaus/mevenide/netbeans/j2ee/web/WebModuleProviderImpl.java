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

import java.io.IOException;
import java.util.ArrayList;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.j2ee.J2eeMavenSourcesImpl;
import org.codehaus.mevenide.netbeans.j2ee.MavenDeploymentImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.deployment.common.api.ConfigurationException;
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
import org.openide.util.Exceptions;


/**
 * web module provider implementation for maven2 project type.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleProviderImpl extends J2eeModuleProvider implements WebModuleProvider {
    
    
    private NbMavenProject project;
    private WebModuleImpl implementation;
    private WebModule module;
    private J2eeModule j2eemodule;
    
    private ModuleChangeReporter moduleChange;
    
    private String serverInstanceID;

    static final String ATTRIBUTE_CONTEXT_PATH = "WebappContextPath"; //NOI18N
    static final String ATTRIBUTE_DEPLOYMENT_SERVER = "netbeans.deployment.server.type"; //NOI18N
    static final String ATTRIBUTE_DEPLOYMENT_SERVER_ID = "netbeans.deployment.server.id"; //NOI18N
    
    
    public WebModuleProviderImpl(NbMavenProject proj) {
        project = proj;
        implementation = new WebModuleImpl(project, this);
        moduleChange = new ModuleChangeReporterImpl();
        loadPersistedServerId(false);
    }
    
    public void loadPersistedServerId() {
        loadPersistedServerId(true);
    }
    
    private void loadPersistedServerId(boolean ensureReady) {
        String oldId = getServerInstanceID();
        String oldSer = getServerID();
        String val = project.getOriginalMavenProject().getProperties().getProperty(ATTRIBUTE_DEPLOYMENT_SERVER_ID);
        String server = project.getOriginalMavenProject().getProperties().getProperty(ATTRIBUTE_DEPLOYMENT_SERVER);
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
        if (instanceFound == null) {
            String[] ids = Deployment.getDefault().getServerInstanceIDs(new Object[] {J2eeModule.WAR});
            if (ids != null && ids.length > 0) {
                instanceFound = ids[0];
            }
        }
        serverInstanceID = instanceFound;
        if (oldId != null) {
            fireServerChange(oldSer, getServerID());
        }
        if (ensureReady) {
            getConfigSupport().ensureConfigurationReady();
            val = (String)project.getProjectDirectory().getAttribute(ATTRIBUTE_CONTEXT_PATH);
            setContextPathImpl(val != null ? val : implementation.getContextPath());
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
    
//    /**
//     * 
//     * @param name 
//     * @return 
//     */
//    public File getDeploymentConfigurationFile(String name) {
//        if (name == null) {
//            return null;
//        }
//        String path = getConfigSupport().getContentRelativePath(name);
//        if (path == null) {
//            path = name;
//        }
//        return implementation.getDDFile(path);
//    }
//
//    
//    /**
//     * 
//     * @param string 
//     * @return 
//     */
//    public FileObject findDeploymentConfigurationFile(String string) {
//        File fil = getDeploymentConfigurationFile(string);
//        if (fil != null) {
//            return FileUtil.toFileObject(fil);
//        }
//        return null;
//    }
//    
    /**
     * 
     * @return 
     */
    public String getContextPath() {
        if(implementation.getDeploymentDescriptor() == null) {
            return (String)project.getProjectDirectory().getAttribute(ATTRIBUTE_CONTEXT_PATH);
        }
        try {
            return getConfigSupport().getWebContextRoot();
        } catch (ConfigurationException e) {
            // TODO #95280: inform the user that the context root cannot be retrieved
            return null;
        }
    }
    
    public void setContextPath(String path) {
        try {
            // remember the path for next time..
            project.getProjectDirectory().setAttribute(ATTRIBUTE_CONTEXT_PATH, path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        setContextPathImpl(path);
    }
    
    private void setContextPathImpl(String path) {
        if (implementation.getDeploymentDescriptor() != null) {
            try         {
                getConfigSupport().setWebContextRoot(path);
            }
            catch (ConfigurationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
//    
    public void setServerInstanceID(String str) {
        serverInstanceID = str;
        // TODO write into the private/public profile..
//        try {
//            // remember the instance for next time..
//            project.getProjectDirectory().setAttribute(ATTRIBUTE_DEPLOYMENT_SERVER, serverInstanceID);
//            Model mdl = project.getEmbedder().readModel(project.getPOMFile());
//            mdl.getProperties().put(ATTRIBUTE_DEPLOYMENT_SERVER, Deployment.getDefault().getServerID(serverInstanceID));
//            WriterUtils.writePomModel(FileUtil.toFileObject(project.getPOMFile()), mdl);
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//        } catch (XmlPullParserException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//        }
        
    }
    
    /** If the module wants to specify a target server instance for deployment
     * it needs to override this method to return false.
     */
    public boolean useDefaultServer() {
        return serverInstanceID == null;
    }
    
    /** Id of server isntance for deployment. The default implementation returns
     * the default server instance selected in Server Registry.
     * The return value may not be null.
     * If modules override this method they also need to override {@link useDefaultServer}.
     */
    public String getServerInstanceID() {
        if (serverInstanceID != null && Deployment.getDefault().getServerID(serverInstanceID) != null) {
            return serverInstanceID;
        }
        return MavenDeploymentImpl.DEV_NULL;
    }
    
    /** This method is used to determin type of target server.
     * The return value must correspond to value returned from {@link getServerInstanceID}.
     */
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
    
    public FileObject[] getSourceRoots() {
        ArrayList toRet = new ArrayList();
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
        return (FileObject[])toRet.toArray(new FileObject[toRet.size()]);
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
