/*
 * BeanRunConfig.java
 *
 * Created on June 1, 2006, 10:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.codehaus.mevenide.netbeans.execute;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.codehaus.mevenide.netbeans.NbMavenProject;

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
    private Boolean showDebug;
    private Boolean showError;
    private Boolean offline;
    private List activeteProfiles;
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

    public Boolean isShowDebug() {
        return showDebug;
    }

    public void setShowDebug(Boolean showDebug) {
        this.showDebug = showDebug;
    }

    public Boolean isShowError() {
        return showError;
    }

    public void setShowError(Boolean showError) {
        this.showError = showError;
    }

    public Boolean isOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }

    public List getActiveteProfiles() {
        return activeteProfiles;
    }

    public void setActiveteProfiles(List activeteProfiles) {
        this.activeteProfiles = activeteProfiles;
    }
}
