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

import java.io.Writer;

import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;


/**
 * 
 * @choice we do not respect the structure defined in maven-project.xsd
 * 
 * instead we follow the structure used  by DefaultProjectUnmarshaller
 * incase we want to respect the xsd, just replace with a previous 
 * version (should be 1.2)   
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DefaultProjectMarshaller implements IProjectMarshaller {
	
	MavenXpp3Writer xpp3Writer;
	
	public DefaultProjectMarshaller() throws Exception {
		xpp3Writer = new MavenXpp3Writer();
	}

	public  void marshall(Writer pom, MavenProject project) throws Exception {
	    xpp3Writer.write(pom, project.getModel());
	}
}
