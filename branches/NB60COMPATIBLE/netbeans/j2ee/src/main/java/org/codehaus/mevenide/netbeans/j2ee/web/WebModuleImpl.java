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

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.j2ee.J2eeMavenSourcesImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.modules.j2ee.dd.api.common.RootInterface;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.dd.api.web.WebAppMetadata;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.netbeans.modules.web.spi.webmodule.WebModuleImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.dd.spi.MetadataUnit;
import org.netbeans.modules.j2ee.dd.spi.web.WebAppMetadataModelFactory;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation;
import org.openide.filesystems.FileUtil;


/**
 * war/webapp related apis implementation..
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class WebModuleImpl implements WebModuleImplementation, J2eeModuleImplementation {
    private NbMavenProject project;
    private WebModuleProviderImpl provider;
    private MetadataModel<WebAppMetadata> webAppMetadataModel;
    private MetadataModel<WebAppMetadata> webAppAnnMetadataModel;
    
    private String url = ""; //NOI18N

    private boolean inplace = false;
    
    public WebModuleImpl(NbMavenProject proj, WebModuleProviderImpl prov) {
        project = proj;
        provider = prov;
    }
    
    public FileObject getWebInf() {
        FileObject root = getDocumentBase();
        if (root != null) {
            return root.getFileObject("WEB-INF"); //NOI18N
        }
        return null;
    }
    
    /**
     * to be used to denote that a war:inplace goal is used to build the web app.
     */
    public void setWarInplace(boolean inplace) {
        this.inplace = inplace;
    }
    
    public String getJ2eePlatformVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                WebApp wa = prov.getDDRoot(dd);
                String waVersion = wa.getVersion() ;
                
                if(WebApp.VERSION_2_3.equals(waVersion)) {
                    return WebModule.J2EE_13_LEVEL;
                }
                if (WebApp.VERSION_2_4.equals(waVersion)) {
                    return WebModule.J2EE_14_LEVEL;
                }
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
        //make 15 the default..
        return WebModule.JAVA_EE_5_LEVEL;
    }
    
    public FileObject getDocumentBase() {
        Sources srcs = ProjectUtils.getSources(project);
        SourceGroup[] grp = srcs.getSourceGroups(J2eeMavenSourcesImpl.TYPE_DOC_ROOT);
        if (grp.length > 0) {
            return grp[0].getRootFolder();
        }
//        System.out.println("NO DOCUMENT BASE!!! " + project.getProjectDirectory());
        return null;
    }
    
    File getDDFile(final String path) {
        String webxmlDefined = PluginPropertyUtils.getPluginProperty(project,
                "org.apache.maven.plugins", "maven-war-plugin", //NOI18N
                "webXml", "war"); //NOI18N
        if (webxmlDefined != null) {
            //TODO custom location.. relative or absolute? what the *&#! is the default resolved to?
        }
        URI dir = project.getWebAppDirectory();
        File fil = new File(new File(dir), path);
        fil = FileUtil.normalizeFile(fil);
        return fil;
    }
    
    public FileObject getDeploymentDescriptor() {
        File dd = getDDFile(J2eeModule.WEB_XML);
//        System.out.println("getDDFIle=" + dd);
        if (dd != null) {
            return FileUtil.toFileObject(dd);
        }
        return null;
    }
    
    public String getContextPath() {
        String toRet =  "/" + project.getOriginalMavenProject().getBuild().getFinalName(); //NOI18N
//        System.out.println("get context path=" + toRet);
        return toRet;
    }
    
    public boolean isValid() {
        //TODO any checks necessary?
        return true;
    }
    
    //88888888888888888888888888888888888888888888888888888888888888888888888888
    // methods of j2eeModule
    //88888888888888888888888888888888888888888888888888888888888888888888888888
    
    public String getModuleVersion() {
        WebApp wapp = getWebApp ();
        String version = WebApp.VERSION_2_5;
        if (wapp != null)
            version = wapp.getVersion();
        return version;
    }
    
    public Object getModuleType() {
        return J2eeModule.WAR;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String string) {
        url = string;
    }
    
    public FileObject getArchive() throws IOException {
        //TODO get the correct values for the plugin properties..
        MavenProject proj = project.getOriginalMavenProject();
        String finalName = proj.getBuild().getFinalName();
        String loc = proj.getBuild().getDirectory();
        File fil = FileUtil.normalizeFile(new File(loc, finalName + ".war")); //NOI18N
//        System.out.println("get archive=" + fil);
        return FileUtil.toFileObject(fil);
    }

    /**
     * according to sharold@netbeans.org this should return the iterator over
     * non-warred file, meaning from the expanded webapp. weird.
     */
    public Iterator getArchiveContents() throws IOException {
        
//        System.out.println("get archive content");
        FileObject fo = getContentDirectory();
        if (fo != null) {
            return new ContentIterator(fo);
        }
        return null;
    }
    
    public FileObject getContentDirectory() throws IOException {
        FileObject fo;
        if (inplace) {
            fo = getDocumentBase();
        } else {
            MavenProject proj = project.getOriginalMavenProject();
            String finalName = proj.getBuild().getFinalName();
            String loc = proj.getBuild().getDirectory();
            File fil = FileUtil.normalizeFile(new File(loc, finalName));
    //        System.out.println("get content=" + fil);
            fo = FileUtil.toFileObject(fil);
        } 
        if (fo != null) {
            fo.refresh();
        }
        return fo;
    }
    
    
    private WebApp getWebApp () {
        try {
            FileObject deploymentDescriptor = getDeploymentDescriptor ();
            if(deploymentDescriptor != null) {
                WebApp webApp = DDProvider.getDefault().getDDRoot(deploymentDescriptor);
            }
        } catch (java.io.IOException e) {
            org.openide.ErrorManager.getDefault ().log (e.getLocalizedMessage ());
        }
        return null;
    }    
    
    //TODO this probably also adds test sources.. is that correct?
    public FileObject[] getJavaSources() {
        Sources srcs = ProjectUtils.getSources(project);
        SourceGroup[] gr = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        List toRet = new ArrayList();
        if (gr != null) {
            for (int i = 0; i < gr.length; i++) {
                toRet.add(gr[i].getRootFolder());
            }
        }
        return (FileObject[])toRet.toArray(new FileObject[toRet.size()]);
    }
    
    
    // inspired by netbeans' webmodule codebase, not really sure what is the point
    // of the iterator..
    private static final class ContentIterator implements Iterator {
        private ArrayList ch;
        private FileObject root;
        
        private ContentIterator(FileObject f) {
            this.ch = new ArrayList();
            ch.add(f);
            this.root = f;
        }
        
        public boolean hasNext() {
            return ! ch.isEmpty();
        }
        
        public Object next() {
            FileObject f = (FileObject) ch.get(0);
            ch.remove(0);
            if (f.isFolder()) {
                f.refresh();
                FileObject[] chArr = f.getChildren();
                for (int i = 0; i < chArr.length; i++) {
                    ch.add(chArr [i]);
                }
            }
            return new FSRootRE(root, f);
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
    }
    
    private static final class FSRootRE implements J2eeModule.RootedEntry {
        private FileObject f;
        private FileObject root;
        
        FSRootRE(FileObject rt, FileObject fo) {
            f = fo;
            root = rt;
        }
        
        public FileObject getFileObject() {
            return f;
        }
        
        public String getRelativePath() {
            return FileUtil.getRelativePath(root, f);
        }
    }

    /**
     * Returns the module resource directory, or null if the module has no resource
     * directory.
     * 
     * @return the module resource directory, or null if the module has no resource
     *         directory.
     */

    public File getResourceDirectory() {
        //TODO .. in ant projects equals to "setup" directory.. what's it's use?
        File toRet = new File(project.getPOMFile().getParentFile(), "src" + File.separator + "main" + File.separator + "setup"); //NOI18N
        return toRet;
    }

    /**
     * Returns source deployment configuration file path for the given deployment 
     * configuration file name.
     *
     * @param name file name of the deployment configuration file, WEB-INF/sun-web.xml
     *        for example.
     * 
     * @return absolute path to the deployment configuration file, or null if the
     *         specified file name is not known to this J2eeModule.
     */
    public File getDeploymentConfigurationFile(String name) {
       if (name == null) {
            return null;
        }
        String path = provider.getConfigSupport().getContentRelativePath(name);
        if (path == null) {
            path = name;
        }
        return getDDFile(path);
    }

   /**
     * Add a PropertyChangeListener to the listener list.
     * 
     * @param listener PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener arg0) {
        //TODO..
    }
    
    /**
     * Remove a PropertyChangeListener from the listener list.
     * 
     * @param listener PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener arg0) {
        //TODO..
    }

    public <T> MetadataModel<T> getMetadataModel(Class<T> type) {
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
    
    public synchronized MetadataModel<WebAppMetadata> getMetadataModel() {
        if (webAppMetadataModel == null) {
            FileObject ddFO = getDeploymentDescriptor();
            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
            ClassPathProviderImpl cpProvider = project.getLookup().lookup(ClassPathProviderImpl.class);
            MetadataUnit metadataUnit = MetadataUnit.create(
                cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
                cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
                cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
                // XXX: add listening on deplymentDescriptor
                ddFile);
            webAppMetadataModel = WebAppMetadataModelFactory.createMetadataModel(metadataUnit, true);
        }
        return webAppMetadataModel;
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
            FileObject ddFO = getDeploymentDescriptor();
            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
            ClassPathProviderImpl cpProvider = project.getLookup().lookup(ClassPathProviderImpl.class);
            
            MetadataUnit metadataUnit = MetadataUnit.create(
                cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
                cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
                cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
                // XXX: add listening on deplymentDescriptor
                ddFile);
            webAppAnnMetadataModel = WebAppMetadataModelFactory.createMetadataModel(metadataUnit, false);
        }
        return webAppAnnMetadataModel;
    }
    
}
