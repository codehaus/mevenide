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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.mevenide.project.dependency.DefaultDependencyResolver;
import org.mevenide.project.dependency.IDependencyResolver;

/**
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
abstract class AbstractRepositoryReader implements IRepositoryReader {
    /** Creates a new instance of LocalRepositoryReader */
    protected AbstractRepositoryReader() {
    }
    
    protected final RepoPathElement copyElement(RepoPathElement old) {
        RepoPathElement el = new RepoPathElement(this);
        el.setArtifactId(old.getArtifactId());
        el.setGroupId(old.getGroupId());
        el.setType(old.getType());
        el.setVersion(old.getVersion());
        return el;
    }
    
    protected final RepoPathElement levelArtifactCheck(RepoPathElement parent, 
                                                       IDependencyResolver resolver) {
        if (parent.getType().equals(resolver.guessType())
                  && parent.getArtifactId().equals(resolver.guessArtifactId())
                  && resolver.guessVersion() != null) {
            boolean filterOut = true;
            if ("plugin".equals(parent.getType()) && "jar".equals(resolver.guessExtension())) {
                filterOut = false;
            } else if (resolver.guessType().equals(resolver.guessExtension())) {
                //default behaviour, take only *.jar in jars/ etc.
                filterOut = false;
            }
            if (!filterOut) {
                RepoPathElement elem = copyElement(parent);
                elem.setVersion(resolver.guessVersion());
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
                boolean filterOut = true;
                if ("plugin".equals(parent.getType()) && "jar".equals(resolver.guessExtension())) {
                    filterOut = false;
                } else if (resolver.guessType().equals(resolver.guessExtension())) {
                    //default behaviour, take only *.jar in jars/ etc.
                    filterOut = false;
                }
                if (!filterOut) {
                    knownArtifacts.add(resolver.guessArtifactId());
                    RepoPathElement elem = copyElement(parent);
                    elem.setArtifactId(resolver.guessArtifactId());
                    return elem;
                }
            }
        }
        return null;
    }

}
