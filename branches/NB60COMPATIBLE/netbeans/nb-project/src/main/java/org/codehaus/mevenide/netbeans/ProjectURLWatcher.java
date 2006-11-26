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

package org.codehaus.mevenide.netbeans;

import java.util.ArrayList;
import java.util.Collection;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * an instance resides in project lookup, allows to get notified on relative path changes.
 * @author mkleint
 */
public final class ProjectURLWatcher {
    
    private NbMavenProject project;
    private Collection<String> paths = new ArrayList<String>();
    
    /** Creates a new instance of ProjectURLWatcher */
    private ProjectURLWatcher(NbMavenProject proj) {
        project = proj;
    }
    
    static ProjectURLWatcher createWatcher(NbMavenProject proj) {
        ProjectURLWatcher watch = new ProjectURLWatcher(proj);
        return watch;
    }
    
    public synchronized void addWatchedPath(String relPath) {
        paths.add(relPath);
    } 
    
    public synchronized void removeWatchedPath(String relPath) {
        paths.remove(relPath);
    }
    
    synchronized void checkFileObject(FileObject fo) {
        String relPath = FileUtil.getRelativePath(project.getProjectDirectory(), fo);
        if (relPath != null && paths.contains(relPath)) {
            fireChange(relPath);
        }
    }
    
    //TODO better do in ReqProcessor to break the listener chaining??
    private void fireChange(String path) {
        project.firePropertyChange(NbMavenProject.PROP_RESOURCE, null, path);
    }
    
}
