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
	private File file;
	private String artifactId;
	private String version;
	private String extension;
	private String groupId;
        private String type;
	

	private IDependencySplitter.DependencyParts dependencyParts ;
	
	public void setFileName(String fName) {
		artifact = fName;
                file = new File(fName);
		fileName = file.getName();
		dependencyParts = new DependencySplitter(fileName).split();
		init();
		//set id if groupId == null ?
	}

	private void init() {
                artifactId = null;
                version = null;
                extension = null;
                groupId = null;
                type = null;
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
            File firstLevelParent = file.getParentFile();
            if (firstLevelParent.getName().endsWith("s")) {
                type = firstLevelParent.getName().substring(0, firstLevelParent.getName().length() - 1);
            }
            if (type != null && fileName.endsWith(type)) {
                extension = type;
            } else {
                extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            }
            if (type == null) {
                type = extension;
            }
	}
	
	private void initGroupId() {
		File firstLevelParent = file.getParentFile();
		if ( firstLevelParent != null  
                      && firstLevelParent.getName().equalsIgnoreCase(guessExtension() + "s")
                      && firstLevelParent.getParentFile() != null ) {
			groupId = firstLevelParent.getParentFile().getName();
		}
// mkleint - DependencyUtil.isValidGroupId() is evil - consults the default 
//                local repository. what if there's more local repos?
//		if ( !DependencyUtil.isValidGroupId(groupId) ) {
//			groupId = null;
//		}
	}

	public String guessArtifactId() {
		return artifactId != null ? artifactId : getShortName();
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
        
        public String guessType() {
            return type;
        }
	
	private String getShortName() {
		String shortFileName = new File(fileName).getName();
		if ( shortFileName.lastIndexOf('.') >= 0 ) { 	
			return shortFileName.substring(0, shortFileName.lastIndexOf('.'));
		}
		return shortFileName;
	}
	
	
	

}
