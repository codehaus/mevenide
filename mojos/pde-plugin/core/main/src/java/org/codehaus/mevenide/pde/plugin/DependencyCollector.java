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
package org.codehaus.mevenide.pde.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencyCollector {

    /** absolute path of base directory */
    private String basedir;
    
    /** project under construction */
    private MavenProject project;
    
    /** default directory where collected deps will be placed. relative to <code>basedir</code> */
    private String defaultTargetPath;
    
    public DependencyCollector(String basedir, String defaultTargetPath, MavenProject project) {
        this.basedir = basedir;
        this.project = project;
    }
    
    public void collect() throws CollectException {
        List dependencies = project.getDependencies();
        
        for (Iterator it = dependencies.iterator(); it.hasNext();) {
            Dependency dependency = (Dependency) it.next();
            
            Properties props = dependency.getProperties();
            String overridenTargetPath = null;
            if ( props != null ) {
                overridenTargetPath = props.getProperty("maven.pde.targetPath");
            }
            String targetPath = !StringUtils.isEmpty(overridenTargetPath) ? overridenTargetPath : defaultTargetPath;
            File targetDir = new File(basedir, targetPath);
            if ( !targetDir.exists() ) {
                targetDir.mkdirs();
            }
            
            String artifact = dependency.getArtifact();
            try {
                IOUtil.copy(new FileInputStream(artifact), new FileOutputStream(new File(targetPath, new File(artifact).getName())));
            }
            catch (IOException e) {
                throw new CollectException("Collector.CannotCollect", e);
            }
        }
    }
    
}
