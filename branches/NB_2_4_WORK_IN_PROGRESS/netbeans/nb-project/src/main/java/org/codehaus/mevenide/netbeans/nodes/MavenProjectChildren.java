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
package org.codehaus.mevenide.netbeans.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.MavenSourcesImpl;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.PluginPropertyUtils;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.spi.java.project.support.ui.PackageView;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.nodes.Children;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class MavenProjectChildren extends Children.Keys {
    
//    private static final String KEY_RESOURCES = "resources"; //NOI18N
//    private static final String KEY_TEST_RESOURCES = "testresources"; //NOI18N
    private static final String KEY_WEBAPP = "webapp"; //NOI18N
//    private static final String KEY_EAR = "ear"; //NOI18N
//    private static final String KEY_EJB = "ejb"; //NOI18N
    private static final String KEY_OTHER = "otherRoots"; //NOI18N
    private static final String KEY_OTHER_TEST = "otherTestRoots"; //NOI18N
    private static final String KEY_DEPENDENCIES = "dependencies"; //NOI18N
    private static final String KEY_TEST_DEPENDENCIES = "dependencies2"; //NOI18N
    private static final String KEY_RUNTIME_DEPENDENCIES = "dependencies3"; //NOI18N
    private static final String KEY_PROJECT_FILES = "projectfiles"; //NOI18N
    private static final String KEY_MODULES = "modules"; //NOI18N
    private static final String KEY_SITE = "SITE"; //NOI18N
    
    
    private NbMavenProject project;
    private PropertyChangeListener changeListener;
    private String currentWebAppKey;

//    private String currentEarKey;
//    private String currentEjbKey;
    
    public MavenProjectChildren(NbMavenProject proj) {
        project = proj;
        changeListener  = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    regenerateKeys();
                    refresh();
                }
            }
        };
    }
    
    protected void addNotify() {
        super.addNotify();
        project.addPropertyChangeListener(changeListener);
        regenerateKeys();
    }
    
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
        project.removePropertyChangeListener(changeListener);
        super.removeNotify();
        
    }
    
    private void regenerateKeys() {
        List list = new ArrayList();
        Sources srcs = (Sources)project.getLookup().lookup(Sources.class);
        if (srcs == null) {
            throw new IllegalStateException("need Sources instance in lookup");
        }
        SourceGroup[] javagroup = srcs.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
        for (int i = 0; i < javagroup.length; i++) {
            list.add(javagroup[i]);
        }
        SourceGroup[] gengroup = srcs.getSourceGroups(MavenSourcesImpl.TYPE_GEN_SOURCES);
        for (int i = 0; i < gengroup.length; i++) {
            list.add(gengroup[i]);
        }
        URI webapp = project.getWebAppDirectory();
        if (webapp != null) {
            currentWebAppKey = KEY_WEBAPP + webapp;
            list.add(currentWebAppKey);
        }
        if (project.getOtherRoots(false).length > 0) {
            list.add(KEY_OTHER);
        }
        if (project.getOtherRoots(true).length > 0) {
            list.add(KEY_OTHER_TEST);
        }

        //TODO one should check if someone overrides the path to the site directory,
        // no real way doing it now.. just ignore..
//        String siteDir = PluginPropertyUtils.getPluginProperty(project,
//                "org.apache.maven.plugins",
//                "maven-site-plugin",
//                "siteDirectory",
//                "site");
//        if (siteDir == null) {
//            siteDir = "src/site";
//        }
        if (project.getProjectDirectory().getFileObject("src/site") != null) {
            list.add(KEY_SITE);
        }
        list.add(KEY_DEPENDENCIES);
        MavenProject orig = project.getOriginalMavenProject();
        List runtimes = new ArrayList(orig.getRuntimeArtifacts());
        runtimes.removeAll(orig.getCompileArtifacts());
        if (runtimes.size() > 0) {
            list.add(KEY_RUNTIME_DEPENDENCIES);
        }
        List tests = new ArrayList(orig.getTestArtifacts());
        tests.removeAll(orig.getRuntimeArtifacts());
        if (tests.size() > 0) {
            list.add(KEY_TEST_DEPENDENCIES);
        }
        if ("pom".equals(project.getOriginalMavenProject().getPackaging())) {
            list.add(KEY_MODULES);
        }
        list.add(KEY_PROJECT_FILES);
        setKeys(list);
    }
    
    
    protected Node[] createNodes(Object key) {
        Node n = null;
        if (key instanceof SourceGroup) {
            SourceGroup grp = (SourceGroup)key;
                n = PackageView.createPackageView(grp);
        } else if (key == currentWebAppKey) {
            n = createWebAppNode();
        } else if (key == KEY_OTHER) {
            n = new OthersRootNode(project, false);
        } else if (key == KEY_OTHER_TEST) {
            n = new OthersRootNode(project, true);
        } else if (key == KEY_DEPENDENCIES) {
            n = new DependenciesNode(project, DependenciesNode.TYPE_COMPILE);
        } else if (key == KEY_TEST_DEPENDENCIES) {
            n = new DependenciesNode(project, DependenciesNode.TYPE_TEST);
        } else if (key == KEY_RUNTIME_DEPENDENCIES) {
            n = new DependenciesNode(project, DependenciesNode.TYPE_RUNTIME);
        } else if (key == KEY_PROJECT_FILES) {
            n = new ProjectFilesNode(project);
        } else if (key == KEY_MODULES) {
            n = new ModulesNode(project);
        } else if (key == KEY_SITE) {
            n = createSiteDocsNode();
        }
        return n == null ? new Node[0] : new Node[] {n};
    }
    
