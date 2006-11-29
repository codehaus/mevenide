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
package org.codehaus.mevenide.netbeans.execute;

import org.codehaus.mevenide.netbeans.api.execute.RunConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.options.MavenExecutionSettings;

/**
 *
 * @author mkleint
 */
public class BeanRunConfig implements RunConfig {
    
    private File executionDirectory;
    private NbMavenProject project;
    private List goals;
    private String executionName;
    private Properties properties = new Properties();
    private boolean showDebug = MavenExecutionSettings.getDefault().isShowDebug();
    private boolean showError = MavenExecutionSettings.getDefault().isShowErrors();
    private Boolean offline;
    private List activate = new ArrayList();
    private boolean updateSnapshots = false;
    private boolean recursive = true;
    
    /** Creates a new instance of BeanRunConfig */
    public BeanRunConfig() {
    }

    public File getExecutionDirectory() {
        return executionDirectory;
    }

    public void setExecutionDirectory(File executionDirectory) {
        this.executionDirectory = executionDirectory;
    }

    public NbMavenProject getProject() {
        return project;
    }

    public void setProject(NbMavenProject project) {
        this.project = project;
    }

    public List getGoals() {
        return goals;
    }

    public void setGoals(List goals) {
        this.goals = goals;
    }

    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public boolean isShowDebug() {
        return showDebug;
    }

    public void setShowDebug(boolean showDebug) {
        this.showDebug = showDebug;
    }

    public boolean isShowError() {
        return showError;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    public Boolean isOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public List getActivatedProfiles() {
        return activate;
    }

    public void setActivatedProfiles(List activeteProfiles) {
        activate = new ArrayList();
        activate.addAll(activeteProfiles);
    }

    public boolean isRecursive() {
        return recursive;
    }
    
    public void setRecursive(boolean rec) {
        recursive = rec;
    }

    public boolean isUpdateSnapshots() {
        return updateSnapshots;
    }
    
    public void setUpdateSnapshots(boolean set) {
        updateSnapshots = set;
    }
}
