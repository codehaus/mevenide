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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.maven.artifact.UnknownRepositoryLayoutException;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.ResolutionListener;
import org.apache.maven.embedder.Configuration;
import org.apache.maven.embedder.ContainerCustomizer;
import org.apache.maven.embedder.DefaultConfiguration;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.lifecycle.LifecycleExecutor;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.profiles.activation.DefaultProfileActivationContext;
import org.apache.maven.profiles.activation.ProfileActivationContext;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.build.model.DefaultModelLineage;
import org.apache.maven.project.build.model.ModelLineage;
import org.apache.maven.project.build.model.ModelLineageBuilder;
import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.apache.maven.wagon.events.TransferListener;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.ComponentRequirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.component.repository.exception.ComponentRepositoryException;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfigurationException;
import org.codehaus.plexus.configuration.xml.XmlPlexusConfiguration;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 *  Factory for creating MavenEmbedder instances for various purposes.
 * 
 * @author mkleint
 */
public final class EmbedderFactory {
    
    private static ThreadLocal<MavenEmbedder> projectTL = new ThreadLocal<MavenEmbedder>();
    private static boolean wasReset = true;
    private static MavenEmbedder project;
    private static MavenEmbedder online;
    
    private static long userSettingTimeStamp = 0L;
    private static boolean settingsCheckPassed = false;
    
    private static SettingsFileListener fileListener = new SettingsFileListener();
    
    private static Logger LOG = Logger.getLogger(EmbedderFactory.class.getName());

    //#96919
    private static void checkUserSettingsTimeStamp(File userSettingsPath) {
        long userFileStamp = userSettingsPath.lastModified();
        if (userFileStamp != userSettingTimeStamp) {
            userSettingTimeStamp = userFileStamp;
            if (!userSettingsPath.exists()) {
                settingsCheckPassed = true;
            } else {
                Reader rr = null;
                try {
                    //check if settings.xml can be read.
                    SettingsXpp3Reader reader = new SettingsXpp3Reader();
                    rr = new InputStreamReader(new FileInputStream(userSettingsPath));
                    reader.read(rr);
                    settingsCheckPassed = true;
                } catch (IOException ex) {
                    Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Maven Settings file cannot be properly parsed. Until it's fixed, it will be ignored."));
                    settingsCheckPassed = false;
                } catch (XmlPullParserException ex) {
                    Exceptions.printStackTrace(Exceptions.attachMessage(ex, "Maven Settings file cannot be properly parsed. Until it's fixed, it will be ignored."));
                    settingsCheckPassed = false;
                } finally {
                    IOUtil.close(rr);
                }
            }
        }
    }
    
    /** Creates a new instance of EmbedderFactory */
    private EmbedderFactory() {
    }
    
    /**
     * embedder seems to cache some values..
     */ 
    public synchronized static void resetProjectEmbedder() {
        project = null;
        wasReset = true;
    }
    
