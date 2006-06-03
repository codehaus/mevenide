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

import java.io.File;

/**  
 * Provides the default behavior of a <tt>ILocationFinder</tt>. If any
 * value may be derived from any of the other values, that derivation
 * happens in this class. For example, <tt>getMavenLocalHome()</tt>,
 * if not explicitly set, may be derived by appending '.maven' to the
 * user's home directory (<tt>${user.home}/.maven</tt>). So, the
 * implementation provided by this class does just that. 
 * 
 * @see org.mevenide.environment.ILocationFinder
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 */
public abstract class AbstractLocationFinder implements ILocationFinder {

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getMavenLocalHome()
     */
    public String getMavenLocalHome() {
        String result = null;

        final String parent = getUserHome();
        if (parent != null) {
            File location = new File(parent, ".maven");
            if (true /*location.exists()*/) {
                result = location.getAbsolutePath();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getMavenLocalRepository()
     */
    public String getMavenLocalRepository() {
        String result = null;

        final String parent = getMavenLocalHome();
        if (parent != null) {
            File location = new File(parent, "repository");
            if (true /*location.exists()*/) {
                result = location.getAbsolutePath();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getMavenPluginsDir()
     */
    public String getMavenPluginsDir() {
        String result = null;

        final String parent = getMavenLocalHome();
        if (parent != null) {
            File location = new File(parent, "cache");
            if (true /*location.exists()*/) {
                result = location.getAbsolutePath();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getPluginJarsDir()
     */
    public String getPluginJarsDir() {
        String result = null;

        final String parent = getMavenHome();
        if (parent != null) {
            File location = new File(parent, "plugins");
            if (true /*location.exists()*/) {
                result = location.getAbsolutePath();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getUserPluginsDir()
     */
    public String getUserPluginsDir() {
        String result = null;

        final String parent = getMavenLocalHome();
        if (parent != null) {
            File location = new File(parent, "plugins");
            if (true /*location.exists()*/) {
                result = location.getAbsolutePath();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.mevenide.environment.ILocationFinder#getConfigurationFileLocation()
     */
	public String getConfigurationFileLocation() {
        String result = null;

        final String mavenHome = getMavenHome();
        if (mavenHome != null) {
            File conf = new File(new File(mavenHome, "bin"), "forehead.conf");
            if (conf.exists()) {
                result = conf.getAbsolutePath();
			}
		}

        return result;
	}
}