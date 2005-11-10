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

import java.io.File;
import java.util.List;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class DefaultRunConfig implements RunConfig {
    
    private NbMavenProject project;
    private List goals;
    /** Creates a new instance of DefaultRunConfig */
    public DefaultRunConfig(NbMavenProject proj, List gls) {
        project = proj;
        goals = gls;
    }

    public File getExecutionDirectory() {
        return FileUtil.toFile(project.getProjectDirectory());
    }

    public List getGoals() {
        return goals;
    }

    public String[] getParameters() {
        return new String[0];
    }

    public String getExecutionName() {
        return project.getName();
    }
    
    public NbMavenProject getProject() {
        return project;
    }
    
}
