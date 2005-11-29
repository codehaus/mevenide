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
import org.apache.maven.embedder.MavenEmbedderLogger;

/**
 *
 * @author mkleint
 */
public class EmbedderFactory {
    
    private static MavenEmbedder project;
    private static MavenEmbedder online;
    /** Creates a new instance of EmbedderFactory */
    public EmbedderFactory() {
    }
    
    public static MavenEmbedder getProjectEmbedder() throws MavenEmbedderException {
        if (project == null) {
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setOffline(true);
            embedder.setInteractiveMode(false);
            embedder.setCheckLatestPluginVersion(false);
            embedder.setUpdateSnapshots(false);
            embedder.setClassLoader(new TweakingClassLoader(EmbedderFactory.class.getClassLoader()));
            try {
                embedder.start();
            } catch (MavenEmbedderException e) {
                e.printStackTrace();
            }
            project = embedder;
        }
        return project;
    }
    
    public static MavenEmbedder getOnlineEmbedder() {
        if (online == null) {
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setOffline(false);
            embedder.setInteractiveMode(false);
            embedder.setClassLoader(new TweakingClassLoader(EmbedderFactory.class.getClassLoader()));
            try {
                embedder.start();
            } catch (MavenEmbedderException e) {
                e.printStackTrace();
            }
            online = embedder;
        }
        return online;
        
    }
    
    public static MavenEmbedder createExecuteEmbedder(MavenEmbedderLogger logger) throws MavenEmbedderException {
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setClassLoader(new TweakingClassLoader(EmbedderFactory.class.getClassLoader()));
            embedder.setLogger(logger);
            embedder.start();
            return embedder;
    }
}
