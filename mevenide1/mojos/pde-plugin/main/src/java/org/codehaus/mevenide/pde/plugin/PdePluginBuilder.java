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

import org.codehaus.mevenide.pde.CollectException;
import org.codehaus.mevenide.pde.archive.Include;
import org.codehaus.mevenide.pde.archive.PdeArchiveException;
import org.codehaus.mevenide.pde.archive.SimpleZipCreator;
import org.codehaus.mevenide.pde.artifact.AbstractPdeArtifactBuilder;
import org.codehaus.mevenide.pde.descriptor.ReplaceException;


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
    
	/** indicates if lib folder should be cleaned */
	private boolean cleanLib; 
	
	/** true if project has sources */
	private boolean sourcesPresent;
	
	public void createArchive() throws PdeArchiveException {
		String primaryDirectory = new File(classesLocation).getAbsolutePath();
		SimpleZipCreator zipCreator = new SimpleZipCreator(primaryDirectory, new File(artifact).getAbsolutePath());
		zipCreator.setExcludes(excludes);
		includeResources();
		includeLibraries();
		zipCreator.setIncludes(includes);
		zipCreator.zip();
	}

	public void collectDependencies() throws CollectException {
		PluginDependencyCollector collector = new PluginDependencyCollector(basedir.getAbsolutePath(), libFolder, project);
		collector.setCleanLib(cleanLib);
		collector.collect();
	}

	public void updateDescriptor() throws ReplaceException {
		PdePluginDescriptorReplacer replacer = new PdePluginDescriptorReplacer(basedir.getAbsolutePath(), project, libFolder);
		replacer.setArtifactName(artifactName);
		replacer.setSourcesPresent(sourcesPresent);
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
	}
    
    
	protected String[] getCommonIncludes() {
		return new String[] { "plugin.xml", "plugin.properties", "license.txt" };
	}

	public String getClassesLocation() { return classesLocation; }
    public void setClassesLocation(String classesLocation) { this.classesLocation = classesLocation; }
    
    public String getLibFolder() { return libFolder; }
    public void setLibFolder(String libFolder) { 
		this.libFolder = libFolder != null ? libFolder : "lib"; 
	}
 
    public void setCleanLib(boolean cleanLib) { this.cleanLib = cleanLib; }
	
}
