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

package org.codehaus.mevenide.netbeans.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * an instance resides in project lookup, allows to get notified on project and 
 * relative path changes.
 * @author mkleint
 */
public final class ProjectURLWatcher {
    
    private NbMavenProject project;
    private Collection<String> paths = new ArrayList<String>();
    private PropertyChangeSupport support;
    
    static {
        AccessorImpl impl = new AccessorImpl();
        impl.assign();
    }
    
    
    static class AccessorImpl extends NbMavenProject.WatcherAccessor {
        
        
         public void assign() {
             if (NbMavenProject.ACCESSOR == null) {
                 NbMavenProject.ACCESSOR = this;
             }
         }
    
        public ProjectURLWatcher createWatcher(NbMavenProject proj) {
            return new ProjectURLWatcher(proj);
        }
        
        public void doFireReload(ProjectURLWatcher watcher) {
            watcher.doFireReload();
        }
        
        public void checkFileObject(ProjectURLWatcher watcher, FileObject fo) {
            watcher.checkFileObject(fo);
        }
        
    }
    
    
    /** Creates a new instance of ProjectURLWatcher */
    private ProjectURLWatcher(NbMavenProject proj) {
        project = proj;
        //TODO oh well, the sources is the actual project instance not the watcher.. a problem?
        support = new PropertyChangeSupport(proj);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.addPropertyChangeListener(propertyChangeListener);
        }
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        synchronized (support) {
            support.removePropertyChangeListener(propertyChangeListener);
        }
    }
    
    /**
     * Returns the current maven project model from the embedder.
     * Should never be kept around for long but always reloaded from here, on 
     * a project change the correct instance changes as the embedder reloads it.
     * 
     */ 
    public MavenProject getMavenProject() {
        return project.getOriginalMavenProject();
    }
    
    
    public synchronized void addWatchedPath(String relPath) {
        paths.add(relPath);
    } 
    
    public synchronized void removeWatchedPath(String relPath) {
        paths.remove(relPath);
    }
    
    private synchronized void checkFileObject(FileObject fo) {
        String relPath = FileUtil.getRelativePath(project.getProjectDirectory(), fo);
        if (relPath != null && paths.contains(relPath)) {
            fireChange(relPath);
        }
    }
    
    //TODO better do in ReqProcessor to break the listener chaining??
    private void fireChange(String path) {
        synchronized (support) {
            support.firePropertyChange(NbMavenProject.PROP_RESOURCE, null, path);
        }
    }
    
    /**
     * 
     */ 
    private void fireProjectReload() {
        project.fireProjectReload();
    }
    
    private void doFireReload() {
        synchronized (support) {
            support.firePropertyChange(NbMavenProject.PROP_PROJECT, null, null);
        }
    }
    
    /**
     * utility method for triggering a maven project reload. 
     * if the project passed in is a Maven based project, will
     * fire reload of the project, otherwise will do nothing.
     */ 
    
    //TODO figure if needed to be public.. currently just nb-project uses it..
    public static void fireMavenProjectReload(Project prj) {
        if (prj != null) {
            ProjectURLWatcher watcher = prj.getLookup().lookup(ProjectURLWatcher.class);
            if (watcher != null) {
                watcher.fireProjectReload();
            }
        }
    }
    
    public static void addPropertyChangeListener(Project prj, PropertyChangeListener listener) {
        if (prj != null && prj instanceof NbMavenProject) {
            // cannot call getLookup() -> stackoverflow when called from NbMavenProject.createBasicLookup()..
            ProjectURLWatcher watcher = ((NbMavenProject)prj).getProjectWatcher();
            watcher.addPropertyChangeListener(listener);
        } else {
            assert false : "Attempted to add PropertyChangeListener to project " + prj; //NOI18N
        }
    }
    
    public static void removePropertyChangeListener(Project prj, PropertyChangeListener listener) {
        if (prj != null && prj instanceof NbMavenProject) {
            // cannot call getLookup() -> stackoverflow when called from NbMavenProject.createBasicLookup()..
            ProjectURLWatcher watcher = ((NbMavenProject)prj).getProjectWatcher();
            watcher.removePropertyChangeListener(listener);
        } else {
            assert false : "Attempted to remove PropertyChangeListener from project " + prj; //NOI18N
        }
    }
    
}
