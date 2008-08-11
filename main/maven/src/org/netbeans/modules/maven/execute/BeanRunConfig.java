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
package org.netbeans.modules.maven.execute;

import org.netbeans.modules.maven.api.execute.RunConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.netbeans.modules.maven.options.MavenExecutionSettings;
import org.netbeans.api.project.Project;

/**
 *
 * @author mkleint
 */
public class BeanRunConfig implements RunConfig {
    
    private File executionDirectory;
    private Project project;
    private List<String> goals;
    private String executionName;
    private Properties properties;
    private boolean showDebug = MavenExecutionSettings.getDefault().isShowDebug();
    private boolean showError = MavenExecutionSettings.getDefault().isShowErrors();
    private Boolean offline;
    private List<String> activate;
    private boolean updateSnapshots = false;
    private boolean recursive = true;
    private String taskName;
    private boolean interactive = true;
    private RunConfig parent;
    private String actionName;
    
    /** Creates a new instance of BeanRunConfig */
    public BeanRunConfig() {
    }

    /**
     * create a new instance that wraps around the parent instance, allowing
     * to change values while delegating to originals if not changed.
     * @param parent
     */
    public BeanRunConfig(RunConfig parent) {
        this.parent = parent;
        //boolean props need to be caried over
        setRecursive(parent.isRecursive());
        setInteractive(parent.isInteractive());
        setOffline(parent.isOffline());
        setShowDebug(parent.isShowDebug());
        setShowError(parent.isShowError());
        setUpdateSnapshots(parent.isUpdateSnapshots());
    }

    public File getExecutionDirectory() {
        if (parent != null && executionDirectory == null) {
            return parent.getExecutionDirectory();
        }
        return executionDirectory;
    }

    public void setExecutionDirectory(File executionDirectory) {
        this.executionDirectory = executionDirectory;
    }

    public Project getProject() {
        if (parent != null && project == null) {
            return parent.getProject();
        }
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<String> getGoals() {
        if (parent != null && goals == null) {
            return parent.getGoals();
        }
        return goals;
    }

    public void setGoals(List<String> goals) {
        this.goals = goals;
    }

    public String getExecutionName() {
        if (parent != null && executionName == null) {
            return parent.getExecutionName();
        }
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public Properties getProperties() {
        if (parent != null && properties == null) {
            return parent.getProperties();
        }
        Properties newProperties = new Properties();
        if (properties != null) {
            newProperties.putAll(properties);
        }
        return newProperties;
    }

    public  String removeProperty(String key) {
        if (properties == null) {
            properties = new Properties();
            if (parent != null) {
                properties.putAll(parent.getProperties());
            }
        }
        return (String) properties.remove(key);
    }

    public  String setProperty(String key, String value) {
        if (properties == null) {
            properties = new Properties();
            if (parent != null) {
                properties.putAll(parent.getProperties());
            }
        }
        return (String) properties.setProperty(key, value);
    }

    public void setProperties(Properties props) {
        if (properties == null) {
            properties = new Properties();
        }
        properties.clear();
        properties.putAll(props);
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

    public List<String> getActivatedProfiles() {
        if (parent != null && activate == null) {
            return parent.getActivatedProfiles();
        }
        if (activate != null) {
            return Collections.unmodifiableList(activate);
        }
        return Collections.<String>emptyList();
    }

    public void setActivatedProfiles(List<String> activeteProfiles) {
        activate = new ArrayList<String>();
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

    public String getTaskDisplayName() {
        if (parent != null && taskName == null) {
            return parent.getTaskDisplayName();
        }
        return taskName;
    }
    
    public void setTaskDisplayName(String name) {
        taskName = name;
    }

    public boolean isInteractive() {
        return interactive;
    }
    
    public void setInteractive(boolean ia) {
        interactive = ia;
    }


    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionName()
    {
        if (parent != null && actionName == null) {
            return parent.getActionName();
        }
        return actionName;
    }
    
    
}
