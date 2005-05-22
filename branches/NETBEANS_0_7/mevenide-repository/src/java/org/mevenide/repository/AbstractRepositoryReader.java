/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.mevenide.repository;
import java.net.URI;
import java.util.Collection;
import org.mevenide.project.dependency.IDependencyResolver;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
abstract class AbstractRepositoryReader implements IRepositoryReader {
    /** Creates a new instance of LocalRepositoryReader */
    private URI rootURI;
    protected AbstractRepositoryReader(URI root) {
        rootURI = root;
    }
    
    public URI getRootURI() {
        return rootURI;
    }
    
    protected final RepoPathElement newChild(RepoPathElement parent) {
        RepoPathElement el = new RepoPathElement(this, parent);
        el.setArtifactId(parent.getArtifactId());
        el.setGroupId(parent.getGroupId());
        el.setType(parent.getType());
        el.setVersion(parent.getVersion());
        return el;
    }
    
    protected final RepoPathElement levelArtifactCheck(RepoPathElement parent, 
                                                       IDependencyResolver resolver) {
        if (parent.getType().equals(resolver.guessType())
                  && parent.getArtifactId().equals(resolver.guessArtifactId())
                  && resolver.guessVersion() != null) {
            boolean keep = false;
            if ("plugin".equals(parent.getType()) && "jar".equals(resolver.guessExtension())) {
                keep = true;
            } else if ("distribution".equals(parent.getType()) && "zip".equals(resolver.guessExtension())) {
                keep = true;
            } else if (resolver.guessType().equals(resolver.guessExtension())) {
                //default behaviour, take only *.jar in jars/ etc.
                keep = true;
            }
            if (keep) {
                RepoPathElement elem = newChild(parent);
                elem.setVersion(resolver.guessVersion());
                elem.setExtension(resolver.guessExtension());
                return elem;
            }
        }
        return null;
    }
    
    protected final RepoPathElement levelTypeCheck(RepoPathElement parent, 
                                                   IDependencyResolver resolver, 
                                                   Collection knownArtifacts) {
        if (parent.getType().equals(resolver.guessType())
                    && resolver.guessArtifactId() != null) {
            if (!knownArtifacts.contains(resolver.guessArtifactId())) {
                boolean keep = false;
                if ("plugin".equals(parent.getType()) && "jar".equals(resolver.guessExtension())) {
                    keep = true;
                } else if ("distribution".equals(parent.getType()) 
                    && ("zip".equals(resolver.guessExtension()) || "tar.gz".equals(resolver.guessExtension()))) {
                    keep = true;
                } else if (resolver.guessType().equals(resolver.guessExtension())) {
                    //default behaviour, take only *.jar in jars/ etc.
                    keep = true;
                }
                if (keep) {
                    knownArtifacts.add(resolver.guessArtifactId());
                    RepoPathElement elem = newChild(parent);
                    elem.setArtifactId(resolver.guessArtifactId());
                    elem.setExtension(resolver.guessExtension());
                    return elem;
                }
            }
        }
        return null;
    }

}
