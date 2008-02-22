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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences;
import org.codehaus.mevenide.indexer.api.RepositoryPreferences.RepositoryInfo;
import org.codehaus.mevenide.indexer.api.RepositoryUtil;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Module install that checks if the local repo index shall be refreshed.
 * @author mkleint
 */
public class ModInstall extends ModuleInstall {

    private static int MILIS_IN_SEC = 1000;
    private static int MILIS_IN_MIN = MILIS_IN_SEC * 60;
    private transient PropertyChangeListener projectsListener;

     /*logger*/
    private static final Logger LOGGER = 
            Logger.getLogger("org.codehaus.mevenide.netbeans.ModuleInstall");//NOI18N
    
    /** Creates a new instance of ModInstall */
    public ModInstall() {
    }

    @Override
    public void restored() {
        super.restored();


        projectsListener = new OpenProjectsListener();
        OpenProjects.getDefault().addPropertyChangeListener(projectsListener);

        List<RepositoryInfo> ris = RepositoryPreferences.getInstance().getRepositoryInfos();
        for (final RepositoryInfo ri : ris) {
            int freq = RepositoryPreferences.getInstance().getIndexUpdateFrequency();
            if (freq != RepositoryPreferences.FREQ_NEVER) {
                boolean run = false;
                if (freq == RepositoryPreferences.FREQ_STARTUP) {
                    LOGGER.finer("Index At Startup :"+ri.getId());//NOI18N
                    run = true;
                } else if (freq == RepositoryPreferences.FREQ_ONCE_DAY && checkDiff(ri.getId(),86400000L)) {
                    LOGGER.finer("Index Once a Day :"+ri.getId());//NOI18N
                    run = true;
                } else if (freq == RepositoryPreferences.FREQ_ONCE_WEEK && checkDiff(ri.getId(),604800000L)) {
                    LOGGER.finer("Index once a Week :"+ri.getId());//NOI18N
                    run = true;
                }
                if (run) {
                    RequestProcessor.getDefault().post(new Runnable() {

                        public void run() {

                            if (ri.getIndexUpdateUrl() != null) {
                                RepositoryUtil.getDefaultRepositoryIndexer().indexRepo(ri.getId());
                            }

                        }
                    }, MILIS_IN_MIN * 2);
                }
            }
        }
    }

    private boolean checkDiff(String repoid,long amount) {
        Date date = RepositoryPreferences.getInstance().getLastIndexUpdate(repoid);
        Date now = new Date();
        LOGGER.finer("Check Date Diff :"+repoid);//NOI18N
        LOGGER.finer("Last Indexed Date :"+SimpleDateFormat.getInstance().format(date));//NOI18N
        LOGGER.finer("Now :"+SimpleDateFormat.getInstance().format(now));//NOI18N
        long diff = now.getTime() - date.getTime();
        LOGGER.finer("Diff :"+diff);//NOI18N
        return (diff < 0 || diff > amount);
    }

    @Override
    public void uninstalled() {
        super.uninstalled();
        if (projectsListener != null) {
            OpenProjects.getDefault().removePropertyChangeListener(projectsListener);
        }
    }

    private static class OpenProjectsListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent evt) {


            Project[] prjs = OpenProjects.getDefault().getOpenProjects();
            for (int i = 0; i < prjs.length; i++) {
                NbMavenProject mavProj = prjs[i].getLookup().lookup(org.codehaus.mevenide.netbeans.NbMavenProject.class);
                if (mavProj != null) {
                    List repos = mavProj.getOriginalMavenProject().getRemoteArtifactRepositories();
                    if (repos != null) {
                        Iterator it = repos.iterator();
                        while (it.hasNext()) {
                            ArtifactRepository rep = (ArtifactRepository) it.next();
                            if (RepositoryPreferences.getInstance().
                                    getRepositoryInfoById(rep.getId()) == null) {
                                RepositoryInfo ri = new RepositoryInfo(rep.getId(),
                                        RepositoryPreferences.TYPE_NEXUS, //TODO this should be somehow omittable, these repos are not meant to be indexed or shown in the browser UI.
                                        rep.getId() + " " + NbBundle.getMessage(ModInstall.class, "LBL_REPOSITORY"),//NOI18N
                                        null,rep.getUrl(), null, true);
                                RepositoryPreferences.getInstance().addRepositoryInfo(ri);
                            }
                        }
                    }
                }
            }

        }
    }
}
