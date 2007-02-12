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

package org.codehaus.mevenide.idea;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;

import org.codehaus.mevenide.idea.console.PluginLogger;
import org.codehaus.mevenide.idea.console.LoggerConsole;
import org.codehaus.mevenide.idea.configuration.ConfigurationBean;
import org.codehaus.mevenide.idea.configuration.ConfigurationForm;
import org.codehaus.mevenide.idea.configuration.PluginJDOMExternalizer;

import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;


/**
 * A class that acts as the entry point for the MavenReloaded IntelliJ plugin.
 */
public class MavenReloadedPlugin implements ProjectComponent, JDOMExternalizable, Configurable {

    // the current project in use
    private final Project project;

    // the maven logger console
    private LoggerConsole logConsole;

    // the plugin configuration
    public ConfigurationBean config = new ConfigurationBean();
    private ConfigurationForm configForm;

    // a unique identifier for the logging console tool window
    private static final String LOG_CONSOLE_ID = "Maven";
    private static final String LOG_CONSOLE_TITLE = "Maven Reloaded Log Console";
    private static final String LOG_CONSOLE_ICON = "/images/maven.png";
    private static final String CONFIGURATION_ICON = "/images/maven_large.png";

    // the logger that corresponds to this instance of the plugin
    private PluginLogger logger;


    /**
     * A constructor that takes the project in use.
     *
     * @param project The currently loaded project.
     */
    public MavenReloadedPlugin(Project project) {
        this.project = project;
    }


    /** {@inheritDoc} */
    public void initComponent() {
    }


    /** {@inheritDoc} */
    public void disposeComponent() {
    }


    /** {@inheritDoc} */
    public String getComponentName() {
        return "Maven Reloaded";
    }


    /** {@inheritDoc} */
    public void projectOpened() {

        // store the plugin config in the config manager so other classes can get access
        PluginConfigurationManager.getInstance(project).setConfig(config);

        logger = PluginLoggerManager.getInstance(project).getPluginLogger(MavenReloadedPlugin.class);

        // bring up the logging console
        createLogConsole();

        // tell the POM manager to start working
        PluginPomManager.getInstance(project).projectOpened();

        logger.debug("Opened project " + project.getName());
    }


    /** {@inheritDoc} */
    public void projectClosed() {

        // tell the POM manager to stop working
        PluginPomManager.getInstance(project).projectClosed();

        logger.debug("Closed project " + project.getName());

        // bring down the logging console
        destroyLogConsole();

        // get rid of references to the project from the managers
        PluginConfigurationManager.releaseInstance(project);
        PluginPomManager.releaseInstance(project);
        PluginLoggerManager.releaseInstance(project);
    }


    /**
     * Creates the logging console for the plugin.
     */
    private void createLogConsole() {

        // make a new logging console
        logConsole = new LoggerConsole();

        // add the console as a listener of log messages
        PluginLoggerManager.getInstance(project).addListener(logConsole);

        // make a tool window for the console
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);

        ToolWindow logWindow = toolWindowManager.registerToolWindow(LOG_CONSOLE_ID, logConsole, ToolWindowAnchor.BOTTOM);
        logWindow.setTitle(LOG_CONSOLE_TITLE);
        logWindow.setIcon(new ImageIcon(MavenReloadedPlugin.class.getResource(LOG_CONSOLE_ICON)));
    }


    /**
     * Destroys the logging console for the plugin.
     */
    private void destroyLogConsole() {

        // remove the console as a listener of log messages
        PluginLoggerManager.getInstance(project).removeListener(logConsole);

        // destroy the tool window
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.unregisterToolWindow(LOG_CONSOLE_ID);
    }


    /** {@inheritDoc} */
    public void readExternal(Element element) throws InvalidDataException {
        PluginJDOMExternalizer.readExternal(this, element);
    }


    /** {@inheritDoc} */
    public void writeExternal(Element element) throws WriteExternalException {
        PluginJDOMExternalizer.writeExternal(this, element);
    }


    /** {@inheritDoc} */
    public String getDisplayName() {
        return "Maven Reloaded";
    }


    /** {@inheritDoc} */
    public Icon getIcon() {
        return new ImageIcon(MavenReloadedPlugin.class.getResource(CONFIGURATION_ICON));
    }


    /** {@inheritDoc} */
    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }


    /** {@inheritDoc} */
    public JComponent createComponent() {

        if (configForm == null) {
            configForm = new ConfigurationForm();
        }

        return configForm.getRootComponent();
    }


    /** {@inheritDoc} */
    public boolean isModified() {
        return ((configForm != null) && configForm.isModified(config));
    }


    /** {@inheritDoc} */
    public void apply() throws ConfigurationException {

        if (configForm != null) {
            configForm.getData(config);
        }
    }


    /** {@inheritDoc} */
    public void reset() {

        if (configForm != null) {
            configForm.setData(config);
        }
    }


    /** {@inheritDoc} */
    public void disposeUIResources() {
        configForm = null;
    }

}
