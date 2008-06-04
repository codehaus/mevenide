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

import org.codehaus.mevenide.netbeans.api.FileUtilities;
import org.codehaus.mevenide.netbeans.api.NbMavenProject;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Resource;
import org.apache.maven.project.InvalidProjectModelException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.reactor.MissingModuleException;
import org.codehaus.mevenide.netbeans.api.Constants;
import org.codehaus.mevenide.netbeans.api.PluginPropertyUtils;
import org.codehaus.mevenide.netbeans.api.ProjectProfileHandler;
import org.codehaus.mevenide.netbeans.classpath.ClassPathProviderImpl;
import org.codehaus.mevenide.netbeans.configurations.ConfigurationProviderEnabler;
import org.codehaus.mevenide.netbeans.customizer.CustomizerProviderImpl;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.codehaus.mevenide.netbeans.execute.JarPackagingRunChecker;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.codehaus.mevenide.netbeans.problems.ProblemReporterImpl;
import org.codehaus.mevenide.netbeans.queries.MavenForBinaryQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSharabilityQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenSourceLevelImpl;
import org.codehaus.mevenide.netbeans.queries.MavenTestForSourceImpl;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.queries.VisibilityQuery;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.PrivilegedTemplates;
import org.netbeans.spi.project.ui.RecommendedTemplates;
import org.openide.ErrorManager;
import org.openide.filesystems.*;
import org.openide.modules.InstalledFileLocator;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.codehaus.mevenide.netbeans.embedder.EmbedderFactory;
import org.codehaus.mevenide.netbeans.operations.OperationsImpl;
import org.codehaus.mevenide.netbeans.api.problem.ProblemReport;
import org.codehaus.mevenide.netbeans.queries.MavenBinaryForSourceQueryImpl;
import org.codehaus.mevenide.netbeans.queries.MavenFileEncodingQueryImpl;
import org.netbeans.spi.project.support.LookupProviderSupport;
import org.netbeans.spi.project.ui.support.UILookupMergerSupport;
import org.openide.util.ContextAwareAction;
import org.openide.util.NbBundle;

