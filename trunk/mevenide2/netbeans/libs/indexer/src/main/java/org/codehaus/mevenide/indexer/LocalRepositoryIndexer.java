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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
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
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.embedder.MavenEmbedderException;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Cancellable;
import org.openide.util.Mutex;
import org.openide.util.Mutex.ExceptionAction;
import org.openide.util.Mutex.ExceptionAction;
import org.openide.util.MutexException;

/**
 * a wrapper for things dealing with local repository search..
 * 
 * //TODO make sure to get means of figuring if it's save to query the index, disallowing when index is being rebuilt.
 * @author mkleint@codehaus.org
 */
public class LocalRepositoryIndexer {
    
    /**
     * any reads, writes from/to index shal be done under mutex access.
     */
    public static final Mutex MUTEX = new Mutex();
    
    private static LocalRepositoryIndexer instance;
    
    private PlexusContainer embedder;
    private RepositoryArtifactIndexFactory indexFactory;
    private ArtifactDiscoverer discoverer;
    private RepositoryArtifactIndex defaultIndex;
    private ArtifactRepository repository;
    private RepositoryIndexRecordFactory recordFactory;
    private ArtifactFactory artifactFactory;
    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();
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
    private LocalRepositoryIndexer() throws MavenEmbedderException, RepositoryIndexException, PlexusContainerException, ComponentLookupException {
        ContainerConfiguration config = new DefaultContainerConfiguration();
        embedder = new DefaultPlexusContainer(config);
        indexFactory = (RepositoryArtifactIndexFactory) embedder.lookup(RepositoryArtifactIndexFactory.ROLE, "lucene"); //NOI18N
        discoverer = (ArtifactDiscoverer) embedder.lookup(ArtifactDiscoverer.ROLE, "default" ); //NOI18N
        repository = EmbedderFactory.getProjectEmbedder().getLocalRepository();
        defaultIndex = createIndex();
        recordFactory = (RepositoryIndexRecordFactory)embedder.lookup(RepositoryIndexRecordFactory.ROLE, "standard"); //NOI18N
        artifactFactory = (ArtifactFactory)embedder.lookup(ArtifactFactory.ROLE);
    }
    
