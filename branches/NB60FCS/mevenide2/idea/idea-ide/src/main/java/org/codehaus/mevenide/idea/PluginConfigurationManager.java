/* ==========================================================================
 * Copyright 2006 Mevenide Team
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



package org.codehaus.mevenide.idea;

import com.intellij.openapi.project.Project;

import org.codehaus.mevenide.idea.configuration.ConfigurationBean;

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
    private static Map<Project, PluginConfigurationManager> instances = new ConcurrentHashMap<Project,
                                                                            PluginConfigurationManager>();

    /**
     * Private constructor for factory pattern.
     */
    private PluginConfigurationManager() {}

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
