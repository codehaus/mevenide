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
package org.mevenide.project.io;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Project;
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
	
	void jarOverride(String artifactId, String path, Project project) throws Exception {
		File propertiesFile = new File(project.getFile().getParentFile(), "project.properties");
		
		PropertyModel model = PropertyModelFactory.getFactory().newPropertyModel(propertiesFile);
		
		model.newKeyPair("maven.jar.override", '=', "on");
		model.newKeyPair("maven.jar." + artifactId, '=', path.replaceAll("\\\\", "/"));
		model.store(new FileOutputStream(propertiesFile));
	}

}
