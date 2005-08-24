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
package org.mevenide.project;




/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public final class ProjectConstants {
	private ProjectConstants() { }
	
	public static final String MAVEN_TEST_DIRECTORY = "unitTestSourceDirectory";
	public static final String MAVEN_SRC_DIRECTORY = "sourceDirectory";
	public static final String MAVEN_ASPECT_DIRECTORY = "aspectSourceDirectory";
	public static final String MAVEN_OUTPUT_DIRECTORY = "outputDirectory";
	
	public static final String MAVEN_RESOURCE = "resources";
	public static final String MAVEN_TEST_RESOURCE = "unitTestResources";
	//public static final String MAVEN_INTEGRATION_TEST_RESOURCE = "integrationUnitTestResources";
	
	public static final String MAVEN_DEFAULT_OUTPUT_LOCATION = "target/classes";
	
	public static final String BASEDIR = "${basedir}";
}

