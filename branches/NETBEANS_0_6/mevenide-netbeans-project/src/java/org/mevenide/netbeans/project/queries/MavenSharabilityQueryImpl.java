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
package org.mevenide.netbeans.project.queries;

import java.io.File;
import org.mevenide.netbeans.project.MavenProject;
import org.netbeans.spi.queries.SharabilityQueryImplementation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author  Milos Kleint (ca206216@tiscali.cz)
 */
public class MavenSharabilityQueryImpl implements SharabilityQueryImplementation {
    
    private MavenProject project;
    /** Creates a new instance of MavenSharabilityQueryImpl */
    public MavenSharabilityQueryImpl(MavenProject proj) {
        project = proj;
    }
    
    public Boolean isSharable(FileObject fileObject) {
        File file = FileUtil.toFile(fileObject);
        return checkShare(file, false, false);
    }
    
    public Boolean willBeSharable(FileObject fileObject, String str, boolean directory) {
        File parent = FileUtil.toFile(fileObject);
        File child = new File(parent, str);
        return checkShare(child, true, directory);
    }

    
    private Boolean checkShare(File file, boolean future, boolean directory) {
        File basedir = new File(project.getPropertyResolver().getResolvedValue("basedir"));
        // is this condition necessary?
        if (!file.getAbsolutePath().startsWith(basedir.getAbsolutePath())) {
            return Boolean.FALSE;
        }
        File target = new File(project.getPropertyResolver().getResolvedValue("maven.build.dir"));
        if (target.equals(file) || file.getAbsolutePath().startsWith(target.getAbsolutePath())) {
            return Boolean.FALSE;
        }
        File buildProps = new File(basedir, "build.properties");
        if (file.equals(buildProps)) {
            return Boolean.FALSE;
        }
        File pluginxml = new File(basedir, "plugin.xml");
        if (file.equals(pluginxml)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public int getSharability(java.io.File file) {
        //TODO
        return 0;
    }
    
}
