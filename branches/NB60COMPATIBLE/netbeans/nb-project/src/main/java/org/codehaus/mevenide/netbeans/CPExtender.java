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

package org.codehaus.mevenide.netbeans;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.archiva.digest.DigesterException;
import org.apache.maven.archiva.digest.Md5Digester;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.lucene.LuceneQuery;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
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
import org.openide.filesystems.URLMapper;

/**
 * an implementation of ProjectClassPathModifierImplementation that tried to match 
 * maven dependencies to the way classpath items are added through this api.
 * @author mkleint
 */
public class CPExtender extends ProjectClassPathModifierImplementation implements ProjectClassPathExtender {

    private NbMavenProject project;
    
    /** Creates a new instance of CPExtender */
    public CPExtender(NbMavenProject project) {
        this.project = project;
    }
    
    public boolean addLibrary(Library library) throws IOException {
        FileObject pom = project.getProjectDirectory().getFileObject("pom.xml");
        Model model = WriterUtils.loadModel(pom);
        boolean added = addLibrary(library, model, null);
        try {
            WriterUtils.writePomModel(pom, model);
            project.firePropertyChange(NbMavenProject.PROP_PROJECT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return added;
    }
    
    
    public boolean addArchiveFile(FileObject arch) throws IOException {
        FileObject file = FileUtil.getArchiveFile(arch);
        if (file.isFolder()) {
            throw new IOException("Cannot add folders to Maven projects as dependencies: " + file.getURL());
        }
        FileObject fo = project.getProjectDirectory().getFileObject("pom.xml");
        Model model = WriterUtils.loadModel(fo);
        assert model != null;
        boolean added = addArchiveFile(file, model, null);
        try {
            WriterUtils.writePomModel(fo, model);
            project.firePropertyChange(NbMavenProject.PROP_PROJECT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return added;
    }
    
    private boolean addLibrary(Library library, Model model, String scope) throws IOException {
        List<URL> urls = library.getContent("classpath");
        boolean added = urls.size() > 0;
        assert model != null;
        for (URL url : urls) {
            FileObject fo = URLMapper.findFileObject(FileUtil.getArchiveFile(url));
            assert fo != null;
            if (fo.isFolder()) {
                throw new IOException("Cannot add folders to Maven projects as dependencies: " + fo.getURL());
            }
            added = added && addArchiveFile(fo, model, scope);
        }
        return added;
    }
    
    private boolean addArchiveFile(FileObject file, Model mdl, String scope) {
        //TODO have some other means of figuring out the id..
        // have a special register of id+md5 pairs for default libraries in nb
        // eventually have the local repo populate from the netbeans install?
        try {
            Md5Digester digest = new Md5Digester();
            String checksum = digest.calc(FileUtil.toFile(file));
            //TODO before searching the index, check the checksums of existing dependencies, should be faster..
            
            LocalRepositoryIndexer index = LocalRepositoryIndexer.getInstance();
            //toLowercase() seems to be required, not sure why..
            TermQuery tq  = new TermQuery( new Term(StandardIndexRecordFields.MD5, checksum.toLowerCase()));
            LuceneQuery q = new LuceneQuery(tq);
            List lst = index.searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
            Iterator it = lst.iterator();
            if (it.hasNext()) {
                StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
                System.out.println("elem=" + elem.getPrimaryKey());
                List<Dependency> deps = mdl.getDependencies();
                Dependency dep = new Dependency();
                dep.setArtifactId(elem.getArtifactId());
                dep.setGroupId(elem.getGroupId());
                dep.setVersion(elem.getVersion());
                dep.setType("jar");
                if (scope != null) {
                    dep.setScope(scope);
                }
                for (Dependency exist : deps) {
                    if (dep.getManagementKey().equals(exist.getManagementKey())) {
                        return true;
                    }
                }
                mdl.addDependency(dep);
                return true;
            }
        }
        catch (RepositoryIndexSearchException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        }
        catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        }
        catch (DigesterException ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        }
        return false;
    }
    
    public boolean addAntArtifact(AntArtifact arg0, URI arg1) throws IOException {
        throw new IOException("Cannot add Ant based projects as subprojecs to Maven projects.");
    }
    
    protected SourceGroup[] getExtensibleSourceGroups() {
        Sources s = (Sources) this.project.getLookup().lookup(Sources.class);
        assert s != null;
        return s.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA);
    }
    
    protected String[] getExtensibleClassPathTypes(SourceGroup arg0) {
        return new String[] {
            ClassPath.COMPILE,
            ClassPath.EXECUTE
        };
    }
    
    protected boolean addLibraries(Library[] libraries, SourceGroup grp, String type) throws IOException {
        FileObject pom = project.getProjectDirectory().getFileObject("pom.xml");
        Model model = WriterUtils.loadModel(pom);
        boolean added = libraries.length > 0;
        String scope = type == ClassPath.EXECUTE ? "runtime" : null;
        for (Library library : libraries) {
            added = added && addLibrary(library, model, scope);
        }
        try {
            WriterUtils.writePomModel(pom, model);
            project.firePropertyChange(NbMavenProject.PROP_PROJECT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return added;
    }
    
    protected boolean removeLibraries(Library[] arg0, SourceGroup arg1,
                                      String arg2) throws IOException,
                                                          UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected boolean addRoots(URL[] urls, SourceGroup grp, String type) throws IOException {
        FileObject pom = project.getProjectDirectory().getFileObject("pom.xml");
        Model model = WriterUtils.loadModel(pom);
        boolean added = urls.length > 0;
        String scope = type == ClassPath.EXECUTE ? "runtime" : null;
        for (URL url : urls) {
            FileObject fo  = URLMapper.findFileObject(FileUtil.getArchiveFile(url));
            assert fo != null;
            added = added && addArchiveFile(fo, model, scope);
        }
        try {
            WriterUtils.writePomModel(pom, model);
            project.firePropertyChange(NbMavenProject.PROP_PROJECT);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return added;
    }
    
    protected boolean removeRoots(URL[] arg0, SourceGroup arg1, String arg2) throws IOException,
                                                                                    UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    protected boolean addAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                      SourceGroup arg2, String arg3) throws IOException {
        throw new IOException("Cannot add Ant based projects as subprojecs to Maven projects.");
    }
    
    protected boolean removeAntArtifacts(AntArtifact[] arg0, URI[] arg1,
                                         SourceGroup arg2, String arg3) throws IOException {
        return false;
    }
    
}
