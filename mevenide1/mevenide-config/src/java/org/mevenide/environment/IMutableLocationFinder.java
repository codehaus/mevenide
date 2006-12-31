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
 * A variant of ILocationFinder that permits setting the locations.
 */
public interface IMutableLocationFinder extends ILocationFinder {

    /**
     * The directory on the local machine that contains Maven.
     * <p>This is equivalent to setting the <tt>maven.home</tt> system property.</p>
     * <p>Defaults to <tt>${env.MAVEN_HOME}</tt></p>
     */
    void setMavenHome(String value);

    /**
     * The directory on the local machine that contains the JDK. Note that a JRE
     * alone is not sufficient to execute Maven.
     * <p>This is equivalent to setting the <tt>java.home</tt> system property.</p>
     * <p>Defaults to <tt>${env.JAVA_HOME}</tt></p>
     */
    void setJavaHome(String value);

    /**
     * The directory on the local machine Maven uses to write user specific
     * details to, such as expanded plugins and cache data.
     * <p>This is equivalent to setting the <tt>maven.home.local</tt> system property.</p>
     * <p>Defaults to <tt>${user.home}/.maven</tt></p>
     */
    void setMavenLocalHome(String value);

    /**
     * The repository on the local machine Maven should use to store downloaded
     * artifacts (jars etc).
     * <p>This is equivalent to setting the <tt>maven.repo.local</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home.local}/repository</tt></p>
     */
    void setMavenLocalRepository(String value);

    /**
     * Where Maven expands installed plugins for processing.
     * <p>This is equivalent to setting the <tt>maven.plugin.unpacked.dir</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home.local}/cache</tt></p>
     */
    void setMavenPluginsDir(String value);

    /**
     * Where Maven can find plugins for this user only.
     * <p>This is equivalent to setting the <tt>maven.plugin.user.dir</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home.local}/plugins</tt></p>
     */
    void setUserPluginsDir(String value);

    /**
     * Where Maven can find it's plugins.
     * <p>This is equivalent to setting the <tt>maven.plugin.dir</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home}/plugins</tt></p>
     */
    void setPluginJarsDir(String value);

    /**
     * Where Maven can find it's forehead configuration file.
     * <p>This is equivalent to setting the <tt>forehead.conf.file</tt> system property.</p>
     * <p>Defaults to <tt>${maven.home}/bin/forehead.conf</tt></p>
     */
//    void setConfigurationFileLocation(String value);

    /**
     * The user's home directory on the local machine.
     * <p>This is equivalent to setting the <tt>user.home</tt> system property.</p>
     * <p>Defaults to <tt>${env.HOME}</tt></p>
     */
    void setUserHome(String value);

}
