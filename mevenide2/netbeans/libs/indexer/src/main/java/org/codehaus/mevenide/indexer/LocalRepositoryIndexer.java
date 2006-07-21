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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.repository.discovery.ArtifactDiscoverer;
import org.apache.maven.repository.indexing.ArtifactRepositoryIndex;
import org.apache.maven.repository.indexing.PomRepositoryIndex;
import org.apache.maven.repository.indexing.RepositoryIndex;
import org.apache.maven.repository.indexing.RepositoryIndexException;
import org.apache.maven.repository.indexing.RepositoryIndexSearchException;
import org.apache.maven.repository.indexing.RepositoryIndexSearcher;
import org.apache.maven.repository.indexing.RepositoryIndexingFactory;
import org.apache.maven.repository.indexing.query.Query;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 * a wrapper for things dealing with local repository search..
 * @author mkleint
 */
public class LocalRepositoryIndexer {
    
    private static LocalRepositoryIndexer instance;
    
    private Embedder embedder;
    private RepositoryIndexingFactory indexFactory;
    private ArtifactDiscoverer discoverer;
    private RepositoryIndexSearcher searcher;
    private ArtifactRepositoryIndex defaultIndex;
    private PomRepositoryIndex defaultPomIndex;
    private MavenProjectBuilder projectBuilder;
    
    /** Creates a new instance of LocalRepositoryIndexer */
    private LocalRepositoryIndexer() throws PlexusContainerException, ComponentLookupException, MavenEmbedderException, RepositoryIndexException {
        embedder = new Embedder();
        ClassWorld world = new ClassWorld();
        embedder.start( world );
        indexFactory = (RepositoryIndexingFactory) embedder.lookup(RepositoryIndexingFactory.ROLE);
        discoverer = (ArtifactDiscoverer) embedder.lookup(ArtifactDiscoverer.ROLE, "default" );
        searcher = (RepositoryIndexSearcher) embedder.lookup(RepositoryIndexSearcher.ROLE, "complete");
        defaultIndex = createIndex(EmbedderFactory.getProjectEmbedder().getLocalRepository());
        defaultPomIndex = createPomIndex(EmbedderFactory.getProjectEmbedder().getLocalRepository());
        projectBuilder = (MavenProjectBuilder) embedder.lookup(MavenProjectBuilder.ROLE);
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
    
    private ArtifactRepositoryIndex createIndex(ArtifactRepository repo) throws RepositoryIndexException {
        File basedir = new File(repo.getBasedir());
        if (!basedir.exists()) {
            basedir.mkdirs();
        }
        ArtifactRepositoryIndex index = indexFactory.createArtifactRepositoryIndex(new File(basedir, ".index"), repo);
        return index;
    }
    
    private PomRepositoryIndex createPomIndex(ArtifactRepository repo) throws RepositoryIndexException {
        File basedir = new File(repo.getBasedir());
        if (!basedir.exists()) {
            basedir.mkdirs();
        }
        PomRepositoryIndex index = indexFactory.createPomRepositoryIndex(new File(basedir, ".pomindex"), repo);
        return index;
    }
    
    public void updateIndex(boolean updateSnapshots) throws RepositoryIndexException {
        ProgressHandle handle = ProgressHandleFactory.createHandle("Maven local repository indexing");
        try {
            handle.start();
            handle.progress("Discovering artifacts...");
            List artifacts = discoverer.discoverArtifacts( defaultIndex.getRepository(), null, updateSnapshots );
            doUpdate(defaultIndex, defaultPomIndex, handle, artifacts);
            MavenIndexSettings.getDefault().setLastIndexUpdate(new Date());
        } finally {
            handle.finish();
        }
    }
    
    private void doUpdate(ArtifactRepositoryIndex index, PomRepositoryIndex pomIndex, ProgressHandle handle, Collection artifacts) throws RepositoryIndexException {
        int size = artifacts.size();
        handle.switchToDeterminate(size + (size / 5));
        MavenXpp3Reader reader = new MavenXpp3Reader();
        int count = 0;
        for ( Iterator i = artifacts.iterator(); i.hasNext(); ) {
            Artifact artifact = (Artifact) i.next();
            count++;
            handle.progress("Indexing " + count + " out of " + size, count);
            index.indexArtifact( artifact );
            if ("pom".equals(artifact.getType())) {
//                InputStreamReader rr = null;
                try {
                    
//                    rr = new InputStreamReader(new FileInputStream(artifact.getFile()));
//                    EmbedderFactory.getProjectEmbedder().readModel(artifact.getFile());
//                    Model mdl = reader.read(rr, true);
                    MavenProject prj = projectBuilder.buildFromRepository(artifact, Collections.EMPTY_LIST, index.getRepository());
                    Model mdl = prj.getModel();
                    //maven-clean-plugin 2.1 ??
                    if (mdl.getGroupId() == null || mdl.getArtifactId() == null ||
                        mdl.getVersion() == null || mdl.getPackaging() == null) {
                        System.out.println("excluding=" + mdl.getId());
                        continue;
                    }
                    if (mdl.getPackaging().equals("maven-plugin")) {
                        System.out.println("mdl=" + mdl.getId() + " gr=" + mdl.getGroupId());
                    }
                    pomIndex.indexPom(mdl);
                } catch (RepositoryIndexException ex) {
                    ex.printStackTrace();
                } catch (ProjectBuildingException ex) {
                    System.out.println("bad pom.." + artifact.getFile());
                    ex.printStackTrace();
                } finally {
//                    IOUtil.close(rr);
                }
            }
        }
        handle.progress("Optimizing index...", count + (size / 10));
        index.optimize();
        pomIndex.optimize();
        
    }
    
    public void updateIndexWithArtifacts(Collection artifacts) throws RepositoryIndexException {
        ProgressHandle handle = ProgressHandleFactory.createHandle("Maven local repository index update");
        try {
            handle.start();
            doUpdate(defaultIndex, defaultPomIndex, handle, artifacts);
        } finally {
            handle.finish();
        }
    }
    
    public List searchIndex(RepositoryIndex index, Query query) throws RepositoryIndexSearchException {
        return searcher.search(query, index);
    }
    

    public ArtifactRepositoryIndex getDefaultIndex() {
        //TODO this needs to update everytime the settings.xml file changes..
        return defaultIndex;
    }
    
    public PomRepositoryIndex getDefaultPomIndex() {
        return defaultPomIndex;
    }
}
