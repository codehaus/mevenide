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

package org.codehaus.mevenide.netbeans;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.queries.MavenForBinaryQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSharabilityQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSourceLevelImpl;
import org.codehaus.mevenide.netbeans.queries.MavenTestForSourceImpl;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.Sources;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.ErrorManager;
import org.openide.filesystems.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;



/**
 * the ultimate source for all maven project like. Most code in mevenide takes this
 * class as parameter, there's always just one instance per projects.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class NbMavenProject implements Project {
    
    /**
     * the only property change fired by the class, means that the pom file
     * has changed.
     */
    public static final String PROP_PROJECT = "MavenProject"; //NOI18N
    
    private FileObject fileObject;
    private File projectFile;
    private Image icon;
    private Lookup lookup;
    private PropertyChangeSupport support;
    private Updater updater1;
    private Updater updater2;
    private Updater updater3;
    private Sources sources;
    private MavenProject project;

    private Info projectInfo;

    private MavenProject oldProject;
    
    /** 
     * Creates a new instance of MavenProject, should never be called by user code.
     * but only by MavenProjectFactory!!!
     */
    public NbMavenProject(FileObject projectFO, File projectFile) throws Exception {
        this.projectFile = projectFile;
        support = new PropertyChangeSupport(this);
        fileObject = projectFO;
        projectInfo = new Info();
        updater1 = new Updater(true);
        updater2 = new Updater(true, USER_DIR_FILES);
        updater3 = new Updater(false);
    }
    
    public File getPOMFile() {
        return projectFile;
    }
    
    
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    /**
     * getter for the maven's own project representation.. this instance is cached but gets reloaded
     * when one the pom files have changed.
     */
    public synchronized MavenProject getOriginalMavenProject() {
        if (project == null) {
            try {
                try {
                    project = getEmbedder().readProjectWithDependencies(projectFile);
                } catch (ArtifactResolutionException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                    project = getEmbedder().readProject(projectFile);
                } catch (ArtifactNotFoundException ex) {
                    ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                    project = getEmbedder().readProject(projectFile);
                }
            } catch (ProjectBuildingException ex) {
                ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                try {
                    project = new MavenProject(getEmbedder().readModel(projectFile));
                } catch (FileNotFoundException ex2) {
                    ex2.printStackTrace();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                } catch (XmlPullParserException ex2) {
                    ex2.printStackTrace();
                }
//            } catch (MavenEmbedderException exc) {
//                exc.printStackTrace();
            }
            if (project == null && oldProject != null) {
                project = oldProject;
            }
            oldProject = null;
        }
        
        return project;
    }
    
    public void firePropertyChange(String property) {
        synchronized (support) {
            oldProject = project;
            project = null;
            projectInfo.reset();
            support.firePropertyChange(new PropertyChangeEvent(this, property, null, null));
        }
    }
    
    public String getDisplayName() {
        String displayName = projectInfo.getDisplayName();
        if (displayName == null) {
            displayName = "<Maven2 project with no name>";
        }
        return displayName;
    }
    
    public String getShortDescription() {
        String desc = null;
        if (desc == null) {
            desc = getOriginalMavenProject().getDescription();
        }
        if (desc == null) {
            desc = "A Maven2 based project";
        }
        return desc;
    }
    
    Updater getProjectFolderUpdater() {
        return updater1;
    }
    
    Updater getUserFolderUpdater() {
        return updater2;
    }
    
    Updater getFileUpdater() {
        return updater3;
    }
    
    private Image getIcon() {
        if (icon == null) {
            icon = Utilities.loadImage("org/codehaus/mevenide/netbeans/Maven2Icon.gif");
        }
        return icon;
    }
        
    public String getName() {
        String toReturn = null;
        MavenProject pr = getOriginalMavenProject();
        if (pr != null) {
            toReturn = pr.getId();
        }
        if (toReturn == null) {
            toReturn = getProjectDirectory().getName() + " <No Project ID>";
        }
        return toReturn;
    }

    /**
     * TODO move elsewhere?
     */
    public Action createRefreshAction() {
        return new RefreshAction();
    }
    
    /**
     * the root dirtectory of the project.. that;s where the pom resides.
     */
    public FileObject getProjectDirectory() {
        return fileObject.getParent();
    }
    
    public FileObject getHomeDirectory() {
        File homeFile = MavenSettingsSingleton.getInstance().getM2UserDir();
        if (!homeFile.exists()) {
            homeFile.mkdirs();
        }
        FileObject home = FileUtil.toFileObject(homeFile);
        if (home == null) {
            //TODO this is a problem, probably UNC path on windows - MEVENIDE-380
            // some functionality won't work
            ErrorManager.getDefault().log("Cannot convert home dir to FileObject, some functionality won't work. It's usually the case on Windows and UNC paths. The path is " + homeFile);
        }
        return home;
    }
    
    public String getArtifactRelativeRepositoryPath() {
        return getArtifactRelativeRepositoryPath(getOriginalMavenProject().getArtifact());
    }
    
    public String getArtifactRelativeRepositoryPath(Artifact artifact) {
//        embedder.setLocalRepositoryDirectory(FileUtil.toFile(getRepositoryRoot()));
        String toRet = getEmbedder().getLocalRepository().pathOf(artifact);
        //TODO this is more or less a hack..
        // if packaging is nbm, the path suggests the extension to be nbm.. override that to be jar
        return toRet.substring(0 , toRet.length() - artifact.getType().length()) + "jar";
    }
    
    public MavenEmbedder getEmbedder() {
        try {
            return EmbedderFactory.getProjectEmbedder();
        } catch (MavenEmbedderException ex) {
            ErrorManager.getDefault().notify(ex);
        }
        throw new IllegalStateException("Cannot start the embedder.");
    }
    
   

   public URI[] getGeneratedSourceRoots() {
       //TODO more or less a hack.. should be better supported by embedder itself.
       URI uri = FileUtilities.getDirURI(getProjectDirectory(), "target/generated-sources");
       File fil = new File(uri);
       if (fil.exists()) {
           File[] fils = fil.listFiles(new FileFilter() {
               public boolean accept(File pathname) {
                   return pathname.isDirectory();
               }
           });
           URI[] uris = new URI[fils.length];
           for (int i = 0; i < fils.length; i++) {
               uris[i] = fils[i].toURI();
           }
           return uris;
       }
       return new URI[0];
   }

   public URI getWebAppDirectory() {
       //TODO hack, should be supported somehow to read this..
       String prop = PluginPropertyUtils.getPluginProperty(this, "org.apache.maven.plugins",
                                              "maven-war-plugin", 
                                              "warSourceDirectory", 
                                              "war");
       prop = prop == null ? "src/main/webapp" : prop;
       URI uri = FileUtilities.getDirURI(getProjectDirectory(), prop);
       File fil = new File(uri);
       return fil.toURI();
    }
   
   public URI getEarAppDirectory() {
       //TODO hack, should be supported somehow to read this..
       String prop = PluginPropertyUtils.getPluginProperty(this, "org.apache.maven.plugins",
                                              "maven-ear-plugin", 
                                              "earSourceDirectory", 
                                              "ear");
       prop = prop == null ? "src/main/application" : prop;
       URI uri = FileUtilities.getDirURI(getProjectDirectory(), prop);
       File fil = new File(uri);
       return fil.toURI();
    }
   
   public URI[] getResources(boolean test) {
       List toRet = new ArrayList();
       List res = test ? getOriginalMavenProject().getTestResources() : getOriginalMavenProject().getResources();
       Iterator it = res.iterator();
       while (it.hasNext()) {
           Resource elem = (Resource) it.next();
           URI uri = FileUtilities.getDirURI(getProjectDirectory(), elem.getDirectory());
           if (new File(uri).exists()) {
               toRet.add(uri);
           }
       }
       return (URI[])toRet.toArray(new URI[toRet.size()]);
   }
   
   
   
   public File[] getOtherRoots(boolean test) {
       URI uri = FileUtilities.getDirURI(getProjectDirectory(), test ? "src/test" : "src/main");
       File fil = new File(uri);
       if (fil.exists()) {
           return fil.listFiles(new FilenameFilter() {
               public boolean accept(File dir, String name) {
                   //TODO most probably a performance bottleneck of sorts..
                   FileObject fo = FileUtil.toFileObject(new File(dir, name));
                   return !("java".equalsIgnoreCase(name))  && !("webapp".equalsIgnoreCase(name))  && VisibilityQuery.getDefault().isVisible(fo);
               }
           });
       }
       return new File[0];
   }
   
   /**
    * gets a set of profile ids accessible to the project, is rather slow as it reloads the files all over again.
    */
   
   
   public Set getAvailableProfiles() {
       Set profiles = new HashSet();
       profiles.addAll(MavenSettingsSingleton.getInstance().createUserSettingsModel().getProfilesAsMap().keySet());
       Iterator it = getOriginalMavenProject().getModel().getProfiles().iterator();
       while (it.hasNext()) {
           Profile prof = (Profile)it.next();
           profiles.add(prof.getId());
       }
       it = MavenSettingsSingleton.createProfilesModel(getProjectDirectory()).getProfiles().iterator();
       while (it.hasNext()) {
           org.apache.maven.profiles.Profile prof = (org.apache.maven.profiles.Profile)it.next();
           profiles.add(prof.getId());
       }
       return profiles;
   }
   
    public synchronized Lookup getLookup() {
        if (lookup == null) {
            lookup = createBasicLookup();
            // now in the creation of extended lookup by MavenLookupProvider
            // it can happen that someone is using the project's lookup to find some 
            // basic stuff. That would create a cycle..
            // obviously one cannot call getlookup() in the basic lookup setup..
            lookup = createCompleteLookups();
        }
        return lookup;
    }   
    private Lookup createBasicLookup() {
        Lookup staticLookup = Lookups.fixed(new Object[] {
            projectInfo,
            this,
            new MavenForBinaryQueryImpl(this),
            new ActionProviderImpl(this),
            new M2AuxilaryConfigImpl(this),
            new CustomizerProviderImpl(this),
            new LogicalViewProviderImpl(this),
            new ProjectOpenedHookImpl(this),
            new ClassPathProviderImpl(this),
            new MavenSharabilityQueryImpl(this),
            new MavenTestForSourceImpl(this),
////            new MavenFileBuiltQueryImpl(this),
            new SubprojectProviderImpl(this),
            new MavenSourcesImpl(this), 
            new RecommendedTemplatesImpl(this),
            new MavenSourceLevelImpl(this), 
            
            new UserActionGoalProvider(this)
                    
        });
        return staticLookup;
    }
    
    private Lookup createCompleteLookups() {
        Collection toReturn = new ArrayList();
        // add the static lookup that acts as complete instance as of now..
        toReturn.add(lookup);
        
        Lookup.Template template = new Lookup.Template(AdditionalM2LookupProvider.class);
        Lookup.Result result = Lookup.getDefault().lookup(template);
        Collection col = result.allInstances();
        Iterator it = col.iterator();
        while (it.hasNext()) {
            AdditionalM2LookupProvider prov = (AdditionalM2LookupProvider)it.next();
            toReturn.add(prov.createMavenLookup(this));
        }
        Lookup[] lookups = new Lookup[toReturn.size()];
        lookups = (Lookup[])toReturn.toArray(lookups);
        ProxyLookup look = new ProxyLookup(lookups);
        return look;
    }
    
   // Private innerclasses ----------------------------------------------------
    
    private final class Info implements ProjectInformation {
        
        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        
        Info() {}
        
        public void reset() {
            firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
            pcs.firePropertyChange(ProjectInformation.PROP_ICON, null, getIcon());
        }
        
        void firePropertyChange(String prop) {
            pcs.firePropertyChange(prop, null, null);
        }
        
        public String getName() {
            String toReturn = NbMavenProject.this.getName();
            return toReturn;
        }
        
        public String getDisplayName() {
            String toReturn = NbMavenProject.this.getOriginalMavenProject().getName();
            if (toReturn == null) {
                toReturn = "<No name defined>";
            }
            toReturn = toReturn + " (" + NbMavenProject.this.getOriginalMavenProject().getPackaging() + ")";
            return toReturn;
        }
        
        public Icon getIcon() {
            return new ImageIcon(NbMavenProject.this.getIcon());
        }
        
        public Project getProject() {
            return NbMavenProject.this;
        }
        
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }
        
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
        
    }    
 
    // needs to be binary sorted;
    private static final String[] DEFAULT_FILES = new String[] {
        "pom.xml"
    };
    private static final String[] USER_DIR_FILES = new String[] {
        "settings.xml"
    };

    
    private class Updater implements FileChangeListener {
        
//        private FileObject fileObject;
        private boolean isFolder;
        private String[] filesToWatch;
        Updater(boolean folder) {
            this(folder, DEFAULT_FILES);
        }
        
        Updater(boolean folder, String[] toWatch) {
            isFolder = folder;
            filesToWatch = toWatch;
        }
        
        public void fileAttributeChanged(FileAttributeEvent fileAttributeEvent) {
        }
        
        public void fileChanged(FileEvent fileEvent) {
            if (!isFolder) {
                String nameExt = fileEvent.getFile().getNameExt();
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1)  {
                    firePropertyChange(PROP_PROJECT);
                }
            }
        }
        
        public void fileDataCreated(FileEvent fileEvent) {
            //TODO shall also include the parent of the pom if available..
            if (isFolder) {
                String nameExt = fileEvent.getFile().getNameExt();
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1) {
                    fileEvent.getFile().addFileChangeListener(getFileUpdater());
                    firePropertyChange(PROP_PROJECT);
                }
            }
        }
        
        public void fileDeleted(FileEvent fileEvent) {
            if (!isFolder) {
                fileEvent.getFile().removeFileChangeListener(getFileUpdater());
                firePropertyChange(PROP_PROJECT);
            }
        }
        
        public void fileFolderCreated(FileEvent fileEvent) {
            firePropertyChange(PROP_PROJECT);
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
    }
    
    
    /**
     * TODO maybe make somehow extensible from other modules?
    */
    
    private static final class RecommendedTemplatesImpl 
                        implements RecommendedTemplates, PrivilegedTemplates {
    
        
        private static final String[] EAR_TYPES = new String[] {
            "XML",            //NOPMD      // NOI18N
            "ear-types",                 // NOI18N
            "wsdl",          //NOPMD       // NOI18N
            "simple-files"   //NOPMD       // NOI18N
        };
        
        private static final String[] EAR_PRIVILEGED_NAMES = new String[] {
            "Templates/XML/XMLWizard",
            "Templates/Other/Folder"
        };
        
        private static final String[] EJB_TYPES = new String[] {
            "java-classes",         // NOI18N
            "ejb-types",            // NOI18N
            "web-services",         // NOI18N
            "wsdl",                 // NOI18N
            "j2ee-types",           // NOI18N
            "java-beans",           // NOI18N
            "java-main-class",      // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "junit",                // NOI18N
            "simple-files"          // NOI18N
        };
        
        private static final String[] EJB_PRIVILEGED_NAMES = new String[] {
            
            "Templates/J2EE/Session", // NOI18N
            "Templates/J2EE/Entity",  // NOI18N
            "Templates/J2EE/RelatedCMP", // NOI18N                    
            "Templates/J2EE/Message", //NOI18N
            "Templates/WebServices/WebService", // NOI18N
            "Templates/WebServices/MessageHandler", // NOI18N
            "Templates/Classes/Class.java" // NOI18N
        };

        private static final String[] WEB_TYPES = new String[] { 
            "java-classes",         // NOI18N
            "java-main-class",      // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "servlet-types",        // NOI18N
            "web-types",            // NOI18N
            "web-services",         // NOI18N
            "web-service-clients",  // NOI18N
            "wsdl",                 // NOI18N
            "j2ee-types",           // NOI18N                    
            "junit",                // NOI18N
            "simple-files"          // NOI18N
        };

        private static final String[] WEB_PRIVILEGED_NAMES = new String[] {
            "Templates/JSP_Servlet/JSP.jsp",            // NOI18N
            "Templates/JSP_Servlet/Html.html",          // NOI18N
            "Templates/JSP_Servlet/Servlet.java",       // NOI18N
            "Templates/Classes/Class.java",             // NOI18N
            "Templates/Classes/Package",                // NOI18N
            "Templates/WebServices/WebService",         // NOI18N
            "Templates/WebServices/WebServiceClient",   // NOI18N                    
            "Templates/Other/Folder",                   // NOI18N
        };
        
        private static final String[] JAR_APPLICATION_TYPES = new String[] { 
            "java-classes",         // NOI18N
            "java-main-class",      // NOI18N
            "java-forms",           // NOI18N
            "gui-java-application", // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "web-service-clients",  // NOI18N
            "wsdl",                 // NOI18N
            // "servlet-types",     // NOI18N
            // "web-types",         // NOI18N
            "junit",                // NOI18N
            // "MIDP",              // NOI18N
            "simple-files"          // NOI18N
        };
        
        private static final String[] JAR_PRIVILEGED_NAMES = new String[] {
            "Templates/Classes/Class.java", // NOI18N
            "Templates/Classes/Package", // NOI18N
            "Templates/Classes/Interface.java", // NOI18N
            "Templates/GUIForms/JPanel.java", // NOI18N
            "Templates/GUIForms/JFrame.java", // NOI18N
            "Templates/WebServices/WebServiceClient"   // NOI18N                    
        };
        
        private static final String[] POM_APPLICATION_TYPES = new String[] { 
            "XML",                  // NOI18N
            "simple-files"          // NOI18N
        };
        
        private static final String[] POM_PRIVILEGED_NAMES = new String[] {
            "Templates/XML/XMLWizard", // NOI18N
            "Templates/Other/Folder" // NOI18N
        };
        
        private static final String[] ALL_TYPES = new String[] {
            "java-classes",         // NOI18N
            "java-main-class",      // NOI18N
            "java-forms",           // NOI18N
            "java-beans",           // NOI18N
            "j2ee-types",           // NOI18N                    
            "gui-java-application", // NOI18N
            "java-beans",           // NOI18N
            "oasis-XML-catalogs",   // NOI18N
            "XML",                  // NOI18N
            "ant-script",           // NOI18N
            "ant-task",             // NOI18N
            "web-services",         // NOI18N
            "web-service-clients",  // NOI18N
            "wsdl",                 // NOI18N
            "servlet-types",        // NOI18N
            "web-types",            // NOI18N
            "junit",                // NOI18N
            // "MIDP",              // NOI18N
            "simple-files",         // NOI18N
            "ear-types",            // NOI18N
        };
        
        private NbMavenProject project;
        
        RecommendedTemplatesImpl(NbMavenProject proj) {
            project = proj;
        }
        
        public String[] getRecommendedTypes() {
            String packaging = project.getOriginalMavenProject().getPackaging();
            if (packaging == null) {
                packaging = "jar";
            }
            packaging = packaging.trim();
            if ("ejb".equals(packaging)) {
                return EJB_TYPES;
            }
            if ("ear".equals(packaging)) {
                return EAR_TYPES;
            }
            if ("war".equals(packaging)) {
                return WEB_TYPES;
            }
            if ("pom".equals(packaging)) {
                return POM_APPLICATION_TYPES;
            }
            if ("jar".equals(packaging)) {
                return JAR_APPLICATION_TYPES;
            }
            //NBM also fall under this I guess..
            if ("nbm".equals(packaging)) {
                return JAR_APPLICATION_TYPES;
            }
            
            // If packaging is unknown, any type of sources is recommanded.
            //TODO in future we probably can try to guess based on what plugins are 
            // defined in the lifecycle. 
            return ALL_TYPES;
        }
        
        public String[] getPrivilegedTemplates() {
            String packaging = project.getOriginalMavenProject().getPackaging();
            if (packaging == null) {
                packaging = "jar";
            }
            packaging = packaging.trim();
            if ("ejb".equals(packaging)) {
                return EJB_PRIVILEGED_NAMES;
            }
            if ("ear".equals(packaging)) {
                return EAR_PRIVILEGED_NAMES;
            }
            if ("war".equals(packaging)) {
                return WEB_PRIVILEGED_NAMES;
            }
            if ("pom".equals(packaging)) {
                return POM_PRIVILEGED_NAMES;
            }
            
            return JAR_PRIVILEGED_NAMES;
        }
        
    }   
    
    private class RefreshAction extends AbstractAction {
        public RefreshAction() {
            putValue(Action.NAME, "Reload Project");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent event) {
            NbMavenProject.this.firePropertyChange(PROP_PROJECT);
        }
        
    }
    
}
