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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.repository.discovery.ArtifactDiscoverer;
import org.apache.maven.repository.discovery.DiscovererException;
import org.apache.maven.repository.indexing.RepositoryArtifactIndex;
import org.apache.maven.repository.indexing.RepositoryArtifactIndexFactory;
import org.apache.maven.repository.indexing.RepositoryIndexException;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.lucene.LuceneQuery;
import org.apache.maven.repository.indexing.record.RepositoryIndexRecordFactory;
import org.apache.maven.repository.indexing.record.StandardArtifactIndexRecord;
import org.apache.maven.repository.indexing.record.StandardIndexRecordFields;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

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
    
    /** Creates a new instance of LocalRepositoryIndexer */
    private LocalRepositoryIndexer() throws PlexusContainerException, ComponentLookupException, MavenEmbedderException, RepositoryIndexException {
        embedder = new Embedder();
        ClassWorld world = new ClassWorld();
        embedder.start( world );
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
            } catch (PlexusContainerException ex) {
                //TODO exception handling..
                ex.printStackTrace();
            } catch (RepositoryIndexException ex) {
                ex.printStackTrace();
            } catch (MavenEmbedderException ex) {
                ex.printStackTrace();
            } catch (ComponentLookupException ex) {
                ex.printStackTrace();
            }
        }
        return instance;
    }
    
    private RepositoryArtifactIndex createIndex() throws RepositoryIndexException {
        File basedir = new File(repository.getBasedir());
        if (!basedir.exists()) {
            basedir.mkdirs();
        }
        RepositoryArtifactIndex index = indexFactory.createStandardIndex(new File(basedir, ".index"), repository);
        return index;
    }
    
    public void updateIndex(boolean updateSnapshots) throws RepositoryIndexException {
        ProgressHandle handle = ProgressHandleFactory.createHandle("Maven local repository indexing");
        try {
            handle.start();
            handle.progress("Discovering artifacts...");
            List artifacts = discoverer.discoverArtifacts(repository, "update", null, updateSnapshots);
            System.out.println("discovered=" + artifacts.size());
            doUpdate(defaultIndex, handle, artifacts);
            MavenIndexSettings.getDefault().setLastIndexUpdate(new Date());
        } catch (DiscovererException ex) {
            //TODO
            ex.printStackTrace();
        } finally {
            handle.finish();
        }
    }
    
    private void doUpdate(RepositoryArtifactIndex index, ProgressHandle handle, Collection artifacts) throws RepositoryIndexException {
        int size = artifacts.size();
        handle.switchToDeterminate(size);
        int count = 0;
        for ( Iterator i = artifacts.iterator(); i.hasNext(); ) {
            Artifact artifact = (Artifact) i.next();
            count++;
            handle.progress("Indexing " + count + " out of " + size, count);
            index.indexRecords(Collections.singletonList(recordFactory.createRecord(artifact)));
        }
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
