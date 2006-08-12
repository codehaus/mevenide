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
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.codehaus.plexus.util.StringInputStream;
import org.netbeans.modules.j2ee.api.ejbjar.EjbJar;
import org.netbeans.modules.j2ee.api.ejbjar.EjbProjectConstants;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.application.DDProvider;
import org.netbeans.modules.j2ee.deployment.common.api.EjbChangeDescriptor;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModuleContainer;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleChangeReporter;
import org.netbeans.modules.j2ee.deployment.devmodules.api.ModuleListener;
import org.netbeans.modules.j2ee.spi.ejbjar.EarImplementation;
import org.netbeans.modules.schema2beans.BaseBean;
import org.netbeans.modules.web.api.webmodule.WebModule;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * implementation of ear related netbeans functionality
 * @author Milos Kleint (mkleint@codehaus.org)
 */
class EarImpl implements EarImplementation, J2eeModule, J2eeModuleContainer, ModuleChangeReporter {

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
        System.out.println("eariml: getj2eepaltform");
        return EjbProjectConstants.J2EE_14_LEVEL;
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
                generatedLoc = project.getOriginalMavenProject().getBuild().getDirectory();
            }
            FileObject fo = FileUtilities.convertURItoFileObject(FileUtilities.getDirURI(project.getProjectDirectory(), generatedLoc));
            if (fo != null) {
                return fo.getFileObject("application.xml");
            } else {
                //TODO maybe run the generate-resources phase to get a DD
                System.out.println("we don't have the application.xmk generated yet at=" + generatedLoc);
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
        throw new IllegalStateException("Not implemented for maven based projects.");
    }

    /** Add j2ee ejbjar module into application.
     * @param module the module to be added
     */
    public void addEjbJarModule(EjbJar ejbJar) {
        //TODO this probably means adding the module as dependency to the pom.
        throw new IllegalStateException("Not implemented for maven based projects.");
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
        //TODO how to check and what to check for..
        return true;
    }

    public Object getModuleType() {
        return J2eeModule.EAR;
    }

    /**
     * Returns module specification version
     */
    public String getModuleVersion() {
        System.out.println("earimpl: get module version");
        //TODO??
        return J2eeModule.J2EE_14;
    }

    /**
     * Returns the location of the module within the application archive.
     */
    public String getUrl() {
        System.out.println("EarImpl: getURL");
        return "/";
    }

    /**
     * Sets the location of the modules within the application archive.
     * For example, a web module could be at "/wbmodule1.war" within the ear
     * file. For standalone module the URL cannot be set to a different value
     * then "/"
     */
    public void setUrl(String url) {
        throw new IllegalStateException("Cannot set url for maven ear projects");
    }

    /**
     * Returns the archive file for the module of null if the archive file 
     * does not exist (for example, has not been compiled yet).
     */
    public FileObject getArchive() throws IOException {
        //TODO get the correct values for the plugin properties..
        MavenProject proj = project.getOriginalMavenProject();
        String finalName = proj.getBuild().getFinalName();
        String loc = proj.getBuild().getDirectory();
        File fil = FileUtil.normalizeFile(new File(loc, finalName + ".ear"));
        System.out.println("ear = get archive=" + fil);
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
     */
    public Iterator getArchiveContents() throws IOException {
        System.out.println("ear get archive content");
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
        MavenProject proj = project.getOriginalMavenProject();
        String finalName = proj.getBuild().getFinalName();
        String loc = proj.getBuild().getDirectory();
        File fil = FileUtil.normalizeFile(new File(loc, finalName));
        System.out.println("earimpl. get content=" + fil);
        FileObject fo = FileUtil.toFileObject(fil);
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
        if (J2eeModule.APP_XML.equals(location)) {
            try {
                
                FileObject content = getDeploymentDescriptor();
                if (content == null) {
                    System.out.println("getDeploymentDescriptor.application dd is null");
                    StringInputStream str = new StringInputStream(
  "<application xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/application_1_4.xsd\" version=\"1.4\">" +
  "<description>DayTrader Stock Trading Performance Benchmark Sample</description>" +
  "<display-name>Trade</display-name></application>");
                    try {
                        Application app = DDProvider.getDefault().getDDRoot(new InputSource(str));
                        if (app != null) {
                            System.out.println("getDeploymentDescriptor.returning a base bean...");
                            return DDProvider.getDefault().getBaseBean(app);
                        }
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Application app = DDProvider.getDefault().getDDRoot(content);
                    if (app != null) {
                        System.out.println("getDeploymentDescriptor bean-have dd");
                        return DDProvider.getDefault().getBaseBean(app);
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
    }

    /**
     * Remove module version change listener.
     * 
     * @param listener on version change
     */
    public void removeVersionListener(J2eeModule.VersionListener listener) {
    }

    public J2eeModule[] getModules(ModuleListener ml) {
        System.out.println("EarImpl.getModules called");
        Iterator it = project.getOriginalMavenProject().getArtifacts().iterator();
        List toRet = new ArrayList();
        while (it.hasNext()) {
            Artifact elem = (Artifact) it.next();
            if ("war".equals(elem.getType()) || "ejb".equals(elem.getType())) {
                System.out.println("adding " + elem.getId());
                //TODO probaby figure out the context root etc..
                toRet.add(new NonProjectJ2eeModule(elem, getJ2eePlatformVersion()));
            }
        }
        return (J2eeModule[])toRet.toArray(new J2eeModule[toRet.size()]);
    }
    
    File getDDFile(String path) {
        System.out.println("getDD file=" + path);
        //TODO what is the actual path.. sometimes don't have any sources for deployment descriptors..
        URI dir = project.getEarAppDirectory();
        File fil = new File(new File(dir), path);
        if (!fil.getParentFile().exists()) {
            fil.getParentFile().mkdirs();
        }
        fil = FileUtil.normalizeFile(fil);
        return fil;
    }
    

    public void addModuleListener(ModuleListener ml) {
    }

    public void removeModuleListener(ModuleListener ml) {
    }

    public EjbChangeDescriptor getEjbChanges(long timestamp) {
        return new EjbChange();
    }

    public boolean isManifestChanged(long timestamp) {
        return false;
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
    
    //TODO
    private class EjbChange implements EjbChangeDescriptor {
        public boolean ejbsChanged() {
            return false;
        }
        
        public String[] getChangedEjbs() {
            return new String[0];
        }
    }
    

}
