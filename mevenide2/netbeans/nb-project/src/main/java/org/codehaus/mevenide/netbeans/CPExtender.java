/* ==========================================================================
 * Copyright 2006 Mevenide Team
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

package org.codehaus.mevenide.netbeans;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.indexer.MavenIndexSettings;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.writer.WriterUtils;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.api.project.ant.AntArtifact;
import org.netbeans.api.project.libraries.Library;
import org.netbeans.spi.java.project.classpath.ProjectClassPathExtender;
import org.netbeans.spi.java.project.classpath.ProjectClassPathModifierImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.Repository;
import org.openide.filesystems.URLMapper;
import org.openide.util.Exceptions;

/**
 * an implementation of ProjectClassPathModifierImplementation that tried to match 
 * maven dependencies to the way classpath items are added through this api.
 * @author mkleint@codehaus.org
 */
@SuppressWarnings("deprecation")
public class CPExtender extends ProjectClassPathModifierImplementation implements ProjectClassPathExtender {

    private NbMavenProject project;
    private static final String MD5_ATTR = "MD5"; //NOI18N
    private static final String POM_XML = "pom.xml"; //NOI18N
    
    /** Creates a new instance of CPExtender */
    public CPExtender(NbMavenProject project) {
        this.project = project;
    }
    
