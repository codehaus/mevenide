/*
 * CustomQueries.java
 *
 * Created on July 16, 2006, 9:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.indexer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Model;
import org.apache.maven.repository.indexing.RepositoryIndex;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.RepositoryIndexSearchHit;
import org.apache.maven.repository.indexing.query.CompoundQuery;
import org.apache.maven.repository.indexing.query.Query;
import org.apache.maven.repository.indexing.query.SinglePhraseQuery;

/**
 *
 * @author mkleint
 */
public class CustomQueries {
    
    /** Creates a new instance of CustomQueries */
    private CustomQueries() {
    }
    
    public static Query createGroupIdQuery(String groupId) {
        //TODO evil and performance hog...
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        return new QQuery(groupId);
    }
    
    public static Query createArtifactQuery(String groupId, String artifactId) {
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        return new QQuery(groupId, artifactId);
    }
    
    //TODO very slow and memory hog for empty or very small prefix.
    public static Set retrieveGroupIds(String prefix) throws RepositoryIndexSearchException {
        Query q = CustomQueries.createGroupIdQuery(prefix);
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isHashMap()) {
                HashMap map = (HashMap)elem.getObject();
                Artifact art = (Artifact)map.get(RepositoryIndex.ARTIFACT);
                if (art.getGroupId().startsWith(prefix)) {
                    elems.add(art.getGroupId());
                }
            }
        }
        return elems;
    }
    
    public static Set retrieveArtifactIdForGroupId(String groupId, String prefix) throws RepositoryIndexSearchException {
        Query q = CustomQueries.createArtifactQuery(groupId, prefix);
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isHashMap()) {
                HashMap map = (HashMap)elem.getObject();
                Artifact art = (Artifact)map.get(RepositoryIndex.ARTIFACT);
                if (art.getArtifactId().startsWith(prefix)) {
                    elems.add(art.getArtifactId());
                }
            }
        }
        return elems;
    }
    
    public static Set retrievePluginGroupIds(String prefix) throws RepositoryIndexSearchException {
        CompoundQuery q = new CompoundQuery();
        q.and(new SinglePhraseQuery(RepositoryIndex.FLD_PACKAGING, "maven-plugin"));
        // for some reason the prefix query doens't work here..
//        q.and(CustomQueries.createGroupIdQuery(prefix));
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultPomIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isModel()) {
                Model art = (Model)elem.getObject();
                if (art.getGroupId() != null && art.getGroupId().startsWith(prefix)) {
                    elems.add(art.getGroupId());
                }
            }
        }
        return elems;
    }
    
    public static Set retrievePluginArtifactIds(String groupId, String prefix) throws RepositoryIndexSearchException {
        CompoundQuery q = new CompoundQuery();
        q.and(new SinglePhraseQuery(RepositoryIndex.FLD_PACKAGING, "maven-plugin"));
        // for some reason the prefix query doens't work here..
//        q.and(CustomQueries.createArtifactQuery(groupId, prefix));
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultPomIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isModel()) {
                Model art = (Model)elem.getObject();
                if (art.getGroupId() != null && art.getGroupId().equals(groupId) && 
                    art.getArtifactId() != null && art.getArtifactId().startsWith(prefix)) {
                    elems.add(art.getArtifactId());
                }
            }
        }
        return elems;
    }

    /**
     * returns a list of Artifacts that are archetypes.
     */
    public static Set retrievePossibleArchetypes() throws RepositoryIndexSearchException {
        Query q = new SinglePhraseQuery(RepositoryIndex.FLD_FILES, "archetype.xml");
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        System.out.println("set size = " + lst.size());
        while (it.hasNext()) {
            RepositoryIndexSearchHit elem = (RepositoryIndexSearchHit) it.next();
            if (elem.isHashMap()) {
                HashMap map = (HashMap)elem.getObject();
                Artifact art = (Artifact)map.get(RepositoryIndex.ARTIFACT);
                JarFile jf = null;
                System.out.println("art=" + art.getId());
                try {
                    if (!art.getFile().exists()) {
                        System.out.println("artifact doesn't exist" + art.getFile());
                        continue;
                    }
                    jf = new JarFile(art.getFile());
                    if (jf.getJarEntry("META-INF/archetype.xml") != null) {
                         System.out.println("is actually an archetype");
                         elems.add(art);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                } finally {
                    if (jf != null) {
                        try {
                            jf.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
        return elems;
    }
    
    
    /**
     * Class to hold a single field search condition
     *
     */
    private static  class QQuery
            implements Query {
        
        private String value;
        
        public QQuery(String groupId ) {
            value = RepositoryIndex.ARTIFACT + ":" + groupId + "*";
        }
        
        public QQuery(String groupId, String artifactid ) {
            this.value = RepositoryIndex.ARTIFACT + ":" + groupId + ":" + artifactid + "*";
        }
        
        
        public org.apache.lucene.search.Query createLuceneQuery( RepositoryIndex index ) {
            WildcardQuery q = new WildcardQuery(new Term(RepositoryIndex.FLD_ID, value));
            return q;
        }
    }
    
}
