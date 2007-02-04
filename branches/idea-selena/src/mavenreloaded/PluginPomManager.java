/*
 * Copyright (c) 2006 Bryan Kate
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package mavenreloaded;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.JdkOrderEntry;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.javaee.JavaeeModuleProperties;
import com.intellij.javaee.module.LibraryLink;
import com.intellij.javaee.module.J2EEPackagingMethod;
import com.intellij.javaee.module.TransactionalEditable;
import com.intellij.javaee.module.ModuleContainer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedRequest;
import org.apache.maven.embedder.DefaultMavenEmbedRequest;
import org.apache.maven.embedder.PlexusLoggerAdapter;
import org.apache.maven.model.Resource;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.ReactorManager;
import org.apache.maven.reactor.MavenExecutionException;
import org.apache.maven.monitor.event.DefaultEventMonitor;
import org.apache.maven.monitor.event.EventMonitor;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.io.File;

import mavenreloaded.console.PluginLogger;
import mavenreloaded.configuration.ConfigurationBean;


/**
 * A class that manages the Maven 2 POMs in the project. It attempts to add the dependencies of each pom to the
 * containing module's classpath. It uses the Maven 2 embedder to resolve the dependencies if needed. In addition,
 * it tries to attach sources to the dependencies and work out any inter-module dependencies.
 *
 * @author bkate
 */
public class PluginPomManager implements ModuleListener {

    // the project that this plugin operates on
    private Project project = null;

    // a listener that responds to changes in the file system
    private PomFileListener pomFileListener = new PomFileListener();

    // plugin logger
    private PluginLogger logger;

    // plugin config
    private ConfigurationBean config;

    // maven embedder
    private MavenEmbedder maven;

    // a set that holds the known POM files from this project
    private Set<VirtualFile> knownPoms = new HashSet<VirtualFile>();

    // a string that is prefixed on all names of maven related libraries in the module classpath
    public static final String LIBRARY_PREFIX = "Maven Dependency: ";

    // a map of instances tied to the project they service
    private static final Map<Project, PluginPomManager> instances = new ConcurrentHashMap<Project, PluginPomManager>();


    /**
     * Private constructor - factory pattern.
     *
     * @param proj The project that is being monitors.
     */
    private PluginPomManager(Project proj) {

        this.project = proj;

        config = PluginConfigurationManager.getInstance(project).getConfig();
        logger = PluginLoggerManager.getInstance(project).getPluginLogger(PluginPomManager.class);

        // make an embedder to run maven
        maven = new MavenEmbedder();

        maven.setClassLoader(this.getClass().getClassLoader());
        maven.setLogger(PluginLoggerManager.getInstance(project).getEmbedderLogger());

        // setup to read the settings properly
        MavenEmbedRequest req = new DefaultMavenEmbedRequest();

        req.setUserSettingsFile(new File(config.getSettingsPath()));

        try {
            maven.start(req);
        }
        catch(MavenEmbedderException e) {
            logger.error("Could not create Maven 2 Embedder.");
        }
    }


    /**
     * Gets the singleton instance of the POM manager for the project.
     *
     * @return The PluginPomManager being used.
     */
    public static PluginPomManager getInstance(Project proj) {

        if (!instances.containsKey(proj)) {
            instances.put(proj, new PluginPomManager(proj));
        }

        return instances.get(proj);
    }


    /**
     * Gets rid of any reference to the instance of the manager that is registered to the Project passed.
     *
     * @param proj The Project for which the instance is being cleared.
     */
    public static void releaseInstance(Project proj) {
        instances.remove(proj);
    }


