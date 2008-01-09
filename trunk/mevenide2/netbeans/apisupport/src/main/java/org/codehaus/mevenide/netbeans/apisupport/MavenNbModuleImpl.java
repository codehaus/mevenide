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

package org.codehaus.mevenide.netbeans.apisupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.VersionInfo;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.modules.apisupport.project.spi.NbModuleProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.SpecificationVersion;

/**
 *
 * @author mkleint
 */
public class MavenNbModuleImpl implements NbModuleProvider {
    private Project project;
    
    /**
     * the property defined by nbm-maven-plugin's run-ide goal.
     * can help finding the defined netbeans platform.
     */ 
    public static final String PROP_NETBEANS_INSTALL = "netbeans.installation"; //NOI18N
    
    /** Creates a new instance of MavenNbModuleImpl 
     * @param project 
     */
    public MavenNbModuleImpl(Project project) {
        this.project = project;
    }
    
    private File getModuleXmlLocation() {
        String file = PluginPropertyUtils.getPluginProperty(project, 
                "org.codehaus.mojo", //NOI18N
                "nbm-maven-plugin", //NOI18N
                "descriptor", null); //NOI18N
        if (file == null) {
            file = "src/main/nbm/module.xml"; //NOI18N
        }
        File rel = new File(file);
        if (!rel.isAbsolute()) {
            rel = new File(FileUtil.toFile(project.getProjectDirectory()), file);
        }
        return FileUtil.normalizeFile(rel);
    }
    
    private Xpp3Dom getModuleDom() throws UnsupportedEncodingException, IOException, XmlPullParserException {
        //TODO convert to FileOBject and have the IO stream from there..
        if (!getModuleXmlLocation().exists()) {
            return null;
        }
        FileInputStream is = new FileInputStream(getModuleXmlLocation());
        Reader reader = new InputStreamReader(is, "UTF-8"); //NOI18N
        try {
            return Xpp3DomBuilder.build(reader);
        } finally {
            IOUtil.close(reader);
        }
    }
    
    public String getSpecVersion() {
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        String specVersion = AdaptNbVersion.adaptVersion(watch.getMavenProject().getVersion(), AdaptNbVersion.TYPE_SPECIFICATION);
        return specVersion;
    }

