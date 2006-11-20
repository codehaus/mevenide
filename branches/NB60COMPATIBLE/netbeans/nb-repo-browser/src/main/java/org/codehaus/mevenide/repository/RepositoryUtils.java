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

package org.codehaus.mevenide.repository;

import java.io.File;
import org.apache.maven.archiva.indexer.record.StandardArtifactIndexRecord;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;

/**
 *
 * @author mkleint
 */
public class RepositoryUtils {
    
    /** Creates a new instance of RepositoryUtils */
    private RepositoryUtils() {
    }
    
    public static Artifact createArtifact(StandardArtifactIndexRecord record, ArtifactRepository repo) {
        return createArtifact(record, repo, null);
    }
    
    public static Artifact createJavadocArtifact(StandardArtifactIndexRecord record, ArtifactRepository repo) {
        return createArtifact(record, repo, "javadoc");
    }
    
    private static Artifact createArtifact(StandardArtifactIndexRecord record, ArtifactRepository repo, String classifier) {
        Artifact art;
        
        if (record.getClassifier() != null || classifier != null) {
            art = EmbedderFactory.getOnlineEmbedder().createArtifactWithClassifier(record.getGroupId(),
                    record.getArtifactId(),
                    record.getVersion(),
                    record.getType(),
                    classifier == null ? record.getClassifier() : classifier);
        } else {
            art = EmbedderFactory.getOnlineEmbedder().createArtifact(record.getGroupId(),
                    record.getArtifactId(),
                    record.getVersion(),
                    null,
                    record.getType());
        }
        String localPath = repo.pathOf( art );
        art.setFile( new File( repo.getBasedir(), localPath ) );
        
        return art;
    }
}
