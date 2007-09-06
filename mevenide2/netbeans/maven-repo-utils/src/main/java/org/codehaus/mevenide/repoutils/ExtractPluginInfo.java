package org.codehaus.mevenide.repoutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.jar.JarFile;
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
                throw new IllegalArgumentException(args[2] + " folder exists already");
            }
            results.mkdirs();
            
            
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
                        InputStream str = jar.getInputStream(entry);
                        File newFile = new File(results, rec.getGroupId().replace('.', '/') + "/" + 
                                                rec.getArtifactId() + "-" + rec.getVersion() + ".xml");
                        newFile.getParentFile().mkdirs();
                        newFile.createNewFile();
                        OutputStream out = new FileOutputStream(newFile);
                        IOUtil.copy(str, out);
                        IOUtil.close(str);
                        IOUtil.close(out);
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
}
