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

/**
 * Reader for remote repository
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
class HttpRepositoryReader implements IRepositoryReader {
    
    /** Creates a new instance of HttpRepositoryReader */
    public HttpRepositoryReader() {
    }

    public RepoPathElement[] readElements(RepoPathElement element) throws Exception {
        return new RepoPathElement[0];
    }
    
}
