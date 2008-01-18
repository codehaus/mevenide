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
import org.codehaus.mevenide.indexer.api.NBVersionInfo;
import org.codehaus.mevenide.indexer.api.NBGroupInfo;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
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
import org.codehaus.plexus.util.FileUtils;
import org.openide.util.Exceptions;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.sonatype.nexus.index.ArtifactInfo;
import org.sonatype.nexus.index.ArtifactInfoGroup;
import org.sonatype.nexus.index.GAGrouping;
import org.sonatype.nexus.index.GAVGrouping;
import org.sonatype.nexus.index.GGrouping;
import org.sonatype.nexus.index.NexusIndexer;
import org.sonatype.nexus.index.context.IndexingContext;
import org.sonatype.nexus.index.creator.IndexCreator;

/**
 *
 * @author Anuradha G
 */
public class NexusRepositoryIndexserImpl implements RepositoryIndexer {

    private ArtifactRepository repository;
    private NexusIndexer indexer;
    /**
     * any reads, writes from/to index shal be done under mutex access.
     */
    public static final Mutex MUTEX = new Mutex();

    public NexusRepositoryIndexserImpl() {
        try {
            PlexusContainer embedder;
            ContainerConfiguration config = new DefaultContainerConfiguration();
            embedder = new DefaultPlexusContainer(config);

            repository = EmbedderFactory.getProjectEmbedder().getLocalRepository();
            indexer = (NexusIndexer) embedder.lookup(NexusIndexer.class);
        } catch (ComponentLookupException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PlexusContainerException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void indexRepo(final String repoId) {
        try {
            MUTEX.writeAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                    IndexingContext indexingContext = indexingContexts.get(repoId);
                    if (indexingContext == null) {

                        final File repoDirectory = new File(repository.getBasedir());
                        final File indexDirectory = new File(getDefaultIndexLocation(), repoId);


                        //Deleting exsisting index
                        FileUtils.deleteDirectory(indexDirectory);



                        RepositoryInfo ri = RepositoryPreferences.getRepositoryInfoById(repoId);
                        List<? extends IndexCreator> indexers = NexusIndexer.FULL_INDEX;
                        indexingContext = indexer.addIndexingContext( //
                                repoId, // context id
                                repoId, // repository id
                                repoDirectory, // repository folder
                                indexDirectory, // index folder
                                ri.getRepositoryUrl(), // repositoryUrl
                                ri.getIndexUpdateUrl(), // index update url
                                indexers);
                    }
                    indexer.scan(indexingContext, new RepositoryIndexerListener(indexer, indexingContext));
                    fireChangeIndex();
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



                    Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                    IndexingContext indexingContext = indexingContexts.get(repoId);
                    if (indexingContext == null) {

                        final File repoDirectory = new File(repository.getBasedir());
                        final File indexDirectory = new File(getDefaultIndexLocation(), repoId);


                        //Deleting exsisting index
                        FileUtils.deleteDirectory(indexDirectory);


                        List<? extends IndexCreator> indexers = NexusIndexer.FULL_INDEX;
                        RepositoryInfo ri = RepositoryPreferences.getRepositoryInfoById(repoId);
                        indexingContext = indexer.addIndexingContext( //
                                repoId, // context id
                                repoId, // repository id
                                repoDirectory, // repository folder
                                indexDirectory, // index folder
                                ri.getRepositoryUrl(), // repositoryUrl
                                ri.getIndexUpdateUrl(), // index update url
                                indexers);
                    }

                    for (Artifact artifact : artifacts) {
                        String absolutePath = artifact.getFile().getAbsolutePath();
                        String extension = artifact.getArtifactHandler().getExtension();

                        String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
                        pomPath += "pom"; //NOI18N
                        File pom = new File(pomPath);

                        indexer.addArtifactToIndex(pom, indexingContext);
                        fireChangeIndex();
                    }
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
                    Map<String, IndexingContext> indexingContexts = indexer.getIndexingContexts();
                    IndexingContext indexingContext = indexingContexts.get(repoId);
                    if (indexingContext == null) {

                        final File repoDirectory = new File(repository.getBasedir());
                        final File indexDirectory = new File(getDefaultIndexLocation(), repoId);


                        //Deleting exsisting index
                        FileUtils.deleteDirectory(indexDirectory);


                        List<? extends IndexCreator> indexers = NexusIndexer.FULL_INDEX;
                        RepositoryInfo ri = RepositoryPreferences.getRepositoryInfoById(repoId);
                        indexingContext = indexer.addIndexingContext( //
                                repoId, // context id
                                repoId, // repository id
                                repoDirectory, // repository folder
                                indexDirectory, // index folder
                                ri.getRepositoryUrl(), // repositoryUrl
                                ri.getIndexUpdateUrl(), // index update url
                                indexers);
                    }

                    String absolutePath = artifact.getFile().getAbsolutePath();
                    String extension = artifact.getArtifactHandler().getExtension();

                    String pomPath = absolutePath.substring(0, absolutePath.length() - extension.length());
                    pomPath += "pom"; //NOI18N
                    File pom = new File(pomPath);

                    indexer.deleteArtifactFromIndex(pom, indexingContext);
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
            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();

                    //bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.GROUP_ID, (prefix) + "*")), BooleanClause.Occur.MUST));

                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    groups.addAll(searchGrouped.keySet());
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
            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    //  bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, (groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.ARTIFACT_ID, (artifactId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.VERSION, (version))), BooleanClause.Occur.MUST));
                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GAVGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {
                        infos.addAll(convertToNBVersionInfo(aig.getArtifactInfos()));
                    }


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
            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    //  bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, (groupId))), BooleanClause.Occur.MUST));
                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GAGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {

                        Set<ArtifactInfo> artifactInfos = aig.getArtifactInfos();
                        for (ArtifactInfo artifactInfo : artifactInfos) {
                            artifacts.add(artifactInfo.artifactId);
                        }

                    }
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
            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    //    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, (groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.ARTIFACT_ID, (artifactId))), BooleanClause.Occur.MUST));
                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GAVGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {
                        infos.addAll(convertToNBVersionInfo(aig.getArtifactInfos()));
                    }
                    return null;
                }
            });
        } catch (MutexException ex) {
            Exceptions.printStackTrace(ex);
        }
        return infos;
    }

    public List<NBGroupInfo> findDependencyUsage(String repoId, String groupId, String artifactId, String version) {
        //need to find methode to do this in nexus
        return new ArrayList<NBGroupInfo>(0);
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
            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    // bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.MD5, (md5))), BooleanClause.Occur.SHOULD));
                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GAVGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {
                        infos.addAll(convertToNBVersionInfo(aig.getArtifactInfos()));
                    }
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
            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {
                    BooleanQuery bq = new BooleanQuery();
                    // bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.PACKAGING, ("maven-archetype"))), BooleanClause.Occur.SHOULD));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.FNAME, ("archetype-metadata.xml"))), BooleanClause.Occur.SHOULD));
                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GAVGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {
                        infos.addAll(convertToNBVersionInfo(aig.getArtifactInfos()));
                    }
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

            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    // bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, (groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.PACKAGING, ("maven-plugin"))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new WildcardQuery(new Term(ArtifactInfo.ARTIFACT_ID, (prefix) + "*")), BooleanClause.Occur.MUST));

                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GAVGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {

                        Set<ArtifactInfo> artifactInfos = aig.getArtifactInfos();
                        for (ArtifactInfo artifactInfo : artifactInfos) {
                            artifacts.add(artifactInfo.artifactId);
                        }

                    }
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

            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    //  bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    
                    bq.add(new BooleanClause(new WildcardQuery(new Term(ArtifactInfo.GROUP_ID, (prefix) + "*")), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.PACKAGING, ("maven-plugin"))), BooleanClause.Occur.MUST));
                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {

                        Set<ArtifactInfo> artifactInfos = aig.getArtifactInfos();
                        for (ArtifactInfo artifactInfo : artifactInfos) {
                            artifacts.add(artifactInfo.artifactId);
                        }

                    }

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

            MUTEX.readAccess(new Mutex.ExceptionAction() {

                public Object run() throws Exception {

                    BooleanQuery bq = new BooleanQuery();
                    // bq.add(new BooleanClause((indexer.constructQuery(ArtifactInfo.REPOSITORY, repoId)), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new TermQuery(new Term(ArtifactInfo.GROUP_ID, (groupId))), BooleanClause.Occur.MUST));
                    bq.add(new BooleanClause(new WildcardQuery(new Term(ArtifactInfo.ARTIFACT_ID, (prefix) + "*")), BooleanClause.Occur.MUST));

                    Map<String, ArtifactInfoGroup> searchGrouped = indexer.searchGrouped(new GGrouping(),
                            new Comparator<String>() {

                                public int compare(String o1, String o2) {
                                    return o1.compareTo(o2);
                                }
                            },
                            bq);
                    for (ArtifactInfoGroup aig : searchGrouped.values()) {

                        Set<ArtifactInfo> artifactInfos = aig.getArtifactInfos();
                        for (ArtifactInfo artifactInfo : artifactInfos) {
                            artifacts.add(artifactInfo.artifactId);
                        }

                    }
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

    private List<NBVersionInfo> convertToNBVersionInfo(Collection<ArtifactInfo> artifactInfos) {
        List<NBVersionInfo> bVersionInfos = new ArrayList<NBVersionInfo>();
        for (ArtifactInfo ai : artifactInfos) {
            bVersionInfos.add(new NBVersionInfo(ai.repository, ai.groupId, ai.artifactId,
                    ai.version, ai.packaging, ai.packaging, ai.name, ai.description, ai.classifier));
        }
        return bVersionInfos;
    }
}
