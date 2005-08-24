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

package org.mevenide.ui.eclipse.pom.manager;

import java.io.File;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.mevenide.environment.CustomLocationFinder;
import org.mevenide.ui.eclipse.preferences.MevenidePreferenceKeys;
import org.mevenide.util.StringUtils;

class PreferenceBasedLocationFinder extends CustomLocationFinder {
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