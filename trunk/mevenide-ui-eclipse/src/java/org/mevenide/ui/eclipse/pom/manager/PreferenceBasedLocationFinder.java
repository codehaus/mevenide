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

package org.mevenide.ui.eclipse.pom.manager;

import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.mevenide.environment.AbstractLocationFinder;
import org.mevenide.environment.IMutableLocationFinder;

/**
 * Looks for important Maven locations in user preferences.
 * @author fdutton
 */
class PreferenceBasedLocationFinder extends AbstractLocationFinder implements IMutableLocationFinder {
    private IPersistentPreferenceStore preferences;

    /**
     * Initializes a new instance of PreferenceBasedLocationFinder.
     * @param preferences the preference store to use
     */
    public PreferenceBasedLocationFinder(IPersistentPreferenceStore preferences) {
        this.preferences = preferences;
    }

	/**
     * @return the preference store in use
     */
    protected IPersistentPreferenceStore getPreferences() {
        return this.preferences;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getJavaHome()
     */
    public String getJavaHome() {
        return getPreferences().getString(JAVA_HOME);
    }

	/* (non-Javadoc)
	 * @see org.mevenide.environment.ILocationFinder#getMavenHome()
	 */
	public String getMavenHome() {
		return getPreferences().getString(MAVEN_HOME);
    }

	/* (non-Javadoc)
	 * @see org.mevenide.environment.ILocationFinder#getMavenLocalHome()
	 */
	public String getMavenLocalHome() {
		return getPreferences().getString(MAVEN_HOME_LOCAL);
	}

	/* (non-Javadoc)
	 * @see org.mevenide.environment.ILocationFinder#getMavenLocalRepository()
	 */
	public String getMavenLocalRepository() {
		return getPreferences().getString(MAVEN_REPO_LOCAL);
	}

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getMavenPluginsDir()
     */
    public String getMavenPluginsDir() {
        return super.getMavenPluginsDir();
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getUserPluginsDir()
     */
    public String getUserPluginsDir() {
        return super.getUserPluginsDir();
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getPluginJarsDir()
     */
    public String getPluginJarsDir() {
        return super.getPluginJarsDir();
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getConfigurationFileLocation()
     */
    public String getConfigurationFileLocation() {
        return super.getConfigurationFileLocation();
    }

    /* (non-Javadoc)
	 * @see org.mevenide.environment.ILocationFinder#getUserHome()
	 */
	public String getUserHome() {
        return System.getProperty(USER_HOME);
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setJavaHome(java.lang.String)
     */
    public void setJavaHome(String value) {
        getPreferences().setValue(JAVA_HOME, value);
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setMavenHome(java.lang.String)
     */
    public void setMavenHome(String value) {
        getPreferences().setValue(MAVEN_HOME, value);
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setMavenLocalHome(java.lang.String)
     */
    public void setMavenLocalHome(String value) {
        getPreferences().setValue(MAVEN_HOME_LOCAL, value);
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setMavenLocalRepository(java.lang.String)
     */
    public void setMavenLocalRepository(String value) {
        getPreferences().setValue(MAVEN_REPO_LOCAL, value);
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setMavenPluginsDir(java.lang.String)
     */
    public void setMavenPluginsDir(String value) {
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setUserPluginsDir(java.lang.String)
     */
    public void setUserPluginsDir(String value) {
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setPluginJarsDir(java.lang.String)
     */
    public void setPluginJarsDir(String value) {
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.IMutableLocationFinder#setUserHome(java.lang.String)
     */
    public void setUserHome(String value) {
    }
}