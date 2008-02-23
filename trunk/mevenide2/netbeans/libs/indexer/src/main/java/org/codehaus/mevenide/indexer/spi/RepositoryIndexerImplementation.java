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
 *
 * @author Milos Kleint
 */
public interface RepositoryIndexerImplementation {
    
    String getType();

    void indexRepo(RepositoryInfo repo);
    
    void updateIndexWithArtifacts(RepositoryInfo repo, Collection<Artifact> artifacts);

    void deleteArtifactFromIndex(RepositoryInfo repo, Artifact artifact);

    Lookup getCapabilityLookup();
}
