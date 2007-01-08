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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.j2ee.J2eeMavenSourcesImpl;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.ProjectUtils;
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
    
    private String url = "";

    private boolean inplace = false;
    
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
        Sources srcs = ProjectUtils.getSources(project);
        SourceGroup[] grp = srcs.getSourceGroups(J2eeMavenSourcesImpl.TYPE_DOC_ROOT);
        if (grp.length > 0) {
            return grp[0].getRootFolder();
        }
//        System.out.println("NO DOCUMENT BASE!!!");
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
        String toRet =  "/" + project.getOriginalMavenProject().getBuild().getFinalName();
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
        return getJ2eePlatformVersion();
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
        File fil = FileUtil.normalizeFile(new File(loc, finalName + ".war"));
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
    
    public BaseBean getDeploymentDescriptor(String string) {
//        System.out.println("get DD");
        if (J2eeModule.WEB_XML.equals(string)) {
            try {
                FileObject content = getContentDirectory();
                if (content == null) {
                    content = getDocumentBase();
                }
                FileObject deploymentDescriptor = content.getFileObject(J2eeModule.WEB_XML);
                if(deploymentDescriptor != null) {
                    WebApp web = DDProvider.getDefault().getDDRoot(deploymentDescriptor);
                    if (web != null) {
                        return DDProvider.getDefault().getBaseBean(web);
                    }
                }
            } catch (IOException e) {
                ErrorManager.getDefault().log(e.getLocalizedMessage());
            }
        }
        System.out.println("no dd for=" + string);
        return null;
    }
    
    public void addVersionListener(J2eeModule.VersionListener versionListener) {
        System.out.println("adding version listener");
    }
    
    public void removeVersionListener(J2eeModule.VersionListener versionListener) {
        System.out.println("removing version listener");
    }

    //55 only..
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
    
}
