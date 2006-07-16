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

package org.codehaus.mevenide.netbeans.execute;

import java.io.File;
import java.util.List;
import java.util.Properties;
import org.codehaus.mevenide.netbeans.NbMavenProject;

/**
 * Context provider for maven executor
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public interface RunConfig {
    
    File getExecutionDirectory();

    NbMavenProject getProject();
    
    List getGoals();

    String getExecutionName();

    Properties getProperties();
    
    Boolean isShowDebug();
    
    Boolean isShowError();
    
    Boolean isOffline();

    List getActiveteProfiles();

    
}
