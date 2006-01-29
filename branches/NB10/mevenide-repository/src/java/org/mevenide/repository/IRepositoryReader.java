/* ==========================================================================
 * Copyright (c) 2003-2005 Mevenide Team
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

/**
 * Reader of the repository, be it local or remote.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public interface IRepositoryReader {

    /**
     * Retrieves the child elements of the given repository element.
     * @param element the repositry element to read
     * @return the children of the element
     * @throws Exception if anything goes wrong
     */
    RepoPathElement[] readElements(RepoPathElement element) throws Exception;
    
    /**
     * Returns the URI for the repository. 
     * @return the root URI
     */
    URI getRootURI();
}
