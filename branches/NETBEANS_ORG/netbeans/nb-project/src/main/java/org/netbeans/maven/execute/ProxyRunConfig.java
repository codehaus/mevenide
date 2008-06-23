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

package org.netbeans.maven.execute;

import org.netbeans.maven.api.execute.RunConfig;
import java.io.File;
import java.util.List;
import java.util.Properties;
import org.netbeans.api.project.Project;

/**
 *
 * @author mkleint
 */
public class ProxyRunConfig implements RunConfig {

    private RunConfig delegate;
    
    /** Creates a new instance of ProxyRunConfig */
    public ProxyRunConfig(RunConfig delegate) {
        this.delegate = delegate;
    }

    public File getExecutionDirectory() {
        return delegate.getExecutionDirectory();
    }

    public Project getProject() {
        return delegate.getProject();
    }

    public List getGoals() {
        return delegate.getGoals();
    }

    public String getExecutionName() {
        return delegate.getExecutionName();
    }

    public Properties getProperties() {
        return delegate.getProperties();
    }
    
    public  String removeProperty(String key) {
        return delegate.removeProperty(key);
    }

    public  String setProperty(String key, String value) {
        return delegate.setProperty(key, value);
    }
    public void setProperties(Properties properties){
      delegate.setProperties(properties);
    }
    public boolean isShowDebug() {
        return delegate.isShowDebug();
    }

    public boolean isShowError() {
        return delegate.isShowError();
    }

    public Boolean isOffline() {
        return delegate.isOffline();
    }

    public List getActivatedProfiles() {
        return delegate.getActivatedProfiles();
    }
    
    public void setActivatedProfiles(List<String> activeteProfiles) {
        delegate.setActivatedProfiles(activeteProfiles);
    }
    

    public boolean isRecursive() {
        return delegate.isRecursive();
    }

    public boolean isUpdateSnapshots() {
        return delegate.isUpdateSnapshots();
    }
    
    public void setOffline(Boolean off) {
        delegate.setOffline(off);
    }

    public String getTaskDisplayName() {
        return delegate.getTaskDisplayName();
    }

    public boolean isInteractive() {
        return delegate.isInteractive();
    }
    
}
