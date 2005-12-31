/* ==========================================================================
 * Copyright 2003-2005 Mevenide Team
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
 * Finder for important maven locations.
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ILocationFinder.java,v 1.1 15 nov. 2003 Exp gdodinet
 */
public interface ILocationFinder {

    String FOREHEAD_CONF_FILE        = "forehead.conf.file";        //$NON-NLS-1$
    String JAVA_HOME                 = "java.home";                 //$NON-NLS-1$
    String MAVEN_HOME                = "maven.home";                //$NON-NLS-1$
    String MAVEN_HOME_LOCAL          = "maven.home.local";          //$NON-NLS-1$
    String MAVEN_PLUGIN_DIR          = "maven.plugin.dir";          //$NON-NLS-1$
    String MAVEN_PLUGIN_UNPACKED_DIR = "maven.plugin.unpacked.dir"; //$NON-NLS-1$
    String MAVEN_PLUGIN_USER_DIR     = "maven.plugin.user.dir";     //$NON-NLS-1$
    String MAVEN_REPO_LOCAL          = "maven.repo.local";          //$NON-NLS-1$
    String USER_HOME                 = "user.home";                 //$NON-NLS-1$

    /**
     * The directory on the local machine that contains Maven.
     * <p>This is equivalent to getting the <tt>maven.home</tt> system property.</p>
     * <p>Defaults to <tt>${env.MAVEN_HOME}</tt></p>
     */
    String getMavenHome();

    /**
     * The directory on the local machine that contains the JDK. Note that a JRE
     * alone is not sufficient to execute Maven.
     * <p>This is equivalent to getting the <tt>java.home</tt> system property.</p>
     * <p>Defaults to <tt>${env.JAVA_HOME}</tt></p>
     */
    String getJavaHome();

    /**
     * The directory on the local machine Maven uses to write user specific
     * details to, such as expanded plugins and cache data.
     * <p>This is equivalent to getting the <tt>maven.home.local</tt> system property.</p>
     * <p>Defaults to <tt>${user.home}/.maven</tt></p>
     */
    String getMavenLocalHome();

    /**
     * The repository on the local machine Maven should use to store downloaded
     * artifacts (jars etc).
     * <p>This is equivalent to getting the <tt>maven.repo.local</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home.local}/repository</tt></p>
     */
    String getMavenLocalRepository();

    /**
     * Where Maven expands installed plugins for processing.
     * <p>This is equivalent to getting the <tt>maven.plugin.unpacked.dir</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home.local}/cache</tt></p>
     */
    String getMavenPluginsDir();

    /**
     * Where Maven can find plugins for this user only.
     * <p>This is equivalent to getting the <tt>maven.plugin.user.dir</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home.local}/plugins</tt></p>
     */
    String getUserPluginsDir();

    /**
     * Where Maven can find it's plugins.
     * <p>This is equivalent to getting the <tt>maven.plugin.dir</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home}/plugins</tt></p>
     */
    String getPluginJarsDir();

    /**
     * Where Maven can find it's forehead configuration file.
     * <p>This is equivalent to getting the <tt>forehead.conf.file</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home}/bin/forehead.conf</tt></p>
     */
    String getConfigurationFileLocation();

    /**
     * The user's home directory on the local machine.
     * <p>This is equivalent to getting the <tt>user.home</tt> system property.</p>
     * <p>Defaults to <tt>${env.HOME}</tt></p>
     */
    String getUserHome();

}