/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mevenide.context.DefaultQueryContext;
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.environment.ILocationFinder;
import org.mevenide.environment.LocationFinderAggregator;
import org.mevenide.runner.RunnerHelper;
import org.mevenide.ui.eclipse.classpath.MavenClasspathManager;
import org.mevenide.ui.eclipse.nature.ActionDefinitionsManager;
import org.mevenide.ui.eclipse.pom.manager.DefaultPOMManager;
import org.mevenide.ui.eclipse.pom.manager.POMManager;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.ui.eclipse.preferences.dynamic.DynamicPreferencesManager;
import org.mevenide.ui.eclipse.util.ExceptionHandler;
import org.mevenide.ui.eclipse.util.LifecycleListener;
import org.mevenide.util.StringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 * @todo clean-me move preferences related fields outside of Mevenide class 
 *  
 */
public class Mevenide extends AbstractUIPlugin {

    private static Log log = LogFactory.getLog(Mevenide.class);

    private static Mevenide plugin;

    private Object lock = new Object();

    public static final String NATURE_ID = "org.mevenide.ui.mavennature"; //$NON-NLS-1$
    public static final String SYNCHRONIZE_VIEW_ID = "org.mevenide.ui.synchronize.view.SynchronizationView"; //$NON-NLS-1$
    public static final String PLUGIN_ID = "org.mevenide.ui"; //$NON-NLS-1$
    public static final String PLUGIN_NAME = "Mevenide"; //$NON-NLS-1$

    private static final String ICONS_PATH = "icons/"; //$NON-NLS-1$
    private static final String LIB_FOLDER = "lib/"; //$NON-NLS-1$
    private static final String FOREHEAD_LIBRARY = "forehead-1.0-beta-5.jar"; //$NON-NLS-1$
    private static final String PROJECT_PROPERTIES_FILE_NAME = "project.properties"; //$NON-NLS-1$

    private ResourceBundle resourceBundle;
    private ExceptionHandler exceptionHandler;
    private POMManager pomManager;
    private MavenClasspathManager mavenClasspathManager;

    private String currentDir;
    private IProject project;

    public static final String DEPENDENCY_TYPE_JAR = "jar"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_EJB = "ejb"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_PLUGIN = "plugin"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_ASPECT = "aspect"; //$NON-NLS-1$
    public static final String DEPENDENCY_TYPE_WAR = "war"; //$NON-NLS-1$

    private ActionDefinitionsManager actionDefinitionsManager;

    public ActionDefinitionsManager getActionDefinitionsManager() {
        if (actionDefinitionsManager == null) {
            actionDefinitionsManager = new ActionDefinitionsManager();
        }
        return actionDefinitionsManager;
    }

    public static final String[] KNOWN_DEPENDENCY_TYPES = new String[] {
        DEPENDENCY_TYPE_JAR,
        DEPENDENCY_TYPE_EJB,
        DEPENDENCY_TYPE_PLUGIN,
        DEPENDENCY_TYPE_ASPECT,
        DEPENDENCY_TYPE_WAR
    };

    public static final String MAVEN_MENU_ID = "org.mevenide.maven.menu.id";

    /// initialization methods ---
    public Mevenide() throws Exception {
        try {
            plugin = this;
        } catch (Exception x) {
            log.debug("Mevenide couldnot initialize due to : ", x); //$NON-NLS-1$
            throw x;
        }
    }

    /**
     * osgi startup : initialize resources
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        this.exceptionHandler = new ExceptionHandler(this);

        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        ((Workspace)workspace).addLifecycleListener(new LifecycleListener(this));
        // NOTE: There is no way to remove a lifecycle listener.

        initializeDefaultLocationFinder();
        initEnvironment();

        this.pomManager = new DefaultPOMManager();
        ((DefaultPOMManager)this.pomManager).initialize();

        this.mavenClasspathManager = new MavenClasspathManager();
        this.mavenClasspathManager.initialize();
    }

    protected void initializeImageRegistry(ImageRegistry reg) {
        for (int i = 0; i < IImageRegistry.IMAGE_KEYS.length; i++) {
            reg.put(IImageRegistry.IMAGE_KEYS[i],
                    getImageDescriptor(IImageRegistry.IMAGE_KEYS[i]));
        }
    }

    /**
     * osgi shutdown : dispose resources
     */
    public void stop(BundleContext context) throws Exception {
        this.mavenClasspathManager.dispose();
        this.mavenClasspathManager = null;

        ((DefaultPOMManager)this.pomManager).dispose();
        this.pomManager = null;

        super.stop(context);
    }

    public IPersistentPreferenceStore getCustomPreferenceStore() {
        // TODO: Switch to IoC for creating the custom preference store.
        return PreferencesManager.getManager().getPreferenceStore();
    }

    public IPersistentPreferenceStore getDynamicPreferenceStore() {
        // TODO: Switch to IoC for creating the custom preference store.
        return DynamicPreferencesManager.getManager().getPreferenceStore();
    }

