/*
 *  Copyright 2005-2008 Mevenide Team.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package org.codehaus.mevenide.indexer;

import java.util.Map;
import org.apache.lucene.document.Document;
import org.codehaus.mevenide.indexer.api.QueryField;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.InvalidArtifactRTException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.codehaus.mevenide.indexer.spi.ArchetypeQueries;
import org.codehaus.mevenide.indexer.spi.BaseQueries;
import org.codehaus.mevenide.indexer.spi.ChecksumQueries;
import org.codehaus.mevenide.indexer.spi.ClassesQuery;
import org.codehaus.mevenide.indexer.spi.DependencyInfoQueries;
import org.codehaus.mevenide.indexer.spi.GenericFindQuery;
import org.codehaus.mevenide.indexer.spi.RepositoryIndexerImplementation;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.openide.util.lookup.Lookups;
import org.sonatype.nexus.index.ArtifactAvailablility;
import org.sonatype.nexus.index.ArtifactContextProducer;
import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.ArtifactInfoGroup;
import org.sonatype.nexus.index.GGrouping;
import org.sonatype.nexus.index.NexusIndexer;
import org.sonatype.nexus.index.context.ArtifactIndexingContext;
import org.sonatype.nexus.index.context.IndexingContext;
import org.sonatype.nexus.index.context.UnsupportedExistingLuceneIndexException;
import org.sonatype.nexus.index.creator.AbstractIndexCreator;
import org.sonatype.nexus.index.creator.IndexCreator;
import org.sonatype.nexus.index.creator.JarFileContentsIndexCreator;
import org.sonatype.nexus.index.creator.MinimalArtifactInfoIndexCreator;
import org.sonatype.nexus.index.updater.IndexUpdater;

/**
 *
 * @author Anuradha G
 */
