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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.project.dependency.DependencyFactory;
import org.mevenide.project.dependency.DependencyUtil;
import org.mevenide.properties.KeyValuePair;
import org.mevenide.properties.PropertyModel;
import org.mevenide.properties.PropertyModelFactory;

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
	
	void jarOverride(Dependency dependency, File propertiesFile, File pom) throws Exception {
		String path = dependency.getArtifact();
		Project project = ProjectReader.getReader().read(pom);
		if ( project.getDependencies() == null ) {
			project.setDependencies(new ArrayList());
		}
			
		Dependency dep = DependencyFactory.getFactory().getDependency(path);
	
		String groupId = dep.getGroupId();
		String artifactId = dep.getArtifactId();
	
		if ( groupId == null || groupId.length() == 0 ) {
			dep.setGroupId(null);
			//set id explicitly so that dependencies w/o groupId still are comparable are added to dep list if needed
			dep.setId(artifactId);
		}

		addPropertiesOverride(path, propertiesFile, dep);
	
		//project.getDependencies().remove(DependencyFactory.getFactory().getDependency(path));
		log.debug("adding unresolved dependency (" + path + ")" + DependencyUtil.toString(dep) + "to file " + pom.getAbsolutePath());
		project.addDependency(dep);
		
		writer.write(project, pom);
	
	}

	private void addPropertiesOverride(String path, File propertiesFile, Dependency dep) throws Exception {
		PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(propertiesFile, true);
		
		model.newKeyPair("maven.jar.override", '=', "on");
		
		model.newKeyPair("maven.jar." + dep.getArtifactId(), '=', path);
	
		model.store(new FileOutputStream(propertiesFile));
	} 

	void unsetOverriding(File propertiesFile) throws Exception {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			//fis = new FileInputStream(propertiesFile);
			PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(propertiesFile, true);
			
			List elements = model.getList();
			for (int i = 0; i < elements.size(); i++) {
				Object elem = elements.get(i);
				if ( elem instanceof KeyValuePair ) {
					KeyValuePair pair = (KeyValuePair) elem; 
					if ( pair.getKey().startsWith("maven.jar.") ) {
						model.removeElement(pair);
					}
				}
			}
	
			fos = new FileOutputStream(propertiesFile);
			model.store(fos);
		}
		finally {
			if ( fis != null ) {
				fis.close();
			}
			if ( fos != null ) {
				fos.close();
			}
		}
	}

}
