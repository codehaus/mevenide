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
package org.mevenide.project.resource;

import org.apache.maven.project.Project;
import org.apache.maven.project.Resource;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public interface IResourceResolver {
	/**
	 * iterate ${pom.build.resources} and merge those whose directory is equal to 
	 * the directory of resource passed as parameter with the later.
	 * 
	 * @param project
	 * @param resource
	 * @return boolean
	 */
	void mergeSimilarResources(
		Project project,
		Resource resource);

	/**
	 * iterate ${pom.build.unitTest.resources} and merge those whose directory is equal to 
	 * the directory of resource passed as parameter with the later.
	 * 
	 * @param project
	 * @param resource
	 * @return boolean
	 */
	void mergeSimilarUnitTestResources(
		Project project,
		Resource resource);
	
}