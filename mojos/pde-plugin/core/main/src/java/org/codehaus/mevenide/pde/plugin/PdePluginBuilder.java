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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.maven.model.Dependency;
import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.archive.Include;
import org.codehaus.mevenide.pde.archive.PdeArchiveException;
import org.codehaus.mevenide.pde.archive.SimpleZipCreator;
import org.codehaus.mevenide.pde.artifact.AbstractPdeArtifactBuilder;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;
import org.codehaus.plexus.util.StringUtils;


/**  
 * 
 * @author <a href="mailto:rhill2@free.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class PdePluginBuilder extends AbstractPdeArtifactBuilder {
    
    
    
    /** location of bundled libraries */
    private String libFolder;
    
    /** location of compiled classes */
    private String classesLocation;
    
    /** comma separated list of files to exclude from the generated zip */
    private String excludes;
    
    /** comma separated list of files to include in the generated zip */
    private List /** <org.codehaus.mevenide.pde.archive.Include> */ includes;
    
	/** indicates if the primary artifact should be marked as exported in the plugin descriptor */
	private boolean shouldExportArtifact;
	
	/** indicates if lib folder should be cleaned */
	private boolean cleanLib; 
	
	/** indicates if single jar'd plugin should be generated */
	private boolean singleJar;
	
	/** name of classes jar when single jar flag is not set */
	private String classesJarLocation;
	
	/** true if project has sources */
	private boolean sourcesPresent;
	
	public void createArchive() throws PdeArchiveException {
		String primaryDirectory = new File(classesLocation).getAbsolutePath();
		if ( !singleJar ) {
			primaryDirectory = null;
			SimpleZipCreator classesZipper = new SimpleZipCreator(new File(classesLocation).getAbsolutePath(), classesJarLocation);
			classesZipper.zip();
			Include classesJarInclude = new Include();
			classesJarInclude.setAbsolutePath(classesJarLocation);
			includes.add(classesJarInclude);
		}
		SimpleZipCreator zipCreator = new SimpleZipCreator(primaryDirectory, new File(artifact).getAbsolutePath());
		zipCreator.setExcludes(excludes);
		includeLibraries();
		zipCreator.setIncludes(includes);
		zipCreator.zip();
	}

	public void collectDependencies() throws CollectException {
		PluginDependencyCollector collector = new PluginDependencyCollector(basedir.getAbsolutePath(), libFolder, project);
		collector.setCleanLib(cleanLib);
		collector.setHelper(helper);
		collector.collect();
	}

	public void updateDescriptor() throws ReplaceException {
		PdePluginValuesReplacer replacer = new PdePluginValuesReplacer(basedir.getAbsolutePath(), project, libFolder);
		replacer.setArtifactName(artifactName);
		replacer.setSourcesPresent(sourcesPresent);
		replacer.shouldExportArtifact(shouldExportArtifact);
		replacer.setSingleJar(singleJar);
		if ( !singleJar ) {
			replacer.setClassesJarName(new File(classesJarLocation).getName());
		}
		replacer.replace();
	}

	private void includeLibraries() {
		File libFolderPath = new File(basedir, this.libFolder);
		File[] libs = libFolderPath.listFiles();
		if ( libs != null ) {
			for ( int u = 0; u < libs.length; u++ ) {
				if ( libs[u].isFile() ) {
					includes.add(new Include(libs[u].getAbsolutePath(), libFolderPath.getName() + "/" + libs[u].getName()));
				}
			}
		}
		
		includeNonDefaultFolderLibraries();
	}
    
    private void includeNonDefaultFolderLibraries() {
		List dependencies = project.getDependencies();
		
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
			if ( !excluded && !require && !StringUtils.isEmpty(overridenTargetPath) &&!libFolder.equals(overridenTargetPath) ) {
				File targetDir = new File(basedir, overridenTargetPath);
				
				String artifact = helper.getArtifact(dependency);
				
				File folder = new File(basedir, overridenTargetPath);
				String fileName = new File(artifact).getName();
				String path = overridenTargetPath.startsWith("/") ? overridenTargetPath : "/" + overridenTargetPath;
				includes.add(new Include(new File(folder, fileName).getAbsolutePath(), path + "/" + fileName));
			}
        }
	}

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
    
	public void setCleanLib(boolean cleanLib) { this.cleanLib = cleanLib; }
	public void setClassesJarLocation(String classesJarName) { this.classesJarLocation = classesJarName; }
	public void setSingleJar(boolean singleJar) { this.singleJar = singleJar; }
	
	public void setSourcesPresent(boolean sourcesPresent) { this.sourcesPresent = sourcesPresent; }
    
}
