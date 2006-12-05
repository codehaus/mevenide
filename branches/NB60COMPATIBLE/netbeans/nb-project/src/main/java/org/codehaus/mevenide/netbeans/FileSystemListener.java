/* ==========================================================================
 * Copyright 2005 Mevenide Team
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

package org.codehaus.mevenide.netbeans;

import java.util.logging.Logger;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.embedder.MavenSettingsSingleton;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author mkleint@codehaus.org
 */
class FileSystemListener implements FileChangeListener {
    /** Creates a new instance of FileSystemListener */
    FileSystemListener() {
    }
    
    public void attach() {
        try {
            FileObject fo = FileUtil.toFileObject(MavenSettingsSingleton.getInstance().getM2UserDir().getParentFile());
            fo.getFileSystem().addFileChangeListener(this);
        }
        catch (FileStateInvalidException ex) {
            Logger.getLogger(FileSystemListener.class.getName()).log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        };
        
    }
    
    public void detach() {
        try {
            FileObject fo = FileUtil.toFileObject(MavenSettingsSingleton.getInstance().getM2UserDir().getParentFile());
            if (fo != null) {
                fo.getFileSystem().removeFileChangeListener(this);
            }
        }
        catch (FileStateInvalidException ex) {
            Logger.getLogger(FileSystemListener.class.getName()).log(java.util.logging.Level.SEVERE,
                                                             ex.getMessage(), ex);
        };
    }
    
    private void check(FileEvent event) {
        Project prj = FileOwnerQuery.getOwner(event.getFile());
        if (prj != null) {
            ProjectURLWatcher watcher = prj.getLookup().lookup(ProjectURLWatcher.class);
            if (watcher != null) {
                NbMavenProject.ACCESSOR.checkFileObject (watcher, event.getFile());
            }
        }
    }
    
    public void fileFolderCreated(FileEvent event) {
        check(event);
    }

    public void fileDataCreated(FileEvent event) {
        check(event);
    }
    
    public void fileChanged(FileEvent event) {
        check(event);
    }
    
    public void fileDeleted(FileEvent event) {
        check(event);
    }
    
    public void fileRenamed(FileRenameEvent event) {
        check(event);
    }
    
    public void fileAttributeChanged(FileAttributeEvent event) {
    }
    
}