     static synchronized LocalRepositoryIndexer getInstance() {
        if (instance == null) {
            try {
                instance = new LocalRepositoryIndexer();
            } catch (Exception ex) {
                Logger.getLogger(LocalRepositoryIndexer.class.getName()).log(Level.INFO, "Cannot instantiate LocalRepositoryIndexer.", ex);
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
       List<ChangeListener> lists = new ArrayList<ChangeListener>();
       synchronized (this) {
           lists.addAll(listeners);
       }
       for (ChangeListener elem : lists) {
           elem.stateChanged(new ChangeEvent(this));
       }
    }
    
    private RepositoryArtifactIndex createIndex() throws RepositoryIndexException {
        File basedir = new File(repository.getBasedir());
        if (!basedir.exists()) {
            basedir.mkdirs();
        }
        RepositoryArtifactIndex index = indexFactory.createStandardIndex(new File(basedir, ".index")); //NOI18N
        return index;
    }
    
    /**
     * update the local repository index. Done under write access of the mutex.
     */
    public void updateIndex() throws RepositoryIndexException {
        final ProgressHandle handle = ProgressHandleFactory.createHandle(org.openide.util.NbBundle.getMessage(LocalRepositoryIndexer.class, "LBL_Handle"), new Cancellable() {
            public boolean cancel() {
                doCancel = true;
                return true;
            }
        });
        try {
            handle.start();
            handle.progress(org.openide.util.NbBundle.getMessage(LocalRepositoryIndexer.class, "LBL_Discovering"));
            ArtifactFilter filter;
            if (MavenIndexSettings.getDefault().isIncludeSnapshots()) {
                filter = new AcceptAllArtifactFilter();
            } else {
                filter = new SnapshotArtifactFilter();
            }
            final List artifacts = discoverer.discoverArtifacts(repository,  null, filter);
            MUTEX.writeAccess(new ExceptionAction() {
                public Object run() throws Exception {
                    doUpdate(defaultIndex, handle, artifacts);
                    return null;
                }
            });
            MavenIndexSettings.getDefault().setLastIndexUpdate(new Date());
        } catch (MutexException ex) {
            throw (RepositoryIndexException)ex.getException();
        }
        catch (DiscovererException ex) {
            // TODO
            ex.printStackTrace();
        } finally {
            handle.finish();
        }
        fireStateChanged();
    }

    /**
     * to be called under mutex..
     */
    private void doUpdate(RepositoryArtifactIndex index, ProgressHandle handle, Collection artifacts) throws RepositoryIndexException {
        int size = artifacts.size();
        handle.switchToDeterminate(size + 1);
        int count = 0;
        Collection records = new ArrayList(artifacts.size());
        for ( Iterator i = artifacts.iterator(); i.hasNext(); ) {
            Artifact artifact = (Artifact) i.next();
            count++;
            handle.progress(org.openide.util.NbBundle.getMessage(LocalRepositoryIndexer.class, "LBL_Recording", count, size), count);
            try {
                records.add(recordFactory.createRecord(artifact));
                
            } catch (InvalidArtifactRTException e) {
                //TODO.. some better handling..
                e.printStackTrace();
            }
            if (count % 200 == 0) {
                handle.progress(org.openide.util.NbBundle.getMessage(LocalRepositoryIndexer.class, "LBL_Indexing"));
                index.indexRecords(records);
                records.clear();
            }
            if (doCancel) {
                doCancel = false;
                return;
            }
        }
        handle.progress(org.openide.util.NbBundle.getMessage(LocalRepositoryIndexer.class, "LBL_Indexing"));
        index.indexRecords(records);
    }
    
    public void updateIndexWithArtifacts(Collection artifacts) throws RepositoryIndexException {
        ProgressHandle handle = ProgressHandleFactory.createHandle(org.openide.util.NbBundle.getMessage(LocalRepositoryIndexer.class, "LBL_repo_update"));
        try {
            handle.start();
//TODO            doUpdate(defaultIndex, handle, artifacts);
        } finally {
            handle.finish();
        }
    }
    
    /**
     * Accesses the search index and processes the query. All is done under Mutex readAccess.
     */
    public List<StandardArtifactIndexRecord> searchIndex(final RepositoryArtifactIndex index, final LuceneQuery query) throws RepositoryIndexSearchException {
        try {
            return MUTEX.readAccess(new ExceptionAction<List<StandardArtifactIndexRecord>>() {
                                        public List<StandardArtifactIndexRecord> run() throws Exception {
                                            return index.search(query);
                                        }
                                    });
        }
        catch (MutexException ex) {
            throw (RepositoryIndexSearchException)ex.getException();
        }
    }
    

    public RepositoryArtifactIndex getDefaultIndex() {
        //TODO this needs to update everytime the settings.xml file changes..
        return defaultIndex;
    }
    
    public File getDefaultIndexLocation() {
        return new File(repository.getBasedir(), ".index"); //NOI18N
    }
    
    public static Query parseQuery(String parse) throws ParseException {
        //simpleanalyzer seems to wreak havoc in non-tokenized fields..
        Analyzer anal = new WhitespaceAnalyzer();
        // ?? contents??
        QueryParser parser = new QueryParser("contents", anal); //NOI18N
        return parser.parse(parse);
    }
    
    public static Query parseMultiFieldQuery(String parse) throws ParseException {
        Analyzer anal = new WhitespaceAnalyzer();
        MultiFieldQueryParser parser = new MultiFieldQueryParser(ALL_FIELDS, anal);
        return parser.parse(parse);
    }
    
    
    
}
