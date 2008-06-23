package org.codehaus.mevenide.repoutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.codehaus.mevenide.indexer.NexusRepositoryIndexserImpl;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.QueryField;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.plexus.util.IOUtil;
import org.sonatype.nexus.index.ArtifactInfo;

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
            if (args.length < 2) {
                throw new IllegalArgumentException("Must have 2 parameters [repo index directory] [reSULT DIR]");
            }
            File repodir = new File(args[0]);
            if (!repodir.exists()) {
                throw new IllegalArgumentException(args[0] + " folder doesn't exist");
            }
            NexusRepositoryIndexserImpl index = new NexusRepositoryIndexserImpl();
        
            File results = new File(args[1]);
            if (results.exists()) {
                org.codehaus.plexus.util.FileUtils.deleteDirectory(results);
            }
            results.mkdirs();
            System.out.println("result dir = " + results);
            System.out.println("repo dir=" + repodir);
            HashMap<File, String> release = new HashMap<File, String>();
            QueryField qf = new QueryField();
            qf.setField(ArtifactInfo.PACKAGING);
            qf.setValue("maven-plugin");
            qf.setOccur(QueryField.OCCUR_MUST);
            RepositoryInfo info = new RepositoryInfo("central", RepositoryPreferences.TYPE_NEXUS, "central", repodir.getAbsolutePath(), null, null);
            
            List<NBVersionInfo> result = index.find(Collections.singletonList(qf), Collections.singletonList(info));
            System.out.println("results=" + result.size());
            for (NBVersionInfo rec : result)  {
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
