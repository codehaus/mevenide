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
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.maven.model.Plugin;
import org.codehaus.mevenide.netbeans.NbMavenProject;
import org.codehaus.mevenide.netbeans.execute.model.NetbeansActionMapping;
import org.codehaus.mevenide.netbeans.execute.model.io.xpp3.NetbeansBuildActionXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.openide.filesystems.FileUtil;

/**
 * run configuration backed up by model
 * @author mkleint
 */
public final class ModelRunConfig implements RunConfig {
    
    private ClassLoader classloader;
    
    private NetbeansActionMapping model;
    
    private NbMavenProject project;
    
    /** Creates a new instance of ModelRunConfig */
    public ModelRunConfig(NbMavenProject proj, NetbeansActionMapping mod, ClassLoader loader) {
        project = proj;
        model = mod;
        classloader = loader;
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
    
    public ClassLoader getClassLoader() {
        return classloader;
    }

    
}
