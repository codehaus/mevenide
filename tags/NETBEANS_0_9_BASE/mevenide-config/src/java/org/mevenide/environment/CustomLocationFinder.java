/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.environment;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: CustomLocationFinder.java,v 1.1 14 d?c. 2003 Exp gdodinet
 * 
 */
public class CustomLocationFinder implements ILocationFinder {
    private String javaHome;
    private String mavenHome;
    private String mavenLocalHome;
    private String mavenLocalRepository;
    private String mavenPluginsDir;
    private String userHome;
    private String userPluginsDir;
    private String pluginJarsDir;

    public String getConfigurationFileLocation() {
        return null;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getMavenHome() {
        return mavenHome;
    }

    public void setMavenHome(String mavenHome) {
        this.mavenHome = mavenHome;
    }

    public String getMavenLocalHome() {
        return mavenLocalHome;
    }

    public void setMavenLocalHome(String mavenLocalHome) {
        this.mavenLocalHome = mavenLocalHome;
    }

    public String getMavenLocalRepository() {
        return mavenLocalRepository;
    }

    public void setMavenLocalRepository(String mavenLocalRepository) {
        this.mavenLocalRepository = mavenLocalRepository;
    }

    public String getMavenPluginsDir() {
        return mavenPluginsDir;
    }

    public void setMavenPluginsDir(String mavenPluginsDir) {
        this.mavenPluginsDir = mavenPluginsDir;
    }

    public String getUserHome() {
        return userHome;
    }

    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }

    /**
     * directory with user's custom plugins, denoted by maven.plugin.user.dir property
     */
    public String getUserPluginsDir() {
        return userPluginsDir;
    }

    /**
     * the directory containing maven plugin jars, denoted by maven.plugin.dir propertu
     */
    public String getPluginJarsDir() {
        return pluginJarsDir;
    }

    public void setUserPluginsDir(String userPluginsDir) {
        this.userPluginsDir = userPluginsDir;
    }

    public void setPluginJarsDir(String pluginJarsDir) {
        this.pluginJarsDir = pluginJarsDir;
    }

}