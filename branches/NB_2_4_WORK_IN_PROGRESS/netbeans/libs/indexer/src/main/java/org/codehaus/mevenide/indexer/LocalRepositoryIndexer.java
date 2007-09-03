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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.archiva.discoverer.ArtifactDiscoverer;
import org.apache.maven.archiva.discoverer.DiscovererException;
import org.apache.maven.archiva.discoverer.filter.AcceptAllArtifactFilter;
import org.apache.maven.archiva.discoverer.filter.SnapshotArtifactFilter;
import org.apache.maven.archiva.indexer.RepositoryArtifactIndex;
import org.apache.maven.archiva.indexer.RepositoryArtifactIndexFactory;
import org.apache.maven.archiva.indexer.RepositoryIndexException;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.lucene.LuceneQuery;
import org.apache.maven.archiva.indexer.record.RepositoryIndexRecordFactory;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.embedder.MavenEmbedderException;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.embed.EmbedderException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;

/**
 * a wrapper for things dealing with local repository search..
 * 
 * //TODO make sure to get means of figuring if it's save to query the index, disallowing when index is being rebuilt.
 * @author mkleint
 */
public class LocalRepositoryIndexer {
    
    private static LocalRepositoryIndexer instance;
    
    private Embedder embedder;
    private RepositoryArtifactIndexFactory indexFactory;
    private ArtifactDiscoverer discoverer;
    private RepositoryArtifactIndex defaultIndex;
    private ArtifactRepository repository;
    private RepositoryIndexRecordFactory recordFactory;
    private ArtifactFactory artifactFactory;
    private List listeners = new ArrayList();
    private boolean doCancel = false;
    
    private static final String[] ALL_FIELDS = new String[] {
        StandardIndexRecordFields.ARTIFACTID,
        StandardIndexRecordFields.BASE_VERSION,
        StandardIndexRecordFields.CLASSES,
        StandardIndexRecordFields.CLASSIFIER, 
        StandardIndexRecordFields.FILES,
        StandardIndexRecordFields.GROUPID, 
        StandardIndexRecordFields.PACKAGING, 
        StandardIndexRecordFields.PROJECT_DESCRIPTION,
        StandardIndexRecordFields.PROJECT_NAME,
        StandardIndexRecordFields.TYPE
    };
    
    /** Creates a new instance of LocalRepositoryIndexer */
    private LocalRepositoryIndexer() throws  EmbedderException, ComponentLookupException, PlexusContainerException, MavenEmbedderException, RepositoryIndexException {
        embedder = new Embedder();
        embedder.start();
        indexFactory = (RepositoryArtifactIndexFactory) embedder.lookup(RepositoryArtifactIndexFactory.ROLE, "lucene");
        discoverer = (ArtifactDiscoverer) embedder.lookup(ArtifactDiscoverer.ROLE, "default" );
        repository = EmbedderFactory.getProjectEmbedder().getLocalRepository();
        defaultIndex = createIndex();
        recordFactory = (RepositoryIndexRecordFactory)embedder.lookup(RepositoryIndexRecordFactory.ROLE, "standard");
        artifactFactory = (ArtifactFactory)embedder.lookup(ArtifactFactory.ROLE);
    }
    
    public static synchronized LocalRepositoryIndexer getInstance() {
        if (instance == null) {
            try {
                instance = new LocalRepositoryIndexer();
            } catch (Exception ex) {
                //TODO exception handling..
                ex.printStackTrace();
            }
        }
        return instance;
    }
    
    public synchronized void addChangeListener(ChangeListener list) {
        listeners.add(list);
    }
    
    public synchronized void removeChangeListener(ChangeListener list) {
        listeners.remove(list);
    }
    
    private void fireStateChanged() {
       List lists = new ArrayList();
       synchronized (this) {
           lists.addAll(listeners);
       }
       Iterator it = lists.iterator();
       while (it.hasNext()) {
           ChangeListener elem = (ChangeListener) it.next();
           elem.stateChanged(new ChangeEvent(this));
       }
    }
    
    private RepositoryArtifactIndex createIndex() throws RepositoryIndexException {
        File basedir = new File(repository.getBasedir());
        if (!basedir.exists()) {
            basedir.mkdirs();
        }
        RepositoryArtifactIndex index = indexFactory.createStandardIndex(new File(basedir, ".index"));
        return index;
    }
    