/**
 * the ultimate source for all maven project like. Most code in mevenide takes this
 * class as parameter, there's always just one instance per projects.
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public final class NbMavenProjectImpl implements Project {

    //TODO remove
    public static final String PROP_PROJECT = "MavenProject"; //NOI18N

    //TODO remove
    public static final String PROP_RESOURCE = "RESOURCES"; //NOI18N

    private FileObject fileObject;
    private FileObject folderFileObject;
    private File projectFile;
    private Image icon;
    private Lookup lookup;
    private Updater updater1;
    private Updater updater2;
    private Updater updater3;
    private MavenProject project;
    private ProblemReporterImpl problemReporter;
    private Info projectInfo;
    private MavenProject oldProject;
    private NbMavenProject watcher;
    private ProjectState state;
    private ConfigurationProviderEnabler configEnabler;
    private M2AuxilaryConfigImpl auxiliary;
    private ProjectProfileHandler profileHandler;
    public static WatcherAccessor ACCESSOR = null;
    

    static {
        // invokes static initializer of ModelHandle.class
        // that will assign value to the ACCESSOR field above
        Class c = NbMavenProject.class;
        try {
            Class.forName(c.getName(), true, c.getClassLoader());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static abstract class WatcherAccessor {

        public abstract NbMavenProject createWatcher(NbMavenProjectImpl proj);

        public abstract void doFireReload(NbMavenProject watcher);
    }

    /**
     * Creates a new instance of MavenProject, should never be called by user code.
     * but only by MavenProjectFactory!!!
     */
    NbMavenProjectImpl(FileObject folder, FileObject projectFO, File projectFile, ProjectState projectState) throws Exception {
        this.projectFile = projectFile;
        fileObject = projectFO;
        folderFileObject = folder;
        projectInfo = new Info();
        updater1 = new Updater(true);
        updater2 = new Updater(true, USER_DIR_FILES);
        updater3 = new Updater(false);
        state = projectState;
        problemReporter = new ProblemReporterImpl(this);
        watcher = ACCESSOR.createWatcher(this);
        auxiliary = new M2AuxilaryConfigImpl(this);
        profileHandler = new ProjectProfileHandlerImpl(this,auxiliary);
        configEnabler = new ConfigurationProviderEnabler(this, auxiliary, profileHandler);
    }

    public File getPOMFile() {
        return projectFile;
    }

    public NbMavenProject getProjectWatcher() {
        return watcher;
    }

    /**
     * getter for the maven's own project representation.. this instance is cached but gets reloaded
     * when one the pom files have changed.
     */
    public synchronized MavenProject getOriginalMavenProject() {
        if (project == null) {
            try {
                MavenExecutionRequest req = new DefaultMavenExecutionRequest();
                if (configEnabler.isConfigurationEnabled()) {
                    req.addActiveProfiles(configEnabler.getConfigProvider().getActiveConfiguration().getActivatedProfiles());
                } else {
                    List<String> activeProfiles = profileHandler.getActiveProfiles( false);
                    req.addActiveProfiles(activeProfiles);
                }
                req.setPomFile(projectFile.getAbsolutePath());
                req.setNoSnapshotUpdates(true);
                req.setUpdateSnapshots(false);
                //MEVENIDE-634 i'm wondering if this fixes the issue
                req.setInteractiveMode(false);
                // recursive == false is important to avoid checking all submodules for extensions
                // that will not be used in current pom anyway..
                // #135070
                req.setRecursive(false);
                MavenExecutionResult res = getEmbedder().readProjectWithDependencies(req);
                project = res.getProject();
                if (res.hasExceptions()) {
                    for (Object e : res.getExceptions()) {
                        Logger.getLogger(NbMavenProjectImpl.class.getName()).log(Level.INFO, "Error on loading project " + projectFile.getAbsolutePath(), (Throwable)e); //NOI18N
                        if (e instanceof ArtifactResolutionException) {
                            ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                                    NbBundle.getMessage(NbMavenProjectImpl.class, "TXT_Artifact_Resolution_problem"),
                                    ((Exception) e).getMessage(), null);
                            problemReporter.addReport(report);
                        } else if (e instanceof ArtifactNotFoundException) {
                            ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                                    NbBundle.getMessage(NbMavenProjectImpl.class, "TXT_Artifact_Not_Found"),
                                    ((Exception) e).getMessage(), null);
                            problemReporter.addReport(report);
                        } else if (e instanceof InvalidProjectModelException) {
                            //validation failure..
                            problemReporter.addValidatorReports((InvalidProjectModelException) e);
                        } else if (e instanceof ProjectBuildingException) {
                            //igonre if the problem is in the project validation codebase, we handle that later..
                            problemReporter.addReport(new ProblemReport(ProblemReport.SEVERITY_HIGH,
                                    "Cannot load project properly",
                                    ((Exception) e).getMessage(), null));
                        } else if (e instanceof MissingModuleException) {
                            MissingModuleException exc = (MissingModuleException)e;
                            ProblemReport report = new ProblemReport(ProblemReport.SEVERITY_HIGH,
                                    NbBundle.getMessage(NbMavenProjectImpl.class, "TXT_MissingSubmodule", exc.getModuleName()),
                                    ((Exception) e).getMessage(), null);
                            problemReporter.addReport(report);
                        }
                    }
                }
            } catch (RuntimeException exc) {
                //guard against exceptions that are not processed by the embedder
                //#136184 NumberFormatException
                Logger.getLogger(NbMavenProjectImpl.class.getName()).log(Level.INFO, "Runtime exception thrown while loading maven project at " + getProjectDirectory(), exc);
            } finally {
                if (project == null) {
                    try {
                        if (projectFile.exists()) { //#120860

                            project = new MavenProject(getEmbedder().readModel(projectFile));
                        }
                    } catch (FileNotFoundException ex2) {
                        ex2.printStackTrace();
                    } catch (IOException ex2) {
                        ex2.printStackTrace();
                    } catch (XmlPullParserException ex2) {
                        ex2.printStackTrace();
                    } finally {
                        File fallback = InstalledFileLocator.getDefault().locate("maven2/fallback_pom.xml", null, false); //NOI18N

                        try {
                            project = getEmbedder().readProject(fallback);
                        } catch (Exception x) {
                            // oh well..
                            //NOPMD
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
            displayName = NbBundle.getMessage(NbMavenProjectImpl.class, "LBL_NoProjectName");
        }
        return displayName;
    }

    public String getShortDescription() {
        String desc = null;
        if (desc == null) {
            desc = getOriginalMavenProject().getDescription();
        }
        if (desc == null) {
            desc = NbBundle.getMessage(NbMavenProjectImpl.class, "LBL_DefaultDescription");
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
            icon = Utilities.loadImage("org/codehaus/mevenide/netbeans/Maven2Icon.gif");//NOI18N

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
            toReturn = getProjectDirectory().getName() + " <No Project ID>"; //NOI18N

        }
        return toReturn;
    }
    /**
     * TODO move elsewhere?
     */
    private static Action refreshAction;

    public static Action createRefreshAction() {
        if (refreshAction == null) {
            refreshAction = new RefreshAction(Lookup.EMPTY);
        }
        return refreshAction;
    }

    /**
     * the root dirtectory of the project.. that;s where the pom resides.
     */
    public FileObject getProjectDirectory() {
        return folderFileObject;
    }

    public FileObject getHomeDirectory() {
        File homeFile = MavenSettingsSingleton.getInstance().getM2UserDir();

        FileObject home = null;
        try {
            home = FileUtil.createFolder(homeFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (home == null) {
            //TODO this is a problem, probably UNC path on windows - MEVENIDE-380
            // some functionality won't work
            ErrorManager.getDefault().log("Cannot convert home dir to FileObject, some functionality won't work. It's usually the case on Windows and UNC paths. The path is " + homeFile); //NOI18N

        }
        return home;
    }

    public String getArtifactRelativeRepositoryPath() {
        return getArtifactRelativeRepositoryPath(getOriginalMavenProject().getArtifact());
    }

    public String getArtifactRelativeRepositoryPath(Artifact artifact) {
        //        embedder.setLocalRepositoryDirectory(FileUtil.toFile(getRepositoryRoot()));
        String toRet = getEmbedder().getLocalRepository().pathOf(artifact);
        return toRet;
    }

    public MavenEmbedder getEmbedder() {
        return EmbedderFactory.getProjectEmbedder();
    }

    public URI[] getSourceRoots(boolean test) {
        List srcs = test ? getOriginalMavenProject().getTestCompileSourceRoots() : getOriginalMavenProject().getCompileSourceRoots();
        if (!test && getProjectDirectory().getFileObject("src/main/aspect") != null) {
            srcs = new ArrayList(srcs);
            srcs.add(FileUtil.toFile(getProjectDirectory().getFileObject("src/main/aspect")).getAbsolutePath());
        }
        //TODO groovy and scala stuff should probably end up in separate module's
        //ClassPathProvider
        //TODO the folder should be checked against the configuration of scala/groovy plugin.
        String groovy = test ? "src/test/groovy" : "src/main/groovy"; //NOI18N
        if (getProjectDirectory().getFileObject(groovy) != null) {
            srcs = new ArrayList(srcs);
            srcs.add(FileUtil.toFile(getProjectDirectory().getFileObject(groovy)).getAbsolutePath());
        }
        String scala = test ? "src/test/scala" : "src/main/scala"; //NOI18N
        if (getProjectDirectory().getFileObject(scala) != null) {
            srcs = new ArrayList(srcs);
            srcs.add(FileUtil.toFile(getProjectDirectory().getFileObject(scala)).getAbsolutePath());
        }
        
        URI[] uris = new URI[srcs.size()];
        Iterator it = srcs.iterator();
        int count = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            File fil = FileUtil.normalizeFile(new File(str));
            uris[count] = fil.toURI();
            count = count + 1;
        }
        return uris;
    }

    public URI[] getGeneratedSourceRoots() {
        
        //TODO more or less a hack.. should be better supported by embedder itself.
        URI uri = FileUtilities.getDirURI(getProjectDirectory(), "target/generated-sources"); //NOI18N
        Set<URI> uris = new HashSet<URI>();
        
        File fil = new File(uri);
        if (fil.exists() && fil.isDirectory()) {
            File[] fils = fil.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (int i = 0; i < fils.length; i++) {
                uris.add(fils[i].toURI());
            }
        }
        
        String[] buildHelpers = PluginPropertyUtils.getPluginPropertyList(this, 
                "org.codehaus.mojo", 
                "build-helper-maven-plugin", "sources", "source", "add-source"); //TODO split for sources and test sources..
        if (buildHelpers != null && buildHelpers.length > 0) {
            File root = FileUtil.toFile(getProjectDirectory());
            for (String helper : buildHelpers) {
                uris.add(FileUtilities.getDirURI(root, helper));
            }
        }
        
        return uris.toArray(new URI[uris.size()]);
    }

    public URI getWebAppDirectory() {
        //TODO hack, should be supported somehow to read this..
        String prop = PluginPropertyUtils.getPluginProperty(this, Constants.GROUP_APACHE_PLUGINS,
                Constants.PLUGIN_WAR, //NOI18N
                "warSourceDirectory", //NOI18N
                "war"); //NOI18N

        prop = prop == null ? "src/main/webapp" : prop; //NOI18N

        return FileUtilities.getDirURI(getProjectDirectory(), prop);
    }
    
    public URI getSiteDirectory() {
        //TODO hack, should be supported somehow to read this..
        String prop = PluginPropertyUtils.getPluginProperty(this, Constants.GROUP_APACHE_PLUGINS,
                Constants.PLUGIN_SITE, //NOI18N
                "siteDirectory", //NOI18N
                "site"); //NOI18N

        prop = prop == null ? "src/site" : prop; //NOI18N

        return FileUtilities.getDirURI(getProjectDirectory(), prop);
    }
    

    public URI getEarAppDirectory() {
        //TODO hack, should be supported somehow to read this..
        String prop = PluginPropertyUtils.getPluginProperty(this, Constants.GROUP_APACHE_PLUGINS,
                Constants.PLUGIN_EAR, //NOI18N
                "earSourceDirectory", //NOI18N
                "ear"); //NOI18N

        prop = prop == null ? "src/main/application" : prop; //NOI18N

        return FileUtilities.getDirURI(getProjectDirectory(), prop);
    }

    public URI[] getResources(boolean test) {
        List<URI> toRet = new ArrayList<URI>();
        List res = test ? getOriginalMavenProject().getTestResources() : getOriginalMavenProject().getResources();
        Iterator it = res.iterator();
        while (it.hasNext()) {
            Resource elem = (Resource) it.next();
            URI uri = FileUtilities.getDirURI(getProjectDirectory(), elem.getDirectory());
//            if (new File(uri).exists()) {
            toRet.add(uri);
//            }
        }
        return toRet.toArray(new URI[toRet.size()]);
    }

    public File[] getOtherRoots(boolean test) {
        URI uri = FileUtilities.getDirURI(getProjectDirectory(), test ? "src/test" : "src/main"); //NOI18N

        File fil = new File(uri);
        if (fil.exists()) {
            return fil.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    //TODO most probably a performance bottleneck of sorts..
                    return !("java".equalsIgnoreCase(name)) && !("webapp".equalsIgnoreCase(name)) /*NOI18N*/ && VisibilityQuery.getDefault().isVisible(FileUtil.toFileObject(new File(dir, name)));
                }
            });
        }
        return new File[0];
    }


    public synchronized Lookup getLookup() {
        if (lookup == null) {
            lookup = createBasicLookup();
            lookup = LookupProviderSupport.createCompositeLookup(lookup, "Projects/org-codehaus-mevenide-netbeans/Lookup"); //NOI18N
        }
        return lookup;
    }

    private Lookup createBasicLookup() {
        CPExtender extender = new CPExtender(this);
        Lookup staticLookup = Lookups.fixed(new Object[]{
                    projectInfo,
                    this,
                    new MavenForBinaryQueryImpl(this),
                    new MavenBinaryForSourceQueryImpl(this),
                    new ActionProviderImpl(this),
                    auxiliary,
                    profileHandler,
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
                    watcher,
                    new MavenFileEncodingQueryImpl(this),
                    new TemplateAttrProvider(this),
                    //operations
                    new OperationsImpl(this, state),
                    configEnabler,
                    // default mergers..        
                    UILookupMergerSupport.createPrivilegedTemplatesMerger(),
                    UILookupMergerSupport.createRecommendedTemplatesMerger(),
                    LookupProviderSupport.createSourcesMerger(),
                    new CPExtenderLookupMerger(extender),
                    new CPModifierLookupMerger(extender)
                });
        return staticLookup;
    }

    private final class Info implements ProjectInformation {

        private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

        Info() {
        }

        public void reset() {
            firePropertyChange(ProjectInformation.PROP_DISPLAY_NAME);
            pcs.firePropertyChange(ProjectInformation.PROP_ICON, null, getIcon());
        }

        void firePropertyChange(String prop) {
            pcs.firePropertyChange(prop, null, null);
        }

        public String getName() {
            String toReturn = NbMavenProjectImpl.this.getName();
            return toReturn;
        }

        public String getDisplayName() {
            String toReturn = NbMavenProjectImpl.this.getOriginalMavenProject().getName();
            if (toReturn == null) {
                String grId = NbMavenProjectImpl.this.getOriginalMavenProject().getGroupId();
                String artId = NbMavenProjectImpl.this.getOriginalMavenProject().getArtifactId();
                if (grId != null && artId != null) {
                    toReturn = grId + ":" + artId; //NOI18N

                } else {
                    toReturn = org.openide.util.NbBundle.getMessage(NbMavenProjectImpl.class, "TXT_Maven_project_at", NbMavenProjectImpl.this.getProjectDirectory().getPath());
                }
            }
            toReturn = toReturn + " (" + NbMavenProjectImpl.this.getOriginalMavenProject().getPackaging() + ")"; //NOI18N

            return toReturn;
        }

        public Icon getIcon() {
            return new ImageIcon(NbMavenProjectImpl.this.getIcon());
        }

        public Project getProject() {
            return NbMavenProjectImpl.this;
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            pcs.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            pcs.removePropertyChangeListener(listener);
        }
    }
    // needs to be binary sorted;
    private static final String[] DEFAULT_FILES = new String[]{
        "pom.xml" //NOI18N

    };
    private static final String[] USER_DIR_FILES = new String[]{
        "settings.xml" //NOI18N

    };

    //MEVENIDE-448 seems to help against creation of duplicate project instances
    // no idea why, it's supposed to be ProjectManager job.. maybe related to
    // maven impl of SubProjectProvider or FileOwnerQueryImplementation
    //TODO need to investigate why it's like that..
    @Override
    public int hashCode() {
        return getProjectDirectory().hashCode() * 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Project) {
            return getProjectDirectory().equals(((Project) obj).getProjectDirectory());
        }
        return false;
    }

    @Override
    public String toString() {
        return "Maven[" + fileObject.getPath() + "]"; //NOI18N

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
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1) {
                    NbMavenProject.fireMavenProjectReload(NbMavenProjectImpl.this);
                }
            }
        }

        public void fileDataCreated(FileEvent fileEvent) {
            //TODO shall also include the parent of the pom if available..
            if (isFolder) {
                String nameExt = fileEvent.getFile().getNameExt();
                if (Arrays.binarySearch(filesToWatch, nameExt) != -1) {
                    fileEvent.getFile().addFileChangeListener(getFileUpdater());
                    NbMavenProject.fireMavenProjectReload(NbMavenProjectImpl.this);
                }
            }
        }

        public void fileDeleted(FileEvent fileEvent) {
            if (!isFolder) {
                fileEvent.getFile().removeFileChangeListener(getFileUpdater());
                NbMavenProject.fireMavenProjectReload(NbMavenProjectImpl.this);
            }
        }

        public void fileFolderCreated(FileEvent fileEvent) {
            //TODO possibly remove this fire.. watch for actual path..
            NbMavenProject.fireMavenProjectReload(NbMavenProjectImpl.this);
        }

        public void fileRenamed(FileRenameEvent fileRenameEvent) {
        }
    }

    private static final class RecommendedTemplatesImpl
            implements RecommendedTemplates, PrivilegedTemplates {

        private static final String[] JAR_APPLICATION_TYPES = new String[]{
            "java-classes", // NOI18N
            "java-main-class", // NOI18N
            "java-forms", // NOI18N
            "gui-java-application", // NOI18N
            "java-beans", // NOI18N
            "oasis-XML-catalogs", // NOI18N
            "XML", // NOI18N
            //            "web-service-clients",  // NOI18N
            "wsdl", // NOI18N
            // "servlet-types",     // NOI18N
            // "web-types",         // NOI18N
            "junit", // NOI18N
            // "MIDP",              // NOI18N
            "simple-files"          // NOI18N

        };
        private static final String[] JAR_PRIVILEGED_NAMES = new String[]{
            "Templates/Classes/Class.java", // NOI18N
            "Templates/Classes/Package", // NOI18N
            "Templates/Classes/Interface.java", // NOI18N
            "Templates/GUIForms/JPanel.java", // NOI18N
            "Templates/GUIForms/JFrame.java" // NOI18N
//            "Templates/WebServices/WebServiceClient"   // NOI18N

        };
        private static final String[] POM_APPLICATION_TYPES = new String[]{
            "XML", // NOI18N
            "simple-files"          // NOI18N

        };
        private static final String[] POM_PRIVILEGED_NAMES = new String[]{
            "Templates/XML/XMLWizard", // NOI18N
            "Templates/Other/Folder" // NOI18N

        };
        private static final String[] ALL_TYPES = new String[]{
            "java-classes", // NOI18N
            "java-main-class", // NOI18N
            "java-forms", // NOI18N
            "java-beans", // NOI18N
            "j2ee-types", // NOI18N
            "gui-java-application", // NOI18N
            "java-beans", // NOI18N
            "oasis-XML-catalogs", // NOI18N
            "XML", // NOI18N
            "ant-script", // NOI18N
            "ant-task", // NOI18N
            //            "web-services",         // NOI18N
            //            "web-service-clients",  // NOI18N
            "wsdl", // NOI18N
            "servlet-types", // NOI18N
            "web-types", // NOI18N
            "junit", // NOI18N
            // "MIDP",              // NOI18N
            "simple-files", // NOI18N
            "ear-types",            // NOI18N

        };
        private static final String[] GENERIC_WEB_TYPES = new String[]{
            "java-classes", // NOI18N
            "java-main-class", // NOI18N
            "java-beans", // NOI18N
            "oasis-XML-catalogs", // NOI18N
            "XML", // NOI18N
            "wsdl", // NOI18N
            "junit", // NOI18N
            "simple-files"          // NOI18N

        };
        private static final String[] GENERIC_EJB_TYPES = new String[]{
            "java-classes", // NOI18N
            "wsdl", // NOI18N
            "java-beans", // NOI18N
            "java-main-class", // NOI18N
            "oasis-XML-catalogs", // NOI18N
            "XML", // NOI18N
            "junit", // NOI18N
            "simple-files"          // NOI18N

        };
        private static final String[] GENERIC_EAR_TYPES = new String[]{
            "XML", //NOPMD      // NOI18N
            "wsdl", //NOPMD       // NOI18N
            "simple-files"   //NOPMD       // NOI18N

        };
        private List<String> prohibited;
        private NbMavenProjectImpl project;

        RecommendedTemplatesImpl(NbMavenProjectImpl proj) {
            project = proj;
            prohibited = new ArrayList<String>();
            prohibited.add(NbMavenProject.TYPE_EAR);
            prohibited.add(NbMavenProject.TYPE_EJB);
            prohibited.add(NbMavenProject.TYPE_WAR);
            prohibited.add(NbMavenProject.TYPE_NBM);
        }

        public String[] getRecommendedTypes() {
            String packaging = project.getProjectWatcher().getPackagingType();
            if (packaging == null) {
                packaging = NbMavenProject.TYPE_JAR;
            }
            packaging = packaging.trim();
            if (NbMavenProject.TYPE_POM.equals(packaging)) {
                return POM_APPLICATION_TYPES;
            }
            if (NbMavenProject.TYPE_JAR.equals(packaging)) {
                return JAR_APPLICATION_TYPES;
            }
            //TODO when apisupport module becomes 'non-experimental', delete this block..
            //NBM also fall under this I guess..
            if (NbMavenProject.TYPE_NBM.equals(packaging)) {
                return JAR_APPLICATION_TYPES;
            }

            if (NbMavenProject.TYPE_WAR.equals(packaging)) {
                return GENERIC_WEB_TYPES;
            }
            if (NbMavenProject.TYPE_EJB.equals(packaging)) {
                return GENERIC_EJB_TYPES;
            }
            if (NbMavenProject.TYPE_EAR.equals(packaging)) {
                return GENERIC_EAR_TYPES;
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
            String packaging = project.getProjectWatcher().getPackagingType();
            if (packaging == null) {
                packaging = NbMavenProject.TYPE_JAR;
            }
            packaging = packaging.trim();
            if (NbMavenProject.TYPE_POM.equals(packaging)) {
                return POM_PRIVILEGED_NAMES;
            }
            if (prohibited.contains(packaging)) {
                return new String[0];
            }
            return JAR_PRIVILEGED_NAMES;
        }
    }

    private static class RefreshAction extends AbstractAction implements ContextAwareAction {

        private Lookup context;

        public RefreshAction(Lookup lkp) {
            context = lkp;
            Collection col = context.lookupAll(NbMavenProjectImpl.class);
            if (col.size() > 1) {
                putValue(Action.NAME, NbBundle.getMessage(NbMavenProjectImpl.class, "ACT_Reload_Projects", col.size()));
            } else {
                putValue(Action.NAME, NbBundle.getMessage(NbMavenProjectImpl.class, "ACT_Reload_Project"));
            }
        }

        public void actionPerformed(java.awt.event.ActionEvent event) {
            EmbedderFactory.resetProjectEmbedder();
            for (NbMavenProjectImpl prj : context.lookupAll(NbMavenProjectImpl.class)) {
                NbMavenProject.fireMavenProjectReload(prj);
            }
        }

        public Action createContextAwareInstance(Lookup actionContext) {
            return new RefreshAction(actionContext);
        }
    }
}
