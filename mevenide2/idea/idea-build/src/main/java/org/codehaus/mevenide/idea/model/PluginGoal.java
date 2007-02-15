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



package org.codehaus.mevenide.idea.model;

/**
 * Describe what this class does.
 *
 * @author Ralf Quebbemann
 * @version $Revision$
 */
public class PluginGoal {
    private String goal;
    private String pluginPrefix;

    /**
     * Constructs ...
     */
    public PluginGoal() {}

    /**
     * Constructs ...
     *
     * @param pluginPrefix Document me!
     * @param goal         Document me!
     */
    public PluginGoal(String pluginPrefix, String goal) {
        this.pluginPrefix = pluginPrefix;
        this.goal = goal;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String toString() {
        return pluginPrefix + ":" + goal;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Method description
     *
     * @return Document me!
     */
    public String getPluginPrefix() {
        return pluginPrefix;
    }

    /**
     * Method description
     *
     * @param goal Document me!
     */
    public void setGoal(String goal) {
        this.goal = goal;
    }

    /**
     * Method description
     *
     * @param pluginPrefix Document me!
     */
    public void setPluginPrefix(String pluginPrefix) {
        this.pluginPrefix = pluginPrefix;
    }
}