    public String getCodeNameBase() {
        try {
            Xpp3Dom dom = getModuleDom();
            if (dom != null) {
                Xpp3Dom cnb = dom.getChild("codeNameBase"); //NOI18N
                if (cnb != null) {
                    String val = cnb.getValue();
                    if (val.indexOf( "/") > -1) { //NOI18N
                        val = val.substring(0, val.indexOf("/")); //NOI18N
                    }
                    return val;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MavenProject prj = project.getLookup().lookup(ProjectURLWatcher.class).getMavenProject();
        //same fallback is in nbm-maven-plugin
        return prj.getGroupId() + "." + prj.getArtifact(); //NOI18N
    }

    public String getSourceDirectoryPath() {
        //TODO
        return "src/main/java"; //NOI18N
    }

    public FileObject getSourceDirectory() {
        FileObject fo = project.getProjectDirectory().getFileObject(getSourceDirectoryPath());
        if (fo == null) {
            try {
                fo = FileUtil.createFolder(project.getProjectDirectory(),
                                           getSourceDirectoryPath());
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return fo;
    }

    public FileObject getManifestFile() {
        String path = "src/main/nbm/manifest.mf";  //NOI18N

        try {
            Xpp3Dom dom = getModuleDom();
            if (dom != null) {
                Xpp3Dom cnb = dom.getChild("manifest"); //NOI18N
                if (cnb != null) {
                    path = cnb.getValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return project.getProjectDirectory().getFileObject(path);
    }

    public String getResourceDirectoryPath(boolean isTest) {
        if (isTest) {
            return "src/test/resources"; //NOI18N
        }
        return "src/main/resources"; //NOI18N
    }

    public boolean addDependency(String codeNameBase, String releaseVersion,
                                 SpecificationVersion version,
                                 boolean useInCompiler) throws IOException {
        String artifactId = codeNameBase.replaceAll("\\.", "-"); //NOI18N
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        Set set = watch.getMavenProject().getDependencyArtifacts();
        if (set != null) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Artifact art = (Artifact)it.next();
                if (art.getGroupId().startsWith("org.netbeans") && art.getArtifactId().equals(artifactId)) { //NOI18N
                    //TODO
                    //not sure we ought to check for spec or release version.
                    // just ignore for now, not any easy way to upgrade anyway I guess.
                    return false;
                }
            }
        }
        Dependency dep = null;
        File platformFile = lookForModuleInPlatform(artifactId);
        if (platformFile != null) {
            try {
                List<VersionInfo> lst = CustomQueries.findByMD5(platformFile);
                for (VersionInfo elem : lst) {
                    dep = new Dependency();
                    dep.setArtifactId(elem.getArtifactId());
                    dep.setGroupId(elem.getGroupId());
                    dep.setVersion(elem.getVersion());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dep == null) {
            //TODO try to guess 
            dep = new Dependency();
            dep.setGroupId("org.netbeans.api"); //NOI18N
            dep.setArtifactId(artifactId);
            if (version != null) {
                dep.setVersion(version.toString());
            } else {
                //try guessing the version according to the rest of netbeans dependencies..
                List deps = watch.getMavenProject().getModel().getDependencies();
                if (deps != null) {
                    Iterator it = deps.iterator();
                    while (it.hasNext()) {
                        Dependency d = (Dependency)it.next();
                        if ("org.netbeans.api".equals(d.getGroupId())) { //NOI18N
                            dep.setVersion(d.getVersion());
                        }
                    }
                }
            }
            if (dep.getVersion() == null) {
                dep.setVersion("SNAPSHOT"); //NOI18N
            }
        }
        
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml"); //NOI18N
        Model model = WriterUtils.loadModel(fo); //NOI18N
        if (model != null) {
            Dependency mdlDep = PluginPropertyUtils.checkModelDependency(model, dep.getGroupId(), dep.getArtifactId(), true);
            mdlDep.setVersion(dep.getVersion());
            try {
                WriterUtils.writePomModel(fo, model);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else { //NOPMD
            //TODO warn somehow?
            //the pom file is probably being edited..
        } 

        return true;
    }

    public NbModuleType getModuleType() {
        return NbModuleProvider.STANDALONE;
    }


    public String getProjectFilePath() {
        return "pom.xml"; //NOI18N
    }

    /**
     * get specification version for the given module.
     * The module isn't necessary a project dependency, more a property of the associated 
     * netbeans platform.
     */ 
    public SpecificationVersion getDependencyVersion(String codenamebase) throws IOException {
        String artifactId = codenamebase.replaceAll("\\.", "-"); //NOI18N
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        Set set = watch.getMavenProject().getDependencyArtifacts();
        if (set != null) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Artifact art = (Artifact)it.next();
                if (art.getGroupId().startsWith("org.netbeans") && art.getArtifactId().equals(artifactId)) { //NOI18N
                    ExamineManifest exa = new ExamineManifest();
                    exa.setJarFile(art.getFile());
                    if (exa.getSpecVersion() != null) {
                        return new SpecificationVersion(exa.getSpecVersion());
                    }
                }
            }
        }
        File fil = lookForModuleInPlatform(artifactId);
        if (fil != null) {
            ExamineManifest exa = new ExamineManifest();
            exa.setJarFile(fil);
            if (exa.getSpecVersion() != null) {
                return new SpecificationVersion(exa.getSpecVersion());
            }
        }
        //TODO search local repository?? that's probably irrelevant here..
        
        //we're completely clueless.
        return new SpecificationVersion("1.0"); //NOI18N
    }
    
    private File lookForModuleInPlatform(String artifactId) {
        File actPlatform = getActivePlatformLocation();
        if (actPlatform != null) {
            DirectoryScanner walk = new DirectoryScanner();
            walk.setBasedir(actPlatform);
            walk.setIncludes(new String[] {
                "**/" + artifactId + ".jar" //NOI18N
            });
            walk.scan();
            String[] candidates = walk.getIncludedFiles();
            assert candidates != null && candidates.length <= 1;
            if (candidates.length > 0) {
                return new File(actPlatform, candidates[0]);
            }
        }
        return null;
    }

    public File getActivePlatformLocation() {
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        String installProp = watch.getMavenProject().getProperties().getProperty(PROP_NETBEANS_INSTALL);
        if (installProp != null) {
            File fil = new File(installProp);
            if (fil.exists()) {
                return fil;
            }
        }
        return null;
    }

}
