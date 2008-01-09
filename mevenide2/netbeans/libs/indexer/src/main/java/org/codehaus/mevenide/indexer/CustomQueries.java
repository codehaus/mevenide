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

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.archiva.digest.DigesterException;
import org.apache.maven.archiva.digest.Md5Digester;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.lucene.LuceneQuery;
import org.apache.maven.archiva.indexer.lucene.LuceneStandardIndexRecordConverter;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.openide.util.Exceptions;
import org.openide.util.Mutex.ExceptionAction;
import org.openide.util.MutexException;

/**
 * custom queries into the local repo index.
 * All run under read access mutex.
 * @author mkleint
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class CustomQueries {

    /** Creates a new instance of CustomQueries */
    private CustomQueries() {
    }

    public static Set<String> retrieveGroupIds(String prefix) throws IOException {
        List<String> lst = enumerateGroupIds();
        Set<String> elems = new TreeSet<String>();
        for (String id : lst) {
            if (id.startsWith(prefix)) {
                elems.add(id);
            }
        }
        return elems;
    }

    public static Set<String> retrieveArtifactIdForGroupId(String groupId, String prefix) throws IOException {
        List<String> lst = getArtifacts(groupId);
        Set<String> elems = new TreeSet<String>();
        for (String art : lst) {
            if (art.startsWith(prefix)) {
                elems.add(art);
            }
        }
        return elems;
    }

    public static Set<String> retrievePluginGroupIds(String prefix)  {
        try {
            TermQuery tq = new TermQuery(new Term(StandardIndexRecordFields.TYPE, "maven-plugin"));
            LuceneQuery q = new LuceneQuery(tq);
            List<StandardArtifactIndexRecord> lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
            Set<String> elems = new TreeSet<String>();
            for (StandardArtifactIndexRecord elem : lst) {
                if (elem.getGroupId() != null && elem.getGroupId().startsWith(prefix)) {
                    elems.add(elem.getGroupId());
                }
            }
            return elems;
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.emptySet();
    }

    public static Set<String> retrievePluginArtifactIds(String groupId, String prefix)  {
        try {
            TermQuery tq = new TermQuery(new Term(StandardIndexRecordFields.TYPE, "maven-plugin"));
            LuceneQuery q = new LuceneQuery(tq);
            List<StandardArtifactIndexRecord> lst = LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q);
            Set<String> elems = new TreeSet<String>();
            for (StandardArtifactIndexRecord elem : lst) {
                if (elem.getGroupId() != null && elem.getGroupId().equals(groupId) && elem.getArtifactId() != null && elem.getArtifactId().startsWith(prefix)) {
                    elems.add(elem.getArtifactId());
                }
            }
            return elems;
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.emptySet();
    }

    /**
     * returns a list of Artifacts that are archetypes.
     *  @returns Set of VersionInfo instances
     */
    public static List<VersionInfo> retrievePossibleArchetypes() {
        List<StandardArtifactIndexRecord> indexRecords = new ArrayList<StandardArtifactIndexRecord>();
        BooleanQuery bq = new BooleanQuery();
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.TYPE, "maven-archetype")), BooleanClause.Occur.SHOULD));
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.PACKAGING, "maven-archetype")), BooleanClause.Occur.SHOULD));
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.FILES, "archetype-metadata.xml")), BooleanClause.Occur.SHOULD));
        LuceneQuery q = new LuceneQuery(bq);
        try {
            indexRecords.addAll(LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q));
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
         return IndexerUtil.convertToVersionInfos(indexRecords);
    }

    public static List<VersionInfo> getRecords(String groupId, String artifactId, String version)  {
        List<StandardArtifactIndexRecord> indexRecords = new ArrayList<StandardArtifactIndexRecord>();
        BooleanQuery bq = new BooleanQuery();
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.GROUPID_EXACT, groupId)), BooleanClause.Occur.MUST));
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.ARTIFACTID_EXACT, artifactId)), BooleanClause.Occur.MUST));
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.VERSION_EXACT, version)), BooleanClause.Occur.MUST));
        LuceneQuery q = new LuceneQuery(bq);
        try {
            indexRecords.addAll(LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q));
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return IndexerUtil.convertToVersionInfos(indexRecords);
    }

    public static List<String> enumerateGroupIds() throws IOException {
        Set<String> groups = new HashSet<String>();
        try {
            groups = LocalRepositoryIndexer.MUTEX.readAccess(new ExceptionAction<Set<String>>() {

                public Set<String> run() throws Exception {
                    IndexReader indexReader = IndexReader.open(LocalRepositoryIndexer.getInstance().getDefaultIndexLocation().getAbsolutePath());
                    Set<String> groups = new HashSet<String>();

                    try {
                        for (int i = 0; i <
                                indexReader.numDocs(); i++) {
                            Document doc = indexReader.document(i);
                            Field fld = doc.getField(StandardIndexRecordFields.GROUPID);

                            if (fld !=
                                    null) {
                                groups.add(fld.stringValue());
                            } else {
                                //TODO??
                                System.out.println("no groupid field for " +
                                        doc.getField(StandardIndexRecordFields.FILENAME));
                            }
                        }
                    } finally {
                        indexReader.close();
                    }
                    return groups;
                }
            });
        } catch (MutexException ex) {
            throw (IOException) ex.getException();
        }
        List<String> sortedGroups = new ArrayList<String>(groups);
        Collections.sort(sortedGroups);
        return sortedGroups;
    }

    public static List<String> getArtifacts(final String groupId) throws IOException {
        Set<String> artifactIds = new HashSet<String>();
        try {
            artifactIds = LocalRepositoryIndexer.MUTEX.readAccess(new ExceptionAction<Set<String>>() {

                public Set<String> run() throws Exception {
                    Set<String> artifactIds = new HashSet<String>();
                    IndexReader indexReader = IndexReader.open(LocalRepositoryIndexer.getInstance().getDefaultIndexLocation().getAbsolutePath());

                    try {
                        for (int i = 0; i <
                                indexReader.numDocs(); i++) {
                            Document doc = indexReader.document(i);
                            Field fld = doc.getField(StandardIndexRecordFields.GROUPID);

                            if (fld !=
                                    null) {
                                if (fld.stringValue().equals(groupId)) {
                                    artifactIds.add(doc.getField(StandardIndexRecordFields.ARTIFACTID).stringValue());
                                }
                            } else {
                                //TODO
                                System.out.println("no groupid field for " +
                                        doc.getField(StandardIndexRecordFields.FILENAME));
                            }
                        }
                    } finally {
                        indexReader.close();
                    }
                    return artifactIds;
                }
            });
        } catch (MutexException ex) {
            throw (IOException) ex.getException();
        }
        List<String> sortedArtifactIds = new ArrayList<String>(artifactIds);
        Collections.sort(sortedArtifactIds);
        return sortedArtifactIds;
    }

    public static List<VersionInfo> getVersions(final String groupId, final String artifactId) throws IOException {
        Set<StandardArtifactIndexRecord> versions = new HashSet<StandardArtifactIndexRecord>();
        try {
            versions = LocalRepositoryIndexer.MUTEX.readAccess(new ExceptionAction<Set<StandardArtifactIndexRecord>>() {

                public Set<StandardArtifactIndexRecord> run() throws Exception {
                    Set<StandardArtifactIndexRecord> versions = new HashSet<StandardArtifactIndexRecord>();
                    IndexReader indexReader = IndexReader.open(LocalRepositoryIndexer.getInstance().getDefaultIndexLocation().getAbsolutePath());
                    LuceneStandardIndexRecordConverter conv = new LuceneStandardIndexRecordConverter();

                    try {
                        for (int i = 0; i < indexReader.numDocs(); i++) {
                            Document doc = indexReader.document(i);

                            if (doc.getField(StandardIndexRecordFields.GROUPID).stringValue().equals(groupId) &&
                                    doc.getField(StandardIndexRecordFields.ARTIFACTID).stringValue().equals(artifactId)) {
                                versions.add((StandardArtifactIndexRecord) conv.convert(doc));
                            }
                        }
                    } finally {
                        indexReader.close();
                    }
                    return versions;
                }
            });
        } catch (MutexException ex) {
            throw (IOException) ex.getException();
        }
        //TODO
//        // DefaultArtifactVersion is used for correct ordering
        List<StandardArtifactIndexRecord> sortedVersions = new ArrayList<StandardArtifactIndexRecord>();
        sortedVersions.addAll(versions);
//        for (String elem : versions) {
//            sortedVersions.add(new DefaultArtifactVersion(elem));
//        }
//        Collections.sort( sortedVersions );
        return IndexerUtil.convertToVersionInfos(sortedVersions);
    }

    public static List<GroupInfo> findDependencyUsage(String groupId, String artifactId, String version) {
        LocalRepositoryIndexer index = LocalRepositoryIndexer.getInstance();
        // no escaping seems to be necessary.. oh well..
        String term = /*QueryParser.escape(*/ groupId + ":" + artifactId + ":" + version; //); //NOI18N
        TermQuery tq = new TermQuery(new Term(StandardIndexRecordFields.DEPENDENCIES, term));
        LuceneQuery q = new LuceneQuery(tq);
        List<StandardArtifactIndexRecord> artifactIndexRecords = new ArrayList<StandardArtifactIndexRecord>();

        try {
            artifactIndexRecords.addAll(index.searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q));
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }

        return IndexerUtil.convertToGroupInfos(artifactIndexRecords);
    }

    public static List<VersionInfo> findByMD5(File file) {
        try {
            
            Md5Digester digest = new Md5Digester();
            String md5 = digest.calc(file);



            return findByMD5(md5.toLowerCase());
        } catch (NoSuchAlgorithmException ex) {
            Exceptions.printStackTrace(ex);
        } catch (DigesterException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Collections.emptyList();
    }

    public static List<VersionInfo> findByMD5(String md5) {
        List<StandardArtifactIndexRecord> indexRecords = new ArrayList<StandardArtifactIndexRecord>();
        LocalRepositoryIndexer index = LocalRepositoryIndexer.getInstance();
        TermQuery tq = new TermQuery(new Term(StandardIndexRecordFields.MD5, md5));
        LuceneQuery q = new LuceneQuery(tq);
        try {
            indexRecords.addAll(index.searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q));
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        return IndexerUtil.convertToVersionInfos(indexRecords);
    }
}
