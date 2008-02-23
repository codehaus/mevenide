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
package org.codehaus.mevenide.indexer.api;

import java.util.Collection;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.indexer.spi.RepositoryIndexerImplementation;
import org.openide.util.Lookup;

/**
 *
 * @author Anuradha G
 */
public final class RepositoryIndexer {

    public static void indexRepo(RepositoryInfo repo) {
        RepositoryIndexerImplementation impl = findImplementation(repo);
        if (impl == null) {
            return;
        }
        impl.indexRepo(repo);
    }
    
    public static void updateIndexWithArtifacts(RepositoryInfo repo, Collection<Artifact> artifacts) {
        RepositoryIndexerImplementation impl = findImplementation(repo);
        if (impl == null) {
            return;
        }
        impl.updateIndexWithArtifacts(repo, artifacts);
    }

    public static void deleteArtifactFromIndex(RepositoryInfo repo, Artifact artifact) {
        RepositoryIndexerImplementation impl = findImplementation(repo);
        if (impl == null) {
            return;
        }
        impl.deleteArtifactFromIndex(repo, artifact);
    }
    
    static RepositoryIndexerImplementation findImplementation(RepositoryInfo repo) {
        Collection<? extends RepositoryIndexerImplementation> res = Lookup.getDefault().lookupAll(RepositoryIndexerImplementation.class);
        for (RepositoryIndexerImplementation impl : res) {
            if (impl.getType().equals(repo.getType())) {
                return impl;
            }
        }
        Logger.getLogger(RepositoryIndexer.class.getName()).info("Cannot find repository indexer type:" + repo.getType() + " for repository " + repo.getName());
        return null;
    }

}