public class NexusRepositoryIndexserImpl implements RepositoryIndexerImplementation, 
        BaseQueries, ChecksumQueries, ArchetypeQueries, DependencyInfoQueries,
        ClassesQuery, GenericFindQuery {

    private ArtifactRepository repository;
    private NexusIndexer indexer;
    private IndexUpdater remoteIndexUpdater;
    private ArtifactContextProducer contextProducer;
    
    /*Indexer Keys*/
    private static final String NB_DEPENDENCY_GROUP = "nbdg"; //NOI18N
    private static final String NB_DEPENDENCY_ARTIFACT = "nbda"; //NOI18N
    private static final String NB_DEPENDENCY_VERSION = "nbdv"; //NOI18N
    
    /*logger*/
    private static final Logger LOGGER = 
            Logger.getLogger("org.codehaus.mevenide.indexer.RepositoryIndexer");//NOI18N
    
    /*custom Index creators*/
    public static final List<? extends IndexCreator> NB_INDEX = Arrays.asList(
            new MinimalArtifactInfoIndexCreator(),
            new JarFileContentsIndexCreator(),
            new NbIndexCreator());
    
    /**
     * any reads, writes from/to index shal be done under mutex access.
     */
    static final Mutex MUTEX = new Mutex();
    private Lookup lookup;

    public NexusRepositoryIndexserImpl() {
        //to prevent MaxClauseCount exception (will investigate better way)
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
        
        try {
            PlexusContainer embedder;
            ContainerConfiguration config = new DefaultContainerConfiguration();
            embedder = new DefaultPlexusContainer(config);

            repository = EmbedderFactory.getProjectEmbedder().getLocalRepository();
            indexer = (NexusIndexer) embedder.lookup(NexusIndexer.class);
            remoteIndexUpdater = (IndexUpdater) embedder.lookup(IndexUpdater.class);
            contextProducer = (ArtifactContextProducer) embedder.lookup(ArtifactContextProducer.class);
        } catch (ComponentLookupException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PlexusContainerException ex) {
            Exceptions.printStackTrace(ex);
        }
        lookup = Lookups.singleton(this);
    }

    public String getType() {
        return RepositoryPreferences.TYPE_NEXUS;
    }
    
    public Lookup getCapabilityLookup() {
        return lookup;
    }

    //always call from mutex.writeAccess
    private void loadIndexingContext(final RepositoryInfo... repoids) throws IOException, UnsupportedExistingLuceneIndexException {
        assert MUTEX.isWriteAccess();
         
        for (RepositoryInfo info : repoids) {
            LOGGER.finer("Loading Context :" + info.getId());//NOI18N
            if (info.isLocal() || info.isRemoteDownloadable()) {
                indexer.addIndexingContext( //
                        info.getId(), // context id
                        info.getId(), // repository id
                        info.isLocal() ? new File(info.getRepositoryPath()) : null, // repository folder
                        new File(getDefaultIndexLocation(), info.getId()), // index folder
                        info.isRemoteDownloadable() ? info.getRepositoryUrl() : null, // repositoryUrl
                        info.isRemoteDownloadable() ? info.getIndexUpdateUrl() : null, // index update url
                        NB_INDEX);
            }
        }
    }


    //TODO mkleint: do we really want to start index whenever it's missing?
    // what about just silently returning empty values and let the scheduled
    // idexing kick in..
    private void checkIndexAvailability(final RepositoryInfo... ids) throws MutexException {
        assert MUTEX.isWriteAccess();
       
        for (RepositoryInfo id : ids) {
            LOGGER.finer("Checking Context.. :"+id.getId());//NOI18N
            File file = new File(getDefaultIndexLocation(), id.getId());
            if (!file.exists() || file.listFiles().length <= 0) {
                LOGGER.finer("Index Not Available :"+id +" At :"+file.getAbsolutePath());//NOI18N
                indexRepo(id);
            }
              LOGGER.finer("Index Available :"+id+" At :"+file.getAbsolutePath());//NOI18N
        }
    }


    //always call from mutex.writeAccess
    private void unloadIndexingContext(final RepositoryInfo... repos) throws IOException {
        assert MUTEX.isWriteAccess();
        for (RepositoryInfo repo : repos) {
             LOGGER.finer("Unloading Context :" + repo.getId());//NOI18N
            IndexingContext ic = indexer.getIndexingContexts().get(repo.getId());
            if (ic != null) {
                indexer.removeIndexingContext(ic, false);
            }
        }
    }

    public void indexRepo(final RepositoryInfo repo) {
        LOGGER.finer("Indexing Context :"+repo);//NOI18N
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    loadIndexingContext(repo);

                    try {
                        Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                        IndexingContext indexingContext = indexingContexts.get(repo.getId());
                        if (indexingContext == null) {
                            LOGGER.warning("Indexing context chould not be created :"+repo.getId());//NOI18N
                            return null;
                        }
                        if (repo.isRemoteDownloadable()) {
                            LOGGER.finer("Indexing Remote Repository :"+repo.getId());//NOI18N
                            RemoteIndexTransferListener listener = new RemoteIndexTransferListener(repo);
                            try {
                                remoteIndexUpdater.fetchAndUpdateIndex(indexingContext, listener);
                            } catch (IOException iOException) {
                                 LOGGER.warning(iOException.getMessage());//NOI18N
                                //handle index not found
                                listener.transferCompleted(null);
                            }

                        } else {
                             LOGGER.finer("Indexing Local Repository :"+repo.getId());//NOI18N
                            indexer.scan(indexingContext, new RepositoryIndexerListener(indexer, indexingContext, false), true);
                        }

                    } finally {
                        unloadIndexingContext(repo);
                    }
                    RepositoryPreferences.getInstance().setLastIndexUpdate(repo.getId(), new Date());
                    fireChangeIndex();

                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    //to be used from external command line tols like mevenide/netbeans/maven-repo-utils
    public void indexRepo(final String repoId, final File repoDir, final File indexDir) {
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    IndexingContext indexingContext = indexer.addIndexingContext( //
                            repoId, // context id
                            repoId, // repository id
                            repoDir, // repository folder
                            new File(indexDir, repoId), // index folder
                            null, // repositoryUrl
                            null, // index update url
                            NB_INDEX);


                    if (indexingContext == null) {
                        LOGGER.warning("Indexing context chould not be created :"+repoId);//NOI18N
                        return null;
                    }

                    indexer.scan(indexingContext, new RepositoryIndexerListener(indexer, indexingContext, true), true);
                    indexer.removeIndexingContext(indexingContext, false);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void updateIndexWithArtifacts(final RepositoryInfo repo, final Collection<Artifact> artifacts) {

        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    checkIndexAvailability(repo);
                    loadIndexingContext(repo);
                    try {
                        Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                        IndexingContext indexingContext = indexingContexts.get(repo.getId());
                        if (indexingContext == null) {
                            LOGGER.warning("Indexing context chould not be created :" + repo.getId());//NOI18N
                            return null;
                        }

                        for (Artifact artifact : artifacts) {
                            String absolutePath;
                            if (artifact.getFile() != null) {
                                absolutePath = artifact.getFile().getAbsolutePath();
                            } else {
                                //well sort of hack, assume the default repo layout in the repository..
                                absolutePath = repo.getRepositoryPath() + File.separator + repository.pathOf(artifact);
                            }
                            String extension = artifact.getArtifactHandler().getExtension();

                            String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
                            pomPath += "pom"; //NOI18N
                            File pom = new File(pomPath);
                            if (pom.exists()) {
                                indexer.addArtifactToIndex(contextProducer.getArtifactContext(indexingContext, pom), indexingContext);
                            }

                        }
                    } finally {
                        unloadIndexingContext(repo);
                    }
                    fireChangeIndex();
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void deleteArtifactFromIndex(final RepositoryInfo repo, final Artifact artifact) {
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(repo);
                    loadIndexingContext(repo);
                    try {
                        Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                        IndexingContext indexingContext = indexingContexts.get(repo.getId());
                        if (indexingContext == null) {
                            LOGGER.warning("Indexing context chould not be created :"+repo.getId());//NOI18N
                            return null;
                        }

                        String absolutePath;
                        if (artifact.getFile() != null) {
                            absolutePath = artifact.getFile().getAbsolutePath();
                        } else {
                            //well sort of hack, assume the default repo layout in the repository..
                            absolutePath = repo.getRepositoryPath() + File.separator + repository.pathOf(artifact);
                        }
                        String extension = artifact.getArtifactHandler().getExtension();

                        String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
                        pomPath += "pom"; //NOI18N
                        File pom = new File(pomPath);
                        if (pom.exists()) {
                            indexer.deleteArtifactFromIndex(contextProducer.getArtifactContext(indexingContext, pom), indexingContext);
                        }
                    } finally {
                        unloadIndexingContext(repo);
                    }
                    fireChangeIndex();
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public File getDefaultIndexLocation() {
        return new File(repository.getBasedir(), ".index/nexus"); //NOI18N
    }

    public Set<String> getGroups(List<RepositoryInfo> repos) {
        return filterGroupIds( "", repos);
    }

    public Set<String> filterGroupIds(final String prefix, final List<RepositoryInfo> repos) {
        final Set<String> groups = new TreeSet<String>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos); 
                    List<RepositoryInfo> slowCheck = new ArrayList<RepositoryInfo>();
                    for (RepositoryInfo repo : repos) {
                        if (repo.isLocal() || repo.isRemoteDownloadable()) {
                            boolean unload = true;
                            try {
                                IndexingContext context = indexer.getIndexingContexts().get(repo.getId());
                                Set<String> all = indexer.getAllGroups(context);
                                if (all.size() > 0) {
                                    if (prefix.length() == 0) {
                                        groups.addAll(all);
                                    } else {
                                        for (String gr : all) {
                                            if (gr.startsWith(prefix)) {
                                                groups.add(gr);
                                            }
                                        }
                                    }
                                } else {
                                    slowCheck.add(repo);
                                    unload = false;
                                }
                            } finally {
                                if (unload) {
                                    unloadIndexingContext(repo);
                                }
                            }
                        }
                    }
                    
                    final RepositoryInfo[] slowrepos = slowCheck.toArray(new RepositoryInfo[slowCheck.size()]);
                    
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, prefix)), BooleanClause.Occur.MUST));

                        Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GGrouping(),
                                new Comparator<String>() {

                                    public int compare(String o1, String o2) {
                                        return o1.compareTo(o2);
                                    }
                                },
                                bq);
                        groups.addAll(searchGrouped.keySet());
                    } finally {
                        unloadIndexingContext(slowrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return groups;
    }

    public List<NBVersionInfo> getRecords(final String groupId, final String artifactId, final String version, List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        String id = groupId + AbstractIndexCreator.FS + artifactId + AbstractIndexCreator.FS + version + AbstractIndexCreator.FS;
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, id)), BooleanClause.Occur.MUST));
                        Collection<ArtifactInfo> searchGrouped = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR,
                                bq);
                        infos.addAll(convertToNBVersionInfo(searchGrouped));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public Set<String> getArtifacts(final String groupId, List<RepositoryInfo> repos) {
        final Set<String> artifacts = new TreeSet<String>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        String id = groupId + AbstractIndexCreator.FS;
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, id)), BooleanClause.Occur.MUST));
                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        for (ArtifactInfo artifactInfo : search) {
                            artifacts.add(artifactInfo.artifactId);
                        }
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }

    public List<NBVersionInfo> getVersions(final String groupId, final String artifactId, List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        String id = groupId + AbstractIndexCreator.FS + artifactId + AbstractIndexCreator.FS;
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, id)), BooleanClause.Occur.MUST));
                        Collection<ArtifactInfo> searchResult = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        infos.addAll(convertToNBVersionInfo(searchResult));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }
    
    public List<NBVersionInfo> findVersionsByClass(final String className, List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                       
                        
                       
                        Collection<ArtifactInfo> searchResult = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR,
                                indexer.constructQuery(ArtifactInfo.NAMES, (className)));
                        infos.addAll(convertToNBVersionInfo(postProcessClasses(searchResult, className)));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBVersionInfo> findDependencyUsage(final String groupId, final String artifactId, final String version, List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        bq.add(new BooleanClause(new TermQuery(new Term(NB_DEPENDENCY_GROUP, groupId)), BooleanClause.Occur.MUST));
                        bq.add(new BooleanClause(new TermQuery(new Term(NB_DEPENDENCY_ARTIFACT, artifactId)), BooleanClause.Occur.MUST));
                        bq.add(new BooleanClause(new TermQuery(new Term(NB_DEPENDENCY_VERSION, version)), BooleanClause.Occur.MUST));
                        Collection<ArtifactInfo> searchResult = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        infos.addAll(convertToNBVersionInfo(searchResult));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBVersionInfo> findByMD5(final String md5, List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.MD5, (md5))), BooleanClause.Occur.SHOULD));
                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        infos.addAll(convertToNBVersionInfo(search));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBVersionInfo> findArchetypes(List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.PACKAGING, "maven-archetype")), BooleanClause.Occur.MUST)); //NOI18N
                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        infos.addAll(convertToNBVersionInfo(search));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public Set<String> filterPluginArtifactIds(final String groupId, final String prefix, List<RepositoryInfo> repos) {
        final Set<String> artifacts = new TreeSet<String>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        String id = groupId + AbstractIndexCreator.FS + prefix;
                        bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.PACKAGING, "maven-plugin")), BooleanClause.Occur.MUST));
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, id)), BooleanClause.Occur.MUST));
                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        for (ArtifactInfo artifactInfo : search) {
                            artifacts.add(artifactInfo.artifactId);
                        }
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }

    public Set<String> filterPluginGroupIds(final String prefix, List<RepositoryInfo> repos) {
        final Set<String> artifacts = new TreeSet<String>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {

            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.PACKAGING, "maven-plugin")), BooleanClause.Occur.MUST));
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, prefix)), BooleanClause.Occur.MUST));
                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        for (ArtifactInfo artifactInfo : search) {
                            artifacts.add(artifactInfo.groupId);
                        }
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }

    public Set<String> filterArtifactIdForGroupId(final String groupId, final String prefix, List<RepositoryInfo> repos) {
        final Set<String> artifacts = new TreeSet<String>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {

            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        String id = groupId + AbstractIndexCreator.FS + prefix;
                        bq.add(new BooleanClause(new PrefixQuery(new Term(ArtifactInfo.UINFO, id)), BooleanClause.Occur.MUST));

                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        for (ArtifactInfo artifactInfo : search) {
                            artifacts.add(artifactInfo.artifactId);
                        }
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }
    
    public List<NBVersionInfo> find(final List<QueryField> fields, List<RepositoryInfo> repos) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        final RepositoryInfo[] allrepos = repos.toArray(new RepositoryInfo[repos.size()]);
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    checkIndexAvailability(allrepos);
                    loadIndexingContext(allrepos);
                    try {
                        BooleanQuery bq = new BooleanQuery();
                        for (QueryField field : fields) {
                            BooleanClause.Occur occur = field.getOccur() == QueryField.OCCUR_SHOULD ? BooleanClause.Occur.SHOULD : BooleanClause.Occur.MUST;
                            String fieldName = toNexusField(field.getField());
                            if (fieldName != null) {
                                Query q;
                                if (field.getMatch() == QueryField.MATCH_EXACT) {
                                    q = new TermQuery(new Term(fieldName, field.getValue()));
                                } else {
                                    q = new PrefixQuery(new Term(fieldName, field.getValue()));
                                }
                                BooleanClause bc = new BooleanClause(q, occur);
                                bq.add(bc); //NOI18N
                            } else {
                                //TODO when all fields, we need to create separate
                                //queries for each field.
                            }
                        }
                        Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                        infos.addAll(convertToNBVersionInfo(search));
                    } finally {
                        unloadIndexingContext(allrepos);
                    }
                    return null;
                }

            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }
    
    private String toNexusField(String field) {
        if (QueryField.FIELD_ARTIFACTID.equals(field)) {
            return ArtifactInfo.ARTIFACT_ID;
        } else if (QueryField.FIELD_GROUPID.equals(field)) {
            return ArtifactInfo.GROUP_ID;
        } else if (QueryField.FIELD_VERSION.equals(field)) {
            return ArtifactInfo.VERSION;
        } else if (QueryField.FIELD_CLASSES.equals(field)) {
            return ArtifactInfo.NAMES;
        } else if (QueryField.FIELD_NAME.equals(field)) {
            return ArtifactInfo.NAME;
        } else if (QueryField.FIELD_DESCRIPTION.equals(field)) {
            return ArtifactInfo.DESCRIPTION;
        }
        return null;
    }

    public void addIndexChangeListener(ChangeListener cl) {
        synchronized (changeListeners) {
            changeListeners.add(cl);
        }
    }

    public void removeIndexChangeListener(ChangeListener cl) {
        synchronized (changeListeners) {
            changeListeners.remove(cl);
        }
    }
    private List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();

    private void fireChangeIndex() {
        synchronized (changeListeners) {
            for (ChangeListener changeListener : changeListeners) {
                changeListener.stateChanged(new ChangeEvent(this));
            }
        }

    }

    private Collection<ArtifactInfo> postProcessClasses(Collection<ArtifactInfo> artifactInfos, String classname) {
        int patter = Pattern.DOTALL + Pattern.MULTILINE;
        Pattern patt = Pattern.compile(".*/" + classname + "$.*", patter);
        Iterator<ArtifactInfo> it = artifactInfos.iterator();
        while (it.hasNext()) {
            ArtifactInfo ai = it.next();
            Matcher m = patt.matcher(ai.classNames);
            if (!m.matches()) {
                it.remove();
            }
        }
        return artifactInfos;
    }

    private List<NBVersionInfo> convertToNBVersionInfo(Collection<ArtifactInfo> artifactInfos) {
        List<NBVersionInfo> bVersionInfos = new ArrayList<NBVersionInfo>();
        for (ArtifactInfo ai : artifactInfos) {
            NBVersionInfo nbvi = new NBVersionInfo(ai.repository, ai.groupId, ai.artifactId,
                    ai.version, ai.packaging, ai.packaging, ai.name, ai.description, ai.classifier);
            /*Javadoc & Sources*/
            nbvi.setJavadocExists(ai.javadocExists == ArtifactAvailablility.PRESENT);
            nbvi.setSourcesExists(ai.sourcesExists == ArtifactAvailablility.PRESENT);
            nbvi.setSignatureExists(ai.signatureExists == ArtifactAvailablility.PRESENT);
            bVersionInfos.add(nbvi);
        }
        return bVersionInfos;
    }
    
    private static class NbIndexCreator extends AbstractIndexCreator {

        public ArtifactRepository repository = EmbedderFactory.getOnlineEmbedder().getLocalRepository();
        
        public boolean updateArtifactInfo(IndexingContext ctx, Document d, ArtifactInfo artifactInfo) {
            return false;
        }

        public void updateDocument(ArtifactIndexingContext context, Document doc) {
            ArtifactInfo ai = context.getArtifactContext().getArtifactInfo();
            if (ai.classifier != null) {
                //don't process items with classifier
                return;
            }
            try {
                    MavenProject mp = RepositoryUtil.readMavenProject(ai.groupId, ai.artifactId, ai.version, repository);
                    if (mp != null) {
                        List<Dependency> dependencies = mp.getDependencies();
                        for (Dependency d : dependencies) {
                            doc.add(new Field(NB_DEPENDENCY_GROUP, d.getGroupId(), Field.Store.NO, Field.Index.UN_TOKENIZED));
                            doc.add(new Field(NB_DEPENDENCY_ARTIFACT, d.getArtifactId(), Field.Store.NO, Field.Index.UN_TOKENIZED));
                            doc.add(new Field(NB_DEPENDENCY_VERSION, d.getVersion(), Field.Store.NO, Field.Index.UN_TOKENIZED));
                        }
                    }
            } catch (InvalidArtifactRTException ex) {
                ex.printStackTrace();
            }
        }

        public void populateArtifactInfo(ArtifactIndexingContext context) throws IOException {
        }
    }

}
