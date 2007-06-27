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
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.FileChangeSupport;
import org.codehaus.mevenide.netbeans.FileChangeSupportEvent;
import org.codehaus.mevenide.netbeans.FileChangeSupportListener;
import org.codehaus.mevenide.netbeans.FileUtilities;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.UserActionGoalProvider;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * an instance resides in project lookup, allows to get notified on project and 
 * relative path changes.
 * @author mkleint
 */
//TODO rename to something else doesn't describe correctly what it does..
public final class ProjectURLWatcher {
    
    private NbMavenProject project;
    private PropertyChangeSupport support;
    private FCHSL listener = new FCHSL();
    
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
        
    }
    
    private class FCHSL implements FileChangeSupportListener {

        public void fileCreated(FileChangeSupportEvent event) {
            fireChange(event.getPath().toURI());
        }

        public void fileDeleted(FileChangeSupportEvent event) {
            fireChange(event.getPath().toURI());
        }

        public void fileModified(FileChangeSupportEvent event) {
            fireChange(event.getPath().toURI());
        }
        
    }
    
    
    /** Creates a new instance of ProjectURLWatcher */
    private ProjectURLWatcher(NbMavenProject proj) {
        project = proj;
        //TODO oh well, the sources is the actual project instance not the watcher.. a problem?
        support = new PropertyChangeSupport(proj);
    }
    
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.addPropertyChangeListener(propertyChangeListener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        support.removePropertyChangeListener(propertyChangeListener);
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
    
    public static final String TYPE_JAR = "jar";
    public static final String TYPE_WAR = "war";
    public static final String TYPE_EAR = "ear";
    public static final String TYPE_EJB = "ejb";
    public static final String TYPE_NBM = "nbm";
    public static final String TYPE_POM = "pom";
    
    /**
     * get the user level packaging type for the project, allows to get the same UI support
     *  of user's custom lifecycles.
     */ 
    public String getPackagingType() {
        String custom = project.getLookup().lookup(UserActionGoalProvider.class).getRawMappings().getPackaging();
        return custom != null ? custom : project.getOriginalMavenProject().getPackaging();
    }
    
    
    public synchronized void addWatchedPath(String relPath) {
        addWatchedPath(FileUtilities.getDirURI(project.getProjectDirectory(), relPath));
    } 
    
    public synchronized void addWatchedPath(URI uri) {
        FileChangeSupport.DEFAULT.addListener(listener, new File(uri));
    } 
    
    public synchronized void removeWatchedPath(String relPath) {
        removeWatchedPath(FileUtilities.getDirURI(project.getProjectDirectory(), relPath));
    }
    public synchronized void removeWatchedPath(URI uri) {
        FileChangeSupport.DEFAULT.removeListener(listener, new File(uri));
    } 
    
    
    //TODO better do in ReqProcessor to break the listener chaining??
    private void fireChange(URI uri) {
        support.firePropertyChange(NbMavenProject.PROP_RESOURCE, null, uri);
    }
    
    /**
     * 
     */ 
    private void fireProjectReload() {
        project.fireProjectReload();
    }
    
    private void doFireReload() {
        support.firePropertyChange(NbMavenProject.PROP_PROJECT, null, null);
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
