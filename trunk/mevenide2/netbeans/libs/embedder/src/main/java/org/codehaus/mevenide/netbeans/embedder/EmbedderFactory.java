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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
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
            embedder.setLocalRepositoryDirectory(new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository()));
            URL components = EmbedderFactory.class.getResource("/org/codehaus/mevenide/netbeans/embedder/components.xml");
            embedder.setEmbedderConfiguration(components);
            embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
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
            embedder.setLocalRepositoryDirectory(new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository()));
            embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
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
        return createExecuteEmbedder(logger, EmbedderFactory.class.getClassLoader());
    }
    
    public static MavenEmbedder createExecuteEmbedder(MavenEmbedderLogger logger, ClassLoader loader) throws MavenEmbedderException {
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setClassLoader(new HackyClassLoader(loader, EmbedderFactory.class.getClassLoader()));
            embedder.setLocalRepositoryDirectory(new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository()));
            embedder.setLogger(logger);
            embedder.start();
            return embedder;
        
    }
    
    private static class HackyClassLoader extends ClassLoader {
        private ClassLoader additionalLoader;
        public HackyClassLoader(ClassLoader additional, ClassLoader embedderModuleLoader) {
            super(embedderModuleLoader);
            additionalLoader = additional;
        }

        public URL getResource(String name) {
            URL retValue;
            if (name.startsWith("META-INF/plexus") || name.startsWith("META-INF/maven") || name.startsWith("org/codehaus/plexus")) {
                return super.getResource(name);
            }
            
            retValue = additionalLoader.getResource(name);
            return retValue;
        }

        public InputStream getResourceAsStream(String name) {
            InputStream retValue;
            if (name.startsWith("META-INF/plexus") || name.startsWith("META-INF/maven") || name.startsWith("org/codehaus/plexus")) {
                return super.getResourceAsStream(name);
            }
            retValue = additionalLoader.getResourceAsStream(name);
            return retValue;
        }

        public Class loadClass(String name) throws ClassNotFoundException {
            Class retValue;
            try {
                retValue = super.loadClass(name);
            } catch (ClassNotFoundException ex) {
                return additionalLoader.loadClass(name);
            }
            return retValue;
        }
        
    }
}
