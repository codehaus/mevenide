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

import org.eclipse.jface.preference.IPreferenceStore;
import org.mevenide.environment.sysenv.DefaultSysEnvProvider;

/**
 * Preference based implementation of SysEnvProvider.
 * @author fdutton
 */
public class PreferencesSysEnvProvider extends DefaultSysEnvProvider {
    private IPreferenceStore preferences;

    public PreferencesSysEnvProvider (IPreferenceStore preferences) {
        this.preferences = preferences;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.sysenv.SysEnvProvider#getProperty(java.lang.String)
     */
    public String getProperty(String name) {
        String result = null;

        if ("JAVA_HOME".equals(name)) {
            result = preferences.getString("java.home");
        } else if ("MAVEN_HOME".equals(name)) {
            result = preferences.getString("maven.home");
        } else if ("MAVEN_HOME_LOCAL".equals(name)) {
            result = preferences.getString("maven.home.local");
        } else if ("MAVEN_REPO_LOCAL".equals(name)) {
            result = preferences.getString("maven.repo.local");
        }

        if (result == null) {
            result = super.getProperty(name);
        }

        return result;
    }
}
