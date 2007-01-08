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


import mavenreloaded.configuration.ConfigurationBean;
import com.intellij.openapi.project.Project;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * A manager that provides access to the configuration of the plugin for specific Project instances.
 *
 * @author bkate
 */
public class PluginConfigurationManager {

    // the config for this Project instance
    private ConfigurationBean config = null;

    // hold all the instances in a static map
    private static Map<Project, PluginConfigurationManager> instances = new ConcurrentHashMap<Project, PluginConfigurationManager>();


    /**
     * Private constructor for factory pattern.
     */
    private PluginConfigurationManager() {
    }


    /**
     * Get the instance of the configuration manager that corresponds to the Project at hand.
     *
     * @param project The Project being requested.
     *
     * @return The instance of this class that is used to manage configuration for the Project passed in.
     */
    public static PluginConfigurationManager getInstance(Project project) {

        // make sure an instance exists for this project
        if (!instances.containsKey(project)) {
            instances.put(project, new PluginConfigurationManager());
        }

        return instances.get(project);
    }


    /**
     * Frees up the instance of the manager that is related to the Project given.
     *
     * @param proj The Project that is being disposed.
     */
    public static void releaseInstance(Project proj) {
        instances.remove(proj);
    }


    /**
     * Gets the configuration that this manager is holding.
     *
     * @return The ConfigurationBean for the Project that this instance is managing.
     */
    public ConfigurationBean getConfig() {
        return config;
    }


    /**
     * Sets the configuration into the instance of this manager.
     *
     * @param config The ConfigurationBean that is being used in the Project that this manager instance represents.
     */
    public void setConfig(ConfigurationBean config) {
        this.config = config;
    }

}

