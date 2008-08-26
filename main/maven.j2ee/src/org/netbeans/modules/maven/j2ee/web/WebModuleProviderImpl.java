/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */

package org.netbeans.modules.maven.j2ee.web;

import java.util.ArrayList;
import org.netbeans.modules.maven.api.Constants;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.modules.maven.j2ee.J2eeMavenSourcesImpl;
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
import org.netbeans.modules.maven.j2ee.ExecutionChecker;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleFactory;
import org.netbeans.modules.web.spi.webmodule.WebModuleProvider;
import org.netbeans.spi.project.AuxiliaryProperties;
import org.openide.filesystems.FileObject;


/**
 * web module provider implementation for maven2 project type.
 * @author  Milos Kleint 
 */
public class WebModuleProviderImpl extends J2eeModuleProvider implements WebModuleProvider {
    
    
    private Project project;
    private WebModuleImpl implementation;
    private WebModule module;
    private J2eeModule j2eemodule;
    
    private ModuleChangeReporter moduleChange;
    
    private String serverInstanceID;

    static final String ATTRIBUTE_CONTEXT_PATH = "WebappContextPath"; //NOI18N
    private NbMavenProject mavenproject;
    
    public WebModuleProviderImpl(Project proj) {
        project = proj;
        mavenproject = project.getLookup().lookup(NbMavenProject.class);
        implementation = new WebModuleImpl(project, this);
        moduleChange = new ModuleChangeReporterImpl();
    }
    
    public void loadPersistedServerId() {
        loadPersistedServerId(true);
    }
    
    private void loadPersistedServerId(boolean ensureReady) {
        String oldId = getServerInstanceID();
        String oldSer = getServerID();
        AuxiliaryProperties props = project.getLookup().lookup(AuxiliaryProperties.class);
        String val = props.get(Constants.HINT_DEPLOY_J2EE_SERVER_ID, true);
        String server = props.get(Constants.HINT_DEPLOY_J2EE_SERVER, true);
        if (server == null) {
            //try checking for old values..
            server = props.get(Constants.HINT_DEPLOY_J2EE_SERVER_OLD, true);
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
        return ExecutionChecker.DEV_NULL;
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
        return ExecutionChecker.DEV_NULL;
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
