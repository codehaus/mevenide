/* ==========================================================================
 * Copyright 2005-2006 Mevenide Team
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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.openide.filesystems.FileUtil;

/**
 * run configuration backed up by model
 * @author mkleint
 */
public final class ModelRunConfig implements RunConfig {
    
    private NetbeansActionMapping model;
    
    private NbMavenProject project;

    private Boolean showError;

    private Boolean showDebug;
    
    private Boolean offline;
    
    private List profiles;
    
    /** Creates a new instance of ModelRunConfig */
    public ModelRunConfig(NbMavenProject proj, NetbeansActionMapping mod) {
        project = proj;
        model = mod;
        profiles = new ArrayList();
    }
    
    public File getExecutionDirectory() {
        return FileUtil.toFile(project.getProjectDirectory());
    }
    
    public NbMavenProject getProject() {
        return project;
    }
    
    public List getGoals() {
        Iterator it = model.getGoals().iterator();
        while (it.hasNext()) {
            String elem = (String)it.next();
        }
        return model.getGoals();
    }
    
    public String getExecutionName() {
        return project.getName();
    }
    
    public Properties getProperties() {
        return model.getProperties();
    }
    
    public Boolean isShowDebug() {
        return showDebug;
    }

    public Boolean isShowError() {
        return showError;
    }

    public void setShowError(Boolean showError) {
        this.showError = showError;
    }

    public void setShowDebug(Boolean showDebug) {
        this.showDebug = showDebug;
    }

    public Boolean isOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }
    
    public List getActiveteProfiles() {
        return profiles;
    }


    
}
