/*
 *  Copyright 2008 Anuradha.
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

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.apache.lucene.document.Document;
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.NBGroupInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.indexer.api.NBArtifactInfo;
import org.codehaus.mevenide.indexer.api.RepositoryIndexer;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.sonatype.nexus.index.ArtifactAvailablility;
import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.ArtifactInfoGroup;
import org.sonatype.nexus.index.GGrouping;
import org.sonatype.nexus.index.NexusIndexer;
import org.sonatype.nexus.index.context.ArtifactIndexingContext;
import org.sonatype.nexus.index.context.IndexingContext;
import org.sonatype.nexus.index.creator.AbstractIndexCreator;
import org.sonatype.nexus.index.creator.IndexCreator;
import org.sonatype.nexus.index.creator.JarFileContentsIndexCreator;
import org.sonatype.nexus.index.creator.MinimalArtifactInfoIndexCreator;
import org.sonatype.nexus.index.updater.IndexUpdater;

/**
 *
 * @author Anuradha G
 */
public class NexusRepositoryIndexserImpl implements RepositoryIndexer {

    private ArtifactRepository repository;
    private NexusIndexer indexer;
    private IndexUpdater remoteIndexUpdater;
    //
    private static final String NB_ARTIFACT = "nba"; //NOI18N
    private static final String NB_DEPENDENCY_GROUP = "nbdg"; //NOI18N
    private static final String NB_DEPENDENCY_ARTIFACT = "nbda"; //NOI18N
    private static final String NB_DEPENDENCY_VERTION = "nbdv"; //NOI18N
    public static final List<? extends IndexCreator> NB_INDEX = Arrays.asList(
            new MinimalArtifactInfoIndexCreator(),
            new JarFileContentsIndexCreator(),
            new NbIndexCreator());
    /**
     * any reads, writes from/to index shal be done under mutex access.
     */
    public static final Mutex MUTEX = new Mutex();

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
        } catch (ComponentLookupException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PlexusContainerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void loadIndexingContext(final String repoid) throws MutexException {
        MUTEX.writeAccess(new Mutex.ExceptionAction() {

            public Object run() throws Exception {
                RepositoryInfo info = RepositoryPreferences.getInstance().getRepositoryInfoById(repoid);
                IndexingContext ic = indexer.addIndexingContext( //
                        info.getId(), // context id
                        info.getId(), // repository id
                        info.isRemote() ? null : new File(repository.getBasedir()), // repository folder
                        new File(getDefaultIndexLocation(), info.getId()), // index folder
                        info.getRepositoryUrl(), // repositoryUrl
                        info.getIndexUpdateUrl(), // index update url
                        NB_INDEX);
                return null;
            }
        });

    }

    private void checkIndexAvailability(final String  id) throws MutexException {
        MUTEX.writeAccess(new Mutex.ExceptionAction() {

            public Object run() throws Exception {
                File file = new File(getDefaultIndexLocation(), id);
                if (file == null || file.listFiles().length <= 0) {
                    indexRepo(id);
                }
                return null;
            }
        });
    }

    private void unloadIndexingContext(final String contextId) throws MutexException {
        MUTEX.writeAccess(new Mutex.ExceptionAction() {

            public Object run() throws Exception {
                IndexingContext ic = indexer.getIndexingContexts().get(contextId);
                if (ic != null) {
                    indexer.removeIndexingContext(ic, false);
                }
                return null;
            }
        });

    }

    public void indexRepo(final String repoId) {
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    loadIndexingContext(repoId);
                    RepositoryInfo info = RepositoryPreferences.getInstance().getRepositoryInfoById(repoId);
                    Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                    IndexingContext indexingContext = indexingContexts.get(repoId);
                    if (info == null || indexingContext == null) {
                        //do nothing
                        return null;
                    }
                    if (info.isRemote()) {
                        RemoteIndexTransferListener listener = new RemoteIndexTransferListener(info);
                        try {
                            remoteIndexUpdater.fetchAndUpdateIndex(indexingContext, listener);

                        } catch (IOException iOException) {
                            //handle index not found
                            listener.transferCompleted(null);
                        }

                    } else {
                        indexer.scan(indexingContext, new RepositoryIndexerListener(indexer, indexingContext));
                        unloadIndexingContext(repoId);
                    }
                    fireChangeIndex();

                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

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
                        //do nothing
                        return null;
                    }

                    indexer.scan(indexingContext, new RepositoryIndexerListener(indexer, indexingContext));
                    indexer.removeIndexingContext(indexingContext, false);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void updateIndexWithArtifacts(final String repoId, final Collection<Artifact> artifacts) {

        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {


                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                    IndexingContext indexingContext = indexingContexts.get(repoId);
                    if (indexingContext == null) {
                        //do nothing
                        return null;
                    }


                    for (Artifact artifact : artifacts) {
                        String absolutePath = artifact.getFile().getAbsolutePath();
                        String extension = artifact.getArtifactHandler().getExtension();

                        String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
                        pomPath += "pom"; //NOI18N
                        File pom = new File(pomPath);

                        indexer.addArtifactToIndex(pom, indexingContext);

                    }
                    unloadIndexingContext(repoId);
                    fireChangeIndex();
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public void deleteArtifactFromIndex(final String repoId, final Artifact artifact) {
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                    IndexingContext indexingContext = indexingContexts.get(repoId);
                    if (indexingContext == null) {
                        //do nothing
                        return null;
                    }


                    String absolutePath = artifact.getFile().getAbsolutePath();
                    String extension = artifact.getArtifactHandler().getExtension();

                    String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
                    pomPath += "pom"; //NOI18N
                    File pom = new File(pomPath);

                    indexer.deleteArtifactFromIndex(pom, indexingContext);
                    unloadIndexingContext(repoId);
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

    public Set<String> getGroups(String repoId) {
        return filterGroupIds(repoId, "");
    }

    public Set<String> filterGroupIds(final String repoId, final String prefix) {
        final Set<String> groups = new TreeSet<String>();

        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    BooleanQuery bq = new BooleanQuery();
                    bq.add(new BooleanClause(new WildcardQuery(new Term(ArtifactInfo.GROUP_ID, QueryParser.escape(prefix) + "*")), BooleanClause.Occur.MUST));

                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    groups.addAll(searchGrouped.keySet());
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return groups;
    }

    public List<NBVersionInfo> getRecords(final String repoId, final String groupId, final String artifactId, final String version) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, QueryParser.escape(groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.ARTIFACT_ID, QueryParser.escape(artifactId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.VERSION, QueryParser.escape(version))), BooleanClause.Occur.MUST));
                    Collection<ArtifactInfo> searchGrouped = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR,
                            bq);
                    infos.addAll(convertToNBVersionInfo(searchGrouped));
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public Set<String> getArtifacts(final String repoId, final String groupId) {
        final Set<String> artifacts = new TreeSet<String>();
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, QueryParser.escape(groupId))), BooleanClause.Occur.MUST));
                    Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    for (ArtifactInfo artifactInfo : search) {
                        artifacts.add(artifactInfo.artifactId);
                    }
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }

    public List<NBVersionInfo> getVersions(final String repoId, final String groupId, final String artifactId) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, groupId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(NB_ARTIFACT, artifactId)), BooleanClause.Occur.MUST));
                    Collection<ArtifactInfo> searchResult = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    infos.addAll(convertToNBVersionInfo(searchResult));
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBGroupInfo> findDependencyUsage(final String repoId, final String groupId, final String artifactId, final String version) {
        final List<NBGroupInfo> infos = new ArrayList<NBGroupInfo>();
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause(new TermQuery(new Term(NB_DEPENDENCY_GROUP, groupId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(NB_DEPENDENCY_ARTIFACT, artifactId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(NB_DEPENDENCY_VERTION, version)), BooleanClause.Occur.MUST));
                    Collection<ArtifactInfo> searchResult = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    infos.addAll(convertToNBGroupInfo(searchResult));
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBVersionInfo> findByMD5(String repoId, File file) {
        try {
            String calculateChecksum = RepositoryUtil.calculateChecksum(file);

            return findByMD5(repoId, calculateChecksum);
        } catch (NoSuchAlgorithmException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return new ArrayList<NBVersionInfo>(0);
    }

    public List<NBVersionInfo> findByMD5(final String repoId, final String md5) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.MD5, (md5))), BooleanClause.Occur.SHOULD));
                    Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    infos.addAll(convertToNBVersionInfo(search));
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBVersionInfo> retrievePossibleArchetypes(final String repoId) {
        final List<NBVersionInfo> infos = new ArrayList<NBVersionInfo>();
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause((new TermQuery(new Term(ArtifactInfo.PACKAGING, "maven-archetype"))), BooleanClause.Occur.MUST));
//doesn't list files in index                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.FNAME, ("archetype-metadata.xml"))), BooleanClause.Occur.SHOULD));
                    Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    infos.addAll(convertToNBVersionInfo(search));
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public Set<String> filterPluginArtifactIds(final String repoId, final String groupId, final String prefix) {
        final Set<String> artifacts = new TreeSet<String>();
        try {

            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.PACKAGING, ("maven-plugin"))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, QueryParser.escape(groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new WildcardQuery(new Term(NB_ARTIFACT, QueryParser.escape(prefix) + "*")), BooleanClause.Occur.MUST));

                    Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    for (ArtifactInfo artifactInfo : search) {
                        artifacts.add(artifactInfo.artifactId);
                    }
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }

    public Set<String> filterPluginGroupIds(final String repoId, final String prefix) {
        final Set<String> artifacts = new TreeSet<String>();
        try {

            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.PACKAGING, ("maven-plugin"))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new WildcardQuery(new Term(ArtifactInfo.GROUP_ID, QueryParser.escape(prefix) + "*")), BooleanClause.Occur.MUST));
                    Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    for (ArtifactInfo artifactInfo : search) {
                        artifacts.add(artifactInfo.artifactId);
                    }
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
    }

    public Set<String> filterArtifactIdForGroupId(final String repoId, final String groupId, final String prefix) {
        final Set<String> artifacts = new TreeSet<String>();
        try {

            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    loadIndexingContext(repoId);
                    checkIndexAvailability(repoId);
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, QueryParser.escape(groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new WildcardQuery(new Term(NB_ARTIFACT, QueryParser.escape(prefix) + "*")), BooleanClause.Occur.MUST));

                    Collection<ArtifactInfo> search = indexer.searchFlat(ArtifactInfo.VERSION_COMPARATOR, bq);
                    for (ArtifactInfo artifactInfo : search) {
                        artifacts.add(artifactInfo.artifactId);
                    }
                    unloadIndexingContext(repoId);
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return artifacts;
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

    private List<NBGroupInfo> convertToNBGroupInfo(Collection<ArtifactInfo> artifactInfos) {
        List<NBGroupInfo> groupInfos = new ArrayList<NBGroupInfo>();

        //tempmaps
        Map<String, NBGroupInfo> groupMap = new HashMap<String, NBGroupInfo>();
        Map<String, NBArtifactInfo> artifactMap = new HashMap<String, NBArtifactInfo>();
        for (ArtifactInfo ai : artifactInfos) {
            String groupId = ai.groupId;
            String artId = ai.artifactId;


            NBGroupInfo ug = groupMap.get(groupId);
            if (ug == null) {
                ug = new NBGroupInfo(groupId);
                groupInfos.add(ug);
                groupMap.put(groupId, ug);
            }
            NBArtifactInfo ua = artifactMap.get(artId);
            if (ua == null) {
                ua = new NBArtifactInfo(artId);
                ug.addArtifactInfo(ua);
                artifactMap.put(artId, ua);
            }
            NBVersionInfo nbvi = new NBVersionInfo(ai.repository, ai.groupId, ai.artifactId,
                    ai.version, ai.packaging, ai.packaging, ai.name, ai.description, ai.classifier);
            /*Javadoc & Sources*/
            nbvi.setJavadocExists(ai.javadocExists == ArtifactAvailablility.PRESENT);
            nbvi.setSourcesExists(ai.sourcesExists == ArtifactAvailablility.PRESENT);
            nbvi.setSignatureExists(ai.signatureExists == ArtifactAvailablility.PRESENT);
        }
        return groupInfos;

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

        public boolean updateArtifactInfo(IndexingContext ctx, Document d, ArtifactInfo artifactInfo) {
            return false;
        }

        public void updateDocument(ArtifactIndexingContext context, Document doc) throws IOException {
            ArtifactInfo ai = context.getArtifactContext().getArtifactInfo();
            doc.add(new Field(NB_ARTIFACT, ai.artifactId, Field.Store.NO, Field.Index.UN_TOKENIZED));
            NBVersionInfo nbvi = new NBVersionInfo(ai.repository, ai.groupId, ai.artifactId,
                    ai.version, ai.packaging, ai.packaging, ai.name, ai.description, ai.classifier);
            Artifact artifact = RepositoryUtil.createArtifact(nbvi);
            if (artifact != null) {
                MavenProject mp = RepositoryUtil.readMavenProject(artifact);
                if (mp != null) {
                    List<Dependency> dependencies = mp.getDependencies();
                    for (Dependency d : dependencies) {
                        doc.add(new Field(NB_DEPENDENCY_GROUP, d.getGroupId(), Field.Store.NO, Field.Index.UN_TOKENIZED));
                        doc.add(new Field(NB_DEPENDENCY_ARTIFACT, d.getArtifactId(), Field.Store.NO, Field.Index.UN_TOKENIZED));
                        doc.add(new Field(NB_DEPENDENCY_VERTION, d.getVersion(), Field.Store.NO, Field.Index.UN_TOKENIZED));

                    }

                }
            }
        }
    }
}
