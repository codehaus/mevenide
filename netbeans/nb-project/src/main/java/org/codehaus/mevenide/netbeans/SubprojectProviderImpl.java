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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.maven.artifact.Artifact;
import org.codehaus.mevenide.netbeans.api.ProjectURLWatcher;
import org.codehaus.mevenide.netbeans.queries.MavenFileOwnerQueryImpl;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;



/**
 * finds subprojects (projects this one depends on) that are locally available
 * and can be build as one unit. Uses maven multiproject infrastructure. (maven.multiproject.includes)
 * @author  Milos Kleint (mkleint@codehaus.org)
 */
public class SubprojectProviderImpl implements SubprojectProvider {
    
    private NbMavenProject project;
    private List<ChangeListener> listeners;
    private ChangeListener listener2;
    /** Creates a new instance of SubprojectProviderImpl */
    public SubprojectProviderImpl(NbMavenProject proj) {
        project = proj;
        listeners = new ArrayList<ChangeListener>();
        ProjectURLWatcher.addPropertyChangeListener(proj, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (NbMavenProject.PROP_PROJECT.equals(evt.getPropertyName())) {
                    fireChange();
                }
            }
        });
        listener2 = new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                fireChange();
            }
        };
        MavenFileOwnerQueryImpl.getInstance().addChangeListener(
                WeakListeners.change(listener2,
                                     MavenFileOwnerQueryImpl.getInstance()));
    }
    
    
    public Set getSubprojects() {
        Set<Project> projects = new HashSet<Project>();
        File basedir = FileUtil.toFile(project.getProjectDirectory());
        addProjectModules(basedir, projects, project.getOriginalMavenProject().getModules());
        addOpenedCandidates(projects);
        projects.remove(project);
        return projects;
    }
    
    private void addOpenedCandidates(Set<Project> resultset) {
        Set<NbMavenProject> opened = MavenFileOwnerQueryImpl.getInstance().getOpenedProjects();
        List<Artifact> compileArtifacts = project.getOriginalMavenProject().getCompileArtifacts();
        List<String> artPaths = new ArrayList<String>();
        for (Artifact ar : compileArtifacts) {
            artPaths.add(project.getArtifactRelativeRepositoryPath(ar));
        }
        for (NbMavenProject prj : opened) {
            String prjpath = prj.getArtifactRelativeRepositoryPath();
            if (artPaths.contains(prjpath)) {
                resultset.add(prj);
            }
        }
    }
    
    private void addProjectModules(File basedir, Set<Project> resultset, List modules) {
        if (modules == null || modules.size() == 0) {
            return;
        }
        Iterator it = modules.iterator();
        while (it.hasNext()) {
            String path = (String)it.next();
            File sub = new File(basedir, path);
            Project proj = processOneSubproject(sub);
            NbMavenProject mv = proj != null ? proj.getLookup().lookup(NbMavenProject.class) : null;
            if (mv != null) {
                // ignore the pom type projects when resolving subprojects..
                // maybe make an user settable option??
                if (! ProjectURLWatcher.TYPE_POM.equalsIgnoreCase(mv.getProjectWatcher().getPackagingType())) {
                    resultset.add(proj);
                }
                addProjectModules(FileUtil.toFile(mv.getProjectDirectory()), 
                                     resultset, mv.getOriginalMavenProject().getModules());
            }
        }
    }
    
    
    private Project processOneSubproject(File dir) {
        File projectFile = FileUtil.normalizeFile(dir);
        if (projectFile.exists()) {
            FileObject projectDir = FileUtil.toFileObject(projectFile);
            if (projectDir != null) {
                try {
                    return ProjectManager.getDefault().findProject(projectDir);
                } catch (IOException exc) {
                    ErrorManager.getDefault().notify(exc);
                }
            } else {
                // HUH?
                ErrorManager.getDefault().log("fileobject not found=" + dir ); //NOI18N
            }
            
        } else {
            ErrorManager.getDefault().log("project file not found=" + dir); //NOI18N
        }
        return null;
    }
    
    public synchronized void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }
    
    public synchronized void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }
    
    private void fireChange() {
        List<ChangeListener> lists = new ArrayList<ChangeListener>();
        synchronized (this) {
            lists.addAll(listeners);
        }
        for (ChangeListener listener : lists) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    
}
