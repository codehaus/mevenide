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

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ChangeListener;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.maven.archiva.digest.DigesterException;
import org.apache.maven.archiva.digest.Md5Digester;
import org.apache.maven.archiva.indexer.RepositoryIndexException;
import org.apache.maven.archiva.indexer.RepositoryIndexSearchException;
import org.apache.maven.archiva.indexer.lucene.LuceneQuery;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.archiva.indexer.record.StandardIndexRecordFields;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.openide.util.Exceptions;

/**
 *
 * @author Anuradha G (anuradha@codehaus.org)
 */
public class IndexerUtil {

    static List<GroupInfo> convertToGroupInfos(List<StandardArtifactIndexRecord> indexRecords) {
        List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();

        //tempmaps
        Map<String, GroupInfo> groupMap = new HashMap<String, GroupInfo>();
        Map<String, ArtifactInfo> artifactMap = new HashMap<String, ArtifactInfo>();
        for (StandardArtifactIndexRecord sair : indexRecords) {
            String groupId = sair.getGroupId();
            String artId = sair.getArtifactId();
            String version = sair.getVersion();
            String type = sair.getType();

            GroupInfo ug = groupMap.get(groupId);
            if (ug == null) {
                ug = new GroupInfo(groupId);
                groupInfos.add(ug);
                groupMap.put(groupId, ug);
            }
            ArtifactInfo ua = artifactMap.get(artId);
            if (ua == null) {
                ua = new ArtifactInfo(artId);
                ug.addArtifactInfo(ua);
                artifactMap.put(artId, ua);
            }
            ua.addVersionInfo(new VersionInfo(groupId, artId, version,
                    type, sair.getPackaging(), sair.getProjectName(),sair.getProjectDescription(), sair.getClassifier()));
        }
        return groupInfos;

    }

    static List<VersionInfo> convertToVersionInfos(List<StandardArtifactIndexRecord> indexRecords) {
        List<VersionInfo> versionInfos = new ArrayList<VersionInfo>();
        for (StandardArtifactIndexRecord sair : indexRecords) {

            versionInfos.add(new VersionInfo(sair.getGroupId(), sair.getArtifactId(),
                    sair.getVersion(), sair.getType(), sair.getPackaging(),
                    sair.getProjectName(),sair.getProjectDescription(), sair.getClassifier()));
        }
        return versionInfos;



    }

    public static Artifact createArtifact(VersionInfo info, ArtifactRepository repo) {
        return createArtifact(info, repo, null);
    }

    public static Artifact createJavadocArtifact(VersionInfo info, ArtifactRepository repo) {
        return createArtifact(info, repo, "javadoc"); //NOI18N
    }

    private static Artifact createArtifact(VersionInfo info, ArtifactRepository repo, String classifier) {
        Artifact art;

        if (info.getClassifier() != null || classifier != null) {
            art = EmbedderFactory.getOnlineEmbedder().createArtifactWithClassifier(info.getGroupId(),
                    info.getArtifactId(),
                    info.getVersion(),
                    info.getType(),
                    classifier == null ? info.getClassifier() : classifier);
        } else {
            art = EmbedderFactory.getOnlineEmbedder().createArtifact(info.getGroupId(),
                    info.getArtifactId(),
                    info.getVersion(),
                    null,
                    info.getType());
        }
        String localPath = repo.pathOf(art);
        art.setFile(new File(repo.getBasedir(), localPath));

        return art;
    }

    public static String calculateChecksum(File file) throws NoSuchAlgorithmException {
        try {
            Md5Digester digest = new Md5Digester();

            return digest.calc(file);
        } catch (DigesterException ex) {
            //XXXX ?
            throw new NoSuchAlgorithmException(ex.getLocalizedMessage());
        }

    }

    public static void removeVertion(VersionInfo versionInfo) {
        List<StandardArtifactIndexRecord> indexRecords = new ArrayList<StandardArtifactIndexRecord>();
        BooleanQuery bq = new BooleanQuery();
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.GROUPID_EXACT, versionInfo.getGroupId())), BooleanClause.Occur.MUST));
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.ARTIFACTID_EXACT, versionInfo.getArtifactId())), BooleanClause.Occur.MUST));
        bq.add(new BooleanClause(new TermQuery(new Term(StandardIndexRecordFields.VERSION_EXACT, versionInfo.getVersion())), BooleanClause.Occur.MUST));
        LuceneQuery q = new LuceneQuery(bq);
        try {
            indexRecords.addAll(LocalRepositoryIndexer.getInstance().searchIndex(LocalRepositoryIndexer.getInstance().getDefaultIndex(), q));
        } catch (RepositoryIndexSearchException ex) {
            Exceptions.printStackTrace(ex);
        }
        try {

            LocalRepositoryIndexer.getInstance().getDefaultIndex().deleteRecords(indexRecords);
        } catch (RepositoryIndexException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    public static void updateIndex() {
        LocalRepositoryIndexer ind = LocalRepositoryIndexer.getInstance();
        try {

            ind.updateIndex();
        } catch (RepositoryIndexException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void updateIndexWithArtifacts(Collection artifacts) {
        LocalRepositoryIndexer index = LocalRepositoryIndexer.getInstance();
        try {
            index.updateIndexWithArtifacts(artifacts);
        //TODO add project's own artifact??
        } catch (RepositoryIndexException ex) {
            ex.printStackTrace();
        }
    }
    public static void addIndexChangeListener(ChangeListener cl){
      LocalRepositoryIndexer.getInstance().addChangeListener(cl);
    
    }
}
