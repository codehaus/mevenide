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

import java.io.File;
import java.net.URI;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;


/**
 * web module provider implementation for maven2 project type.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleProviderImpl extends J2eeModuleProvider implements WebModuleProvider {
    
    private NbMavenProject project;
    private WebModuleImpl implementation;
    private WebModule module;
    
    private ModuleChangeReporter moduleChange;
    
    private String serverId;
    public WebModuleProviderImpl(NbMavenProject proj) {
        project = proj;
        implementation = new WebModuleImpl(project);
        moduleChange = new ModuleChangeReporterImpl();
        setContextPath(implementation.getContextPath());
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
    
    public J2eeModule getJ2eeModule() {
        return implementation;
    }
    
    public ModuleChangeReporter getModuleChangeReporter() {
        return moduleChange;
    }
    
    public File getDeploymentConfigurationFile(String name) {
        if (name == null) {
            return null;
        }
        String path = getConfigSupport().getContentRelativePath(name);
        if (path == null) {
            path = name;
        }
        return implementation.getDDFile(path);
    }

    
    public FileObject findDeploymentConfigurationFile(String string) {
        File fil = getDeploymentConfigurationFile(string);
        if (fil != null) {
            return FileUtil.toFileObject(fil);
        }
        return null;
    }
    
    public String getContextPath() {
        if(implementation.getDeploymentDescriptor() == null) {
            return null;
        }
        return getConfigSupport().getWebContextRoot ();
    }
    
    public void setContextPath(String path) {
        if (implementation.getDeploymentDescriptor() != null) {
            getConfigSupport().setWebContextRoot (path);
        }
    }
    
    
    public void setServerInstanceID(String str) {
        serverId = str;
    }
    
    /** If the module wants to specify a target server instance for deployment
     * it needs to override this method to return false.
     */
    public boolean useDefaultServer() {
        return serverId == null;
    }
    
    /** Id of server isntance for deployment. The default implementation returns
     * the default server instance selected in Server Registry.
     * The return value may not be null.
     * If modules override this method they also need to override {@link useDefaultServer}.
     */
    public String getServerInstanceID() {
        if (serverId != null && Deployment.getDefault().getServerID(serverId) != null) {
            return serverId;
        }
        return super.getServerInstanceID();
    }
    
    /** This method is used to determin type of target server.
     * The return value must correspond to value returned from {@link getServerInstanceID}.
     */
    public String getServerID() {
        if (serverId != null) {
            String tr = Deployment.getDefault().getServerID(serverId);
            if (tr != null) {
                return tr;
            }
        }
        return super.getServerID();
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