    public synchronized static MavenEmbedder getProjectEmbedder() /*throws MavenEmbedderException*/ {
        MavenEmbedder projectEmbedder;
        //since yarda's introduction of lazy project loading, mutliple threads use the embedder at startup at once.
        // that breaks embedder/plexus in many ways..
        // introducing ThreadLocal project embedder instance to workaround the problem
        // 
        if (!wasReset) {
            projectEmbedder = projectTL.get();
        } else {
            projectEmbedder = project;
            projectTL.remove();
        }
        if (projectEmbedder == null) {
            Configuration req = new DefaultConfiguration();
            req.setClassLoader(EmbedderFactory.class.getClassLoader());
            //TODO remove explicit activation
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private"); //NOI18N
            File userLoc = new File(System.getProperty("user.home"), ".m2"); //NOI18N
            File userSettingsPath = new File(userLoc, "settings.xml"); //NOI18N
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false); //NOI18N
            //TODO replace by MavenEmbedder.validateConfiguration()
            checkUserSettingsTimeStamp(userSettingsPath);
            
            if (settingsCheckPassed) {
                req.setUserSettingsFile(userSettingsPath);
            } else {
                LOG.info("Maven settings file is corrupted. See http://www.netbeans.org/issues/show_bug.cgi?id=96919" ); //NOI18N
                req.setUserSettingsFile(globalSettingsPath);
            }
            
            req.setGlobalSettingsFile(globalSettingsPath);
            req.setMavenEmbedderLogger(new NullEmbedderLogger());
            req.setConfigurationCustomizer(new ContainerCustomizer() {
                public void customize(PlexusContainer plexusContainer) {
                    try {
                        ComponentDescriptor desc = plexusContainer.getComponentDescriptor(ArtifactFactory.ROLE);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbArtifactFactory"); //NOI18N
                        
                        desc = plexusContainer.getComponentDescriptor(ResolutionListener.ROLE);
                        if (desc == null) {
                            desc = new ComponentDescriptor();
                            desc.setRole(ResolutionListener.ROLE);
                            plexusContainer.addComponentDescriptor(desc);
                        }
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.MyResolutionListener"); //NOI18N
                        
                        desc = plexusContainer.getComponentDescriptor(ArtifactResolver.ROLE);
                        ComponentRequirement requirement = new ComponentRequirement();
                        requirement.setRole(ResolutionListener.ROLE);
                        desc.addRequirement(requirement);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbArtifactResolver"); //NOI18N
                        
                        desc = plexusContainer.getComponentDescriptor(WagonManager.ROLE);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.NbWagonManager"); //NOI18N
                        
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
            if (!wasReset) {
                projectTL.set(embedder);
            } else {
                project = embedder;
            }
            projectEmbedder = embedder;
        }
        return projectEmbedder;
    }
    
    public synchronized static MavenEmbedder getOnlineEmbedder() {
        if (online == null) {
            Configuration req = new DefaultConfiguration();
            req.setClassLoader(EmbedderFactory.class.getClassLoader());
            //TODO remove explicit activation
            req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private"); //NOI18N
            File userLoc = new File(System.getProperty("user.home"), ".m2"); //NOI18N
            File userSettingsPath = new File(userLoc, "settings.xml"); //NOI18N
            File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false); //NOI18N
            //TODO replace by MavenEmbedder.validateConfiguration()
            checkUserSettingsTimeStamp(userSettingsPath);
            if (settingsCheckPassed) {
                req.setUserSettingsFile(userSettingsPath);
            } else {
                LOG.info("Maven settings file is corrupted. See http://www.netbeans.org/issues/show_bug.cgi?id=96919" ); //NOI18N
                req.setUserSettingsFile(globalSettingsPath);
            }
            req.setGlobalSettingsFile(globalSettingsPath);
            req.setConfigurationCustomizer(new ContainerCustomizer() {

                public void customize(PlexusContainer plexusContainer) {
                    try {
                        ComponentDescriptor desc = new ComponentDescriptor();
                        desc.setRole(TransferListener.class.getName());
                        plexusContainer.addComponentDescriptor(desc);
                        desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener"); //NOI18N
                        desc = plexusContainer.getComponentDescriptor(WagonManager.ROLE);
                        ComponentRequirement requirement = new ComponentRequirement();
                        requirement.setRole(TransferListener.class.getName());
                        desc.addRequirement(requirement);
                    }
                    catch (ComponentRepositoryException ex) {
                        ex.printStackTrace();      
                    }
                }
            });
            
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
    
    public static MavenEmbedder createExecuteEmbedder(MavenEmbedderLogger logger) /*throws MavenEmbedderException*/ {
        ClassLoader loader = Lookup.getDefault().lookup(ClassLoader.class);
        
        // need to have some location for the global plugin registry because otherwise we get NPE
//        File globalPluginRegistry = InstalledFileLocator.getDefault().locate("maven2/plugin-registry.xml", null, false); //NOI18N
//        System.setProperty(MavenPluginRegistryBuilder.ALT_GLOBAL_PLUGIN_REG_LOCATION, globalPluginRegistry.getAbsolutePath());
        
        ClassWorld world = new ClassWorld();
        File rootPackageFolder = FileUtil.normalizeFile(InstalledFileLocator.getDefault().locate("maven2/rootpackage", null, false)); //NOI18N
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
            plexusRealm.importFrom(nbRealm.getId(), "org.aspectj");
            plexusRealm.importFrom(nbRealm.getId(), "org.jdom");
            plexusRealm.importFrom(nbRealm.getId(), "org.w3c.dom");
            plexusRealm.importFrom(nbRealm.getId(), "org.w3c.tidy");
            plexusRealm.importFrom(nbRealm.getId(), "org.xml.sax");
            plexusRealm.importFrom(nbRealm.getId(), "hidden.org.codehaus.plexus");
            
            // from netbeans allow just Lookup and the mevenide bridges
            plexusRealm.importFrom(nbRealm.getId(), "org.openide.util");
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.mevenide.bridges");
            //have custom lifecycle executor to collect all projects in reactor..
            plexusRealm.importFrom(nbRealm.getId(), "org.codehaus.mevenide.netbeans.embedder.exec"); //NOI18N
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
        req.setMavenEmbedderLogger(logger);
        
        //TODO remove explicit activation
        req.addActiveProfile("netbeans-public").addActiveProfile("netbeans-private"); //NOI18N
        File userLoc = new File(System.getProperty("user.home"), ".m2"); //NOI18N
        File userSettingsPath = new File(userLoc, "settings.xml"); //NOI18N
        File globalSettingsPath = InstalledFileLocator.getDefault().locate("maven2/settings.xml", null, false); //NOI18N
        checkUserSettingsTimeStamp(userSettingsPath);
        if (settingsCheckPassed) {
            req.setUserSettingsFile(userSettingsPath);
        } else {
            LOG.info("Maven settings file is corrupted. See http://www.netbeans.org/issues/show_bug.cgi?id=96919" ); //NOI18N
            req.setUserSettingsFile(globalSettingsPath);
        }
        
        req.setGlobalSettingsFile(globalSettingsPath);
        
        req.setConfigurationCustomizer(new ContainerCustomizer() {
            public void customize(PlexusContainer plexusContainer) {
                //have custom lifecycle executor to collect all projects in reactor..
                ComponentDescriptor desc = plexusContainer.getComponentDescriptor(LifecycleExecutor.ROLE);
                desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.exec.MyLifecycleExecutor"); //NOI18N
                try {
                    PlexusConfiguration oldConf = desc.getConfiguration();
                    XmlPlexusConfiguration conf = new XmlPlexusConfiguration(oldConf.getName());
                    copyConfig(oldConf, conf);
                    desc.setConfiguration(conf);
                } catch (PlexusConfigurationException ex) {
                    ex.printStackTrace();
                }
                try {
                    desc = new ComponentDescriptor();
                    desc.setRole(TransferListener.class.getName());
                    plexusContainer.addComponentDescriptor(desc);
                    desc.setImplementation("org.codehaus.mevenide.netbeans.embedder.exec.ProgressTransferListener"); //NOI18N
                    desc = plexusContainer.getComponentDescriptor(WagonManager.ROLE);
                    ComponentRequirement requirement = new ComponentRequirement();
                    requirement.setRole(TransferListener.class.getName());
                    desc.addRequirement(requirement);
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
        return embedder;
    }
    
    
    public static ArtifactRepository createRemoteRepository(MavenEmbedder embedder, String url, String id) {
        try
        {
            ArtifactRepositoryFactory fact = (ArtifactRepositoryFactory) online.getPlexusContainer().lookup(ArtifactRepositoryFactory.ROLE);
            ArtifactRepositoryPolicy snapshotsPolicy = new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN );
            ArtifactRepositoryPolicy releasesPolicy = new ArtifactRepositoryPolicy( true, ArtifactRepositoryPolicy.UPDATE_POLICY_ALWAYS, ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN );
            return fact.createArtifactRepository( id, url, ArtifactRepositoryFactory.DEFAULT_LAYOUT_ID, snapshotsPolicy, releasesPolicy );
        } catch ( UnknownRepositoryLayoutException ex )
        {
            Exceptions.printStackTrace( ex );
        } catch ( ComponentLookupException ex )
        {
            Exceptions.printStackTrace( ex );
        }
        return null;
    }
    
    /**
     * creates model lineage for the given pom file.
     * Useful to be able to locate where certain elements are defined.
     * 
     * @param pom
     * @param embedder
     * @param allowStubs
     * @return
     */
    public static ModelLineage createModelLineage(File pom, MavenEmbedder embedder, boolean allowStubs) {
        try {
            ModelLineageBuilder bldr = (ModelLineageBuilder) embedder.getPlexusContainer().lookup(ModelLineageBuilder.class);
            ProfileActivationContext context = new DefaultProfileActivationContext(new Properties(), true); //TODO shall we pass some execution props in here?
            ProfileManager manager = new DefaultProfileManager(embedder.getPlexusContainer(), context);
            return bldr.buildModelLineage(pom, embedder.getLocalRepository(), new ArrayList(), manager, allowStubs, true);
        } catch (ProjectBuildingException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ComponentLookupException ex) {
            Exceptions.printStackTrace(ex);
        }
        return new DefaultModelLineage();
    }
    
    private static void copyConfig(PlexusConfiguration old, XmlPlexusConfiguration conf) throws PlexusConfigurationException {
        conf.setValue(old.getValue());
        String[] attrNames = old.getAttributeNames();
        if (attrNames != null && attrNames.length > 0) {
            for (int i = 0; i < attrNames.length; i++) {
                conf.setAttribute(attrNames[i], old.getAttribute(attrNames[i]));
            }
        }
        if ("lifecycle".equals(conf.getName())) { //NOI18N
            conf.setAttribute("implementation", "org.apache.maven.lifecycle.Lifecycle"); //NOI18N
        }
        for (int i = 0; i < old.getChildCount(); i++) {
            PlexusConfiguration oldChild = old.getChild(i);
            XmlPlexusConfiguration newChild = new XmlPlexusConfiguration(oldChild.getName());
            conf.addChild(newChild);
            copyConfig(oldChild, newChild);
        }
    }
    
    private static class SettingsFileListener extends FileChangeAdapter {
        private FileObject dir;
        
        public SettingsFileListener() {
            File userLoc = new File(System.getProperty("user.home"), ".m2");
            try {

                dir = FileUtil.createFolder(userLoc);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (dir != null) {
                dir.addFileChangeListener(this);
                FileObject settings = dir.getFileObject("settings.xml");
                if (settings != null) {
                    settings.addFileChangeListener(this);
                }
            }
        }
        
        
        @Override
        public void fileDeleted(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                fe.getFile().removeFileChangeListener(this);
                synchronized (EmbedderFactory.class) {
                    online = null;
                    project = null;
                }
            }
        }
        
        @Override
        public void fileDataCreated(FileEvent fe) {
            if ("settings.xml".equals(fe.getFile().getNameExt())) {
                fe.getFile().addFileChangeListener(this);
                synchronized (EmbedderFactory.class) {
                    online = null;
                    project = null;
                }
            }
        }
        
        @Override
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
