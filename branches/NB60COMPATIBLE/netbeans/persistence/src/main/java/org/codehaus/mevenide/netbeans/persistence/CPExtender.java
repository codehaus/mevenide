/* ==========================================================================
 * Copyright 2007 Mevenide Team
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

package org.codehaus.mevenide.netbeans.persistence;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.Repository;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.java.project.classpath.ProjectClassPathExtender;
import org.netbeans.spi.java.project.classpath.ProjectClassPathModifierImplementation;
import org.openide.filesystems.FileObject;

/**
 *
 * @author mkleint
 */
public class CPExtender extends ProjectClassPathModifierImplementation implements ProjectClassPathExtender {
    private static final String EL_CONFIG = "configuration"; //NOI18N
    private static final String EL_SOURCE = "source"; //NOI18N
    private static final String EL_TARGET = "target"; //NOI18N
    private static final String SL_15 = "1.5"; //NOI18N
    private NbMavenProject project;
    /** Creates a new instance of CPExtender */
    public CPExtender(NbMavenProject project) {
        this.project = project;
    }
    
    protected SourceGroup[] getExtensibleSourceGroups() {
        //the default one privides them.
        return new SourceGroup[0];
    }

    protected String[] getExtensibleClassPathTypes(SourceGroup arg0) {
        return new String[0];
    }

    protected boolean addLibraries(Library[] libs, SourceGroup arg1, String arg2) throws IOException,
                                                                                         UnsupportedOperationException {
        boolean added = false;
        for (Library l : libs) {
            added = added || addLibrary(l);
        }
        return added;
    }

    protected boolean removeLibraries(Library[] arg0, SourceGroup arg1,
                                      String arg2) throws IOException,
                                                          UnsupportedOperationException {
        return false;
    }

    protected boolean addRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                 UnsupportedOperationException {
        return false;
    }

    protected boolean removeRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                    UnsupportedOperationException {
        return false;
    }

    protected boolean addAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                      SourceGroup arg2, String arg3) throws IOException,
                                                                            UnsupportedOperationException {
        return false;
    }

    protected boolean removeAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                         SourceGroup arg2, String arg3) throws IOException,
                                                                               UnsupportedOperationException {
        return false;
    }

    public boolean addLibrary(Library library) throws IOException {
        if ("toplink".equals(library.getName())) { //NOI18N
            //TODO would be nice if the toplink lib shipping with netbeans be the same binary
            // then we could just copy the pieces to local repo.
            FileObject pom = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
            Model mdl = WriterUtils.loadModel(pom);
            MavenProject mp = project.getOriginalMavenProject();
//not necessary any more. toplink will be handled by default library impl..            
//            Dependency dep = new Dependency();
//            dep.setGroupId("toplink.essentials"); //NOI18N
//            dep.setArtifactId("toplink-essentials"); //NOI18N
//            dep.setVersion("2.0-36"); //NOI18N
//            if (!containsDependency(dep, mp)) {
//                mdl.addDependency(dep);
//            }
//            Repository repo = new Repository();
//            repo.setId("m1-java.net"); //NOI18N
//            repo.setUrl("http://download.java.net/maven/1/"); //NOI18N
//            repo.setLayout("legacy"); //NOI18N
//            if (!containsRepository(repo, mp)) {
//                mdl.addRepository(repo);
//            }
            // checking source doesn't work anymore, the wizard requires the level to be 1.5 up front.
            checkSourceLevel(mdl);
            WriterUtils.writePomModel(pom, mdl);
            //TODO have some kind of trigger for download if not locally present?

            //shall not return true, needs processing by the fallback impl as well.
            return false;
        }
        return false;
    }
    
    public boolean addArchiveFile(FileObject arg0) throws IOException {
        return false;
    }
    
    public boolean addAntArtifact(AntArtifact arg0, URI arg1) throws IOException {
        return false;
    }
    
    private boolean containsDependency(Dependency dep, MavenProject mp) {
        List lst = mp.getDependencies();
        if (lst != null) {
            Iterator it = lst.iterator();
            while (it.hasNext()) {
                Dependency dd = (Dependency)it.next();
                if (dd.getManagementKey().equals(dep.getManagementKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean containsRepository(Repository rep, MavenProject mp) {
        List lst = mp.getRepositories();
        if (lst != null) {
            Iterator it = lst.iterator();
            while (it.hasNext()) {
                Repository rr = (Repository)it.next();
                if (rr.getUrl().startsWith(rep.getUrl())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void checkSourceLevel(Model mdl) {
        String source = PluginPropertyUtils.getPluginProperty(project,
                Constants.GROUP_APACHE_PLUGINS, Constants.PLUGIN_COMPILER, EL_SOURCE, 
                "compile"); //NOI18N
        if (source != null && source.contains(SL_15)) {
            return;
        }
        Plugin plugin = new Plugin();
        plugin.setGroupId(Constants.GROUP_APACHE_PLUGINS);
        plugin.setArtifactId(Constants.PLUGIN_COMPILER);
        plugin.setVersion("RELEASE"); //NOI18N
        Plugin old = null;
        Build bld = mdl.getBuild();
        if (bld != null) {
            old = (Plugin) bld.getPluginsAsMap().get(plugin.getKey());
        } else {
            mdl.setBuild(new Build());
        }
        if (old != null) {
            plugin = old;
        } else {
            mdl.getBuild().addPlugin(plugin);
        }
        Xpp3Dom dom = (Xpp3Dom) plugin.getConfiguration();
        if (dom == null) {
            dom = new Xpp3Dom(EL_CONFIG);
            plugin.setConfiguration(dom);
        }
        Xpp3Dom dom2 = dom.getChild(EL_SOURCE);
        if (dom2 == null) {
            dom2 = new Xpp3Dom(EL_SOURCE);
            dom.addChild(dom2);
        }
        dom2.setValue(SL_15);
        
        dom2 = dom.getChild(EL_TARGET);
        if (dom2 == null) {
            dom2 = new Xpp3Dom(EL_TARGET);
            dom.addChild(dom2);
        }
        dom2.setValue(SL_15);
    }

}
