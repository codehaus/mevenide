/* ==========================================================================
 * Copyright 2008 Mevenide Team
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

package org.netbeans.modules.maven;

import java.io.File;
import java.io.IOException;
import org.netbeans.spi.project.CacheDirectoryProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * implementation of CacheDirectoryProvider that places the cache directory in the user
 * directory space of the currently running IDE.
 * @author mkleint
 */
public class CacheDirProvider implements CacheDirectoryProvider {
    private NbMavenProjectImpl project;

    CacheDirProvider(NbMavenProjectImpl prj) {
        project = prj;
    }

    public FileObject getCacheDirectory() throws IOException {
        int code = project.getProjectDirectory().getPath().hashCode();
        File cacheDir = new File(getCacheRoot(), "" + code);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(cacheDir));
        if (fo != null) {
            return fo;
        }
        throw new IOException("Cannot create a cache directory for project at " + cacheDir); //NOI18N
    }

    private File getCacheRoot() {
        String userdir = System.getProperty("netbeans.user"); //NOI18N
        File file = new File(userdir);
        File root = new File(file, "var" + File.separator + "cache" + File.separator + "mavencachedirs"); //NOI18N
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

}
