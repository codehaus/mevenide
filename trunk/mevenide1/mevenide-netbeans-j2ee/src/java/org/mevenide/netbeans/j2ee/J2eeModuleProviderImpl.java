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

package org.mevenide.netbeans.j2ee;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.mevenide.netbeans.j2ee.web.WebModuleImpl;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.api.project.MavenProject;
import org.mevenide.netbeans.project.MavenSourcesImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.Deployment;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * implementatiomn of J2eeMoudleProvider that is used by the IDE for deployment of j2ee applications.
 * the implementation is provided in the project's lookup.
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class J2eeModuleProviderImpl extends J2eeModuleProvider {
    
    /**
     * property that stores the preferred server instanceid for deployment
     */
    public static final String SERVERID_PROPERTY = "maven.netbeans.deploy.serverid"; //NOI18N
    
    
    private String serverId = null;
    private MavenProject project;
    private MavenJ2eeModule j2eeModule;
    private ModuleChangeReporter moduleChangeReporter;
    
    /** Creates a new instance of J2eeModuleProviderImpl */
    public J2eeModuleProviderImpl(MavenProject proj) {
        project = proj;
        j2eeModule = new MavenJ2eeModule(project);
        moduleChangeReporter = new ModuleChangeReporterImpl();
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
        String path = getConfigSupport().getContentRelativePath(str);
        if (J2eeModule.WAR.equals(j2eeModule.getModuleType())) {
            // mkleint: what do do here.. I don't get the concept of teh method at all.
            File buildDir = FileUtilities.getFileForProperty("maven.war.webapp.dir", project.getPropertyResolver());
            File fil = new File(buildDir, path);
            return fil;
        }
        // a reasonable fallback??
        File buildDir = FileUtilities.getFileForProperty("maven.build.dir", project.getPropertyResolver());
        File fil = new File(buildDir, path);
        System.out.println("Now how to lookup this file " + path + " for j2eeType=" + j2eeModule.getModuleType());
        return fil;
    }

    public J2eeModule getJ2eeModule() {
        if (j2eeModule.getModuleType() != null) {
            return j2eeModule;
        }
        return null;
    }

    public ModuleChangeReporter getModuleChangeReporter() {
        return moduleChangeReporter;
    }


    
    /**
     *  Returns list of root directories for source files including configuration files.
     *  Examples: file objects for src/java, src/conf.  
     *  Note: 
     *  If there is a standard configuration root, it should be the first one in
     *  the returned list.
     */
    public FileObject[] getSourceRoots() {
        Set fos = new HashSet();
        //TODO j2ee type specific roots first
        
        Sources sources = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] grps = sources.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
        for (int i = 0; i < grps.length; i++) {
            fos.add(grps[i].getRootFolder());
        }
        grps = sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < grps.length; i++) {
            fos.add(grps[i].getRootFolder());
        }
        URI generated = project.getGeneratedSourcesDir();
        if (generated != null) {
            FileObject fo = FileUtilities.convertURItoFileObject(generated);
            if (fo != null) {
                fos.add(fo);
            }
        }
        Collection additional = project.getAdditionalGeneratedSourceDirs();
        if (additional != null) {
            Iterator it = additional.iterator();
            while (it.hasNext()) {
                FileObject fo = FileUtilities.convertURItoFileObject((URI)it.next());
                if (fo != null) {
                    fos.add(fo);
                }
            }
        }
        return (FileObject[])fos.toArray(new FileObject[fos.size()]);
    }
    
    /**
     * Return name to be used in deployment of the module.
     */
    public String getDeploymentName() {
        String ret = super.getDeploymentName();
        return ret;
    }    

   
    public void setServerInstanceID(String str) {
        serverId = str;
    }
    
    /** If the module wants to specify a target server instance for deployment 
     * it needs to override this method to return false. 
     */
    public boolean useDefaultServer () {
        return serverId == null && (project.getPropertyResolver().getValue(SERVERID_PROPERTY) == null);
    }    
    
    /** Id of server isntance for deployment. The default implementation returns
     * the default server instance selected in Server Registry. 
     * The return value may not be null.
     * If modules override this method they also need to override {@link useDefaultServer}.
     */
    public String getServerInstanceID () {
        if (serverId != null && Deployment.getDefault().getServerID(serverId) != null) {
            return serverId;
        }
        String custom = project.getPropertyResolver().getResolvedValue(SERVERID_PROPERTY);
        if (custom != null && Deployment.getDefault().getServerID(custom) != null) {
            return custom;
        }
        return super.getServerInstanceID();
    }
    
    /** This method is used to determin type of target server.
     * The return value must correspond to value returned from {@link getServerInstanceID}.
     */
    public String getServerID () {
        if (serverId != null) {
            String tr = Deployment.getDefault().getServerID(serverId);
            if (tr != null) {
                return tr;
            }
        }
        String custom = project.getPropertyResolver().getResolvedValue(SERVERID_PROPERTY);
        if (custom != null) {
            String toRet = Deployment.getDefault().getServerID(custom);
            if (toRet != null) {
                return toRet;
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
