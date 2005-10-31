/* ==========================================================================
 * Copyright 2005 Mevenide Team
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
package org.codehaus.mevenide.netbeans.embedder;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;

/**
 *
 * @author mkleint
 */
public class EmbedderFactory {
    
    /** Creates a new instance of EmbedderFactory */
    public EmbedderFactory() {
    }
    
    public static MavenEmbedder createEmbedder() throws MavenEmbedderException {
        MavenEmbedder embedder = new MavenEmbedder();
        embedder.setOffline(true);
        embedder.setInteractiveMode(false);
        embedder.setCheckLatestPluginVersion(false);
        embedder.setUpdateSnapshots(false);
        embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
        embedder.start();
        return embedder;
    }
}