    public void updateIndex() throws RepositoryIndexException {
        ProgressHandle handle = ProgressHandleFactory.createHandle("Maven local repository indexing", new Cancellable() {
            public boolean cancel() {
                doCancel = true;
                return true;
            }
        });
        try {
            handle.start();
            handle.progress("Discovering artifacts...");
            ArtifactFilter filter;
            if (MavenIndexSettings.getDefault().isIncludeSnapshots()) {
                filter = new AcceptAllArtifactFilter();
            } else {
                filter = new SnapshotArtifactFilter();
            }
            List artifacts = discoverer.discoverArtifacts(repository,  null, filter);
            doUpdate(defaultIndex, handle, artifacts);
            MavenIndexSettings.getDefault().setLastIndexUpdate(new Date());
        } catch (DiscovererException ex) {
            //TODO
            ex.printStackTrace();
        } finally {
            handle.finish();
        }
        fireStateChanged();
    }
    
    private void doUpdate(RepositoryArtifactIndex index, ProgressHandle handle, Collection artifacts) throws RepositoryIndexException {
        int size = artifacts.size();
        handle.switchToDeterminate(size + 1);
        int count = 0;
        Collection records = new ArrayList(artifacts.size());
        for ( Iterator i = artifacts.iterator(); i.hasNext(); ) {
            Artifact artifact = (Artifact) i.next();
            count++;
            handle.progress("Recording " + count + " out of " + size, count);
            records.add(recordFactory.createRecord(artifact));
            if (count % 200 == 0) {
                handle.progress("Indexing...");
                index.indexRecords(records);
                records.clear();
            }
            if (doCancel) {
                doCancel = false;
                return;
            }
        }
        handle.progress("Indexing...");
        index.indexRecords(records);
    }
    
    public void updateIndexWithArtifacts(Collection artifacts) throws RepositoryIndexException {
        ProgressHandle handle = ProgressHandleFactory.createHandle("Maven local repository index update");
        try {
            handle.start();
//TODO            doUpdate(defaultIndex, handle, artifacts);
        } finally {
            handle.finish();
        }
    }
    
    public List searchIndex(RepositoryArtifactIndex index, LuceneQuery query) throws RepositoryIndexSearchException {
        return index.search(query);
    }
    

    public RepositoryArtifactIndex getDefaultIndex() {
        //TODO this needs to update everytime the settings.xml file changes..
        return defaultIndex;
    }
    
    public File getDefaultIndexLocation() {
        return new File(repository.getBasedir(), ".index");
    }
    
    public static Query parseQuery(String parse) throws ParseException {
        //simpleanalyzer seems to wreak havoc in non-tokenized fields..
        Analyzer anal = new WhitespaceAnalyzer();
        // ?? contents??
        QueryParser parser = new QueryParser("contents", anal);
        return parser.parse(parse);
    }
    
    public static Query parseMultiFieldQuery(String parse) throws ParseException {
        Analyzer anal = new WhitespaceAnalyzer();
        MultiFieldQueryParser parser = new MultiFieldQueryParser(ALL_FIELDS, anal);
        return parser.parse(parse);
    }
    
    
    /**
     * returns a list of Artifacts that are archetypes.
     *  @returns Set of StandardArtifactIndexRecord instances
     */
    public List retrievePossibleArchetypes() throws RepositoryIndexSearchException {
        TermQuery tq  = new TermQuery( new Term(StandardIndexRecordFields.TYPE, "maven-archetype"));
        LuceneQuery q = new LuceneQuery(tq);
        return searchIndex(getDefaultIndex(), q);
//        Iterator it = lst.iterator();
//        Set elems = new TreeSet();
//        System.out.println("set size = " + lst.size());
//        while (it.hasNext()) {
//            StandardArtifactIndexRecord elem = (StandardArtifactIndexRecord) it.next();
//            if (elem.getClassifier() != null) {
//                elems.add(artifactFactory.createArtifactWithClassifier(elem.getGroupId(), 
//                                                                       elem.getArtifactId(), 
//                                                                       elem.getVersion(), 
//                                                                       elem.getType(), 
//                                                                       elem.getClassifier()));
//            } else {
//                elems.add(artifactFactory.createArtifact(elem.getGroupId(), elem.getArtifactId(),
//                                                         elem.getVersion(), Artifact.SCOPE_RUNTIME, elem.getType()));
//            }
//        }
//        return elems;
    }
    
}
