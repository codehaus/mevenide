/* ==========================================================================
 * Copyright 2003-2004 Apache Software Foundation
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
package org.mevenide.project.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: Overrider.java,v 1.1 24 sept. 2003 Exp gdodinet 
 * 
 */
class JarOverrideWriter {
	private static Log log = LogFactory.getLog(JarOverrideWriter.class);
	
	private ProjectWriter writer;
	
	JarOverrideWriter(ProjectWriter projectWriter) {
		this.writer = projectWriter;
	}
	
	void jarOverride(String path, File propertiesFile, File pom) throws Exception {
		Project project = ProjectReader.getReader().read(pom);
		if ( project.getDependencies() == null ) {
			project.setDependencies(new ArrayList());
		}
			
		Dependency dep = DependencyFactory.getFactory().getDependency(path);
	
		String groupId = dep.getGroupId();
		String artifactId = dep.getArtifactId();
	
		if ( groupId == null || groupId.length() == 0 ) {
			dep.setGroupId("nonResolvedGroupId");
		}
		if ( artifactId == null || artifactId.length() == 0 ) {
			String fname = new File(path).getName().substring(0, new File(path).getName().lastIndexOf('.'));
			dep.setArtifactId(fname);
		}
	
		addPropertiesOverride(path, propertiesFile, dep);
	
		//project.getDependencies().remove(DependencyFactory.getFactory().getDependency(path));
		log.debug("adding unresolved dependency (" + path + ")" + DependencyUtil.toString(dep) + "to file " + pom.getAbsolutePath());
		project.addDependency(dep);
		
		writer.write(project, pom);
	
	}

	private void addPropertiesOverride(String path, File propertiesFile, Dependency dep) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
	
	
		properties.setProperty("maven.jar.override", "on");
	
		properties.setProperty("maven.jar." + dep.getArtifactId(), path);
	
		properties.store(new FileOutputStream(propertiesFile), null);
	} 

	void unsetOverriding(File propertiesFile) throws Exception {
		Properties properties = new Properties();
		properties.load(new FileInputStream(propertiesFile));
				
		List keys = Collections.list(properties.keys());
		for (int i = 0; i < keys.size(); i++) {
			Object key = keys.get(i);
			if ( key instanceof String && ((String) key).startsWith("maven.jar.") ) {
				properties.remove(key);
			}
		}
		properties.store(new FileOutputStream(propertiesFile), null);
	}

}