    public boolean addLibrary(Library library) throws IOException {
        FileObject pom = project.getProjectDirectory().getFileObject(POM_XML);//NOI18N
        Model model = WriterUtils.loadModel(pom);
        boolean added = addLibrary(library, model, null);
        if (added) {
            try {
                WriterUtils.writePomModel(pom, model);
                ProjectURLWatcher.fireMavenProjectReload(project);
                project.getLookup().lookup(ProjectURLWatcher.class).triggerDependencyDownload();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return added;
    }
    
    public boolean addArchiveFile(FileObject arch) throws IOException {
        FileObject file = FileUtil.getArchiveFile(arch);
        if (file.isFolder()) {
            throw new IOException("Cannot add folders to Maven projects as dependencies: " + file.getURL()); //NOI18N
        }
        FileObject fo = project.getProjectDirectory().getFileObject(POM_XML); //NOI18N
        Model model = WriterUtils.loadModel(fo);
        assert model != null;
        boolean added = addArchiveFile(file, model, null);
        if (added) {
            try {
                WriterUtils.writePomModel(fo, model);
                ProjectURLWatcher.fireMavenProjectReload(project);
                project.getLookup().lookup(ProjectURLWatcher.class).triggerDependencyDownload();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return added;
    }

    private boolean addLibrary(Library library, Model model, String scope) throws IOException {
        boolean added = false;
        try {
            added = checkLibraryForPoms(library, model, scope);
        } catch (IllegalArgumentException x) {
            //TODO ignore, remove the catch while maven-pom volume gets into j2se libraries..
            Exceptions.printStackTrace(x);
        }
        if (!added) {
            List<URL> urls = library.getContent("classpath"); //NOI18N
            added = urls.size() > 0;
            assert model != null;
            for (URL url : urls) {
                FileObject fo = URLMapper.findFileObject(FileUtil.getArchiveFile(url));
                assert fo != null;
                if (fo.isFolder()) {
                    throw new IOException("Cannot add folders to Maven projects as dependencies: " + fo.getURL()); //NOI18N
                }
                added = added && addArchiveFile(fo, model, scope);
            }
        }
        return added;
    }
    
    private boolean addArchiveFile(FileObject file, Model mdl, String scope) throws IOException {
        try {
            
            //toLowercase() seems to be required, not sure why..

            String checksum = RepositoryUtil.calculateChecksum(FileUtil.toFile(file)).toLowerCase();
            String[] dep = checkLayer(checksum);
            //TODO before searching the index, check the checksums of existing dependencies, should be faster..
            if (dep == null) {
                dep = checkLocalRepo(checksum);
            }
            //if not found anywhere, add to a custom file:// based repository structure within the project's directory.
            if (dep == null || "unknown.binary".equals(dep[0])) {  //NOI18N
                //also go this route when the artifact was found in local repo but is of unknown.binary groupId.
                dep = new String[3];
                dep[0] = "unknown.binary"; //NOI18N
                dep[1] = file.getName();
                dep[2] = "SNAPSHOT"; //NOI18N
                addJarToPrivateRepo(file, mdl, dep);
            }
            if (dep != null) {
                Dependency dependency = PluginPropertyUtils.checkModelDependency(mdl, dep[0], dep[1], true);
                dependency.setVersion(dep[2]);
                if (scope != null) {
                    dependency.setScope(scope);
                }
                return true;
            }
        }
 
        catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CPExtender.class.getName()).log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        }

        return false;
    }
    
    private String[] checkLocalRepo(String checksum) {
        List<NBVersionInfo> lst = RepositoryUtil.getDefaultRepositoryIndexer().
                findByMD5("local",checksum);
        for (NBVersionInfo elem : lst) {
            String[] dep = new String[3];
            dep[0] = elem.getGroupId();
            dep[1] = elem.getArtifactId();
            dep[2] = elem.getVersion();
            return dep;
        }
        return null;
    }
    
    private String[] checkLayer(String checksum) {
        FileObject root = Repository.getDefault().getDefaultFileSystem().findResource("Projects/org-codehaus-mevenide-netbeans/LibraryPOMs"); //NOI18N
        assert root != null;
        Enumeration<? extends FileObject> objs = root.getData(true);
        while (objs.hasMoreElements()) {
            FileObject fo = objs.nextElement();
            String md5 = (String)fo.getAttribute(MD5_ATTR);
            if (checksum.equals(md5)) {
                Model model = WriterUtils.loadModel(fo);
                String[] dep = new String[3];
                dep[0] = model.getGroupId();
                dep[1] = model.getArtifactId();
                dep[2] = model.getVersion();
                return dep;
            }
        }
        return null;
    }
    
    /**
     */ 
    private boolean checkLibraryForPoms(Library library, Model model, String scope) {
        if (!"j2se".equals(library.getType())) {
            //only j2se library supported for now..
            return false;
        }
        List<URL> poms = library.getContent("maven-pom"); //NOI18N
        boolean added = false;
        if (poms != null && poms.size() > 0) {
            for (URL pom : poms) {
                URL[] repos = MavenIndexSettings.getDefault().getCollectedReposAsURLs();
                String[] result = checkLibrary(pom, repos);
                if (result != null) {
                    added = true;
                    //set dependency
                    Dependency dep = PluginPropertyUtils.checkModelDependency(model, result[2], result[3], true);
                    dep.setVersion(result[4]);
                    if (scope != null) {
                        dep.setScope(scope);
                    }
                    //set repository
                    org.apache.maven.model.Repository reposit = PluginPropertyUtils.checkModelRepository(
                            project.getOriginalMavenProject(), model, result[1], true);
                    if (reposit != null) {
                        reposit.setId(library.getName());
                        reposit.setLayout(result[0]);
                        reposit.setName("Repository for library " + library);
                    }
                }
            }
        }
        return added;
    }
    
    static Pattern DEFAULT = Pattern.compile("(.+)[/]{1}(.+)[/]{1}(.+)[/]{1}(.+)\\.pom"); //NOI18N
    static Pattern LEGACY = Pattern.compile("(.+)[/]{1}poms[/]{1}([a-zA-Z\\-_]+)[\\-]{1}([0-9]{1}.+)\\.pom"); //NOI18N
    /**
     * @returns [0] type - default/legacy
     *          [1] repo root
     *          [2] groupId
     *          [3] artifactId
     *          [4] version
     */ 
    
    static String[] checkLibrary(URL pom, URL[] knownRepos) {
        String path = pom.getPath();
        Matcher match = LEGACY.matcher(path);
        boolean def = false;
        if (!match.matches()) {
            match = DEFAULT.matcher(path);
            def = true;
        }
        if (match.matches()) {
            String[] toRet = new String[5];
            toRet[0] = def ? "default" : "legacy"; //NOI18N
            toRet[1] = pom.getProtocol() + "://" + pom.getHost() + (pom.getPort() != -1 ? (":" + pom.getPort()) : ""); //NOI18N
            toRet[2] = match.group(1);
            toRet[3] = match.group(2);
            toRet[4] = match.group(3);
            for (URL repo : knownRepos) {
                String root = repo.getProtocol() + "://" + repo.getHost() + (repo.getPort() != -1 ? (":" + repo.getPort()) : ""); //NOI18N
                if (root.equals(toRet[1]) && toRet[2].startsWith(repo.getPath())) {
                    toRet[1] = toRet[1] + repo.getPath();
                    toRet[2] = toRet[2].substring(repo.getPath().length());
                    break;
                }
            }
            if (toRet[2].startsWith("/")) { //NOI18N
                toRet[2] = toRet[2].substring(1);
            }
            toRet[2] = toRet[2].replace('/', '.'); //NOI18N
            return toRet;
        }
        return null;
    }
    
    public boolean addAntArtifact(AntArtifact arg0, URI arg1) throws IOException {
        throw new IOException("Cannot add Ant based projects as subprojecs to Maven projects."); //NOI18N
    }
    
    public SourceGroup[] getExtensibleSourceGroups() {
        Sources s = this.project.getLookup().lookup(Sources.class);
        assert s != null;
        return s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
    }
    
    public String[] getExtensibleClassPathTypes(SourceGroup arg0) {
        return new String[] {
            ClassPath.COMPILE,
            ClassPath.EXECUTE
        };
    }
    
    public boolean addLibraries(Library[] libraries, SourceGroup grp, String type) throws IOException {
        FileObject pom = project.getProjectDirectory().getFileObject(POM_XML); //NOI18N
        Model model = WriterUtils.loadModel(pom);
        boolean added = libraries.length > 0;
        String scope = ClassPath.EXECUTE.equals(type) ? "runtime" : null; //NOI18N
        for (Library library : libraries) {
            added = added && addLibrary(library, model, scope);
        }
        if (added) {
            try {
                WriterUtils.writePomModel(pom, model);
                ProjectURLWatcher.fireMavenProjectReload(project);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return added;
    }
    
    public boolean removeLibraries(Library[] arg0, SourceGroup arg1,
                                      String arg2) throws IOException,
                                                          UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported in maven projects.");//NOI18N
    }
    
    public boolean addRoots(URL[] urls, SourceGroup grp, String type) throws IOException {
        FileObject pom = project.getProjectDirectory().getFileObject(POM_XML);//NOI18N
        Model model = WriterUtils.loadModel(pom);
        boolean added = urls.length > 0;
        String scope = ClassPath.EXECUTE.equals(type) ? "runtime" : null;//NOI18N
        for (URL url : urls) {
            FileObject fo  = URLMapper.findFileObject(FileUtil.getArchiveFile(url));
            assert fo != null;
            added = added && addArchiveFile(fo, model, scope);
        }
        if (added) {
            try {
                WriterUtils.writePomModel(pom, model);
                ProjectURLWatcher.fireMavenProjectReload(project);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return added;
    }
    
    public boolean removeRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                    UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");//NOI18N
    }
    
    public boolean addAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                      SourceGroup arg2, String arg3) throws IOException {
        throw new IOException("Cannot add Ant based projects as subprojecs to Maven projects.");//NOI18N
    }
    
    public boolean removeAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                         SourceGroup arg2, String arg3) throws IOException {
        return false;
    }
    
    private void addJarToPrivateRepo(FileObject file, Model mdl, String[] dep) throws IOException {
        //first add the local repo to
        Iterator repos = mdl.getRepositories().iterator();
        boolean found = false;
        String path = null;
        while (repos.hasNext()) {
            org.apache.maven.model.Repository repo = (org.apache.maven.model.Repository)repos.next();
            if ("unknown-jars-temp-repo".equals(repo.getId())) { //NOI18N
                found = true;
                String url = repo.getUrl();
                if (url.startsWith("file:${project.basedir}/")) { //NOI18N
                    path = url.substring("file:${project.basedir}/".length()); //NOI18N
                } else {
                    path = "lib"; //NOI18N
                }
                break;
            }
        }
        if (!found) {
            org.apache.maven.model.Repository repo = new org.apache.maven.model.Repository();
            repo.setId("unknown-jars-temp-repo"); //NOI18N
            repo.setName("A temporary repository created by NetBeans for libraries and jars it could not identify. Please replace the dependencies in this repository with correct ones and delete this repository."); //NOI18N
            repo.setUrl("file:${project.basedir}/lib"); //NOI18N
            mdl.addRepository(repo);
            path = "lib"; //NOI18N
        }
        assert path != null;
        FileObject root = FileUtil.createFolder(project.getProjectDirectory(), path);
        FileObject grp = FileUtil.createFolder(root, dep[0].replace('.', '/')); //NOI18N
        FileObject art = FileUtil.createFolder(grp, dep[1]);
        FileObject ver = FileUtil.createFolder(art, dep[2]);
        FileUtil.copyFile(file, ver, dep[1] + "-" + dep[2], "jar"); //NOI18N
    }
    
    
    
}
