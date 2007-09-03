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

import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.openide.filesystems.FileUtil;

/**
 * run configuration backed up by model
 * @author mkleint
 */
public final class ModelRunConfig extends BeanRunConfig {
    
    private NetbeansActionMapping model;
    
    /** Creates a new instance of ModelRunConfig */
    public ModelRunConfig(NbMavenProject proj, NetbeansActionMapping mod) {
        model = mod;
        setProject(proj);
        setExecutionName(proj.getName());
        setProperties(model.getProperties());
        setGoals(model.getGoals());
        setExecutionDirectory(FileUtil.toFile(proj.getProjectDirectory()));
        setRecursive(mod.isRecursive());
        setActiveteProfiles(mod.getActivatedProfiles());
    }
    
}
