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
import java.io.IOException;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.application.DDProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EarImplementation;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;

/**
 * implementation of ear related netbeans functionality
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class EarImpl implements EarImplementation {

    private NbMavenProject project;
    
    /** Creates a new instance of EarImpl */
    EarImpl(NbMavenProject proj) {
        project = proj;
    }

    /** J2EE platform version - one of the constants 
     * defined in {@link org.netbeans.modules.j2ee.api.common.EjbProjectConstants}.
     * @return J2EE platform version
     */
    public String getJ2eePlatformVersion() {
        if (isApplicationXmlGenerated()) {
            String version = PluginPropertyUtils.getPluginProperty(project, "org.apache.maven.plugins", 
                                              "maven-ear-plugin", "version", "generate-application-xml");
            // the default version in maven plugin is also 1.3
            //TODO what if the default changes?
            if (version != null) {
                return version.trim();
            }
        } else {
            DDProvider prov = DDProvider.getDefault();
            FileObject dd = getDeploymentDescriptor();
            if (dd != null) {
                try {
                    Application app = prov.getDDRoot(dd);
                    String appVersion = app.getVersion().toString();
                    return appVersion;
                } catch (IOException exc) {
                    ErrorManager.getDefault().notify(exc);
                }
            }
        }
        // hardwire?
        return EjbProjectConstants.J2EE_13_LEVEL;
    }

    /** META-INF folder for the Ear.
     */
    public FileObject getMetaInf() {
        String appsrcloc =  PluginPropertyUtils.getPluginProperty(project, "org.apache.maven.plugins", 
                                              "maven-ear-plugin", "earSourceDirectory", "ear");
        if (appsrcloc == null) {
            appsrcloc = "main/src/application";
        }
        FileObject root = FileUtilities.convertURItoFileObject(FileUtilities.getDirURI(project.getProjectDirectory(), appsrcloc));
        if (root != null) {
            return root.getFileObject("META-INF");
        }
        return null;
    }

    /** Deployment descriptor (application.xml file) of the ejb module.
     */
    public FileObject getDeploymentDescriptor() {
        if (isApplicationXmlGenerated()) {
            String generatedLoc = PluginPropertyUtils.getPluginProperty(project, "org.apache.maven.plugins", 
                                              "maven-ear-plugin", "generatedDescriptorLocation", "generate-application-xml");
            if (generatedLoc == null) {
                generatedLoc = project.getOriginalMavenProject().getBuild().getOutputDirectory();
            }
            FileObject fo = FileUtilities.convertURItoFileObject(FileUtilities.getDirURI(project.getProjectDirectory(), generatedLoc));
            if (fo != null) {
                return fo.getFileObject("application.xml");
            } else {
                //TODO maybe run the generate-resources phase to get a DD
            }
        }
        String customLoc =  PluginPropertyUtils.getPluginProperty(project, "org.apache.maven.plugins", 
                                              "maven-ear-plugin", "applicationXml", "ear");
        if (customLoc != null) {
            FileObject fo = FileUtilities.convertURItoFileObject(FileUtilities.getDirURI(project.getProjectDirectory(), customLoc));
            if (fo != null) {
                return fo;
            }
        }

        return null;
    }

    /** Add j2ee webmodule into application.
     * @param module the module to be added
     */
    public void addWebModule(WebModule webModule) {
        //TODO this probably means adding the module as dependency to the pom.
        throw new IllegalStateException("Not implemented yet for maven based projects.");
    }

    /** Add j2ee ejbjar module into application.
     * @param module the module to be added
     */
    public void addEjbJarModule(EjbJar ejbJar) {
        //TODO this probably means adding the module as dependency to the pom.
        throw new IllegalStateException("Not implemented yet for maven based projects.");
    }
    
    private boolean isApplicationXmlGenerated() {
        String str = PluginPropertyUtils.getPluginProperty(project, "org.apache.maven.plugins", 
                                                                    "maven-ear-plugin", 
                                                                    "generateApplicationXml", 
                                                                    "generate-application-xml");
            //either the default or explicitly set generation of application.xml file 
        return (str == null || Boolean.valueOf(str).booleanValue());
    }

    boolean isValid() {
        return true;
    }
    
}
