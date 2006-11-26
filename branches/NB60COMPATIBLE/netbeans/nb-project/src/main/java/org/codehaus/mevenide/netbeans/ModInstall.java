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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.maven.archiva.indexer.RepositoryIndexException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mevenide.indexer.*;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.modules.ModuleInstall;
import org.openide.util.RequestProcessor;

/**
 * Module install that checks if the local repo index shall be refreshed.
 * @author mkleint
 */
public class ModInstall extends ModuleInstall {
    
    private static int MILIS_IN_SEC = 1000;
    private static int MILIS_IN_MIN = MILIS_IN_SEC * 60;
    private transient PropertyChangeListener projectsListener;
    private transient FileSystemListener filesystemListener;
    /** Creates a new instance of ModInstall */
    public ModInstall() {
    }
    
    public void restored() {
        super.restored();
        projectsListener = new OpenProjectsListener();
        filesystemListener = new FileSystemListener();
        filesystemListener.attach();
        OpenProjects.getDefault().addPropertyChangeListener(projectsListener);
        int freq = MavenIndexSettings.getDefault().getIndexUpdateFrequency();
        if (freq != MavenIndexSettings.FREQ_NEVER) {
            boolean run = false;
            if (freq == MavenIndexSettings.FREQ_STARTUP) {
                run = true;
            } else if (freq == MavenIndexSettings.FREQ_ONCE_DAY && checkDiff(86400000L)) {
                run = true;
            }  else if (freq == MavenIndexSettings.FREQ_ONCE_WEEK && checkDiff(604800000L)) {
                run = true;
            }
            if (run) {
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        try {
                            LocalRepositoryIndexer.getInstance().updateIndex();
                        } catch (RepositoryIndexException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, MILIS_IN_MIN * 2);
            }
        }
    }
    
    private boolean checkDiff(long amount) {
        Date date = MavenIndexSettings.getDefault().getLastIndexUpdate();
        Date now = new Date();
        long diff = now.getTime() - date.getTime();
        return  (diff < 0 || diff > amount);
    }

    public void uninstalled() {
        super.uninstalled();
        if (projectsListener != null) {
            OpenProjects.getDefault().removePropertyChangeListener(projectsListener);
        }
        if (filesystemListener != null) {
            filesystemListener.detach();
        }
    }
    
    private static class OpenProjectsListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            List lst = MavenIndexSettings.getDefault().getCollectedRepositories();
            boolean added = false;
            Project[] prjs = OpenProjects.getDefault().getOpenProjects();
            for (int i = 0; i < prjs.length; i++) {
                NbMavenProject mavProj = (NbMavenProject)prjs[i].getLookup().lookup(NbMavenProject.class);
                if (mavProj != null) {
                    List repos = mavProj.getOriginalMavenProject().getRemoteArtifactRepositories();
                    if (repos != null) {
                        Iterator it = repos.iterator();
                        while (it.hasNext()) {
                            ArtifactRepository rep = (ArtifactRepository) it.next();
                            String url = rep.getUrl();
                            if (!lst.contains(url)) {
                                lst.add(url);
                                added = true;
                            }
                        }
                    }
                }
            }
            if (added) {
                MavenIndexSettings.getDefault().setCollectedRepositories(lst);
            }
        }
        
    }
    
}
