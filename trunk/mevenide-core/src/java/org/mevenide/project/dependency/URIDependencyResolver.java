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

import java.net.URI;

/**
 * IDependencyResolver implementation based on URIs.
 * @author Milos Kleint (mkleint@codehaus.org)
 * 
 */
public class URIDependencyResolver implements IDependencyResolver {
	private String artifact;
        private URI uri;
	
	private String fileName;
	private String artifactId;
	private String version;
	private String extension;
	private String groupId;
        private String type;
	

	private IDependencySplitter.DependencyParts dependencyParts ;
	
	public void setURI(URI name) {
		artifact = name.getPath();
                uri = name;
		fileName = artifact.substring(artifact.lastIndexOf('/') + 1);
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
		initExtension();
		initGroupId();
		initArtifactIdAndVersion();
	}
	
	private void initArtifactIdAndVersion() {
		artifactId = dependencyParts.artifactId;
		if ( artifactId == null && fileName.indexOf("SNAPSHOT") > 0 ) {
                    artifactId = fileName.substring(0, fileName.indexOf("SNAPSHOT") - 1);
		}
                if (artifactId == null) {
                    // kind of hack.. even if we can't really guess the artifactId and version
                    // just slice by the last - character.
                    int ind = fileName.lastIndexOf('-');
                    if (ind > 0) {
                        artifactId = fileName.substring(0, ind);
                        version = fileName.substring(ind + 1, fileName.length() - (extension != null ? (extension.length() + 1) : 0));
                    } else {
                        artifactId = fileName;
                        version = "<Unknown>";
                    }
                } else {
                    if (extension != null && artifactId.length() < fileName.length()) {
                        version = fileName.substring(artifactId.length() + 1, fileName.length() - extension.length() - 1);
                    }
                }
	}
	
	private void initExtension() {
            String firstLevelParentName = getName(URI.create(getParent(uri)));
            if (firstLevelParentName.endsWith("s")) {
                type = firstLevelParentName.substring(0, firstLevelParentName.length() - 1);
            }
            if (type != null && fileName.endsWith(type)) {
                extension = type;
            } else if (type != null && fileName.endsWith("tar.gz")) { //NOI18N
                extension = "tar.gz"; //NOI18N
            } else {
                int ind = fileName.lastIndexOf('.');
                if (ind > -1) {
                    extension = fileName.substring(fileName.lastIndexOf('.') + 1);
                }
            }
            if (type == null) {
                type = extension;
            }
	}
	
        private void initGroupId() {
            String firstLevelParent = getParent(uri);
            if ( firstLevelParent != null
                    && getName(URI.create(firstLevelParent)).equalsIgnoreCase(guessExtension() + "s")
                    && getParent(URI.create(firstLevelParent)) != null ) {
                String secondParent = getParent(URI.create(firstLevelParent));
                if (secondParent != null) {
                    groupId = getName(URI.create(secondParent));
                }
            }
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
		String shortFileName = fileName;
		if ( shortFileName.lastIndexOf('.') >= 0 ) { 	
			return shortFileName.substring(0, shortFileName.length() - extension.length());
		}
		return shortFileName;
	}
	
	private static String getParent(URI uri) {
            int index = uri.getRawPath().lastIndexOf('/');
            if (index > 0) {
                String subPath = uri.getRawPath().substring(0, index);
                StringBuffer buf = new StringBuffer(uri.toString());
                int ind = uri.toString().indexOf(uri.getRawPath());
                buf.replace(ind, ind + uri.getRawPath().length(), subPath);
                return buf.toString();
            }
            return null;
        }
        
        private static String getName(URI uri) {
            int index = uri.getPath().lastIndexOf('/');
            if (index > -1 && index + 1 < uri.getPath().length()) {
                return uri.getPath().substring(index + 1);
            }
            return uri.getPath();
        }
	

}
