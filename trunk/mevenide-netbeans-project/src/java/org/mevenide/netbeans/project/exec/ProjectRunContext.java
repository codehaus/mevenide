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

package org.mevenide.netbeans.project.exec;

import java.io.File;
import org.mevenide.netbeans.api.project.MavenProject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author Milos Kleint (mkleint@codehaus.org)
 */
public class ProjectRunContext implements RunContext {
    private MavenProject project;
    /** Creates a new instance of ProjectRunContext */
    public ProjectRunContext(MavenProject proj) {
        project = proj;
    }

    public File getExecutionDirectory() {
        return FileUtil.toFile(project.getProjectDirectory());
    }

    public String getExecutionName() {
        return project.getName();
    }

    public String getMavenHome() {
        return project.getLocFinder().getMavenHome();
    }
    
    public String getMavenLocalHome() {
        return project.getLocFinder().getMavenLocalHome();
    }
    
    public String[] getAdditionalParams() {
        return new String[0];
    }
    
}