//    private DataFolder getFolder(String relPath) {
//        FileObject folder = FileUtilities.findFolder(project.getProjectDirectory(), relPath);
//        if (folder != null) {
//            return DataFolder.findFolder(folder);
//        }
//        //TODO - create the folder if it doesn't exist? and do it here? I'd rather do it when opening project..
//        return null;
//    }
//    
//    private DataFolder getFolder(URI path) {
//        try {
//            FileObject folder = URLMapper.findFileObject(path.toURL());
//            if (folder != null) {
//                return DataFolder.findFolder(folder);
//            }
//        } catch (MalformedURLException exc) {
//            logger.warn("malformed URI=" + path, exc);
//        }
//        //TODO - create the folder if it doesn't exist? and do it here? I'd rather do it when opening project..
//        return null;
//    }
//
    private Node createWebAppNode() {
        Node n =  null;
        try {
            FileObject fo = URLMapper.findFileObject(project.getWebAppDirectory().toURL());
            if (fo != null) {
                DataFolder fold = DataFolder.findFolder(fo);
                File fil = FileUtil.toFile(fo);
                if (fold != null) {
                    n = new WebAppFilterNode(project, fold.getNodeDelegate().cloneNode(), fil);
                }
            }
        } catch (MalformedURLException exc) {
            n = null;
        }
        return n;
    }
//    
//    private Node createEarNode() {
//        Node n =  null;
//        try {
//            FileObject fo = URLMapper.findFileObject(project.getEarDirectory().toURL());
//            if (fo != null) {
//                DataFolder fold = DataFolder.findFolder(fo);
//                File fil = FileUtil.toFile(fo);
//                if (fold != null) {
//                    n = new EarFilterNode(project, fold.getNodeDelegate().cloneNode(), fil);
//                }
//            }
//        } catch (MalformedURLException exc) {
//            logger.debug("malformed ear rootfile url", exc);
//            n = null;
//        }
//        return n;
//    }
//    
//    private Node createEjbNode() {
//        Node n =  null;
//        try {
//            FileObject fo = URLMapper.findFileObject(project.getEjbDirectory().toURL());
//            if (fo != null) {
//                DataFolder fold = DataFolder.findFolder(fo);
//                File fil = FileUtil.toFile(fo);
//                if (fold != null) {
//                    n = new EjbFilterNode(project, fold.getNodeDelegate().cloneNode(), fil);
//                }
//            }
//        } catch (MalformedURLException exc) {
//            logger.debug("malformed ejb rootfile url", exc);
//            n = null;
//        }
//        return n;
//    }    
// 

    private Node createSiteDocsNode() {
        Node n =  null;
        FileObject fo = project.getProjectDirectory().getFileObject("src/site");
        if (fo != null) {
            DataFolder fold = DataFolder.findFolder(fo);
            if (fold != null) {
                n = new SiteDocsNode(project, fold.getNodeDelegate().cloneNode());
            }
        }
        return n;
    }
    
    
}
