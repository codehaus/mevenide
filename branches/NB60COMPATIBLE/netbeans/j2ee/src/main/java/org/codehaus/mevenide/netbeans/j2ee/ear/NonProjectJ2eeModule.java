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

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.modules.j2ee.dd.api.common.RootInterface;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJarMetadata;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.dd.spi.MetadataUnit;
import org.netbeans.modules.j2ee.dd.spi.ejb.EjbJarMetadataModelFactory;
import org.netbeans.modules.j2ee.dd.spi.web.WebAppMetadataModelFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * a j2eemodule implementation that is not tied to a particular project but
 *  works only on top of ear's modules' artifacts.. will this work?
 * @author mkleint
 */
public class NonProjectJ2eeModule implements J2eeModuleImplementation {
    private String moduleVersion;
    private Artifact artifact;
    private String url;
    private EarModuleProviderImpl provider;
    
    
    /** Creates a new instance of NonProjectJ2eeModule */
    public NonProjectJ2eeModule(Artifact art, String modVer, EarModuleProviderImpl prov) {
        artifact = art;
        moduleVersion = modVer;
        provider = prov;
    }
    
    public String getModuleVersion() {
//        System.out.println("NPJM: get Version=" + moduleVersion);
        return moduleVersion;
    }
    
    public Object getModuleType() {
        String type = artifact.getType();
//        System.out.println("NPJM: get type=" + type);
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
//        System.out.println("NPJM: get url=" + ret);
        return ret;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public FileObject getArchive() throws IOException {
//        System.out.println("NPJM: get archive=" + artifact.getFile());
        return FileUtil.toFileObject(FileUtil.normalizeFile(artifact.getFile()));
    }
    
    public Iterator getArchiveContents() throws IOException {
//        System.out.println("NPJM: get archive content..");
        return null;
    }
    
    public FileObject getContentDirectory() throws IOException {
        System.out.println("NPJM: get content..");
        return null;
    }
    
    public RootInterface getDeploymentDescriptor(String location) {
//        System.out.println("NPJM: get DD =" + location);
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
    
    
    private RootInterface readBaseBean(InputStream str) {
//        System.out.println("NPJM:   read base bean");
        String type = artifact.getType();
        if ("war".equals(type)) {
            try {
                FileObject root = FileUtil.getArchiveRoot(getArchive());
//                System.out.println("NPJM:root=" + root);
                return org.netbeans.modules.j2ee.dd.api.web.DDProvider.getDefault().getDDRoot(root.getFileObject(J2eeModule.WEB_XML));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if ("ejb".equals(type)) {
                try {
                    return org.netbeans.modules.j2ee.dd.api.ejb.DDProvider.getDefault().getDDRoot(new InputSource(str));
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        } else if ("ear".equals(type)) {
            try {
                return org.netbeans.modules.j2ee.dd.api.application.DDProvider.getDefault().getDDRoot(new InputSource(str));
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
        return null;
    }

    public File getResourceDirectory() {
        return  null;
    }

    public File getDeploymentConfigurationFile(String name) {
//       if (name == null) {
//            return null;
//        }
//        String path = provider.getConfigSupport().getContentRelativePath(name);
//        if (path == null) {
//            path = name;
//        }
        // here we don't really have access to the source deployment configs, as we operate on top of 
        // maven local repository binaries only..
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePropertyChangeListener(PropertyChangeListener arg0) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized MetadataModel<EjbJarMetadata> getMetadataModel() {
//TODO        if (ejbJarMetadataModel == null) {
//            FileObject ddFO = getDeploymentDescriptor();
//            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
//            ClassPathProviderImpl cpProvider = project.getLookup().lookup(ClassPathProviderImpl.class);
//            MetadataUnit metadataUnit = MetadataUnit.create(
//                cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
//                cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
//                cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
//                // XXX: add listening on deplymentDescriptor
//                ddFile);
//            ejbJarMetadataModel = EjbJarMetadataModelFactory.createMetadataModel(metadataUnit);
//        }
        return ejbJarMetadataModel;
    }

    public <T> MetadataModel<T> getMetadataModel(Class<T> type) {
        if (type == EjbJarMetadata.class) {
            @SuppressWarnings("unchecked") // NOI18N
            MetadataModel<T> model = (MetadataModel<T>)getMetadataModel();
            return model;
        }
        if (type == WebAppMetadata.class) {
            @SuppressWarnings("unchecked") // NOI18N
            MetadataModel<T> model = (MetadataModel<T>)getAnnotationMetadataModel();
            return model;
//        } else if (type == WebservicesMetadata.class) {
//            @SuppressWarnings("unchecked") // NOI18N
//            MetadataModel<T> model = (MetadataModel<T>)getWebservicesMetadataModel();
//            return model;
        }
        return null;
    }
        
    /**
     * The server plugin needs all models to be either merged on annotation-based. 
     * Currently only the web model does a bit of merging, other models don't. So
     * for web we actually need two models (one for the server plugins and another
     * for everyone else). Temporary solution until merging is implemented
     * in all models.
     */
    public synchronized MetadataModel<WebAppMetadata> getAnnotationMetadataModel() {
        if (webAppAnnMetadataModel == null) {
//TODO            FileObject ddFO = getDeploymentDescriptor();
//            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
//            ClassPathProviderImpl cpProvider = project.getLookup().lookup(ClassPathProviderImpl.class);
//            
//            MetadataUnit metadataUnit = MetadataUnit.create(
//                cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
//                cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
//                cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
//                // XXX: add listening on deplymentDescriptor
//                ddFile);
//            webAppAnnMetadataModel = WebAppMetadataModelFactory.createMetadataModel(metadataUnit, false);
        }
        return webAppAnnMetadataModel;
    }
        
    private MetadataModel<EjbJarMetadata> ejbJarMetadataModel;
    private MetadataModel<WebAppMetadata> webAppAnnMetadataModel;
    
}
