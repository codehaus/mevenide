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
import org.apache.maven.embedder.DefaultMavenEmbedRequest;
import org.apache.maven.embedder.MavenEmbedRequest;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.plugin.registry.MavenPluginRegistryBuilder;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;

/**
 *
 * @author mkleint
 */
public class EmbedderFactory {
    
    private static MavenEmbedder project;
    private static MavenEmbedder online;
    
    private static MyResolutionListener listener;
    private static SettingsFileListener fileListener = new SettingsFileListener();
            
    /** Creates a new instance of EmbedderFactory */
    public EmbedderFactory() {
    }    
    
    static void setProjectResolutionListener(MyResolutionListener list) {
        listener = list;
    }
    
    public static MyResolutionListener getProjectResolutionListener() {
        // better be right than sorry..
        if (listener == null) throw new IllegalStateException("Cannot retrieve the listener, the project embedder wasn't run yet..");
        return listener;
    }
    
    public synchronized static MavenEmbedder getProjectEmbedder() throws MavenEmbedderException {
        // let there always be just one embedder, otherwise the resolution listener hack stops working..
        if (project == null) {
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setOffline(true);
            embedder.setInteractiveMode(false);
            embedder.setAlignWithUserInstallation(true);
//            embedder.setLocalRepositoryDirectory(new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository()));
            URL components = EmbedderFactory.class.getResource("/org/codehaus/mevenide/netbeans/embedder/components.xml");
            embedder.setEmbedderConfiguration(components);
            embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            
            MavenEmbedRequest req = new DefaultMavenEmbedRequest();
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettings(userSettingsPath);
            req.setGlobalSettings(globalSettingsPath);
            try {
                embedder.start(req);
            } catch (MavenEmbedderException e) {
                e.printStackTrace();
            } finally {
                //http://jira.codehaus.org/browse/PLX-203
                Thread.currentThread().setContextClassLoader(ldr);
            }
            project = embedder;
        }
        return project;
    }
    
    public synchronized static MavenEmbedder getOnlineEmbedder() {
        if (online == null) {
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setOffline(false);
            embedder.setInteractiveMode(false);
            embedder.setAlignWithUserInstallation(true);
//            embedder.setLocalRepositoryDirectory(new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository()));
            embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            
            MavenEmbedRequest req = new DefaultMavenEmbedRequest();
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettings(userSettingsPath);
            req.setGlobalSettings(globalSettingsPath);
            try {
                embedder.start(req);
            } catch (MavenEmbedderException e) {
                e.printStackTrace();
            } finally {
                //http://jira.codehaus.org/browse/PLX-203
                Thread.currentThread().setContextClassLoader(ldr);
            }
            online = embedder;
        }
        return online;
        
    }
    
    public static MavenEmbedder createExecuteEmbedder(MavenEmbedderLogger logger) throws MavenEmbedderException {
        return createExecuteEmbedder(logger, EmbedderFactory.class.getClassLoader());
    }
    
    public static MavenEmbedder createExecuteEmbedder(MavenEmbedderLogger logger, ClassLoader loader) throws MavenEmbedderException {

            // need to have some location for the global plugin registry because otherwise we get NPE
            File globalPluginRegistry = InstalledFileLocator.getDefault().locate("maven2/plugin-registry.xml", null, false);
            System.setProperty(MavenPluginRegistryBuilder.ALT_GLOBAL_PLUGIN_REG_LOCATION, globalPluginRegistry.getAbsolutePath()); 
            
            MavenEmbedder embedder = new MavenEmbedder();
            embedder.setClassLoader(new HackyClassLoader(loader, EmbedderFactory.class.getClassLoader()));
            embedder.setAlignWithUserInstallation(true);
//            embedder.setLocalRepositoryDirectory(new File(MavenSettingsSingleton.getInstance().getSettings().getLocalRepository()));
            embedder.setLogger(logger);
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            
            MavenEmbedRequest req = new DefaultMavenEmbedRequest();
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettings(userSettingsPath);
            req.setGlobalSettings(globalSettingsPath);
            try {
                embedder.start(req);
            } catch (MavenEmbedderException e) {
                e.printStackTrace();
            } finally {
                //http://jira.codehaus.org/browse/PLX-203
                Thread.currentThread().setContextClassLoader(ldr);
            }
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

        public Class findClass(String name) throws ClassNotFoundException {
//            System.out.println("find class=" + name);
            return additionalLoader.loadClass(name);
        }
        
    }
    
    private static class SettingsFileListener extends FileChangeAdapter {
        private FileObject dir;
    
        public SettingsFileListener() {
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            if (!userLoc.exists()) {
                userLoc.mkdirs();
            }
            dir = FileUtil.toFileObject(userLoc);
            if (dir != null) {
                dir.addFileChangeListener(this);
                FileObject settings = dir.getFileObject("settings.xml");
                settings.addFileChangeListener(this);
            }
        }
            

        public void fileDeleted(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                synchronized (EmbedderFactory.class) {
                    online = null;
                    project = null;
                }
            }
        }

        public void fileDataCreated(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                synchronized (EmbedderFactory.class) {
                    online = null;
                    project = null;
                }
            }
        }
        
        public void fileChanged(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                synchronized (EmbedderFactory.class) {
                    online = null;
                    project = null;
                }
            }
        }
        
    }
}
