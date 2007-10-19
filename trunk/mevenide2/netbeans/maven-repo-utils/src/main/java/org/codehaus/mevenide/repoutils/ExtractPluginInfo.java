package org.codehaus.mevenide.repoutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.archiva.indexer.RepositoryArtifactIndex;
import org.apache.maven.archiva.indexer.RepositoryArtifactIndexFactory;
import org.apache.maven.archiva.indexer.lucene.LuceneQuery;
import org.apache.maven.archiva.indexer.record.RepositoryIndexRecordFactory;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.IOUtil;

/**
 * Extract Plugin information from repository + repository index.
 *
 */
public class ExtractPluginInfo 
{
    /**
     * @param args
     * 0 - repository index directory
     * 1 - repository root directory
     * 2 - results directory
     */ 
    public static void main( String[] args )
    {
        
        try {
            Embedder embedder;
            RepositoryArtifactIndexFactory indexFactory;
            RepositoryArtifactIndex defaultIndex;
            RepositoryIndexRecordFactory recordFactory;
            embedder = new Embedder();
            embedder.start();
            indexFactory = (RepositoryArtifactIndexFactory) embedder.lookup(RepositoryArtifactIndexFactory.ROLE, "lucene");
            File basedir = new File(args[0]);
            if (!basedir.exists()) {
                throw new IllegalArgumentException(args[0] + " folder doesn't exist");
            }

            defaultIndex = indexFactory.createStandardIndex(basedir);
            recordFactory = (RepositoryIndexRecordFactory)embedder.lookup(RepositoryIndexRecordFactory.ROLE, "standard");
            
            File repodir = new File(args[1]);
            if (!repodir.exists()) {
                throw new IllegalArgumentException(args[1] + " folder doesn't exist");
            }
            
            File results = new File(args[2]);
            if (results.exists()) {
                org.codehaus.plexus.util.FileUtils.deleteDirectory(results);
            }
            results.mkdirs();
            
            HashMap<File, String> release = new HashMap<File, String>();
            LuceneQuery q = new LuceneQuery(new TermQuery(new Term(StandardIndexRecordFields.TYPE, "maven-plugin")));
            List<StandardArtifactIndexRecord> lst = defaultIndex.search(q);
            for (StandardArtifactIndexRecord rec : lst)  {
                File path = new File(repodir, rec.getGroupId().replace('.', '/') + "/" + 
                                              rec.getArtifactId() + "/" +
                                              rec.getVersion() + "/" + rec.getArtifactId() + "-" + rec.getVersion() + ".jar");
                System.out.println("" + path.getAbsolutePath());
                if (path.exists()) {
                    JarFile jar = new JarFile(path);
                    ZipEntry entry = jar.getEntry("META-INF/maven/plugin.xml");
                    if (entry != null) {
                        File newFile = new File(results, rec.getGroupId().replace('.', '/') + "/" + rec.getArtifactId() + "-" + rec.getVersion() + ".xml");
                        writePluginXml(jar.getInputStream(entry), newFile);
                        if (isRelease(release, rec.getVersion(), new File(repodir, rec.getGroupId().replace('.', '/') + "/" + 
                                              rec.getArtifactId() + "/maven-metadata.xml"))) {
                            newFile = new File(results, rec.getGroupId().replace('.', '/') + "/" + rec.getArtifactId() + "-RELEASE.xml");
                            writePluginXml(jar.getInputStream(entry), newFile);
                        }
                    } else {
                        System.out.println("  entry not found");
                    }
                } else {
                    System.out.println("  not found");
                }
            }
                    
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    private static boolean isRelease(HashMap<File, String> release, String version, File file) throws FileNotFoundException, IOException {
        String rel = release.get(file);
        if (rel != null) {
            return rel.equals(version);
        }
        if (!file.exists()) {
            return false;
        }
        InputStream in = new FileInputStream(file);
        try {
            String str = IOUtil.toString(in);
            Pattern patt = Pattern.compile(".*<release>(.*)</release>.*", Pattern.DOTALL);
            Matcher match = patt.matcher(str);
            if (match.matches()) {
                rel = match.group(1);
                release.put(file, rel);
                return rel.equals(version);
            } else {
                System.out.println("    release version doesn't match at " + file);
                return false;
            }
        } finally {
            IOUtil.close(in);
        }
    }

    private static void writePluginXml(InputStream str, File newFile) throws IOException, FileNotFoundException, IOException {
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();
            OutputStream out = new FileOutputStream(newFile);
            IOUtil.copy(str, out);
            IOUtil.close(str);
            IOUtil.close(out);
    }
}
