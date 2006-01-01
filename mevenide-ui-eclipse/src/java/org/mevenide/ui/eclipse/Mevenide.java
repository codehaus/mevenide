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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.mevenide.environment.SysEnvLocationFinder;
import org.mevenide.environment.sysenv.SysEnvProvider;
import org.mevenide.ui.eclipse.classpath.ClasspathManager;
import org.mevenide.ui.eclipse.classpath.MavenClasspathManager;
import org.mevenide.ui.eclipse.nature.ActionDefinitionsManager;
import org.mevenide.ui.eclipse.pom.manager.DefaultPOMManager;
import org.mevenide.ui.eclipse.pom.manager.POMManager;
import org.mevenide.ui.eclipse.preferences.PreferencesManager;
import org.mevenide.ui.eclipse.preferences.dynamic.DynamicPreferencesManager;
import org.mevenide.ui.eclipse.util.ExceptionHandler;
import org.mevenide.ui.eclipse.util.LifecycleListener;
import org.mevenide.ui.eclipse.util.Tracer;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

/**
 * Created on 01 feb. 03	
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 */
public class Mevenide extends AbstractUIPlugin {
    /**
     * TODO: Describe how <code>DEFAULT_ERROR_TITLE</code> is used.
     */
    private static final String DEFAULT_ERROR_TITLE = "Internal MevenIDE Error";

    public static final String PLUGIN_ID = "org.mevenide.ui"; //$NON-NLS-1$

    private static final String ICONS_PATH = "/icons"; //$NON-NLS-1$

    private static Mevenide plugin;

    private Object lock = new Object();
    private ResourceBundle resourceBundle;
    private ExceptionHandler exceptionHandler;
    private POMManager pomManager;
    private MavenClasspathManager mavenClasspathManager;
    private ActionDefinitionsManager actionDefinitionsManager;
    private SysEnvProvider environmentProvider;

    /**
     * Initializes a new instance of Mevenide.
     */
    public Mevenide() {
        plugin = this;
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);

        /* FIXME: Remove the need to make this call.
         * We make this call to ensure that the classloader has
         * loaded Tracer. If we do not then we see an error
         * message from the LifecycleListener when it attempts
         * to access Tracer during shutdown and Tracer was
         * never loaded.
         */
        if (Tracer.isDebugging()) {
        	Tracer.trace("Starting MevenIDE.");
        }

        /* Set the system wide environment provider to use values from
         * user preferences if they exist.
         */
        this.environmentProvider = new PreferencesSysEnvProvider(getCustomPreferenceStore());
        SysEnvLocationFinder.setDefaultSysEnvProvider(this.environmentProvider);


        this.exceptionHandler = new ExceptionHandler(this);

        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        ((Workspace)workspace).addLifecycleListener(new LifecycleListener(this));
        // NOTE: There is no way to remove a lifecycle listener.

        this.pomManager = new DefaultPOMManager();
        ((DefaultPOMManager)this.pomManager).initialize();

        this.mavenClasspathManager = new MavenClasspathManager();
        this.mavenClasspathManager.initialize();

        this.actionDefinitionsManager = new ActionDefinitionsManager();
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        this.actionDefinitionsManager = null; 

        this.mavenClasspathManager.dispose();
        this.mavenClasspathManager = null;

        ((DefaultPOMManager)this.pomManager).dispose();
        this.pomManager = null;

        super.stop(context);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry reg) {
        URL rootURL;
        try {
            rootURL = Platform.resolve(getBundle().getEntry(ICONS_PATH));
        } catch (IOException e) {
            final String msg = "Unable to resolve the root URL for the icons in this plugin.";
            displayError(DEFAULT_ERROR_TITLE, msg, e);
            return;
        }

        for (int i = 0; i < IImageRegistry.IMAGE_KEYS.length; i++) {
            final String relativePath = IImageRegistry.IMAGE_KEYS[i];

            try {
                URL url = new URL(rootURL, relativePath);
                reg.put(relativePath, ImageDescriptor.createFromURL(url));
            } catch (MalformedURLException e) {
                final String msg = "Cannot find image descriptor for '" + relativePath + "'.";
                displayError(DEFAULT_ERROR_TITLE, msg, e);
                reg.put(relativePath, ImageDescriptor.getMissingImageDescriptor());
            }
        }
    }

    public IPersistentPreferenceStore getCustomPreferenceStore() {
        // TODO: Switch to IoC for creating the custom preference store.
        return PreferencesManager.getManager().getPreferenceStore();
    }

    public IPersistentPreferenceStore getDynamicPreferenceStore() {
        // TODO: Switch to IoC for creating the custom preference store.
        return DynamicPreferencesManager.getManager().getPreferenceStore();
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
            final String msg = "Cannot find value for '" + key + "' in the resource bundle."; //$NON-NLS-1$//$NON-NLS-2$
            displayError(DEFAULT_ERROR_TITLE, msg, e);
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
        if (this.resourceBundle != null) {
            return this.resourceBundle;
        }

        synchronized (this.lock) {
            if (this.resourceBundle == null) {
                this.resourceBundle = ResourceBundle.getBundle("MavenPluginResources"); //$NON-NLS-1$
            }
            return this.resourceBundle;
        }
    }

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
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param e
     *            the error to show to the user
     */
    public static final void displayError(String message, CoreException e) {
        displayError(null, message, e);
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
        final String theTitle = (title == null)? Mevenide.getResourceString("Mevenide.error.title"): title;
        getInstance().getExceptionHandler().displayError(theTitle, message, e);
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param s
     *            the error to show to the user
     */
    public static final void displayError(String message, IStatus s) {
        getInstance().getExceptionHandler().displayError(null, message, s);
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
        final String theTitle = (title == null)? Mevenide.getResourceString("Mevenide.error.title"): title;
        getInstance().getExceptionHandler().displayError(theTitle, message, s);
    }

    /**
     * Opens an error dialog to display the given error
     * and logs the error in the plugin's error log.
     * 
     * @param message
     *            the message to show in this dialog, or <code>null</code> to
     *            indicate that the error's message should be shown as the
     *            primary message
     * @param t
     *            the error to show to the user
     */
    public static final void displayError(String message, Throwable t) {
        getInstance().getExceptionHandler().displayError(null, message, t);
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
        final String theTitle = (title == null)? Mevenide.getResourceString("Mevenide.error.title"): title;
        getInstance().getExceptionHandler().displayError(theTitle, message, t);
    }

////////////////////////////////////////////////////////////////////////////////

    /**
     * @return the action-definition manager
     */
    public ActionDefinitionsManager getActionDefinitionsManager() {
        return this.actionDefinitionsManager;
    }

    /**
     * @return the POM manager
     */
    public POMManager getPOMManager() {
        return this.pomManager;
    }

    /**
     * @return Returns the Maven classpath manager.
     */
    public ClasspathManager getClasspathManager() {
        return this.mavenClasspathManager;
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
}
