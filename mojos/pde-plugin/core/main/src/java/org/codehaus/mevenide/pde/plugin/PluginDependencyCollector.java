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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.artifact.PdeBuilderHelper;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PluginDependencyCollector {

    /** absolute path of base directory */
    private String basedir;
    
    /** project under construction */
    private MavenProject project;
    
    /** default directory where collected deps will be placed. relative to <code>basedir</code> */
    private String targetPath;
    
	/** indicates if lib folder should be cleaned */
	private boolean cleanLib; 
	
	/** helper */
	private PdeBuilderHelper helper;
	
    public PluginDependencyCollector(String basedir, String targetPath, MavenProject project) {
        this.basedir = basedir;
        this.project = project;
        this.targetPath = targetPath;
    }
    
    public void collect() throws CollectException {
		
		List dependencies = project.getDependencies();
        
		//requires a two-pass iteration
		clean(dependencies);
		
        for (Iterator it = dependencies.iterator(); it.hasNext();) {
            Dependency dependency = (Dependency) it.next();
            
            Properties props = dependency.getProperties();
			
			boolean excluded = false; 
			boolean require = false;
			
            String overridenTargetPath = null;
            if ( props != null ) {
				excluded = BooleanUtils.toBoolean(props.getProperty("maven.pde.exclude"));
                overridenTargetPath = props.getProperty("maven.pde.targetPath");
				require = BooleanUtils.toBoolean(props.getProperty("maven.pde.requires"));
            }
			if ( !excluded && !require ) {
				String path = !StringUtils.isEmpty(overridenTargetPath) ? overridenTargetPath : targetPath;
				File targetDir = new File(basedir, path);
				if ( !targetDir.exists() ) {
					targetDir.mkdirs();
				}
            
				String artifact = helper.getArtifact(dependency);
            
				try {
					IOUtil.copy(new FileInputStream(artifact), new FileOutputStream(new File(targetDir, new File(artifact).getName())));
				}
				catch (IOException e) {
					throw new CollectException("Collector.CannotCollect", e);
				}
			}
        }
    }

    private void clean(List dependencies) {
		Set folderList = new TreeSet();
		if ( cleanLib ) {
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
	            Dependency dependency = (Dependency) it.next();
	            Properties props = dependency.getProperties();
	            if ( props != null ) {
	                String overridenTargetPath = props.getProperty("maven.pde.targetPath");
					if ( overridenTargetPath != null ) {
						folderList.add(overridenTargetPath);
					}
	            }
			}
			folderList.add(targetPath);
			Iterator it;
			for ( it = folderList.iterator(); it.hasNext(); ) {
				String path = (String) it.next();
				File folder = new File(basedir, path);
				if ( folder.exists() ) {
					// delete if contains only jar files 
					File[] children = folder.listFiles(new FileFilter() {
						public boolean accept(File pathname) {
							return pathname.isDirectory() || !pathname.getName().endsWith(".jar");
						}
					});
					if ( children.length == 0 ) {
						try {
							FileUtils.deleteDirectory(folder);
						} 
						catch (IOException e) {
							//do nothing
						}
					}
				}
			}
		}
	}

	
    
	public void setCleanLib(boolean cleanLib) { this.cleanLib = cleanLib; }

	public void setHelper(PdeBuilderHelper helper) { this.helper = helper; }
	
}
