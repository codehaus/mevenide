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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.embedder.ContainerCustomizer;
import org.apache.maven.embedder.DefaultMavenEmbedRequest;
import org.apache.maven.embedder.MavenEmbedRequest;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.plugin.registry.MavenPluginRegistryBuilder;
import org.apache.maven.project.validation.ModelValidator;
import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.DuplicateRealmException;
import org.codehaus.classworlds.NoSuchRealmException;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInfo;
import org.openide.util.Lookup;

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
            embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
            embedder.setLogger(new NullEmbedderLogger());
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            
            MavenEmbedRequest req = new DefaultMavenEmbedRequest();
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettingsFile(userSettingsPath);
            req.setGlobalSettingsFile(globalSettingsPath);
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
                        
                        desc = plexusContainer.getComponentDescriptor(ModelValidator.ROLE);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbModelValidator");
                    } catch (ComponentRepositoryException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            try {
                embedder.start(req);
            } catch (MavenEmbedderException e) {
                ErrorManager.getDefault().notify(e);
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
            embedder.setClassLoader(EmbedderFactory.class.getClassLoader());
            ClassLoader ldr = Thread.currentThread().getContextClassLoader();
            
            MavenEmbedRequest req = new DefaultMavenEmbedRequest();
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            File userSettingsPath = new File(userLoc, "settings.xml");
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
            req.setUserSettingsFile(userSettingsPath);
            req.setGlobalSettingsFile(globalSettingsPath);
            try {
                embedder.start(req);
            } catch (MavenEmbedderException e) {
                ErrorManager.getDefault().notify(e);
            } finally {
                //http://jira.codehaus.org/browse/PLX-203
                Thread.currentThread().setContextClassLoader(ldr);
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
        
        MavenEmbedder embedder = new MavenEmbedder();
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
            plexusRealm.importFrom(nbRealm.getId(), "META-INF/maven");
            plexusRealm.importFrom(nbRealm.getId(), "META-INF/plexus");
            plexusRealm.importFrom(nbRealm.getId(), "com.jcraft.jsch");
            // from netbeans allow just Lookup and the mevenide bridges
            plexusRealm.importFrom(nbRealm.getId(), "org.openide.util");
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.mevenide.bridges");
            //hack to enable reports, default package is EVIL!
            plexusRealm.addConstituent(rootPackageFolder.toURI().toURL());
        } catch (NoSuchRealmException ex) {
            ex.printStackTrace();
        } catch (DuplicateRealmException ex) {
            ex.printStackTrace();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        embedder.setClassWorld(world);
        embedder.setLogger(logger);
        ClassLoader ldr = Thread.currentThread().getContextClassLoader();
        
        MavenEmbedRequest req = new DefaultMavenEmbedRequest();
        req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private");
        File userLoc = new File(System.getProperty("user.home"), ".m2");
        File userSettingsPath = new File(userLoc, "settings.xml");
        File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false);
        req.setUserSettingsFile(userSettingsPath);
        req.setGlobalSettingsFile(globalSettingsPath);
        try {
            embedder.start(req);
        } catch (MavenEmbedderException e) {
            ErrorManager.getDefault().notify(e);
        } finally {
            //http://jira.codehaus.org/browse/PLX-203
            Thread.currentThread().setContextClassLoader(ldr);
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
