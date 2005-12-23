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
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.openide.filesystems.FileUtil;


/**
 * war/webapp related apis implementation..
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleImpl implements WebModuleImplementation, J2eeModule {
    private NbMavenProject project;
    public WebModuleImpl(NbMavenProject proj) {
        project = proj;
    }

    public FileObject getWebInf() {
        FileObject root = getDocumentBase();
        if (root != null) {
            return root.getFileObject("WEB-INF");
        }
        return null;
    }

    public String getJ2eePlatformVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                WebApp wa = prov.getDDRoot(dd);
                String waVersion = wa.getVersion() ;

                if(WebApp.VERSION_2_4.equals(waVersion)) {
                    return WebModule.J2EE_14_LEVEL;
                }
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
        return WebModule.J2EE_13_LEVEL;
    }

    public FileObject getDocumentBase() {
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
        SourceGroup[] grp = srcs.getSourceGroups(MavenSourcesImpl.TYPE_DOC_ROOT);
        if (grp.length > 0) {
            return grp[0].getRootFolder();
        }
        System.out.println("NO DOCUMENT BASE!!!");
        return null;
    }

    File getDDFile(final String path) {
        String webxmlDefined = PluginPropertyUtils.getPluginProperty(project,
                "org.apache.maven.plugins", "maven-war-plugin",
                "webXml", "war");
        if (webxmlDefined != null) {
            //TODO custom location.. relative or absolute? what the *&#! is the default resolved to?
        }
        URI dir = project.getWebAppDirectory();
        File fil = new File(new File(dir), path);
        return fil;
    }
    
    public FileObject getDeploymentDescriptor() {
        File dd = getDDFile(J2eeModule.WEB_XML);
        System.out.println("getDDFIle=" + dd);
        if (dd != null) {
            return FileUtil.toFileObject(dd);
        }
        return null;
    }

    public String getContextPath() {
        return "/" + project.getOriginalMavenProject().getBuild().getFinalName();
    }
    
    public boolean isValid() {
        //TODO any checks necessary?
        return true;
    }
    
    //88888888888888888888888888888888888888888888888888888888888888888888888888
    // methods of j2eeModule
    //88888888888888888888888888888888888888888888888888888888888888888888888888

    public String getModuleVersion() {
        return null;
    }

    public Object getModuleType() {
        return J2eeModule.WAR;
    }

    public String getUrl() {
        return null;
    }

    public void setUrl(String string) {
    }

    public FileObject getArchive() throws IOException {
        return null;
    }

    public Iterator getArchiveContents() throws IOException {
        return null;
    }

    public FileObject getContentDirectory() throws IOException {
        return null;
    }

    public BaseBean getDeploymentDescriptor(String string) {
        return null;
    }

    public void addVersionListener(J2eeModule.VersionListener versionListener) {
    }

    public void removeVersionListener(J2eeModule.VersionListener versionListener) {
    }


}
