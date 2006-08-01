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

package org.codehaus.mevenide.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarFile;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.model.Model;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.lucene.LuceneQuery;
import org.apache.maven.repository.indexing.query.Query;
import org.apache.maven.repository.indexing.query.SingleTermQuery;
import org.apache.maven.repository.indexing.record.StandardArtifactIndexRecord;
import org.apache.maven.repository.indexing.record.StandardIndexRecordFields;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;

/**
 *
 * @author mkleint
 */
public class CustomQueries {
    
    /** Creates a new instance of CustomQueries */
    private CustomQueries() {
    }
    
    public static Set retrieveGroupIds(String prefix) throws IOException {
        
        List lst = enumerateGroupIds();
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            String id = (String)it.next();
            if (id.startsWith(prefix)) {
                elems.add(id);
            }
        }
        return elems;
    }
    
    public static Set retrieveArtifactIdForGroupId(String groupId, String prefix) throws IOException {
        List lst = getArtifacts(groupId);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            String art = (String)it.next();
            if (art.startsWith(prefix)) {
                elems.add(art);
            }
        }
        return elems;
    }
    
    public static Set retrievePluginGroupIds(String prefix) throws RepositoryIndexSearchException {
        TermQuery tq  = new TermQuery( new Term(StandardIndexRecordFields.TYPE, "maven-plugin"));
        LuceneQuery q = new LuceneQuery(tq);
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
            if (elem.getGroupId() != null && elem.getGroupId().startsWith(prefix)) {
                elems.add(elem.getGroupId());
            }
        }
        return elems;
    }
    
    public static Set retrievePluginArtifactIds(String groupId, String prefix) throws RepositoryIndexSearchException {
        TermQuery tq  = new TermQuery( new Term(StandardIndexRecordFields.TYPE, "maven-plugin"));
        LuceneQuery q = new LuceneQuery(tq);
        List lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
        Iterator it = lst.iterator();
        Set elems = new TreeSet();
        while (it.hasNext()) {
            StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
            if (elem.getGroupId() != null && elem.getGroupId().equals(groupId) &&
                    elem.getArtifactId() != null && elem.getArtifactId().startsWith(prefix)) {
                elems.add(elem.getArtifactId());
            }
        }
        return elems;
    }
    
    public static List enumerateGroupIds()
            throws IOException {
        IndexReader indexReader = IndexReader.open( LocalRepositoryIndexer.getInstance().getDefaultIndexLocation().getAbsolutePath() );
        
        Set groups = new HashSet();
        
        try {
            for ( int i = 0; i < indexReader.numDocs(); i++ ) {
                Document doc = indexReader.document( i );
                Field fld = doc.getField( StandardIndexRecordFields.GROUPID );
                if (fld != null) {
                    groups.add( fld.stringValue() );
                } else {
                    System.out.println("no groupid field for " + doc.getField(StandardIndexRecordFields.FILENAME));
                }
            }
        } finally {
            indexReader.close();
        }
        
        List sortedGroups = new ArrayList( groups );
        Collections.sort( sortedGroups );
        return sortedGroups;
    }
    
    public static List getArtifacts( String groupId )
            throws IOException {
        
        IndexReader indexReader = IndexReader.open( LocalRepositoryIndexer.getInstance().getDefaultIndexLocation().getAbsolutePath() );
        
        Set artifactIds = new HashSet();
        
        try {
            for ( int i = 0; i < indexReader.numDocs(); i++ ) {
                Document doc = indexReader.document( i );
                Field fld = doc.getField( StandardIndexRecordFields.GROUPID );
                if (fld != null) {
                    if ( fld.stringValue().equals( groupId ) ) {
                        artifactIds.add( doc.getField( StandardIndexRecordFields.ARTIFACTID ).stringValue() );
                    }
                } else {
                    System.out.println("no groupid field for " + doc.getField(StandardIndexRecordFields.FILENAME));
                }
            }
        } finally {
            indexReader.close();
        }
        
        List sortedArtifactIds = new ArrayList( artifactIds );
        Collections.sort( sortedArtifactIds );
        return sortedArtifactIds;
    }
    
    public static List getVersions( String groupId, String artifactId )
            throws IOException {
        IndexReader indexReader = IndexReader.open( LocalRepositoryIndexer.getInstance().getDefaultIndexLocation().getAbsolutePath() );
        
        Set versions = new HashSet();
        
        try {
            for ( int i = 0; i < indexReader.numDocs(); i++ ) {
                Document doc = indexReader.document( i );
                if ( doc.getField( StandardIndexRecordFields.GROUPID ).stringValue().equals( groupId ) &&
                        doc.getField( StandardIndexRecordFields.ARTIFACTID ).stringValue().equals( artifactId ) ) {
                    // DefaultArtifactVersion is used for correct ordering
                    versions.add( new DefaultArtifactVersion( doc.getField( StandardIndexRecordFields.VERSION ).stringValue() ) );
                }
            }
        } finally {
            indexReader.close();
        }
        
        List sortedVersions = new ArrayList( versions );
        Collections.sort( sortedVersions );
        return sortedVersions;
    }
    
}
