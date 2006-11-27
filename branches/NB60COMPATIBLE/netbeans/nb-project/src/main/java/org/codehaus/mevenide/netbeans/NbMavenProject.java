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

import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import java.awt.Image;
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
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.execute.JarPackagingRunChecker;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.problems.ProblemReport;
import org.codehaus.mevenide.netbeans.problems.ProblemReporter;
import org.codehaus.mevenide.netbeans.queries.MavenForBinaryQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSharabilityQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSourceLevelImpl;
import org.codehaus.mevenide.netbeans.queries.MavenTestForSourceImpl;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.ErrorManager;
import org.openide.filesystems.*;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;



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
    /**
     * 
     */
    public static final String PROP_RESOURCE = "RESOURCES"; //NOI18N
    
    private FileObject fileObject;
    private File projectFile;
    private Image icon;
    private Lookup lookup;
    private Updater updater1;
    private Updater updater2;
    private Updater updater3;
    private MavenProject project;
    private ProblemReporter problemReporter;
    private Info projectInfo;
    private MavenProject oldProject;
    private ProjectURLWatcher watcher;
    
    
    public static WatcherAccessor ACCESSOR = null;

    static {
        // invokes static initializer of ModelHandle.class
        // that will assign value to the ACCESSOR field above
        Class c = ProjectURLWatcher.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    
    
    
    public static abstract class WatcherAccessor {
        
        public abstract ProjectURLWatcher createWatcher(NbMavenProject proj);
        
        public abstract void doFireReload(ProjectURLWatcher watcher);
        
        public abstract void checkFileObject(ProjectURLWatcher watcher, FileObject fo);
    }
    
    /**
     * Creates a new instance of MavenProject, should never be called by user code.
     * but only by MavenProjectFactory!!!
     */
    public NbMavenProject(FileObject projectFO, File projectFile) throws Exception {
        this.projectFile = projectFile;
        fileObject = projectFO;
        projectInfo = new Info();
        updater1 = new Updater(true);
        updater2 = new Updater(true, USER_DIR_FILES);
        updater3 = new Updater(false);
        problemReporter = new ProblemReporter(this);
        watcher = ACCESSOR.createWatcher(this);
    }
    
    public File getPOMFile() {
        return projectFile;
    }
    
    public ProjectURLWatcher getProjectWatcher() {
        return watcher;
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
                    ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                            "Artifact Resolution problem",
                            ex.getMessage(), null);
                    problemReporter.addReport(report);
                    //                    ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                    project = getEmbedder().readProject(projectFile);
                } catch (ArtifactNotFoundException ex) {
                    ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                            "Artifact Not Found",
                            ex.getMessage(), null);
                    problemReporter.addReport(report);
                    //                    ErrorManager.getDefault().notify(ErrorManager.ERROR, ex);
                    project = getEmbedder().readProject(projectFile);
                }
            } catch (InvalidProjectModelException exc) {
                //validation failure..
                problemReporter.addValidatorReports(exc);
            } catch (ProjectBuildingException ex) {
                //igonre if the problem is in the project validation codebase, we handle that later..
                ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                            "Cannot load project properly",
                        ex.getMessage(), null);
                problemReporter.addReport(report);
            } finally {
                if (project == null) {
                    try {
                        project = new MavenProject(getEmbedder().readModel(projectFile));
                    } catch (FileNotFoundException ex2) {
                        ex2.printStackTrace();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    } catch (XmlPullParserException ex2) {
                        ex2.printStackTrace();
                    } finally {
                        File fallback = InstalledFileLocator.getDefault().locate("maven2/fallback_pom.xml", null, false);
                        try {
                            project = getEmbedder().readProject(fallback);
                        } catch (Exception x) {
                            // oh well..
                        }
                    }
                }
            }
            if (project == null && oldProject != null) {
                project = oldProject;
            }
            oldProject = null;
        }
        
        return project;
    }
    
    public void fireProjectReload() {
        oldProject = project;
        project = null;
        projectInfo.reset();
        problemReporter.clearReports();
        ACCESSOR.doFireReload(watcher);
        doBaseProblemChecks();
    }
    void doBaseProblemChecks() {
        problemReporter.doBaseProblemChecks(project);
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
        List<URI> toRet = new ArrayList<URI>();
        List res = test ? getOriginalMavenProject().getTestResources() : getOriginalMavenProject().getResources();
        Iterator it = res.iterator();
        while (it.hasNext()) {
            Resource elem = (Resource) it.next();
            URI uri = FileUtilities.getDirURI(getProjectDirectory(), elem.getDirectory());
            if (new File(uri).exists()) {
                toRet.add(uri);
            }
        }
        return toRet.toArray(new URI[toRet.size()]);
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
        Iterator it2 = MavenSettingsSingleton.createProfilesModel(getProjectDirectory()).getProfiles().iterator();
        while (it2.hasNext()) {
            org.apache.maven.profiles.Profile prof = (org.apache.maven.profiles.Profile)it2.next();
            profiles.add(prof.getId());
        }
        return profiles;
    }
    
    public synchronized Lookup getLookup() {
        if (lookup == null) {
            lookup = createBasicLookup();
            lookup = LookupProviderSupport.createCompositeLookup(lookup, "Projects/org-codehaus-mevenide-netbeans/Lookup");
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
            new JarPackagingRunChecker(),
            problemReporter,
            new UserActionGoalProvider(this),
            new CPExtender(this),
            watcher,

            // default mergers..        
            UILookupMergerSupport.createPrivilegedTemplatesMerger(),
            UILookupMergerSupport.createRecommendedTemplatesMerger(),
            LookupProviderSupport.createSourcesMerger()        
        });
        return staticLookup;
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
                String grId = NbMavenProject.this.getOriginalMavenProject().getGroupId();
                String artId = NbMavenProject.this.getOriginalMavenProject().getArtifactId();
                if (grId != null && artId != null) {
                    toReturn = grId + ":" + artId;
                } else {
                    toReturn = "Maven project at " + NbMavenProject.this.getProjectDirectory().getPath();
                }
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
    
    //MEVENIDE-448 seems to help against creation of duplicate project instances
    // no idea why, it's supposed to be ProjectManager job.. maybe related to
    // maven impl of SubProjectProvider or FileOwnerQueryImplementation
    //TODO need to investigate why it's like that..
    public int hashCode() {
        return getProjectDirectory().hashCode() * 13;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof Project) {
            return getProjectDirectory().equals(((Project)obj).getProjectDirectory());
        }
        return false;
    }
    
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
                    ProjectURLWatcher.fireMavenProjectReload(NbMavenProject.this);
                }
            }
        }
        
        public void fileDataCreated(FileEvent fileEvent) {
            //TODO shall also include the parent of the pom if available..
            if (isFolder) {
                String nameExt = fileEvent.getFile().getNameExt();
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1) {
                    fileEvent.getFile().addFileChangeListener(getFileUpdater());
                    ProjectURLWatcher.fireMavenProjectReload(NbMavenProject.this);
                }
            }
        }
        
        public void fileDeleted(FileEvent fileEvent) {
            if (!isFolder) {
                fileEvent.getFile().removeFileChangeListener(getFileUpdater());
                    ProjectURLWatcher.fireMavenProjectReload(NbMavenProject.this);
            }
        }
        
        public void fileFolderCreated(FileEvent fileEvent) {
            //TODO possibly remove this fire.. watch for actual path..
            ProjectURLWatcher.fireMavenProjectReload(NbMavenProject.this);
        }
        
        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
        
    }
    
    private static final class RecommendedTemplatesImpl
            implements RecommendedTemplates, PrivilegedTemplates {
        
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
//            "web-service-clients",  // NOI18N
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
            "Templates/GUIForms/JFrame.java" // NOI18N
//            "Templates/WebServices/WebServiceClient"   // NOI18N
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
//            "web-services",         // NOI18N
//            "web-service-clients",  // NOI18N
            "wsdl",                 // NOI18N
            "servlet-types",        // NOI18N
            "web-types",            // NOI18N
            "junit",                // NOI18N
            // "MIDP",              // NOI18N
            "simple-files",         // NOI18N
            "ear-types",            // NOI18N
        };
        
        private List<String> prohibited;
        private NbMavenProject project;
        
        RecommendedTemplatesImpl(NbMavenProject proj) {
            project = proj;
            prohibited = new ArrayList<String>();
            prohibited.add("ear");
            prohibited.add("ejb");
            prohibited.add("war");
        }
        
        public String[] getRecommendedTypes() {
            String packaging = project.getOriginalMavenProject().getPackaging();
            if (packaging == null) {
                packaging = "jar";
            }
            packaging = packaging.trim();
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
            
            if (prohibited.contains(packaging)) {
                return new String[0];
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
            if ("pom".equals(packaging)) {
                return POM_PRIVILEGED_NAMES;
            }
            if (prohibited.contains(packaging)) {
                return new String[0];
            }
            return JAR_PRIVILEGED_NAMES;
        }
        
    }
    
    private class RefreshAction extends AbstractAction {
        public RefreshAction() {
            putValue(Action.NAME, "Reload Project");
        }
        
        public void actionPerformed(java.awt.event.ActionEvent event) {
            EmbedderFactory.resetProjectEmbedder();
            ProjectURLWatcher.fireMavenProjectReload(NbMavenProject.this);
        }
        
    }
    
}
