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
import java.net.MalformedURLException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.embedder.Configuration;
import org.apache.maven.embedder.ContainerCustomizer;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.plugin.registry.MavenPluginRegistryBuilder;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Lookup;

/**
 *
 * @author mkleint
 */
public class EmbedderFactory {
    
    private static MavenEmbedder project;
    private static MavenEmbedder online;
    
    private static SettingsFileListener fileListener = new SettingsFileListener();
    
    /** Creates a new instance of EmbedderFactory */
    public EmbedderFactory() {
    }
    
    /**
     * embedder seems to cache some values..
     */ 
    public synchronized static void resetProjectEmbedder() {
        project = null;
    }
    
    public synchronized static MavenEmbedder getProjectEmbedder() throws MavenEmbedderException {
        if (project == null) {
            Configuration req = new DefaultConfiguration();
            req.setClassLoader(EmbedderFactory.class.getClassLoader());
            //TODO remove explicit activation
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettingsFile(userSettingsPath);
            req.setGlobalSettingsFile(globalSettingsPath);
            req.setMavenEmbedderLogger(new NullEmbedderLogger());
            req.setConfigurationCustomizer(new ContainerCustomizer() {
                public void customize(PlexusContainer plexusContainer) {
                    try {
                        ComponentDescriptor desc = plexusContainer.getComponentDescriptor(ArtifactFactory.ROLE);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbArtifactFactory");
                        
                        desc = plexusContainer.getComponentDescriptor(ResolutionListener.ROLE);
                        if (desc == null) {
                            desc = new ComponentDescriptor();
                            desc.setRole(ResolutionListener.ROLE);
                            plexusContainer.addComponentDescriptor(desc);
                        }
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.MyResolutionListener");
                        
                        desc = plexusContainer.getComponentDescriptor(ArtifactResolver.ROLE);
                        ComponentRequirement requirement = new ComponentRequirement();
                        requirement.setRole(ResolutionListener.ROLE);
                        desc.addRequirement(requirement);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbArtifactResolver");
                        
                        desc = plexusContainer.getComponentDescriptor(WagonManager.ROLE);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbWagonManager");
                        
                    } catch (ComponentRepositoryException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            MavenEmbedder embedder = null;
            try {
                embedder = new MavenEmbedder(req);
            } catch (MavenEmbedderException e) {
                ErrorManager.getDefault().notify(e);
            }
            project = embedder;
        }
        return project;
    }
    
    public synchronized static MavenEmbedder getOnlineEmbedder() {
        if (online == null) {
            Configuration req = new DefaultConfiguration();
            req.setClassLoader(EmbedderFactory.class.getClassLoader());
            //TODO remove explicit activation
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettingsFile(userSettingsPath);
            req.setGlobalSettingsFile(globalSettingsPath);
            req.setMavenEmbedderLogger(new NullEmbedderLogger());
            MavenEmbedder embedder = null;
            try {
                embedder = new MavenEmbedder(req);
            } catch (MavenEmbedderException e) {
                ErrorManager.getDefault().notify(e);
            }
            online = embedder;
        }
        return online;
        
    }
    
    public static MavenEmbedder createExecuteEmbedder(MavenEmbedderLogger logger) throws MavenEmbedderException {
        ClassLoader loader = (ClassLoader) Lookup.getDefault().lookup(ClassLoader.class);
        
        // need to have some location for the global plugin registry because otherwise we get NPE
        File globalPluginRegistry = InstalledFileLocator.getDefault().locate("maven2/plugin-registry.xml", null, false);
        System.setProperty(MavenPluginRegistryBuilder.ALT_GLOBAL_PLUGIN_REG_LOCATION, globalPluginRegistry.getAbsolutePath());
        
        ClassWorld world = new ClassWorld();
        File rootPackageFolder = FileUtil.normalizeFile(InstalledFileLocator.getDefault().locate("maven2/rootpackage", null, false));
        // kind of separation layer between the netbeans classloading world and maven classworld.
        try {
            ClassRealm nbRealm = world.newRealm("netbeans", loader);
            ClassRealm plexusRealm = world.newRealm("plexus.core");
            // these are all packages that are from the embedder jar..
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.doxia");
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.plexus");
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.classworlds");
            plexusRealm.importFrom(nbRealm.getId(), "org.apache.maven");
            plexusRealm.importFrom(nbRealm.getId(), "org.apache.commons.cli");
            plexusRealm.importFrom(nbRealm.getId(), "META-INF/maven");
            plexusRealm.importFrom(nbRealm.getId(), "META-INF/plexus");
            plexusRealm.importFrom(nbRealm.getId(), "com.jcraft.jsch");
            plexusRealm.importFrom(nbRealm.getId(), "org.jdom");
            plexusRealm.importFrom(nbRealm.getId(), "org.xml.sax");
            plexusRealm.importFrom(nbRealm.getId(), "org.w3c.dom");
            plexusRealm.importFrom(nbRealm.getId(), "org.w3c.tidy");
            
            // from netbeans allow just Lookup and the mevenide bridges
            plexusRealm.importFrom(nbRealm.getId(), "org.openide.util");
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.mevenide.bridges");
            //have custom lifecycle executor to collect all projects in reactor..
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.mevenide.netbeans.embedder.exec");
            //hack to enable reports, default package is EVIL!
            plexusRealm.addURL(rootPackageFolder.toURI().toURL());
        } catch (NoSuchRealmException ex) {
            ex.printStackTrace();
        } catch (DuplicateRealmException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        
        Configuration req = new DefaultConfiguration();
        req.setClassWorld(world);
        //TODO remove explicit activation
        req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
        File userLoc = new File(System.getProperty("user.home"), ".m2");
        File userSettingsPath = new File(userLoc, "settings.xml");
        File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
        req.setUserSettingsFile(userSettingsPath);
        req.setGlobalSettingsFile(globalSettingsPath);
        
        MavenEmbedder embedder = null;
        try {
            embedder = new MavenEmbedder(req);
            embedder.setLogger(logger);

        } catch (MavenEmbedderException e) {
            ErrorManager.getDefault().notify(e);
        }
        return embedder;
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
                if (settings != null) {
                    settings.addFileChangeListener(this);
                }
            }
        }
        
        
        public void fileDeleted(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                fe.getFile().removeFileChangeListener(this);
                synchronized (EmbedderFactory.class) {
                    online = null;
                    project = null;
                }
            }
        }
        
        public void fileDataCreated(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                fe.getFile().addFileChangeListener(this);
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
