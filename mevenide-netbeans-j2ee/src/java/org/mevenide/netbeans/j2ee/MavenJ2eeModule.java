/*
 * MavenJ2eeModule.java
 *
 * Created on July 13, 2005, 8:47 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.mevenide.netbeans.j2ee;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.mevenide.netbeans.j2ee.web.WebModuleImpl;
import org.mevenide.netbeans.project.FileUtilities;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.modules.j2ee.dd.api.web.DDProvider;
import org.netbeans.modules.j2ee.dd.api.web.WebApp;
import org.netbeans.modules.j2ee.deployment.devmodules.api.J2eeModule;
import org.netbeans.modules.schema2beans.BaseBean;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Administrator
 */
public class MavenJ2eeModule implements J2eeModule {
    private MavenProject project;
    /** Creates a new instance of MavenJ2eeModule */
    public MavenJ2eeModule(MavenProject proj) {
        project = proj;
        url = project.getOriginalMavenProject().getArtifactId();
    }

    public void addVersionListener(J2eeModule.VersionListener listener) {
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
        System.out.println("archive returns NULL!!!!");
        //TODO ears/ejbs/rars
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
//            System.out.println(" getContentDirectory() returning=" + FileUtilities.getFileObjectForProperty("maven.war.webapp.dir", project.getPropertyResolver()));
            return FileUtilities.getFileObjectForProperty("maven.war.webapp.dir", project.getPropertyResolver());        
        }
        System.out.println("getCONTENTDIR is null");
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
                System.out.println("getDD returning..");
                //TODO find a better way to get the BB from WApp and remove the HACK from DDProvider!!
                return DDProvider.getDefault ().getBaseBean (webApp);
            }
        }
//        else if(J2eeModule.WEBSERVICES_XML.equals(location)){
//            Webservices webServices = getWebservices();
//            if(webServices != null){
//                return org.netbeans.modules.j2ee.dd.api.webservices.DDProvider.getDefault().getBaseBean(webServices);
//            }
//        }
        System.out.println("  NUL!!!!!! for " + location);
        return null;        
    }
    
   private WebApp getWebApp () {
        try {
            WebModuleImpl impl = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
            FileObject deploymentDescriptor = impl.getDeploymentDescriptor();
            if(deploymentDescriptor != null) {
                return DDProvider.getDefault ().getDDRoot (deploymentDescriptor);
            }
        } catch (java.io.IOException e) {
            org.openide.ErrorManager.getDefault ().log (e.getLocalizedMessage ());
        }
        return null;
    }    

    public Object getModuleType() {
//        System.out.println("getModuleType()");
        WebModuleImpl wm = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
        if  (wm != null && wm.isValid()) {
//            System.out.println("   is war");
            return J2eeModule.WAR;
        }
        MavenEjbJarImpl ejb = (MavenEjbJarImpl)project.getLookup().lookup(MavenEjbJarImpl.class);
        if (ejb != null && ejb.isValid()) {
            System.out.println("   is ejb");
            return J2eeModule.EJB;
        }
        MavenEarImpl ear = (MavenEarImpl)project.getLookup().lookup(MavenEarImpl.class);
        if (ear != null && ear.isValid()) {
            System.out.println("   is ear");
            return J2eeModule.EAR;
        }
        File connStr = FileUtilities.getFileForProperty("maven.rar.raxml", project.getPropertyResolver());
        if (connStr != null && connStr.exists()) {
            System.out.println("   is rar");
            return J2eeModule.CONN;
        }
        System.out.println("  ... is nothing");
        return null;
    }

    public String getModuleVersion() {
        System.out.println("getModuleVersion()");
        WebModuleImpl wm = (WebModuleImpl)project.getLookup().lookup(WebModuleImpl.class);
        if  (wm != null && wm.isValid()) {
            System.out.println("  for webapp=" + wm.getJ2eePlatformVersion());
            return wm.getJ2eePlatformVersion();
        }
        return "";
    }

    public String getUrl() {
        System.out.println("MavenJ2eeModule=get url=" + url);
        return url;
    }

    public void removeVersionListener(J2eeModule.VersionListener listener) {
    }

    private String url;
    public void setUrl(String url) {
        System.out.println("setting url");
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
