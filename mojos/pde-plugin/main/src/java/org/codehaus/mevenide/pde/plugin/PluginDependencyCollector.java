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
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.PdeConstants;
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
	
	
	
    public PluginDependencyCollector(String basedir, String targetPath, MavenProject project) {
        this.basedir = basedir;
        this.project = project;
        this.targetPath = targetPath;
    }
    
    public void collect() throws CollectException {
		
		Set artifacts = project.getArtifacts();
        
		clean();
		
        for (Iterator it = artifacts.iterator(); it.hasNext();) {
            Artifact artifact = (Artifact) it.next();
            
			if ( !PdeConstants.PDE_TYPE.equals(artifact.getType()) && !Artifact.SCOPE_TEST.equals(artifact.getScope()) ) {
				File targetDir = new File(basedir, targetPath);
				try {
					IOUtil.copy(new FileInputStream(artifact.getFile()), new FileOutputStream(new File(targetDir, artifact.getFile().getName())));
				}
				catch (IOException e) {
					throw new CollectException("Collector.CannotCollect", e);
				}
			}
        }
    }

    private void clean() {
		if ( cleanLib ) {
			File folder = new File(basedir, targetPath);
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

	
    
	public void setCleanLib(boolean cleanLib) { this.cleanLib = cleanLib; }

}
