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
import java.util.Collections;
import java.util.List;
import java.util.Properties;
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
    private Properties props;

    private ClassLoader classLoader;

    private List plugins;
    /** Creates a new instance of DefaultRunConfig */
    public DefaultRunConfig(NbMavenProject proj, List gls) {
        project = proj;
        goals = gls;
        props = new Properties();
        plugins = Collections.EMPTY_LIST;
    }
    
    public DefaultRunConfig(NbMavenProject proj, List gls, ClassLoader ldr) {
        this(proj, gls);
        classLoader = ldr;
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

    public Properties getProperties() {
        return props;
    }
    
    public ClassLoader getClassLoader() {
        return classLoader;
    }
    
    /**
     * list of <org.apache.maven.model.Plugin>
     */
    public List getAdditionalPluginConfigurations() {
        return plugins;
    }
    
    public void setAdditionalPluginConfigurations(List plugs) {
        assert plugs != null;
        plugins = plugs;
    }
}
