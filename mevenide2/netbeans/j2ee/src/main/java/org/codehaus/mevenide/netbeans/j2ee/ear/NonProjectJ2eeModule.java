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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.maven.artifact.Artifact;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * a j2eemodule implementation that is not tied to a particular project but
 *  works only on top of ear's modules' artifacts.. will this work?
 * @author mkleint
 */
public class NonProjectJ2eeModule implements J2eeModule {
    private String moduleVersion;
    private Artifact artifact;
    private String url;
    
    /** Creates a new instance of NonProjectJ2eeModule */
    public NonProjectJ2eeModule(Artifact art, String modVer) {
        artifact = art;
        moduleVersion = modVer;
    }
    
    public String getModuleVersion() {
        System.out.println("NPJM: get Version=" + moduleVersion);
        return moduleVersion;
    }
    
    public Object getModuleType() {
        String type = artifact.getType();
        System.out.println("NPJM: get type=" + type);
        if ("war".equals(type)) {
            return J2eeModule.WAR;
        }
        if ("ejb".equals(type)) {
            return J2eeModule.EJB;
        }
        if ("ear".equals(type)) {
            return J2eeModule.EAR;
        }
        //TODO what to do here?
        return J2eeModule.CLIENT;
    }
    
    public String getUrl() {
        //TODO url should be probably based on application.xml??
        String ret = url == null ? artifact.getFile().getName() : url;
        System.out.println("NPJM: get url=" + ret);
        return ret;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public FileObject getArchive() throws IOException {
        System.out.println("NPJM: get archive=" + artifact.getFile());
        return FileUtil.toFileObject(FileUtil.normalizeFile(artifact.getFile()));
    }
    
    public Iterator getArchiveContents() throws IOException {
        System.out.println("NPJM: get archive content..");
        return null;
    }
    
    public FileObject getContentDirectory() throws IOException {
        System.out.println("NPJM: get content..");
        return null;
    }
    
    public BaseBean getDeploymentDescriptor(String location) {
        System.out.println("NPJM: get DD =" + location);
        try {
            JarFile fil = new JarFile(artifact.getFile());
            ZipEntry entry = fil.getEntry(location);
            if (entry != null) {
                InputStream str = fil.getInputStream(entry);
                return readBaseBean(str);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public void addVersionListener(J2eeModule.VersionListener listener) {
        System.out.println("NonProjectJ2eeModule adding version listener");
    }
    
    public void removeVersionListener(J2eeModule.VersionListener listener) {
        System.out.println("NonProjectJ2eeModule removing version listener");
    }
    
    private BaseBean readBaseBean(InputStream str) {
        System.out.println("NPJM:   read base bean");
        String type = artifact.getType();
        if ("war".equals(type)) {
            try {
                FileObject root = FileUtil.getArchiveRoot(getArchive());
                System.out.println("NPJM:root=" + root);
                WebApp web = org.netbeans.modules.j2ee.dd.api.web.DDProvider.getDefault().getDDRoot(root.getFileObject(J2eeModule.WEB_XML));
                System.out.println("NPJM:web..=" + web);
                if (web != null) {
                    return org.netbeans.modules.j2ee.dd.api.web.DDProvider.getDefault().getBaseBean(web);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if ("ejb".equals(type)) {
                try {
                    EjbJar jar = org.netbeans.modules.j2ee.dd.api.ejb.DDProvider.getDefault().getDDRoot(new InputSource(str));
                    System.out.println("NPJM:ejbjar=" + jar);
                    if (jar != null) {
                        return org.netbeans.modules.j2ee.dd.api.ejb.DDProvider.getDefault().getBaseBean(jar);
                    }
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        } else if ("ear".equals(type)) {
            try {
                Application app = org.netbeans.modules.j2ee.dd.api.application.DDProvider.getDefault().getDDRoot(new InputSource(str));
                if (app != null) {
                    System.out.println("NPJM:getDeploymentDescriptor.returning a base bean...");
                    return org.netbeans.modules.j2ee.dd.api.application.DDProvider.getDefault().getBaseBean(app);
                }
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
        return null;
    }
    
}
