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

package org.codehaus.mevenide.netbeans.j2ee.ejb;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.modules.j2ee.dd.api.common.RootInterface;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJarMetadata;
import org.netbeans.modules.j2ee.dd.spi.MetadataUnit;
import org.netbeans.modules.j2ee.dd.spi.ejb.EjbJarMetadataModelFactory;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.spi.J2eeModuleImplementation;
import org.netbeans.modules.j2ee.metadata.ClassPathSupport;
import org.netbeans.modules.j2ee.metadata.model.api.MetadataModel;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/**
 * implementation of ejb netbeans functionality
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class EjbJarImpl implements EjbJarImplementation, J2eeModuleImplementation, ModuleChangeReporter {
    
    private NbMavenProject project;
    private List versionListeners;
    
    private EjbModuleProviderImpl provider;
    ""

//    private MetadataUnit metadataUnit;
    private ClassPath metadataClassPath;
    private MetadataModel<EjbJarMetadata> ejbJarMetadataModel;
    
    
    /** Creates a new instance of EjbJarImpl */
    EjbJarImpl(NbMavenProject proj, EjbModuleProviderImpl prov) {
        project = proj;
        versionListeners = new ArrayList();
        provider = prov;
    }
    
    boolean isValid() {
        //TODO any checks necessary??.
        return true;
    }
    
    /** J2EE platform version - one of the constants
     * defined in {@link org.netbeans.modules.j2ee.api.common.EjbProjectConstants}.
     * @return J2EE platform version
     */
    
    public String getJ2eePlatformVersion() {
        //TODO??
        return EjbProjectConstants.J2EE_14_LEVEL;
    }
    
    /** META-INF folder for the web module.
     */
    
    public FileObject getMetaInf() {
        Sources srcs = ProjectUtils.getSources(project);
        if (srcs != null) {
            SourceGroup[] grp = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
            for (int i = 0; i < grp.length; i++) {
                FileObject fo = grp[i].getRootFolder().getFileObject("META-INF"); //NOI18N
                if (fo != null) {
                    return fo;
                }
            }
        }
        return null;
    }
    
    File getDDFile(String path) {
        URI[] dir = project.getResources(false);
        File fil = new File(new File(dir[0]), path);
        fil = FileUtil.normalizeFile(fil);
//        System.out.println("EjbM:getDDFile=" + fil.getAbsolutePath());
        
        return fil;
    }
    
    /** Deployment descriptor (ejb-jar.xml file) of the ejb module.
     */
    public FileObject getDeploymentDescriptor() {
        FileObject metaInf = getMetaInf();
        if (metaInf != null) {
            return metaInf.getFileObject("ejb-jar.xml"); //NOI18N
        }
        return null;
    }
    
    public FileObject[] getJavaSources() {
        //TODO !!!!
//        System.out.println("EjbM:getJavaSources");
        
        return new FileObject[0];
    }
    
    public String getModuleVersion() {
        DDProvider prov = DDProvider.getDefault();
        FileObject dd = getDeploymentDescriptor();
        if (dd != null) {
            try {
                EjbJar ejb = prov.getDDRoot(dd);
                String ejbVersion = ejb.getVersion().toString();
                return ejbVersion;
            } catch (IOException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        } else {
            //look in pom's config.
            String version = PluginPropertyUtils.getPluginProperty(project,
                    "org.apache.maven.plugins", "maven-ejb-plugin", //NOI18N
                    "ejbVersion", "ejb"); //NOI18N
            if (version != null) {
                return version.trim();
            }
            
        }
        // in case there is no descriptor, we probably have 3.x spec stuff?
        return EjbJar.VERSION_2_1;
    }
    
    public Object getModuleType() {
        return J2eeModule.EJB;
    }
    
    /**
     * Returns the location of the module within the application archive.
     */
    public String getUrl() {
        return "/" + project.getOriginalMavenProject().getBuild().getFinalName(); //NOI18N
    }
    
    /**
     * Sets the location of the modules within the application archive.
     * For example, a web module could be at "/wbmodule1.war" within the ear
     * file. For standalone module the URL cannot be set to a different value
     * then "/"
     */
    public void setUrl(String url) {
        //
        throw new IllegalStateException("not implemented for maven projects.."); //NOI18N
    }
    
    /**
     * Returns the archive file for the module of null if the archive file
     * does not exist (for example, has not been compiled yet).
     */
    public FileObject getArchive() throws IOException {
        //TODO get the correct values for the plugin properties..
        String jarfile = PluginPropertyUtils.getPluginProperty(project,
                    "org.apache.maven.plugins", "maven-ejb-plugin", //NOI18N
                    "jarName", "ejb"); //NOI18N
        MavenProject proj = project.getOriginalMavenProject();
        if (jarfile == null) {
            jarfile = proj.getBuild().getFinalName();
        }
        String loc = proj.getBuild().getDirectory();
        File fil = FileUtil.normalizeFile(new File(loc, jarfile + ".jar")); //NOI18N
//        System.out.println("get ejb archive=" + fil);
        return FileUtil.toFileObject(fil);
        
    }
    
    /**
     * Returns the contents of the archive, in copyable form.
     *  Used for incremental deployment.
     *  Currently uses its own {@link RootedEntry} interface.
     *  If the J2eeModule instance describes a
     *  j2ee application, the result should not contain module archives.
     *
     * @return Iterator through {@link RootedEntry}s
     * 
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
    
    /**
     * This call is used in in-place deployment.
     *  Returns the directory staging the contents of the archive
     *  This directory is the one from which the content entries returned
     *  by {@link #getArchiveContents} came from.
     *
     * @return FileObject for the content directory
     */
    public FileObject getContentDirectory() throws IOException {
        String loc = project.getOriginalMavenProject().getBuild().getOutputDirectory();
        File fil = FileUtil.normalizeFile(new File(loc));
//        System.out.println("ejb jar..get content=" + fil);
        FileObject fo = FileUtil.toFileObject(fil.getParentFile());
        if (fo != null) {
            fo.refresh();
        }
        return FileUtil.toFileObject(fil);
    }
    
    
    /**
     * Returns a live bean representing the final deployment descriptor
     * that will be used for deploment of the module. This can be
     * taken from sources, constructed on fly or a combination of these
     * but it needs to be available even if the module has not been built yet.
     *
     * @param location Parameterized by location because of possibility of multiple
     * deployment descriptors for a single module (jsp.xml, webservices.xml, etc).
     * Location must be prefixed by /META-INF or /WEB-INF as appropriate.
     * @return a live bean representing the final DD
     */
    public RootInterface getDeploymentDescriptor(String location) {
        if (J2eeModule.EJBJAR_XML.equals(location)) {
            try {
                FileObject content = getContentDirectory();
                if (content == null) {
                    URI[] uris = project.getResources(false);
                    if (uris.length > 0) {
                        content = URLMapper.findFileObject(uris[0].toURL());
                    }
                }
                if (content != null) {
                    FileObject deploymentDescriptor = content.getFileObject(J2eeModule.EJBJAR_XML);
                    if(deploymentDescriptor != null) {
                        return DDProvider.getDefault().getDDRoot(deploymentDescriptor);
                    }
                }
             } catch (IOException e) {
                ErrorManager.getDefault().log(e.getLocalizedMessage());
             }
        }
//        System.out.println("no dd for=" + location);
        return null;
        
    }
    
    
    
    public EjbChangeDescriptor getEjbChanges(long timestamp) {
        return new EjbChange();
    }
    
    public boolean isManifestChanged(long timestamp) {
        //TODO
        return false;
    }

//    public MetadataUnit getMetadataUnit() {
//        synchronized (this) {
//            if (metadataUnit == null) {
//                metadataUnit = new MetadataUnitImpl();
//            }
//            return metadataUnit;
//        }
//    }
//    
//    private class MetadataUnitImpl implements MetadataUnit {
//        public ClassPath getClassPath() {
//            return getMetadataClassPath();
//        }
//        public FileObject getDeploymentDescriptor() {
//            return EjbJarImpl.this.getDeploymentDescriptor();
//        }
//    }
    
    private ClassPath getMetadataClassPath() {
        synchronized (this) {
            if (metadataClassPath == null) {
                ClassPathProviderImpl cpProvider = project.getLookup().lookup(org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl.class);
                metadataClassPath = ClassPathSupport.createWeakProxyClassPath(new ClassPath[] {
                    cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
//                    cpProvider.getJ2eePlatformClassPath(),
                });
            }
            return metadataClassPath;
        }
    }
    
    

    //TODO
    private class EjbChange implements EjbChangeDescriptor {
        public boolean ejbsChanged() {
            return false;
        }
        
        public String[] getChangedEjbs() {
            return new String[0];
        }
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

    public synchronized MetadataModel<EjbJarMetadata> getMetadataModel() {
        if (ejbJarMetadataModel == null) {
            FileObject ddFO = getDeploymentDescriptor();
            File ddFile = ddFO != null ? FileUtil.toFile(ddFO) : null;
            ClassPathProviderImpl cpProvider = project.getLookup().lookup(ClassPathProviderImpl.class);
            MetadataUnit metadataUnit = MetadataUnit.create(
                cpProvider.getProjectSourcesClassPath(ClassPath.BOOT),
                cpProvider.getProjectSourcesClassPath(ClassPath.COMPILE),
                cpProvider.getProjectSourcesClassPath(ClassPath.SOURCE),
                // XXX: add listening on deplymentDescriptor
                ddFile);
            ejbJarMetadataModel = EjbJarMetadataModelFactory.createMetadataModel(metadataUnit);
        }
        return ejbJarMetadataModel;
    }

    public <T> MetadataModel<T> getMetadataModel(Class<T> type) {
        if (type == EjbJarMetadata.class) {
            @SuppressWarnings("unchecked") // NOI18N
            MetadataModel<T> model = (MetadataModel<T>)getMetadataModel();
            return model;
//        } else if (type == WebservicesMetadata.class) {
//            @SuppressWarnings("unchecked") // NOI18N
//            MetadataModel<T> model = (MetadataModel<T>)getWebservicesMetadataModel();
//            return model;
        }
        return null;
    }
    
    
    
}
