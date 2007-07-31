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

package org.mevenide.plugins;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

/**
 * default implementation of IPluginInfo that is based on the directories
 * in maven plugin cache.
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class DefaultPluginInfo extends AbstractPluginInfo {
    private static final Logger LOGGER = Logger.getLogger(DefaultPluginInfo.class.getName());
   
    private File cacheDir;
    
    /** Creates a new instance of DefaultPluginInfo */
    DefaultPluginInfo(File mavenCacheDir) {
        cacheDir = mavenCacheDir;
    }
    
    private File getProjectPropsFile() {
        return new File(cacheDir, getArtifactId() + File.separator + "plugin.properties"); //NOI18N
    }
    
    private File getProjectFile() {
        return new File(cacheDir, getArtifactId() + File.separator + "project.xml");
    }

    protected InputStream getPluginPropsFileInputStream() {
        File fil = getProjectPropsFile();
        if (fil != null && fil.exists()) {
            try {
                return new FileInputStream(fil);
            } catch (FileNotFoundException e) {
                // should not happen
                return null;
            }
        }
        return null;
    }

    protected InputStream getProjectFileInputStream() {
        File fil = getProjectFile();
        if (fil != null && fil.exists()) {
            try {
                return new FileInputStream(fil);
            } catch (FileNotFoundException e) {
                // should not happen
                return null;
            }
        }
        return null;
    }
    
}
