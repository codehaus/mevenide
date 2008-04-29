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

package org.codehaus.mevenide.grammar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.codehaus.plexus.util.IOUtil;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.InstalledFileLocator;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

/**
 * Module install that unzips the xml descriptors of known maven plugins.
 * @author mkleint
 */
public class ModInstall extends ModuleInstall {
    
    private static final String UPGRADE_PATH = "org" + File.separator + "codehaus" + File.separator + "mojo" + File.separator + "nbm-maven-plugin-2.6.2.xml"; //NOI18N
    /** Creates a new instance of ModInstall */
    public ModInstall() {
    }
    
    @Override
    public void restored() {
        super.restored();
        File expandedPath = InstalledFileLocator.getDefault().locate("maven2/maven-plugins-xml", null, false); //NOI18N
        File upgrade = expandedPath == null ? null : new File(expandedPath, UPGRADE_PATH);
        if (expandedPath == null || !expandedPath.exists() || (expandedPath != null && expandedPath.exists() && !upgrade.exists())) {
            File zipFile = InstalledFileLocator.getDefault().locate("maven2/maven-plugins-xml.zip", null, false); //NOI18N
            assert zipFile != null : "Wrong installation, maven2/maven-plugins-xml.zip missing"; //NOI18N
            //TODO place somewhere else to make sure it's writable by user?
            expandedPath = new File(zipFile.getParentFile(), "maven-plugins-xml"); //NOI18N
            
            FileObject fo=null;
            InputStream in = null;
            try {
                fo = FileUtil.createFolder(expandedPath);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            try {
                in = new FileInputStream(zipFile);
                FileUtil.extractJar(fo, in);
            } catch (IOException exc) {
                
            } finally {
                IOUtil.close(in);
            }
        }
    }
}
