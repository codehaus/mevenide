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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.modules.j2ee.dd.api.ejb.DDProvider;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarImplementation;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;

/**
 * implementation of ejb netbeans functionality
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class EjbJarImpl implements EjbJarImplementation, J2eeModule, ModuleChangeReporter {
    
    private NbMavenProject project;
    private List versionListeners;
    /** Creates a new instance of EjbJarImpl */
    EjbJarImpl(NbMavenProject proj) {
        project = proj;
        versionListeners = new ArrayList();
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
        return EjbProjectConstants.J2EE_14_LEVEL;
    }
    
    /** META-INF folder for the web module.
     */
    
    public FileObject getMetaInf() {
        Sources srcs = ProjectUtils.getSources(project);
        if (srcs != null) {
            SourceGroup[] grp = srcs.getSourceGroups(MavenSourcesImpl.TYPE_RESOURCES);
            for (int i = 0; i < grp.length; i++) {
                FileObject fo = grp[i].getRootFolder().getFileObject("META-INF");
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
        System.out.println("EjbM:getDDFile=" + fil.getAbsolutePath());
        
        return fil;
    }
    
    /** Deployment descriptor (ejb-jar.xml file) of the ejb module.
     */
    public FileObject getDeploymentDescriptor() {
        FileObject metaInf = getMetaInf();
        if (metaInf != null) {
            return metaInf.getFileObject("ejb-jar.xml");
        }
        return null;
    }
    
    public FileObject[] getJavaSources() {
        //TODO !!!!
        System.out.println("EjbM:getJavaSources");
        
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
                    "org.apache.maven.plugins", "maven-ejb-plugin",
                    "ejbVersion", "ejb");
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
        return "/" + project.getOriginalMavenProject().getBuild().getFinalName();
    }
    
    /**
     * Sets the location of the modules within the application archive.
     * For example, a web module could be at "/wbmodule1.war" within the ear
     * file. For standalone module the URL cannot be set to a different value
     * then "/"
     */
    public void setUrl(String url) {
        //
        throw new IllegalStateException("not implemented for maven projects..");
    }
    
    /**
     * Returns the archive file for the module of null if the archive file
     * does not exist (for example, has not been compiled yet).
     */
    public FileObject getArchive() throws IOException {
        //TODO get the correct values for the plugin properties..
        String jarfile = PluginPropertyUtils.getPluginProperty(project,
                    "org.apache.maven.plugins", "maven-ejb-plugin",
                    "jarName", "ejb");
        MavenProject proj = project.getOriginalMavenProject();
        if (jarfile == null) {
            jarfile = proj.getBuild().getFinalName();
        }
        String loc = proj.getBuild().getDirectory();
        File fil = FileUtil.normalizeFile(new File(loc, jarfile + ".jar"));
        System.out.println("get ejb archive=" + fil);
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
        System.out.println("ejb jar..get content=" + fil);
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
    public BaseBean getDeploymentDescriptor(String location) {
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
                        EjbJar jar = DDProvider.getDefault().getDDRoot(deploymentDescriptor);
                        if (jar != null) {
                            return DDProvider.getDefault().getBaseBean(jar);
                        }
                    }
                }
             } catch (IOException e) {
                ErrorManager.getDefault().log(e.getLocalizedMessage());
             }
        }
        System.out.println("no dd for=" + location);
        return null;
        
    }
    
    /**
     * Add module change listener.
     *
     * @param listener on version change
     */
    public void addVersionListener(J2eeModule.VersionListener listener) {
        versionListeners.add(listener);
    }
    
    /**
     * Remove module version change listener.
     *
     * @param listener on version change
     */
    public void removeVersionListener(J2eeModule.VersionListener listener) {
        versionListeners.remove(listener);
    }
    
    
    public EjbChangeDescriptor getEjbChanges(long timestamp) {
        return new EjbChange();
    }
    
    public boolean isManifestChanged(long timestamp) {
        //TODO
        return false;
    }

    //55 only..
//    public MetadataUnit getMetadataUnit() {
//        return null;
//    }

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
    
    
}
