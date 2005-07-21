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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.mevenide.netbeans.j2ee.web.WebModuleImpl;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.modules.j2ee.api.ejbjar.Ear;
import org.netbeans.modules.j2ee.dd.api.application.Application;
import org.netbeans.modules.j2ee.dd.api.ejb.EjbJar;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.j2ee.spi.ejbjar.EarProvider;
import org.netbeans.modules.j2ee.spi.ejbjar.EjbJarProvider;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class MavenJ2eeModule implements J2eeModule {
    private MavenProject project;
    private String url;
    private ArrayList listeners;
    
    /** Creates a new instance of MavenJ2eeModule */
    public MavenJ2eeModule(MavenProject proj) {
        project = proj;
        url = project.getOriginalMavenProject().getArtifactId();
        listeners = new ArrayList();
    }

    /**
     * TODO: what are the listeners for?
     */
    public void addVersionListener(J2eeModule.VersionListener listener) {
        listeners.add(listener);
    }
    
    /**
     * TODO: what are the listeners for?
     */
    public void removeVersionListener(J2eeModule.VersionListener listener) {
        listeners.remove(listener);
    }

    /** Returns the archive file for the module of null if the archive file 
     * does not exist (for example, has not been compiled yet). 
     */
    public FileObject getArchive() throws IOException {
//        System.out.println("getArchive()");
        if (J2eeModule.WAR.equals(getModuleType())) {
            FileObject buildDir = FileUtilities.getFileObjectForProperty("maven.war.build.dir", project.getPropertyResolver());
            if (buildDir != null) {
//                System.out.println("final name=" + project.getPropertyResolver().getResolvedValue("maven.war.final.name") );
//                System.out.println("  returning " + buildDir.getFileObject(project.getPropertyResolver().getResolvedValue("maven.war.final.name") + ".war"));
                return buildDir.getFileObject(project.getPropertyResolver().getResolvedValue("maven.war.final.name") + ".war");
            }
        }
        //TODO ears/ejbs/rars
        if (J2eeModule.EAR.equals(getModuleType())) {
            String val = project.getPropertyResolver().resolveString("${maven.build.dir}/${maven.ear.final.name}");
            if (val != null) {
                File fil = new File(val);
                fil = FileUtil.normalizeFile(fil);
                FileObject fo = FileUtil.toFileObject(fil);
                if (fo != null) {
                    return fo;
                }
            }
        }
        if (J2eeModule.EJB.equals((getModuleType()))) {
            String val = project.getPropertyResolver().resolveString("${maven.build.dir}/${maven.final.name}.jar");
            if (val != null) {
                File fil = new File(val);
                fil = FileUtil.normalizeFile(fil);
                FileObject fo = FileUtil.toFileObject(fil);
                if (fo != null) {
                    return fo;
                }
            }
        }
        return null;
    }
    /** Returns the contents of the archive, in copyable form.
     *  Used for incremental deployment.
     *  Currently uses its own {@link RootedEntry} interface.
     *  If the J2eeModule instance describes a
     *  j2ee application, the result should not contain module archives.
     *  @return Iterator through {@link RootedEntry}s
     */

    public Iterator getArchiveContents() throws IOException {
//        System.out.println("getArchiveContents()");
        //TODO probably just solution for wars, not ears.
        return new ContentIterator(getContentDirectory());
    }

    /** This call is used in in-place deployment. 
     *  Returns the directory staging the contents of the archive
     *  This directory is the one from which the content entries returned
     *  by {@link #getArchiveContents} came from.
     *  @return FileObject for the content directory
     */
    public FileObject getContentDirectory() throws IOException {
//        System.out.println("getContentDirectory()");
        if (J2eeModule.WAR.equals(getModuleType())) {
            return FileUtilities.getFileObjectForProperty("maven.war.webapp.dir", project.getPropertyResolver());        
        }
        // what to do for ejbs/ears.. maven doesn't have a build directory with complete content AFAIK.
        return null;
    }

    /** Returns a live bean representing the final deployment descriptor
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
//        System.out.println("getDeploymentDescriptor()");
        if (J2eeModule.WEB_XML.equals(location)){
            WebApp webApp = getWebApp();
            if (webApp != null) {
                //TODO find a better way to get the BB from WApp and remove the HACK from DDProvider!!
                return DDProvider.getDefault ().getBaseBean (webApp);
            }
        }
        if (J2eeModule.APP_XML.equals(location)) {
            EarProvider prov = (EarProvider)project.getLookup().lookup(EarProvider.class);
            Ear ear = prov.findEar(project.getProjectDirectory());
            FileObject fo = ear.getDeploymentDescriptor();
            if (fo != null) {
                try {
                    Application app = org.netbeans.modules.j2ee.dd.api.application.DDProvider.getDefault().getDDRoot(fo);
                    if (app != null) {
                        return org.netbeans.modules.j2ee.dd.api.application.DDProvider.getDefault().getBaseBean(app);
                    }
                } catch (java.io.IOException e) {
                    ErrorManager.getDefault ().log (e.getLocalizedMessage ());
                }
            }
        }
        if (J2eeModule.EJBJAR_XML.equals(location)) {
            EjbJarProvider prov = (EjbJarProvider)project.getLookup().lookup(EjbJarProvider.class);
            org.netbeans.modules.j2ee.api.ejbjar.EjbJar ejb = prov.findEjbJar(project.getProjectDirectory());
            FileObject fo = ejb.getDeploymentDescriptor();
            if (fo != null) {
                try {
                    EjbJar e = org.netbeans.modules.j2ee.dd.api.ejb.DDProvider.getDefault().getDDRoot(fo);
                    if (e != null) {
                        return org.netbeans.modules.j2ee.dd.api.ejb.DDProvider.getDefault().getBaseBean(e);
                    }
                } catch (java.io.IOException e) {
                    ErrorManager.getDefault ().log (e.getLocalizedMessage ());
                }
            }
        }
//        else if(J2eeModule.WEBSERVICES_XML.equals(location)){
//            Webservices webServices = getWebservices();
//            if(webServices != null){
//                return org.netbeans.modules.j2ee.dd.api.webservices.DDProvider.getDefault().getBaseBean(webServices);
//            }
//        }
        return null;        
    }
    
   private WebApp getWebApp () {
        try {
            WebModuleImpl impl = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
            FileObject deploymentDescriptor = impl.getDeploymentDescriptor();
            if(deploymentDescriptor != null) {
                return DDProvider.getDefault().getDDRoot(deploymentDescriptor);
            }
        } catch (IOException e) {
            ErrorManager.getDefault ().log (e.getLocalizedMessage ());
        }
        return null;
    }    

    public Object getModuleType() {
        WebModuleImpl wm = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
        if  (wm != null && wm.isValid()) {
            return J2eeModule.WAR;
        }
        MavenEjbJarImpl ejb = (MavenEjbJarImpl)project.getLookup().lookup(MavenEjbJarImpl.class);
        if (ejb != null && ejb.isValid()) {
            return J2eeModule.EJB;
        }
        MavenEarImpl ear = (MavenEarImpl)project.getLookup().lookup(MavenEarImpl.class);
        if (ear != null && ear.isValid()) {
            return J2eeModule.EAR;
        }
        File connStr = FileUtilities.getFileForProperty("maven.rar.raxml", project.getPropertyResolver());
        if (connStr != null && connStr.exists()) {
            return J2eeModule.CONN;
        }
        return null;
    }

    public String getModuleVersion() {
//        System.out.println("getModuleVersion()");
        WebModuleImpl wm = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
        if  (wm != null && wm.isValid()) {
            return wm.getJ2eePlatformVersion();
        }
        MavenEjbJarImpl ej = (MavenEjbJarImpl)project.getLookup().lookup(MavenEjbJarImpl.class);
        if (ej != null && ej.isValid()) {
            return ej.getJ2eePlatformVersion();
        }
        MavenEarImpl ea = (MavenEarImpl)project.getLookup().lookup(MavenEarImpl.class);
        if (ea != null && ea.isValid()) {
            return ea.getJ2eePlatformVersion();
        }
        // best fallback would be?
        return J2eeModule.J2EE_14;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }
    
    
    // inspired by netbeans' webmodule codebase, not really sure what is the point 
    // of the iterator..
    private static class ContentIterator implements Iterator {
        ArrayList ch;
        FileObject root;
        
        private ContentIterator (FileObject f) {
            this.ch = new ArrayList ();
            ch.add (f);
            this.root = f;
        }
        
        public boolean hasNext () {
            return ! ch.isEmpty();
        }
        
        public Object next () {
            FileObject f = (FileObject) ch.get(0);
            ch.remove(0);
            if (f.isFolder()) {
                f.refresh();
                FileObject chArr[] = f.getChildren ();
                for (int i = 0; i < chArr.length; i++) {
                    ch.add(chArr [i]);
                }
            }
            return new FSRootRE (root, f);
        }
        
        public void remove () {
            throw new UnsupportedOperationException ();
        }
        
    }

    private static final class FSRootRE implements J2eeModule.RootedEntry {
        FileObject f;
        FileObject root;
        
        FSRootRE (FileObject root, FileObject f) {
            this.f = f;
            this.root = root;
        }
        
        public FileObject getFileObject () {
            return f;
        }
        
        public String getRelativePath () {
            return FileUtil.getRelativePath (root, f);
        }
    }    
    
}
