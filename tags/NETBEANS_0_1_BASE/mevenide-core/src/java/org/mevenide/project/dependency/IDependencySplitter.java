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
package org.mevenide.project.dependency;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: IDependencySplitter.java,v 1.1 24 sept. 2003 Exp gdodinet 
 * 
 */
public interface IDependencySplitter {
	
	/**
	 * representation of a splitted dependency, excluding groupId  
	 * 
	 */
	public class DependencyParts {
		public String artifactId;
		public String version;
		public String extension;
	}
	
    /**
     * split a filename into three parts   
     * 
     * @param fileName
     * @return DependencyParts
     */
    DependencyParts split();
}