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
import java.net.URI;

/**
 * Factory for creating repository readers
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class RepositoryReaderFactory {
    
    /** Creates a new instance of RepositoryReaderFactory */
    private RepositoryReaderFactory() {
    }
    
    /**
     * Creates a IRepositoryReader for the local repository.
     */
    public static IRepositoryReader createLocalRepositoryReader(File root) {
        return new LocalRepositoryReader(root);
    }
    
    /**
     * Creates a IRepositoryReader for the remote repository.
     */
    public static IRepositoryReader createRemoteRepositoryReader(URI uri) {
        if (uri.toString().startsWith("http://")) {
            return new HttpRepositoryReader(uri);
        }
        if (uri.toString().startsWith("file://")) {
            return new LocalRepositoryReader(new File(uri));
        }
        return null;
    }

    /**
     * Creates a IRepositoryReader for the remote repository.
     */
    public static IRepositoryReader createRemoteRepositoryReader(URI uri, 
                                    String proxyHost, String proxyPort) {
        if (uri.toString().startsWith("http://")) {
            return new HttpRepositoryReader(uri, proxyHost, proxyPort);
        }
        return null;
    }
    
}
