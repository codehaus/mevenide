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
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.IOException;

/**
 * plugin information retrieved from the jar file of the plugin.
 * @author  <a href="mailto:mkleint@codehaus.org">Milos Kleint</a>
 */
public class JarPluginInfo extends AbstractPluginInfo {
    private File file;
    private JarFile jf;
    /** Creates a new instance of JarPluginInfo */
    public JarPluginInfo(File jarFile) {
        file = jarFile;
        try {
            jf = new JarFile(file);
        } catch (IOException e) {
            jf = null;
        }
    }
    
    public String getName() {
        if (super.getName() == null) {
            readProjectValues();
        }
        return super.getName();
    }

    protected InputStream getPluginPropsFileInputStream() {
        if (jf != null) {
            JarEntry entry = jf.getJarEntry("plugin.properties");
            if (entry != null) {
                try {
                    return jf.getInputStream(entry);
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }

    protected InputStream getProjectFileInputStream() {
        if (jf != null) {
            JarEntry entry = jf.getJarEntry("project.xml");
            if (entry != null) {
                try {
                    return jf.getInputStream(entry);
                } catch (IOException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
}
