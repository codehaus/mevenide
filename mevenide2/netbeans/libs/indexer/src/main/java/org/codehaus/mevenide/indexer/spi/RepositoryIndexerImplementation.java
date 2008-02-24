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
package org.codehaus.mevenide.indexer.spi;

import java.util.Collection;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.indexer.api.RepositoryInfo;
import org.openide.util.Lookup;

/**
 * Implementation of repository indexer (repository manager). Apart from basic
 * indexing features also serves as provider of various index queries.
 * Implementations are expected to be registered in default Lookup (META-INF/services).
 * 
 * @author Milos Kleint
 */
public interface RepositoryIndexerImplementation {
    
    /**
     * type identifier of the implementation.
     * @return
     */
    String getType();

    /**
     * Index local repository or retrieve remote prepopulated index for local use.
     * @param repo
     */
    void indexRepo(RepositoryInfo repo);
    
    void updateIndexWithArtifacts(RepositoryInfo repo, Collection<Artifact> artifacts);

    void deleteArtifactFromIndex(RepositoryInfo repo, Artifact artifact);

    /**
     * Lookup containing the search queries that are supported by the given implementation.
     * The <code>BaseQueries</code> is required to be supported by all implementations.
     * @return
     */
    Lookup getCapabilityLookup();
}
