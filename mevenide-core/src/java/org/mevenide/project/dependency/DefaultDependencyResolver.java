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

import java.io.File;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultDependencyResolver implements IDependencyResolver {
	private String artifact;
	
	private String fileName;
	
	private String artifactId;
	private String version;
	private String extension;
	private String groupId;
	

	private IDependencySplitter.DependencyParts dependencyParts ;
	
	public void setFileName(String fName) {
		this.artifact = fName;
		this.fileName = new File(fName).getName();
		dependencyParts = new DependencySplitter(fileName).split();
		init();
	}

	private void init() {
		initArtifactId();
		initVersion();
		initExtension();
		initGroupId();
	}
	
	private void initArtifactId() {
		artifactId = dependencyParts.artifactId;
		if ( artifactId == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			artifactId = fileName.substring(0, fileName.indexOf("SNAPSHOT") - 1);
		}
	}
	
	private void initVersion() {
		version = dependencyParts.version;
		if ( version == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			version = "SNAPSHOT";
		}
	}
	
	private void initExtension() {
		extension = fileName.substring(fileName.lastIndexOf('.') + 1);
	}
	
	private void initGroupId() {
		File fileToCompute = new File(artifact);
		File firstLevelParent = fileToCompute.getParentFile();
		if ( firstLevelParent != null && firstLevelParent.getParentFile() != null ) {
			groupId = firstLevelParent.getParentFile().getName();
		}
		if ( !DependencyUtil.isValidGroupId(groupId) ) {
			groupId = null;
		}
	}

	public String guessArtifactId() {
		return artifactId;
	}

	public String guessVersion() {
		return version;
	}

	public String guessExtension() {
		return extension;
	}

	public String guessGroupId()  {
		return groupId;
	}

	
	
	
	
	

}