    private LocationFinderAggregator defaultLocationFinder;
    private void initializeDefaultLocationFinder() {
        this.defaultLocationFinder = new LocationFinderAggregator(DefaultQueryContext.getNonProjectContextInstance());
        this.defaultLocationFinder.setCustomLocationFinder(new PreferenceBasedLocationFinder(getCustomPreferenceStore()));
    }

    /**
     * @return Returns the defaultLocationFinder.
     */
    public ILocationFinder getDefaultLocationFinder() {
        return defaultLocationFinder;
    }

    /// usual Plugin methods ---  
    public static Mevenide getInstance() {
        return plugin;
    }

    public static String getResourceString(String key) {
        ResourceBundle bundle = Mevenide.getInstance().getResourceBundle();
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            log.error("Cannot find Bundle Key '" + key + "'", e); //$NON-NLS-1$//$NON-NLS-2$
            return key;
        }
    }

    public static String getResourceString(String key, String param) {
        return getResourceString(key, new String[] { param });
    }

    public static String getResourceString(String key, String[] params) {
        return MessageFormat.format(getResourceString(key), params);
    }

    public ResourceBundle getResourceBundle() {
        if (resourceBundle != null) {
            return resourceBundle;
        }

        synchronized (lock) {
            if (resourceBundle == null) {
                resourceBundle = ResourceBundle.getBundle("MavenPluginResources"); //$NON-NLS-1$
            }
            return resourceBundle;
        }
    }

    private ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = ICONS_PATH;
        try {
            URL installURL = plugin.getBundle().getEntry("/"); //$NON-NLS-1$
            URL url = new URL(installURL, iconPath + "/" + relativePath); //$NON-NLS-1$
            return ImageDescriptor.createFromURL(url);
        } catch (MalformedURLException e) {
            // should not happen
            log.debug("Cannot find ImageDescriptor for '" + relativePath + "' due to : " + relativePath); //$NON-NLS-1$ //$NON-NLS-2$
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    /**
     * should not be necessary since the setters already take care of configuring the environment.
     */
    public void initEnvironment() {
//		TODO Milos: what to do with HeapSize, not in ILocationFinder..
//		Environment.setHeapSize(getHeapSize());

        RunnerHelper.setHelper(new RunnerHelper() {
            private String foreHead = null;

            public String getForeheadLibrary() {
                if (foreHead == null) {
                    try {
                        URL foreHeadURL = new URL(Platform.resolve(getBundle().getEntry("/")), LIB_FOLDER + FOREHEAD_LIBRARY); //$NON-NLS-1$
                        foreHead = foreHeadURL.getFile();
                        log.debug("ForeHead library : " + foreHeadURL); //$NON-NLS-1$
                    } catch (IOException e) {
                        log.debug("Unable to get forehead lib : ", e); //$NON-NLS-1$
                    }

                }
                return foreHead;
            }
        });
    }

    public void setBuildPath() throws Exception {
        Mevenide r = Mevenide.getInstance();
        IFile props = r.project.getFile(Mevenide.PROJECT_PROPERTIES_FILE_NAME);
        if (!props.exists()) {
            props.create(new ByteArrayInputStream(new byte[0]), false, null);
        }

        IJavaProject javaProject = JavaCore.create(project);

        if (!javaProject.exists()) {
            return;
        }

        File f = new Path(project.getLocation().append(PROJECT_PROPERTIES_FILE_NAME).toOSString()).toFile();
        Properties properties = new Properties();
        properties.load(new FileInputStream(f));

        IPathResolver resolver = new DefaultPathResolver();

        String buildPath = resolver.getRelativePath(project, javaProject.getOutputLocation());
        properties.setProperty("maven.build.dest", buildPath); //$NON-NLS-1$
        properties.store(new FileOutputStream(f), null);

        initEnvironment();
    }

    /// setter/getter methods ---

    public String getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(String currentDir) {
        this.currentDir = currentDir;
    }

    public void setProject(IProject project) {
        this.project = project;
        this.currentDir = project.getLocation().toOSString();
    }

////////////////////////////////////////////////////////////////////////////////

    /***************************************************************************
     * [MEVENIDE-176] eclipse 3.1m6 breaks the pom editor.
     *
     * Eclipse Forms 3.1.0 now initializes form parts automatically. Prior to
     * 3.1.0, it was the responsibility of the container to call initialize.
     * This means that existing code will inadvertently call initialize twice.
     * This is undocumented behavior on the part of Eclipse and may be due to a
     * bug fix (i.e., it was supposed to do this but didn't). Workaround added
     * that tests the version of the Forms plugin and calls initialize if the
     * version preceeds 3.1.0.
     * 
     * Affected: Mevenide.java, AbstractPomEditorPage.java, PageSection.java.
     *           Tested against Eclipse 3.1M5a and Eclipse 3.1RC1
     **************************************************************************/

    /**
     * @return The version of Eclipse Forms currently installed.
     */
    public static final PluginVersionIdentifier getEclipseFormsVersion() {
        PluginVersionIdentifier result = null;

        final Bundle formsBundle = Platform.getBundle("org.eclipse.ui.forms");
        if (formsBundle != null) {
            String version = (String) formsBundle.getHeaders().get(Constants.BUNDLE_VERSION);
            IStatus status = PluginVersionIdentifier.validateVersion(version);
            if (status.isOK()) {
                result = new PluginVersionIdentifier(version);
            } else {
                getInstance().getLog().log(status);
            }
        }

        return result;
    }

    public static final PluginVersionIdentifier ECLIPSE_FORMS_3_0_0 = new PluginVersionIdentifier(3, 0, 0);
    public static final PluginVersionIdentifier ECLIPSE_FORMS_3_1_0 = new PluginVersionIdentifier(3, 1, 0);

////////////////////////////////////////////////////////////////////////////////

    /**
     * @return The exception handler for this plugin.
     */
    public ExceptionHandler getExceptionHandler() {
        return this.exceptionHandler;
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param e
     *            the error to show to the user
     */
    public static final void displayError(String title, String message, CoreException e) {
        getInstance().getExceptionHandler().displayError(title, message, e);
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param s
     *            the error to show to the user
     */
    public static final void displayError(String title, String message, IStatus s) {
        getInstance().getExceptionHandler().displayError(title, message, s);
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param title
     *            the title to use for this dialog, or <code>null</code> to
     *            indicate that the default title should be used
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param t
     *            the error to show to the user
     */
    public static final void displayError(String title, String message, Throwable t) {
        getInstance().getExceptionHandler().displayError(title, message, t);
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * @return the POM manager
     */
    public POMManager getPOMManager() {
        return this.pomManager;
    }

////////////////////////////////////////////////////////////////////////////////

    public static class WorkspaceLocationFinder extends CustomLocationFinder {
        private IPersistentPreferenceStore preferences;
        private String defaultMavenLocalHome;

        public WorkspaceLocationFinder(IPersistentPreferenceStore preferences) {
            this.preferences = preferences;
            this.defaultMavenLocalHome = new File(new File(getUserHome()), ".maven").getAbsolutePath();
        }

        public String getConfigurationFileLocation() {
            return null;
        }

        public String getJavaHome() {
            return this.preferences.getString(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY);
        }

        public void setJavaHome(String javaHome) {
            this.preferences.setValue(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY, javaHome);
        }

        public String getMavenHome() {
            return this.preferences.getString(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY);
        }

        public void setMavenHome(String mavenHome) {
            this.preferences.setValue(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY, mavenHome);
        }

        public String getMavenLocalHome() {
            final String localHome = this.preferences.getString(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY);
            return StringUtils.isNull(localHome)? this.defaultMavenLocalHome: localHome;
        }

        public void setMavenLocalHome(String mavenLocalHome) {
            this.preferences.setValue(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY, mavenLocalHome);
        }

        public String getMavenLocalRepository() {
            final String mavenRepo = this.preferences.getString(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY);
            return StringUtils.isNull(mavenRepo)? new File(getMavenLocalHome(), "repository").getAbsolutePath(): mavenRepo;
        }

        public void setMavenLocalRepository(String mavenLocalRepository) {
            this.preferences.setValue(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY, mavenLocalRepository);
        }

        public String getMavenPluginsDir() {
            return new File(getMavenLocalHome(), "cache").getAbsolutePath();
        }

        public void setMavenPluginsDir(String mavenPluginsDir) {
        }

        public String getUserHome() {
            return System.getProperty("user.home");
        }

        public void setUserHome(String userHome) {
        }

    }

    public static class PreferenceBasedLocationFinder extends CustomLocationFinder {
        private IPersistentPreferenceStore preferences;
        private String defaultMavenLocalHome;

        public PreferenceBasedLocationFinder(IPersistentPreferenceStore preferences) {
            this.preferences = preferences;
            this.defaultMavenLocalHome = new File(new File(getUserHome()), ".maven").getAbsolutePath(); //$NON-NLS-1$

            setMavenHome(preferences.getString(MevenidePreferenceKeys.MAVEN_HOME_PREFERENCE_KEY));

            //preferences that are defaulted
            String localHome = preferences.getString(MevenidePreferenceKeys.MAVEN_LOCAL_HOME_PREFERENCE_KEY);
            //maven.local.home has not been initialized - defaults to ${user.home}/.maven
            if (StringUtils.isNull(localHome)) {
                localHome = defaultMavenLocalHome;
            }
            super.setMavenLocalHome(localHome);

            String mavenRepo = preferences.getString(MevenidePreferenceKeys.MAVEN_REPO_PREFERENCE_KEY);
            //maven.repo has not been initialized - defaults to ${maven.local.home}/repository
            if (StringUtils.isNull(mavenRepo)) {
                mavenRepo = new File(super.getMavenLocalHome(), "repository").getAbsolutePath(); //$NON-NLS-1$
            }
            super.setMavenLocalRepository(mavenRepo);

            super.setMavenPluginsDir(new File(super.getMavenLocalHome(), "cache").getAbsolutePath()); //$NON-NLS-1$
        }

        public String getJavaHome() {
            return this.preferences.getString(MevenidePreferenceKeys.JAVA_HOME_PREFERENCE_KEY);
        }

        public final String getUserHome() {
            return System.getProperty("user.home");
        }
    }
}
