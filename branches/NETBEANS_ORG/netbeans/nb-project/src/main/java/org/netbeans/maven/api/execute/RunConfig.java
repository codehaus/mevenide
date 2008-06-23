/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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

package org.netbeans.maven.api.execute;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.netbeans.api.project.Project;

/**
 * Context provider for maven executor
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public interface RunConfig {
    
    /**
     * directory where the maven build execution happens.
     * @return
     */
    File getExecutionDirectory();

    /**
     * project that is being used for execution, can be null.
     */ 
    Project getProject();
    /**
     * goals to be executed.
     */ 
    List<String> getGoals();

    String getExecutionName();
    
    String getTaskDisplayName();
    
    /**
     * properties to be used in the execution. Do not modify the returned Properties instance.
     * 
     * @return
     */
    Properties getProperties();

    /**
     * use the properties in the parameter for execution. Will not necessarily use the
     * parameter instance
     * 
     * @param properties property keys+value to be used in execution.
     */
    void setProperties(Properties properties);
    
    String setProperty(String key, String value);
    
    String removeProperty(String key);

    boolean isShowDebug();
    
    boolean isShowError();
    
    Boolean isOffline();
    
    void setOffline(Boolean bool);
    
    boolean isRecursive();
    
    boolean isUpdateSnapshots();

    List<String> getActivatedProfiles();
    
    void setActivatedProfiles(List<String> profiles);
    
    boolean isInteractive();

    
}
