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
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.mevenide.pde.PdePluginException;
import org.codehaus.mevenide.pde.archive.Include;
import org.codehaus.mevenide.pde.archive.SimpleZipCreator;
import org.codehaus.mevenide.pde.descriptor.CommonPluginValuesReplacer;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdePluginBuilder {
    
    /** base directory */    
    private File basedir;
    
    /** project under construction */
    private MavenProject project;
    
    /** location of bundled libraries */
    private String libFolder;
    
    /** location of compiled classes */
    private String classesLocation;
    
    /** name of generated zip file */
    private String artifact;
    
    /** comma separated list of files to exclude from the generated zip */
    private String excludes;
    
    /** comma separated list of files to include in the generated zip */
    private List /** <org.codehaus.mevenide.pde.archive.Include> */ includes;
    
	/** indicates if the primary artifact should be marked as exported in the plugin descriptor */
	private boolean shouldExportArtifact;
	
	/** artifactName referencing the primary artifact */
	private String artifactName;
	
	/** indicates wether lib folder should be cleaned */
	private boolean cleanLib; 
	
    public void build() throws PdePluginException {
	    CommonPluginValuesReplacer replacer = new CommonPluginValuesReplacer(basedir.getAbsolutePath(), project, libFolder);
		replacer.setArtifactName(artifactName);
		replacer.shouldExportArtifact(shouldExportArtifact);
	    replacer.replace();
	    
	    DependencyCollector collector = new DependencyCollector(basedir.getAbsolutePath(), libFolder, project);
		collector.setCleanLib(cleanLib);
	    collector.collect();
	    
	    SimpleZipCreator zipCreator = new SimpleZipCreator(new File(classesLocation).getAbsolutePath(), new File(artifact).getAbsolutePath());
	    zipCreator.setExcludes(excludes);
	    includeLibraries();
		zipCreator.setIncludes(includes);
	    zipCreator.zip();
    }

	private void includeLibraries() {
		File libFolderPath = new File(basedir, this.libFolder);
		File[] libs = libFolderPath.listFiles();
		for ( int u = 0; u < libs.length; u++ ) {
			if ( libs[u].isFile() ) {
				includes.add(new Include(libs[u].getAbsolutePath(), libFolderPath.getName() + "/" + libs[u].getName()));
			}
		}
	}
	
    public String getArtifact() { return artifact; }
    public void setArtifact(String artifact) { this.artifact = artifact; }
    
    public File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }
    
    public String getClassesLocation() { return classesLocation; }
    public void setClassesLocation(String classesLocation) { this.classesLocation = classesLocation; }
    
    public String getExcludes() { return excludes; }
    public void setExcludes(String excludes) { this.excludes = excludes; }
    
    public List getIncludes() { return includes; }
    public void setIncludes(List includes) { this.includes = includes; }
    
    public String getLibFolder() { return libFolder; }
    public void setLibFolder(String libFolder) { 
		this.libFolder = libFolder != null ? libFolder : "lib"; 
	}
 
	public boolean shouldExportArtifact() { return shouldExportArtifact; }
	public void setExportArtifact(boolean export) { shouldExportArtifact = export; }
	
    public MavenProject getProject() { return project; }
    public void setProject(MavenProject project) { this.project = project; }
	
	public void setArtifactName(String name) { this.artifactName = name; }

	public void setCleanLib(boolean cleanLib) { this.cleanLib = cleanLib; }
	
    
}
