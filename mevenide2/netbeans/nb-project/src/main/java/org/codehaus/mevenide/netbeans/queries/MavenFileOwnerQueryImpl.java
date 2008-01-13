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

package org.codehaus.mevenide.netbeans.queries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;


/**
 * A global implementation of FileOwnerQueryImplementation, is required to link together the maven project
 * and it's artifact in the maven repository. any other files shall be handled by the
 * default netbeans implementation.
 *
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class MavenFileOwnerQueryImpl implements FileOwnerQueryImplementation {
    
    private Set<NbMavenProject> set;
    private final Object lock = new Object();
    private final Object cacheLock = new Object();
    private final List<ChangeListener> listeners;
    private Set cachedProjects;
    private PropertyChangeListener projectListener;
    
    /** Creates a new instance of MavenFileBuiltQueryImpl */
    public MavenFileOwnerQueryImpl() {
        set = new HashSet<NbMavenProject>();
        listeners = new ArrayList<ChangeListener>();
        cachedProjects = null;
        projectListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    synchronized (cacheLock) {
                        cachedProjects = null;
                    }
                }
            }
        };
    }
    
    public static MavenFileOwnerQueryImpl getInstance() {
        Lookup.Result<FileOwnerQueryImplementation> implementations = 
                Lookup.getDefault().lookup(new Lookup.Template<FileOwnerQueryImplementation>(FileOwnerQueryImplementation.class));
        Iterator<? extends FileOwnerQueryImplementation> it = implementations.allInstances().iterator();
        while (it.hasNext()) {
            FileOwnerQueryImplementation obj = it.next();
            if (obj instanceof MavenFileOwnerQueryImpl) {
                return (MavenFileOwnerQueryImpl)obj;
            }
        }
        return null;
    }
    
    public void addMavenProject(NbMavenProject project) {
        synchronized (lock) {
            if (!set.contains(project)) {
                set.add(project);
                ProjectURLWatcher.addPropertyChangeListener(project, projectListener);
            }
        }
        synchronized (cacheLock) {
            cachedProjects = null;
        }
        
        fireChange();
    }
    public void removeMavenProject(NbMavenProject project) {
        synchronized (lock) {
            if (set.contains(project)) {
                set.remove(project);
                ProjectURLWatcher.removePropertyChangeListener(project, projectListener);
            }
        }
        synchronized (cacheLock) {
            cachedProjects = null;
        }
        fireChange();
    }
    
    public void addChangeListener(ChangeListener list) {
        synchronized (listeners) {
            listeners.add(list);
        }
    }
    
    public void removeChangeListener(ChangeListener list) {
        synchronized (listeners) {
            listeners.remove(list);
        }
    }
    
    private void fireChange() {
        List<ChangeListener> lst = new ArrayList<ChangeListener>();
        synchronized (listeners) {
            lst.addAll(listeners);
        }
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener change : lst) {
            change.stateChanged(event);
        }
    }
    
    /**
     * get the list of currently opened maven projects.. kind of hack, but well..
     */
    public Set getOpenedProjects() {
        synchronized (lock) {
            return new HashSet(set);
        }
    }
    
    public Set<FileObject> getOpenedProjectRoots() {
        Set<FileObject> toRet = new HashSet<FileObject>();
        synchronized (lock) {
            for (NbMavenProject prj : set) {
                //TODO have generic and other source roots included to cater for projects with external source roots
                toRet.add(prj.getProjectDirectory());
            }
        }
        return toRet;
    }
    
    public Project getOwner(URI uri) {
        //logger.debug("getOwner of uri=" + uri);
        if (uri.getScheme() != null && "file".equals(uri.getScheme())) { //NOI18N
            File file = new File(uri);
            return getOwner(file);
        }
        // for some reason nbinst:// protocol can be used as well?? WTF.
        return null;
    }
    
    public Project getOwner(FileObject fileObject) {
        File file = FileUtil.toFile(fileObject);
        if (file != null) {
            //logger.fatal("getOwner of fileobject=" + fileObject.getNameExt());
            return getOwner(file);
        }
        return null;
    }
    
    private Project getOwner(File file) {
        Set currentProjects = getAllKnownProjects();
        
        Iterator it = currentProjects.iterator();
        String filepath = file.getAbsolutePath().replace('\\', '/');
        while (it.hasNext()) {
            NbMavenProject project = (NbMavenProject)it.next();
            String path = project.getArtifactRelativeRepositoryPath();
            if (filepath.endsWith(path)) {
                return project;
            }
        }
        return null;
        
    }
    
     
    private Set getAllKnownProjects() {
        Set currentProjects;
        List iterating;
        synchronized (cacheLock) {
            if (cachedProjects != null) {
                return new HashSet(cachedProjects);
            }
            synchronized (lock) {
                currentProjects = new HashSet<NbMavenProject>(set);
                iterating = new ArrayList(set);
            }
            int index = 0;
            // iterate all opened projects and figure their subprojects.. consider these as well. do so recursively.
            //TODO performance.. this could be expensive, maybe cache somehow
            while (index < iterating.size()) {
                NbMavenProject prj = (NbMavenProject)iterating.get(index);
                SubprojectProvider sub = prj.getLookup().lookup(SubprojectProvider.class);
                if (sub != null) {
                    Set subs = sub.getSubprojects();
                    subs.removeAll(currentProjects);
                    currentProjects.addAll(subs);
                    iterating.addAll(subs);
                }
                index = index + 1;
            }
            cachedProjects = currentProjects;
            return new HashSet(cachedProjects);
        }
        
    }
}