    /**
     * Processes any POM files in the project, resolve any inter-module dependencies, and update the classpaths.
     */
    public void updateProjectModules() {

        if (!config.isPluginEnabled()) {
            return;
        }

        // keep a list of modules that have a modified classpath or inter-module dependency
        final Map<Module, ModifiableRootModel> dirtyModules = new HashMap<Module, ModifiableRootModel>();
        final Map<Module, LibraryTable.ModifiableModel> dirtyLibTables = new HashMap<Module, LibraryTable.ModifiableModel>();
        final Map<Module, Map<String, Library.ModifiableModel>> dirtyLibraries = new HashMap<Module, Map<String, Library.ModifiableModel>>();
        final Map<ModifiableRootModel, TransactionalEditable> dirtyJ2eeProps = new HashMap<ModifiableRootModel, TransactionalEditable>();

        // reset the set of known POM files
        knownPoms = new HashSet<VirtualFile>();

        // run the maven parsing in a separate thread, but wait for it to return.
        // this is a way of showing progress and not hanging the gui
        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {

            // structures that keep tabs on module contents and inter-dependencies
            private Map<Module, Set<Module>> moduleToModule = new HashMap<Module, Set<Module>>();
            private Map<VirtualFile, MavenProject> fileToPom = new HashMap<VirtualFile, MavenProject>();
            private Map<VirtualFile, Set<VirtualFile>> rootToFile = new HashMap<VirtualFile, Set<VirtualFile>>();
            private Map<Module, List<VirtualFile>> moduleToFile = new HashMap<Module, List<VirtualFile>>();
            private Map<MavenProject, Set<Artifact>> pomToArtifact = new HashMap<MavenProject, Set<Artifact>>();
            private Map<Module, Set<Artifact>> moduleToArtifact = new HashMap<Module, Set<Artifact>>();
            private Map<Artifact, Module> moduleAsArtifact = new HashMap<Artifact, Module>();

            // the progress indicator in use
            private ProgressIndicator indicator;


            /**
             * Runs the maven process - parses POMs, resolves dependencies, and updates the classpath
             */
            public void run() {

                try {

                    indicator = ProgressManager.getInstance().getProgressIndicator();
                    indicator.setIndeterminate(true);
                    indicator.setFraction(0.0001);

                    indicator.setText("Searching for POM files in the project.");

                    // search for poms
                    kickOffSearch();

                    List<Module> modulesToRemove = new ArrayList<Module>();

                    // read each POM, validate, and record the project it represents
                    for (Module module : moduleToFile.keySet()) {

                        indicator.setText("Parsing POMs in module: " + module.getName());

                        List<VirtualFile> pomFiles = moduleToFile.get(module);

                        // no POM, hurray!
                        if (pomFiles.size() == 0) {
                            continue;
                        }

                        // check to see that there is only one POM, otherwise it is difficult to handle
                        if (pomFiles.size() > 1) {
                            logger.warn("There is more than one POM in module: " + module.getName());
                        }

                        List<VirtualFile> pomsToRemove = new ArrayList<VirtualFile>();

                        // try to read each POM file
                        for (VirtualFile pomFile : pomFiles) {

                            // check for bail out
                            if (indicator.isCanceled()) {
                                throw new MavenWorkerCancelledException();
                            }

                            indicator.setText2(pomFile.getPresentableName());

                            MavenProject pom = null;

                            try {

                                // read the pom and try to resolve dependencies. if they cannot be resolved, the project
                                // should still load fine. it will only be null if there is an actual problem
                                // with the pom file (i.e. it does not conform to the maven schema, etc...)
                                pom = maven.readProjectWithDependencies(new File(pomFile.getPath()), false);

                                // couldn't parse the pom
                                if (pom == null) {
                                    throw new NullPointerException("Could not read POM.");
                                }
                            }
                            catch(Exception e) {

                                logger.error("Could not parse POM file: " + pomFile.getPath());
                                logger.debug("Error parsing POM: " + pomFile.getPath(), e);

                                // get rid of the evidence
                                pomsToRemove.add(pomFile);
                                continue;
                            }

                            // store the parsed result
                            fileToPom.put(pomFile, pom);
                            moduleAsArtifact.put(maven.createArtifact(pom.getGroupId(),
                                                                      pom.getArtifactId(),
                                                                      pom.getVersion(),
                                                                      MavenConstants.COMPILE_SCOPE,
                                                                      MavenConstants.JAR_PACKAGING), module);
                        }

                        // get rid of any unparseable pom files from this module
                        if (!pomsToRemove.isEmpty()) {

                            // get rid of the pom file from the root list
                            for (Set<VirtualFile> poms : rootToFile.values()) {
                                poms.removeAll(pomsToRemove);
                            }

                            // remove the pom file from the module list
                            pomFiles.removeAll(pomsToRemove);

                            if (pomFiles.isEmpty()) {
                                modulesToRemove.add(module);
                            }
                        }
                    }

                    // get rid of any modules that had only unparseable poms
                    for (Module module : modulesToRemove) {

                        logger.warn("Disregarding module: " + module.getName());
                        moduleToFile.remove(module);
                    }

                    // construct a master list of dependencies for this module
                    for (Module module : moduleToFile.keySet()) {

                        indicator.setText("Processing POMs in module: " + module.getName());

                        moduleToArtifact.put(module, new HashSet<Artifact>());

                        List<VirtualFile> pomFiles = moduleToFile.get(module);

                        // add the pom files to the set of known poms
                        knownPoms.addAll(pomFiles);

                        // try to read each POM file
                        for (VirtualFile pomFile : pomFiles) {

                            MavenProject pom = fileToPom.get(pomFile);
                            Set<Artifact> artifacts = new HashSet<Artifact>();

                            for (Artifact artifact : (Set<Artifact>)pom.getArtifacts()) {

                                // check for bail out
                                if (indicator.isCanceled()) {
                                    throw new MavenWorkerCancelledException();
                                }

                                // check to see that the dependency is not actually represented by a module in this project
                                Module depModule = checkForModuleDependency(artifact);

                                if (config.isManageModuleInterdependenciesEnabled() && (depModule != null)) {

                                    // make sure it is not this module
                                    if (!module.getName().equals(depModule.getName())) {

                                        if (!moduleToModule.containsKey(module)) {
                                            moduleToModule.put(module, new HashSet<Module>());
                                        }

                                        // add this module as dependent on the module containing the dependency's pom
                                        moduleToModule.get(module).add(depModule);
                                    }

                                    continue;
                                }

                                // filter out dependencies that are not jars
                                if (!artifact.getType().equalsIgnoreCase(MavenConstants.JAR_PACKAGING)) {
                                    continue;
                                }

                                // filter out dependencies that have a source or javadoc classifier
                                if ((artifact.hasClassifier()) &&
                                    (artifact.getClassifier().equalsIgnoreCase(MavenConstants.SOURCES_CLASSIFIER) ||
                                     artifact.getClassifier().equalsIgnoreCase(MavenConstants.JAVADOC_CLASSIFIER))) {

                                    continue;
                                }

                                artifacts.add(artifact);
                            }

                            pomToArtifact.put(pom, artifacts);
                            moduleToArtifact.get(module).addAll(artifacts);
                        }
                    }

                    // download sources and javadoc for dependencies
                    if (config.isDownloadSourcesEnabled() || config.isDownloadJavadocEnabled()) {

                        indicator.setText("Downloading supporting artifacts (sources and/or javadoc).");

                        for (Module module : moduleToFile.keySet()) {
                            for(VirtualFile pomFile : moduleToFile.get(module)) {

                                MavenProject pom = fileToPom.get(pomFile);

                                for (Artifact artifact : pomToArtifact.get(pom)) {

                                    // check for bail out
                                    if (indicator.isCanceled()) {
                                        throw new MavenWorkerCancelledException();
                                    }

                                    indicator.setText2(artifact.toString());

                                    // try to get sources for all except system deps
                                    if (!artifact.getScope().equalsIgnoreCase(MavenConstants.SYSTEM_SCOPE)) {

                                        // sources
                                        if (config.isDownloadSourcesEnabled()) {

                                            try {

                                                maven.resolve(maven.createArtifactWithClassifier(artifact.getGroupId(),
                                                                                                 artifact.getArtifactId(),
                                                                                                 artifact.getVersion(),
                                                                                                 MavenConstants.JAR_PACKAGING,
                                                                                                 MavenConstants.SOURCES_CLASSIFIER),
                                                              pom.getRemoteArtifactRepositories(),
                                                              maven.getLocalRepository());
                                            }
                                            catch (Exception e) {
                                                logger.debug("Could not resolve source dependency for: " + artifact);
                                            }
                                        }

                                        // javadoc
                                        if (config.isDownloadSourcesEnabled()) {

                                            try {

                                                maven.resolve(maven.createArtifactWithClassifier(artifact.getGroupId(),
                                                                                                 artifact.getArtifactId(),
                                                                                                 artifact.getVersion(),
                                                                                                 MavenConstants.JAR_PACKAGING,
                                                                                                 MavenConstants.JAVADOC_CLASSIFIER),
                                                              pom.getRemoteArtifactRepositories(),
                                                              maven.getLocalRepository());
                                            }
                                            catch (Exception e) {
                                                logger.debug("Could not resolve javadoc dependency for: " + artifact);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // weed out multiple version of the same dependency
                    if (config.isRemoveDuplicateDependenciesEnabled()) {

                        for (Module module : moduleToFile.keySet()) {

                            indicator.setText("Removing duplicate dependencies from classpath for module: " + module.getName());

                            // keep track fo the most appropriate version of each artifact.
                            // keep by group and artifact ID
                            Map<String, Artifact> artifactMap = new HashMap<String, Artifact>();

                            for (Artifact artifact : moduleToArtifact.get(module)) {

                                // check for bail out
                                if (indicator.isCanceled()) {
                                    throw new MavenWorkerCancelledException();
                                }

                                indicator.setText2(artifact.toString());

                                String id = artifact.getGroupId() + "." + artifact.getArtifactId();

                                if (!artifactMap.containsKey(id)) {
                                    artifactMap.put(id, artifact);
                                }
                                else {

                                    // see if this artifact is more appropriate than the one in storage
                                    ArtifactVersion currVersion = new DefaultArtifactVersion(artifactMap.get(id).getVersion());
                                    ArtifactVersion newVersion = new DefaultArtifactVersion(artifact.getVersion());

                                    if (newVersion.compareTo(currVersion) > 0) {
                                        artifactMap.put(id, artifact);
                                    }
                                }
                            }

                            // store the trimmed artifacts for each module
                            moduleToArtifact.put(module, new HashSet<Artifact>(artifactMap.values()));
                        }
                    }

                    // set up module inter-dependencies
                    Set<Module> neededModules = new HashSet<Module>();

                    if (config.isManageModuleInterdependenciesEnabled()) {

                        for (Module module : moduleToFile.keySet()) {

                            logger.debug("Setting inter-dependencies for module: " + module.getName());

                            // check for bail out
                            if (indicator.isCanceled()) {
                                throw new MavenWorkerCancelledException();
                            }

                            indicator.setText("Setting inter-dependencies for module: " + module.getName());

                            // if this is the first access, save the model
                            if (!dirtyModules.containsKey(module)) {
                                dirtyModules.put(module, ModuleRootManager.getInstance(module).getModifiableModel());
                            }

                            final ModifiableRootModel rootModel = dirtyModules.get(module);

                            Map<String, Set<String>> interDependencies = config.getModuleInterDependencies();

                            // first get rid of any inter-deps we set previously
                            if (interDependencies.containsKey(module.getName())) {

                                for (OrderEntry mod: rootModel.getOrderEntries()) {

                                    if (mod instanceof ModuleOrderEntry) {

                                        // make sure we previously added this dependency, if not it was added by the user
                                        if (interDependencies.get(module.getName()).contains(mod.getPresentableName())) {
                                            rootModel.removeOrderEntry(mod);
                                        }
                                    }
                                }

                                interDependencies.remove(module.getName());
                            }

                            // check to see if this module has any dependencies
                            if (moduleToModule.containsKey(module)) {

                                // store the existing deps in a collection
                                Set<Module> existingDeps = new HashSet<Module>();

                                for (Module mod: rootModel.getModuleDependencies()) {
                                    existingDeps.add(mod);
                                }

                                Set<String> added = new TreeSet<String>();

                                // go through all the modules that this module depends on
                                for (Module depModule : moduleToModule.get(module)) {

                                    // add the other module to the dependency list
                                    if (!existingDeps.contains(depModule) && !added.contains(depModule.getName())) {

                                        rootModel.addModuleOrderEntry(depModule);
                                    }

                                    added.add(depModule.getName());
                                }

                                // reorder the module dependencies to put other modules on top of classpath jars
                                rootModel.rearrangeOrderEntries(organizeDependencies(rootModel.getOrderEntries()));

                                // store off the module dependencies set
                                interDependencies.put(module.getName(), added);
                            }
                        }

                        // construct a list of all modules that are depended upon by others
                        for (Module mod : moduleToModule.keySet()) {
                            neededModules.addAll(moduleToModule.get(mod));
                        }
                    }

                    // setup module classpaths
                    if (config.isUpdateClasspathsEnabled()) {

                        for (Module module : ModuleManager.getInstance(project).getModules()) {

                            logger.debug("Inspecting libraries for module: " + module.getName());

                            // check for bail out
                            if (indicator.isCanceled()) {
                                throw new MavenWorkerCancelledException();
                            }

                            indicator.setText("Modifying classpath for module: " + module.getName());

                            Map<String, Artifact> mavenLibraries = new HashMap<String, Artifact>();

                            // if this is the first access, save the model
                            if (!dirtyModules.containsKey(module)) {
                                dirtyModules.put(module, ModuleRootManager.getInstance(module).getModifiableModel());
                            }

                            final ModifiableRootModel rootModel = dirtyModules.get(module);

                            // save the library table
                            if (!dirtyLibTables.containsKey(module)) {
                                dirtyLibTables.put(module, rootModel.getModuleLibraryTable().getModifiableModel());
                            }

                            final LibraryTable.ModifiableModel libTable = dirtyLibTables.get(module);

                            // make a place to save modified libraries from this module
                            dirtyLibraries.put(module, new HashMap<String, Library.ModifiableModel>());

                            // only modify classpath for module with maven pom in it
                            if (moduleToArtifact.containsKey(module)) {

                                // get the list of all (transitively resolved) library dependencies
                                for (Artifact dep : moduleToArtifact.get(module)) {

                                    // look to see if the library is already in the module
                                    Library library = libTable.getLibraryByName(getLibraryName(dep));

                                    // not already in the classpath, try to add it
                                    if ((library == null) || (library.getUrls(OrderRootType.CLASSES).length == 0)) {

                                        logger.debug("Adding new library: " + dep);

                                        // get the compiled jar first
                                        String jarPath = PluginPomManager.unifyPath(getFileSystemPath(dep, null));
                                        String jarURL = VirtualFileManager.constructUrl(JarFileSystem.PROTOCOL, jarPath) + JarFileSystem.JAR_SEPARATOR;

                                        VirtualFile jarClassesFile = VirtualFileManager.getInstance().findFileByUrl(jarURL);

                                        if (jarClassesFile != null) {

                                            // make a new library
                                            if (library == null) {
                                                library = libTable.createLibrary(getLibraryName(dep));
                                            }

                                            // get the model for the module's library
                                            if (!dirtyLibraries.get(module).containsKey(library.getName())) {
                                                dirtyLibraries.get(module).put(library.getName(), library.getModifiableModel());
                                            }

                                            final Library.ModifiableModel libraryModel = dirtyLibraries.get(module).get(library.getName());

                                            // add the library to the model
                                            libraryModel.addRoot(jarClassesFile, OrderRootType.CLASSES);
                                        }
                                        else {
                                            logger.warn("Could not locate library file: " + jarPath);
                                        }
                                    }

                                    // only add sources for non system-scoped dependencies
                                    if ((library != null) && !dep.getScope().equalsIgnoreCase(MavenConstants.SYSTEM_SCOPE)) {

                                        // get the model for the module's library
                                        if (!dirtyLibraries.get(module).containsKey(library.getName())) {
                                            dirtyLibraries.get(module).put(library.getName(), library.getModifiableModel());
                                        }

                                        final Library.ModifiableModel libraryModel = dirtyLibraries.get(module).get(library.getName());

                                        // get the source jar
                                        String jarSourcePath = PluginPomManager.unifyPath(getFileSystemPath(dep, MavenConstants.SOURCES_CLASSIFIER));
                                        String jarSourceURL = VirtualFileManager.constructUrl(JarFileSystem.PROTOCOL, jarSourcePath) + JarFileSystem.JAR_SEPARATOR;

                                        VirtualFile jarSourcesFile = VirtualFileManager.getInstance().findFileByUrl(jarSourceURL);

                                        // add the source jar, if it is available
                                        if (jarSourcesFile != null) {

                                            // make sure that the sources jar isn't already added
                                            VirtualFile[] existingSources = libraryModel.getFiles(OrderRootType.SOURCES);
                                            boolean alreadyAdded = false;

                                            if (existingSources != null) {

                                                // look at all the currently attached files
                                                for (VirtualFile source : existingSources) {

                                                    if (source.equals(jarSourcesFile)) {

                                                        alreadyAdded = true;
                                                        break;
                                                    }
                                                }
                                            }

                                            // add the source jar
                                            if (!alreadyAdded) {
                                                libraryModel.addRoot(jarSourcesFile, OrderRootType.SOURCES);
                                            }
                                        }

                                        // get the javadoc jar
                                        String jarJavadocPath = PluginPomManager.unifyPath(getFileSystemPath(dep, MavenConstants.JAVADOC_CLASSIFIER));
                                        String jarJavadocURL = VirtualFileManager.constructUrl(JarFileSystem.PROTOCOL, jarJavadocPath) + JarFileSystem.JAR_SEPARATOR;

                                        VirtualFile jarJavadocFile = VirtualFileManager.getInstance().findFileByUrl(jarJavadocURL);

                                        // add the javadoc jar, if it is available
                                        if (jarJavadocFile != null) {

                                            // make sure that the javadoc jar isn't already added
                                            VirtualFile[] existingJavadoc = libraryModel.getFiles(OrderRootType.JAVADOC);
                                            boolean alreadyAdded = false;

                                            if (existingJavadoc != null) {

                                                // look at all the currently attached files
                                                for (VirtualFile source : existingJavadoc) {

                                                    if (source.equals(jarJavadocFile)) {

                                                        alreadyAdded = true;
                                                        break;
                                                    }
                                                }
                                            }

                                            // add the javadoc jar
                                            if (!alreadyAdded) {
                                                libraryModel.addRoot(jarJavadocFile, OrderRootType.JAVADOC);
                                            }
                                        }
                                        else {
                                            logger.debug("Could not locate javadoc library: " + jarJavadocPath);
                                        }
                                    }

                                    // record this library as being processed
                                    mavenLibraries.put(getLibraryName(dep), dep);
                                }
                            }

                            indicator.setText2("Removing stale libraries");

                            // go through the library table and remove any stale maven related libraries
                            for (Library lib : libTable.getLibraries()) {

                                // check if it is a maven related library -- leave all others alone
                                if ((lib.getName() != null) && (lib.getName().indexOf(LIBRARY_PREFIX) >= 0)) {

                                    // check to see if it is in the latest set of updates
                                    if (!mavenLibraries.containsKey(lib.getName())) {

                                        logger.debug("Removing old library: " + lib.getName());

                                        libTable.removeLibrary(lib);
                                    }
                                }
                            }

                            // if this is a J2EE module, make sure to package the appropriate jars only
                            if (module.getModuleType().isJ2EE()) {

                                TransactionalEditable trans = dirtyJ2eeProps.get(rootModel);

                                if (trans == null) {
                                    trans = JavaeeModuleProperties.getInstance(module);
                                }

                                trans.startEdit(rootModel);

                                ModuleContainer container = trans.getModifiableModel(); 

                                // todo: fix this so that the list of packaged jars is consistent with the libraries in the module
                                for (LibraryLink lib : container.getContainingLibraries()) {

                                    // check if it is a maven related library -- leave all others alone
                                    if (mavenLibraries.containsKey(lib.getName())) {

                                        Artifact dep = mavenLibraries.get(lib.getName());

                                        // only do something if it is a provided or test scope dependency
                                        if (dep.getScope().equalsIgnoreCase(MavenConstants.PROVIDED_SCOPE)) {

                                            // make sure that these deps are not packaged
                                            if (!lib.getPackagingMethod().equals(J2EEPackagingMethod.DO_NOT_PACKAGE)) {

                                                lib.setPackagingMethod(J2EEPackagingMethod.DO_NOT_PACKAGE);

                                                // store off the change to be committed later
                                                dirtyJ2eeProps.put(rootModel, trans);
                                            }
                                        }
                                    }
                                }
                            }

                            // if this module is a dependency of another, export all maven libraries
                            if (config.isManageModuleInterdependenciesEnabled() && neededModules.contains(module)) {

                                for (OrderEntry entry : rootModel.getOrderEntries()) {

                                    if (entry instanceof LibraryOrderEntry) {

                                        LibraryOrderEntry lib = (LibraryOrderEntry)entry;

                                        // export the library if it comes from maven
                                        if (mavenLibraries.containsKey(lib.getLibraryName())) {

                                            lib.setExported(true);
                                        }
                                    }
                                    else if (entry instanceof ModuleOrderEntry) {

                                        ModuleOrderEntry mod = (ModuleOrderEntry)entry;

                                        // export modules that are needed by this module
                                        mod.setExported(true);
                                    }
                                }
                            }
                        }
                    }

                    // generate sources and determine source roots
                    if (config.isManageSourceRootsEnabled()) {

                        for (Module module : moduleToFile.keySet()) {

                            logger.debug("Inspecting source paths for module: " + module.getName());
                            indicator.setText("Determining source roots for module: " + module.getName());

                            // get the module's model
                            if (!dirtyModules.containsKey(module)) {
                                dirtyModules.put(module, ModuleRootManager.getInstance(module).getModifiableModel());
                            }

                            final ModifiableRootModel rootModel = dirtyModules.get(module);

                            // go though all the content roots
                            ContentEntry[] entries = rootModel.getContentEntries();

                            for (ContentEntry root : entries) {

                                // check for bail out
                                if (indicator.isCanceled()) {
                                    throw new MavenWorkerCancelledException();
                                }

                                VirtualFile rootFile = root.getFile();
                                String rootPath = rootFile.getPath();

                                // get the pom files found in this root
                                Set<VirtualFile> pomsInRoot = rootToFile.get(rootFile);

                                // if no poms are in this root, make no changes to the source roots
                                if (pomsInRoot == null) {
                                    continue;
                                }

                                if (config.isGenerateSourcesEnabled()) {

                                    // run generate-sources on each pom file to produce sources (like jaxb)
                                    List<String> goals = new ArrayList<String>();
                                    Properties props = new Properties();

                                    goals.add("clean");
                                    goals.add("generate-sources");
                                    goals.add("generate-resources");
                                    goals.add("generate-test-sources");
                                    goals.add("generate-test-resources");

                                    for (VirtualFile pomFile : pomsInRoot) {

                                        // run the goals on the pom file and capture the Maven results
                                        ReactorManager results = runMaven(pomFile, goals, props, indicator, false, ReactorManager.FAIL_NEVER);

                                        if (results != null) {

                                            // get the new project object out of the results
                                            MavenProject proj = results.getTopLevelProject();

                                            // store the new project over the old
                                            if (PluginPomManager.unifyPath(proj.getFile().getPath()).equalsIgnoreCase(pomFile.getPath())) {
                                                fileToPom.put(pomFile, proj);
                                            }
                                        }
                                    }
                                }

                                // clear out any old source roots, only if there is a pom in this content root
                                if (pomsInRoot.size() > 0) {

                                    for (SourceFolder source : root.getSourceFolders()) {
                                        root.removeSourceFolder(source);
                                    }
                                }

                                // go though each pom file and try to process it
                                for (VirtualFile pomFile : pomsInRoot) {

                                    indicator.setText2("Processing POM: " + pomFile.getPath());

                                    // get the loaded pom project model
                                    MavenProject pom = fileToPom.get(pomFile);

                                    VirtualFile rootToAdd = null;

                                    // add all compile roots
                                    if (pom.getCompileSourceRoots() != null) {

                                        for (String compileRoot : (List<String>)pom.getCompileSourceRoots()) {

                                            rootToAdd = rootFile.findFileByRelativePath(PluginPomManager.unifyPath(compileRoot).substring(rootPath.length() + 1));

                                            if (rootToAdd != null) {
                                                root.addSourceFolder(rootToAdd, false);
                                            }
                                        }
                                    }

                                    // add resources
                                    if (pom.getResources() != null) {

                                        for (Resource resourceRoot : (List<Resource>)pom.getResources()) {

                                            rootToAdd = rootFile.findFileByRelativePath(PluginPomManager.unifyPath(resourceRoot.getDirectory()).substring(rootPath.length() + 1));

                                            if (rootToAdd != null) {
                                                root.addSourceFolder(rootToAdd, false);
                                            }
                                        }
                                    }

                                    // add all test compile roots
                                    if (pom.getTestCompileSourceRoots() != null) {

                                        for (String testCompileRoot : (List<String>)pom.getTestCompileSourceRoots()) {
                                            rootToAdd = rootFile.findFileByRelativePath(PluginPomManager.unifyPath(testCompileRoot).substring(rootPath.length() + 1));

                                            if (rootToAdd != null) {
                                                root.addSourceFolder(rootToAdd, true);
                                            }
                                        }
                                    }

                                    // add test resources
                                    if (pom.getTestResources() != null) {

                                        for (Resource testResourceRoot : (List<Resource>)pom.getTestResources()) {
                                            rootToAdd = rootFile.findFileByRelativePath(PluginPomManager.unifyPath(testResourceRoot.getDirectory()).substring(rootPath.length() + 1));

                                            if (rootToAdd != null) {
                                                root.addSourceFolder(rootToAdd, true);
                                            }
                                        }
                                    }
                                }

                                // todo: add exclusions when maven handles them
                            }
                        }
                    }

                    // if we are supposed to be sorting the dependencies, do so now
                    if (config.isSortDependenciesEnabled()) {

                        for (Module module : moduleToFile.keySet()) {

                            // get the model for the module
                            if (!dirtyModules.containsKey(module)) {
                                dirtyModules.put(module, ModuleRootManager.getInstance(module).getModifiableModel());
                            }

                            final ModifiableRootModel rootModel = dirtyModules.get(module);

                            rootModel.rearrangeOrderEntries(organizeDependencies(rootModel.getOrderEntries()));
                        }
                    }

                    // finished!
                    indicator.setFraction(1.0);
                }
                catch(MavenWorkerCancelledException e) {
                    logger.error("POM processing cancelled by user. Results may vary.");
                }
            }


            /**
             * Kicks off a top level search for POM files.
             */
            private void kickOffSearch() {

                // get the modules in the project
                Module[] modules = ModuleManager.getInstance(project).getModules();

                if (modules == null) {
                    logger.warn("There are no project modules.");
                    return;
                }

                for (Module module : modules) {
                    locatePoms(module);
                }
            }


            /**
             * Searches for POM files in a specific module.
             * @param module
             */
            private void locatePoms(Module module) {

                // get the content roots
                ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
                ModifiableRootModel model = moduleRootManager.getModifiableModel();
                VirtualFile[] roots = model.getContentRoots();

                if (roots == null) {
                    logger.warn("There are no source roots in the module: " + module.getName());
                    return;
                }

                // search for poms in the content roots
                for (VirtualFile root : roots) {

                    if (!passesSearchFilter(root)) {
                        continue;
                    }

                    checkForPoms(root, root, module);
                }
            }


            /**
             * Recursively checks the module for POM files. The very first file is never checked, assumed to be content root...
             *
             * @param root The content root to wich this file/directory belongs.
             * @param file The current file/directory being searched.
             * @param currentModule The module that the file belongs to.
             */
            private void checkForPoms(VirtualFile root, VirtualFile file, Module currentModule) {

                VirtualFile[] children = file.getChildren();

                if (children == null) {
                    return;
                }

                // look at each child node of this file/directory
                for (VirtualFile child : children) {

                    if (isPomFile(child) && isPomEnabled(child)) {

                        logger.debug("Found POM in module: " + currentModule.getName() + ": " + child.getPath());

                        if (!rootToFile.containsKey(root)) {
                            rootToFile.put(root, new HashSet<VirtualFile>());
                        }

                        rootToFile.get(root).add(child);

                        if (!moduleToFile.containsKey(currentModule)) {
                            moduleToFile.put(currentModule, new ArrayList<VirtualFile>());
                        }

                        moduleToFile.get(currentModule).add(child);
                    }
                    else if (child.isDirectory() && passesSearchFilter(child)) {
                        checkForPoms(root, child, currentModule);
                    }
                }
            }


            /**
             * Checks if a directory passes the filter setup by the user.
             *
             * @param directory The directory to check
             *
             * @return True if the directory is not being filtered, false otherwise.
             */
            private boolean passesSearchFilter(VirtualFile directory) {

                for (String exclude : config.getSearchFilter().split(",")) {

                    // do a case insensitive name match
                    // todo: pattern matching
                    if (directory.getName().equalsIgnoreCase(exclude)) {
                        return false;
                    }
                }

                return true;
            }


            /**
             * Organizes the dependency entries of a module to specify a specific precedence.
             * That precedence is JDK, module source, other modules, classpath jars, then other entries.
             *
             * @param originalOrder The entries in their original order.
             *
             * @return The same entries re-organized to be in the order described above.
             */
            private OrderEntry[] organizeDependencies(OrderEntry[] originalOrder) {

                // keep separate lists of the types of entries
                List<OrderEntry> jdkEntries = new ArrayList<OrderEntry>();
                List<OrderEntry> sourceEntries = new ArrayList<OrderEntry>();
                List<OrderEntry> moduleEntries = new ArrayList<OrderEntry>();
                List<OrderEntry> libraryEntries = new ArrayList<OrderEntry>();
                List<OrderEntry> otherEntries = new ArrayList<OrderEntry>();

                for (OrderEntry entry : originalOrder) {

                    if (entry instanceof JdkOrderEntry) {
                        jdkEntries.add(entry);
                    }
                    else if (entry instanceof ModuleSourceOrderEntry) {
                        sourceEntries.add(entry);
                    }
                    else if (entry instanceof ModuleOrderEntry) {
                        moduleEntries.add(entry);
                    }
                    else if (entry instanceof LibraryOrderEntry) {
                        libraryEntries.add(entry);
                    }
                    else {
                        otherEntries.add(entry);
                    }
                }

                // sort the dependencies
                if (config.isSortDependenciesEnabled()) {

                    Comparator<OrderEntry> comparator = new OrderEntryComparator();

                    Collections.sort(jdkEntries, comparator);
                    Collections.sort(sourceEntries, comparator);
                    Collections.sort(moduleEntries, comparator);
                    Collections.sort(libraryEntries, comparator);
                    Collections.sort(otherEntries, comparator);
                }

                // the master list
                List<OrderEntry> ordered = new ArrayList<OrderEntry>();

                // this will put the types in order, but still preserve any orderering the user did within a type,
                // like ordering the classpath jar entries (if they were not just messed with by maven)
                ordered.addAll(jdkEntries);
                ordered.addAll(sourceEntries);
                ordered.addAll(moduleEntries);
                ordered.addAll(libraryEntries);
                ordered.addAll(otherEntries);

                return ordered.toArray(new OrderEntry[0]);
            }


            /**
             * Gets the path to the file on the disk.
             *
             * @param artifact The artifact sought.
             * @param classifier An additional classifier to tack onto the aritfact.
             *
             * @return The path to the file on disk.
             */
            String getFileSystemPath(Artifact artifact, String classifier) {

                String base = null;

                try {
                    base = artifact.getFile().getPath();
                }
                catch(NullPointerException npe) {
                    return null;
                }

                if (classifier == null) {
                    return base;
                }

                return base.substring(0, base.lastIndexOf(".")) + "-" + classifier + "." + artifact.getType();
            }


            /**
             * Tries to locate a module that represents a needed dependency.
             *
             * @param artifact The artifact being sought.
             *
             * @return The Module that has a POM that represents the artifact, or null if none is found.
             */
            private Module checkForModuleDependency(Artifact artifact) {

                Module ret = moduleAsArtifact.get(artifact);

                // only check for snapshots because the map keys are stored as the original 'SNAPSHOT', and
                // not in terms of the base version that may have been resolved for this artifact
                if ((ret == null) && artifact.isSnapshot()) {

                    for (Artifact art : moduleAsArtifact.keySet()) {

                        if (art.getGroupId().equals(artifact.getGroupId()) &&
                            art.getArtifactId().equals(artifact.getArtifactId()) &&
                            art.getVersion().equals(artifact.getBaseVersion())) {

                            // found a SNAPSHOT that matches
                            ret = moduleAsArtifact.get(art);
                            break;
                        }
                    }
                }

                return ret;
            }


            /**
             * Gets the pretty name that goes into the module library.
             *
             * @param artifact Thhe artifact being added to the library.
             *
             * @return The human readable library name.
             */
            public String getLibraryName(Artifact artifact) {
                return LIBRARY_PREFIX + artifact;
            }

        }, "Processing Maven POMs", true, project);


        // commit any changes to libraries, library tables, J2EE properties, and modules that have been dirtied.
        // this has to take place out here instead of in the parsing thread because IntelliJ throws an error
        // if the modification is not in the Event Dispatch Thread.
        for (Module module : dirtyLibraries.keySet()) {

            for (final Library.ModifiableModel lib : dirtyLibraries.get(module).values()) {

                if (lib.isChanged()) {

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {

                        public void run() {
                            lib.commit();
                        }
                    });
                }
            }
        }

        for (final LibraryTable.ModifiableModel libTable : dirtyLibTables.values()) {

            if (libTable.isChanged()) {

                ApplicationManager.getApplication().runWriteAction(new Runnable() {

                    public void run() {
                        libTable.commit();
                    }
                });
            }
        }

        for (final ModifiableRootModel rootModel : dirtyModules.values()) {

            // check to see if there are J2EE properties to commit first
            if (dirtyJ2eeProps.containsKey(rootModel)) {

                final TransactionalEditable trans = dirtyJ2eeProps.get(rootModel);

                if (trans.isModified(rootModel)) {

                    ApplicationManager.getApplication().runWriteAction(new Runnable() {

                        public void run() {

                            try {
                                trans.commit(rootModel);
                            }
                            catch(ConfigurationException ce) {

                                logger.warn("Could not commit changes to module J2EE packaging for module: " +
                                            rootModel.getModule().getName());
                            }
                        }
                    });
                }
            }

            // commit changes to the module
            if (rootModel.isChanged()) {

                ApplicationManager.getApplication().runWriteAction(new Runnable() {

                    public void run() {
                        rootModel.commit();
                    }
                });
            }
        }
    }


    /**
     * Executes a number of maven goals in the embedder.
     *
     * @param pomFile The POM file that is being executed.
     * @param goals The goals to execute on the POM.
     * @param props A set of properties that is passed to maven (example: http.proxyHost=myproxy).
     * @param recursive A flag to indicate if the execution of this POM should be recursive.
     * @param failureBehavior The Maven failure policy, one of (ReactorManager.FAIL_FAST, FAIL_AT_END, or FAIL_NEVER).
     */
    public void executeGoals(final VirtualFile pomFile,
                             final List<String> goals, final Properties props,
                             final boolean recursive, final String failureBehavior) {

        // do this all in a separate thread controlled by the IDE
        ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable() {

            public void run() {

                try {
                    runMaven(pomFile, goals, props, ProgressManager.getInstance().getProgressIndicator(), recursive, failureBehavior);
                }
                catch(MavenWorkerCancelledException e) {
                    logger.warn("User cancelled execution of POM: " + pomFile.getPath());
                }
            }

        }, "Maven Execution", true, project);
    }


    /**
     * A helper method that executes a mavne goal list. It is a prerequisite that this method be called from within a
     * thread running in the Application synchronously (inside a call to Application.runProcessWithProgressSynchronously).
     *
     * @param pomFile The VirtualFile that represents the pom.xml file being operated on.
     * @param goals The list of goals that are to be run on the maven pom.
     * @param props A set of properties that is passed to maven (example: http.proxyHost=myproxy)
     * @param indicator The progress indicator from the ProgressManager that is running the executing thread.
     * @param recursive A flag to indicate if the execution of this POM should be recursive.
     * @param failureBehavior The Maven failure policy, one of (ReactorManager.FAIL_FAST, FAIL_AT_END, or FAIL_NEVER).
     *
     * @return The build results, or null if an exception was thrown inside Maven.
     *
     * @throws MavenWorkerCancelledException If the user cancels the action before it is complete.
     */
    private ReactorManager runMaven(VirtualFile pomFile, List<String> goals, Properties props,
                                    final ProgressIndicator indicator, boolean recursive, String failureBehavior) throws MavenWorkerCancelledException {

        // save the previous indicator settings in case we are running from within another context
        boolean indicatorWasIndeterminate = indicator.isIndeterminate();
        double indicatorPreviousValue = indicator.getFraction();

        // construct a string representation of the goals list for human consumption
        String goalList = goals.get(0);

        if (goals.size() > 1) {

            StringBuffer buff = new StringBuffer();

            for (String goal : goals) {

                buff.append(goal);
                buff.append(" ");
            }

            goalList = buff.toString().trim().replaceAll(" ", ", ");
        }

        logger.debug("Executing POM: " +  pomFile.getPath() + " with goals: " + goalList);

        // cant tell the progress being made, so dont even try
        indicator.setIndeterminate(true);
        indicator.setFraction(0.1);

        indicator.setText("Compiling Execution Request");

        MavenExecutionRequest req = new DefaultMavenExecutionRequest();

        // make a request out of the known information
        req.setPomFile(pomFile.getPath());
        req.setBasedir(new File(pomFile.getParent().getPath()));
        req.setLocalRepository(maven.getLocalRepository());
        req.setSettings(maven.getSettings());
        req.setProperties(props);
        req.setRecursive(recursive);
        req.setFailureBehavior(failureBehavior);

        // add event monitors to see what is going on with the build
        req.addEventMonitor(new DefaultEventMonitor(new PlexusLoggerAdapter(PluginLoggerManager.getInstance(project).getEmbedderLogger())));
        req.addEventMonitor(new EventMonitor() {

            public void startEvent(String eventName, String target, long timestamp) {

                // check for bail out
                if (indicator.isCanceled()) {
                    throw new MavenWorkerCancelledException();
                }

                // show what is going on in the progress bar
                if (eventName.equals("mojo-execute")) {
                    indicator.setText2(target);
                }
            }

            public void endEvent(String eventName, String target, long timestamp) {
            }

            public void errorEvent(String eventName, String target, long timestamp, Throwable cause) {
            }
        });

        ReactorManager results = null;

        // set the goals to execute
        req.setGoals(goals);

        indicator.setText("Executing POM: " +  pomFile.getPath());

        try {
            results = maven.execute(req);
        }
        catch(MavenExecutionException e) {
            logger.error("Maven execution failed.");
        }

        // reset the indicator to the previous values
        indicator.setIndeterminate(indicatorWasIndeterminate);
        indicator.setFraction(indicatorPreviousValue);

        return results;
    }


    /**
     * Changes all paths to have the same (UNIX) separator.
     *
     * @param input The original string.
     *
     * @return The modified string, with '\' changed to '/'.
     */
    private static String unifyPath(String input) {
        return input.replaceAll("\\\\", "/");
    }


    /**
     * Checks if a POM file is in the disabled list.
     *
     * @param pomFile The POm file in question.
     *
     * @return True if the POM file is enabled, false if it is not being parsed.
     */
    public boolean isPomEnabled(VirtualFile pomFile) {
        return !config.getDisabledPoms().contains(pomFile.getPath());
    }


    /**
     * Sets the status of a POM file.
     *
     * @param pomFile The POM file in question.
     * @param enabled True if the POM should be included in parsing, false otherwise.
     */
    public void setPomEnabled(VirtualFile pomFile, boolean enabled) {

        if (enabled) {
            config.getDisabledPoms().remove(pomFile.getPath());
        }
        else {
            config.getDisabledPoms().add(pomFile.getPath());
        }
    }


    /**
     * Determines if a virtual file is a POM file.
     *
     * @param file The file in question.
     *
     * @return True if the file is a POM file, false otherwise.
     */
    private boolean isPomFile(VirtualFile file) {
        return file.getName().equalsIgnoreCase(MavenConstants.POM_NAME);
    }


    /**
     * Determines if a virtual file is a POM file from this project.
     *
     * @param file The file in question.
     *
     * @return True if the file is a POM file from this project, false otherwise.
     */
    private boolean isKnownPomFile(VirtualFile file) {
        return file.getName().equalsIgnoreCase(MavenConstants.POM_NAME) && knownPoms.contains(file);
    }


    /**
     * {@inheritDoc}
     *
     * Adds this as a virtual file listener and runs the first pom search.
     */
    public void projectOpened() {

        VirtualFileManager.getInstance().addVirtualFileListener(pomFileListener);
        ModuleManager.getInstance(project).addModuleListener(this);

        updateProjectModules();
    }


    /**
     * {@inheritDoc}
     *
     * Unregisters this clss as a virtual file listener.
     */
    public void projectClosed() {

        VirtualFileManager.getInstance().removeVirtualFileListener(pomFileListener);
        ModuleManager.getInstance(project).removeModuleListener(this);
    }


    /**
     * {@inheritDoc}
     *
     * Updates the classpath of the newly added module to reflect any POM files in its content roots.
     */
    public void moduleAdded(Project project, Module module) {
        updateProjectModules();
    }


    /** {@inheritDoc} */
    public void beforeModuleRemoved(Project project, Module module) {
    }


    /**
     * {@inheritDoc}
     *
     * Removes the module from the Maven processing chain and updates the classpaths of all other modules.
     */
    public void moduleRemoved(Project project, Module module) {
        updateProjectModules();
    }


    /** {@inheritDoc} */
    public void modulesRenamed(Project project, List<Module> modules) {
        updateProjectModules();
    }


    /**
     * A class that acts as a POM file listener. It looks for changes to existing POMs, as well as additions and
     * deletions of POM files.
     */
    private class PomFileListener extends VirtualFileAdapter {

        /** {@inheritDoc} */
        public void contentsChanged(VirtualFileEvent event) {

            if (shouldAct(event)) {

                // only act on the change if the pom is enabled
                if (isPomEnabled(event.getFile())) {
                    updateProjectModules();
                }

                logger.debug("POM file changed: " + event.getFile().getPath());
            }
        }


        /** {@inheritDoc} */
        public void fileCreated(VirtualFileEvent event) {

            if (shouldAct(event)) {

                // do not enable the pom when it is brand new
                setPomEnabled(event.getFile(), false);

                // it is a new file, but check anyway
//                if (isPomEnabled(event.getFile())) {
//                    updateProjectModules();
//                }

                logger.info("POM file created (initially disabled): " + event.getFile().getPath());
            }
        }


        /** {@inheritDoc} */
        public void fileDeleted(VirtualFileEvent event) {

            if (shouldAct(event)) {

                // remove it from the disabled pom list
                config.getDisabledPoms().remove(event.getFile().getPath());

                updateProjectModules();

                logger.debug("POM file deleted: " + event.getFile().getPath());
            }
        }


        /** {@inheritDoc} */
        public void fileMoved(VirtualFileMoveEvent event) {

            if (shouldAct(event)) {

                updateProjectModules();

                logger.debug("POM file moved: " + event.getFile().getPath());
            }
        }


        /**
         * Determines if the listener should take action on the event.
         *
         * @param event The event that triggered the listener.
         *
         * @return True if action should be taken, false otherwise.
         */
        private boolean shouldAct(VirtualFileEvent event) {
            return config.isPluginEnabled() && config.isRespondToPomChangesEnabled() && isKnownPomFile(event.getFile());
        }
    }


    /**
     * A simple Comparator that can compare OrderEntries by name.
     */
    private static class OrderEntryComparator implements Comparator<OrderEntry> {

        public int compare(OrderEntry o1, OrderEntry o2) {
            return o1.getPresentableName().compareTo(o2.getPresentableName());
        }
    }


    /**
     * A simple exception that is thrown when a user cancels a maven action.
     */
    private static class MavenWorkerCancelledException extends RuntimeException {

    }

}
