/*
 * MavenNbModuleImpl.java
 *
 * Created on February 18, 2007, 12:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.apisupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.jar.Manifest;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.archiva.digest.Md5Digester;
import org.apache.maven.archiva.indexer.lucene.LuceneQuery;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.indexer.CustomQueries;
import org.codehaus.mevenide.indexer.LocalRepositoryIndexer;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.DirectoryWalker;
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
                "org.codehaus.mojo", 
                "nbm-maven-plugin",
                "descriptor", null);
        return new File(FileUtil.toFile(project.getProjectDirectory()), file);
    }
    
    private Xpp3Dom getModuleDom() throws UnsupportedEncodingException, IOException, XmlPullParserException {
        //TODO convert to FileOBject and have the IO stream from there..
        FileInputStream is = new FileInputStream(getModuleXmlLocation());
        Reader reader = new InputStreamReader(is, "UTF-8");
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
            Xpp3Dom cnb = dom.getChild("codeNameBase");
            if (cnb != null) {
                System.out.println("cnb=" + cnb.getValue());
                String val = cnb.getValue();
                if (val.indexOf( "/") > -1) {
                    val = val.substring(0, val.indexOf("/"));
                }
                return val;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MavenProject prj = project.getLookup().lookup(ProjectURLWatcher.class).getMavenProject();
        //same fallback is in nbm-maven-plugin
        return prj.getGroupId() + "." + prj.getArtifact();
    }

    public String getSourceDirectoryPath() {
        //TODO
        return "src/main/java";
    }

    public FileObject getSourceDirectory() {
        FileObject fo = project.getProjectDirectory().getFileObject(getSourceDirectoryPath());
        if (fo == null) {
            try         {
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
        try {
            Xpp3Dom dom = getModuleDom();
            Xpp3Dom cnb = dom.getChild("manifest");
            if (cnb != null) {
                return project.getProjectDirectory().getFileObject(cnb.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getResourceDirectoryPath(boolean isTest) {
        if (isTest) {
            return "src/test/resources";
        }
        return "src/main/resources";
    }

    public boolean addDependency(String codeNameBase, String releaseVersion,
                                 SpecificationVersion version,
                                 boolean useInCompiler) throws IOException {
        String artifactId = codeNameBase.replaceAll("\\.", "-");
        System.out.println("jar name=" + artifactId);
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        Set set = watch.getMavenProject().getDependencyArtifacts();
        if (set != null) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Artifact art = (Artifact)it.next();
                if (art.getGroupId().startsWith("org.netbeans") && art.getArtifactId().equals(artifactId)) {
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
                Md5Digester digest = new Md5Digester();
                String md5 = digest.calc(platformFile);
                System.out.println("md5=" + md5);
                LocalRepositoryIndexer index = LocalRepositoryIndexer.getInstance();
                TermQuery tq  = new TermQuery( new Term(StandardIndexRecordFields.MD5, md5));
                LuceneQuery q = new LuceneQuery(tq);
                List<StandardArtifactIndexRecord> lst = index.searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
                for (StandardArtifactIndexRecord elem : lst) {
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
        
        return true;
    }

    public NbModuleType getModuleType() {
        return NbModuleProvider.STANDALONE;
    }


    public String getProjectFilePath() {
        return "pom.xml";
    }

    /**
     * get specification version for the given module.
     * The module isn't necessary a project dependency, more a property of the associated 
     * netbeans platform.
     */ 
    public SpecificationVersion getDependencyVersion(String codenamebase) throws IOException {
        String artifactId = codenamebase.replaceAll("\\.", "-");
        System.out.println("jar name=" + artifactId);
        ProjectURLWatcher watch = project.getLookup().lookup(ProjectURLWatcher.class);
        Set set = watch.getMavenProject().getDependencyArtifacts();
        if (set != null) {
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Artifact art = (Artifact)it.next();
                if (art.getGroupId().startsWith("org.netbeans") && art.getArtifactId().equals(artifactId)) {
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
        return new SpecificationVersion("1.0");
    }
    
    private File lookForModuleInPlatform(String artifactId) {
        File actPlatform = getActivePlatformLocation();
        if (actPlatform != null) {
            DirectoryScanner walk = new DirectoryScanner();
            walk.setBasedir(actPlatform);
            walk.setIncludes(new String[] {
                "**/" + artifactId + ".jar"
            });
            walk.scan();
            String[] candidates = walk.getIncludedFiles();
            assert candidates != null && candidates.length <= 1;
            if (candidates.length > 0) {
                System.out.println("found=" + candidates[0]);
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
