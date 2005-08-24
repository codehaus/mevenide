/* ==========================================================================
 * Copyright 2003-2005 MevenIDE Project
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

package org.mevenide.ui.eclipse;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.project.ProjectConstants;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ResourcesUtil.java 4 mai 2003 10:17:0413:34:35 Exp gdodinet 
 * 
 */
public class DefaultPathResolver implements IPathResolver {

	private static final String SOURCE_TYPES_XML_FILE = "sourceTypes.xml"; //$NON-NLS-1$
    private static final String UNKNOWN_SRC_TYPE = "UNKNOWN"; //$NON-NLS-1$
    /**
	 * extract the source path to add to the pom from the given classpathentry
	 * 
	 * @param classpathEntry
	 * @return
	 */
	public String getRelativeSourceDirectoryPath(IClasspathEntry classpathEntry, IProject project) {
		IPath path = classpathEntry.getPath();
		
		return getRelativePath(project, path);
	}

	public String getRelativePath(IProject project, IPath path) {
		String pathToAdd = path.toOSString();
		
		pathToAdd = pathToAdd.substring(project.getFullPath().toOSString().length(), pathToAdd.length());
		
		pathToAdd = (pathToAdd.equals("/") || pathToAdd.equals(""))  //$NON-NLS-1$ //$NON-NLS-2$
		            ? ProjectConstants.BASEDIR : pathToAdd.substring(1); 
		            
		return pathToAdd;
	}

	/**
	 * utility method
	 * compute the absolute file location of the given ipath 
	 * 
	 * @param path
	 * @return
	 */
    public String getAbsolutePath(IPath path) {
    	return path.toOSString();
	}

	public String getMavenSourceType(String sourceDirectoryPath, IProject project) throws Exception {
		if ( project == null ) {
			throw new Exception("project should not be null"); //$NON-NLS-1$
		}
		Document doc = new SAXBuilder().build(Mevenide.getInstance().getStateLocation().append(SOURCE_TYPES_XML_FILE).toOSString());
		Element root = doc.getRootElement();
		List sdGroupElements = root.getChildren(XmlSerializationConstants.SOURCE_DIRECTORY_GROUP_ELEM);
		
		for (int i = 0; i < sdGroupElements.size(); i++) {
			Element group = (Element) sdGroupElements.get(i); 
			if ( project.getName().equals(group.getAttributeValue(XmlSerializationConstants.PROJECT_NAME_ATTR)) ) {
				return getType(sourceDirectoryPath, group);
			}
		}
		
		return UNKNOWN_SRC_TYPE;
	}

	private String getType(String path, Element group) {
		List sourceDirectories = group.getChildren(XmlSerializationConstants.SOURCE_DIRECTORY_GROUP_ELEM);
		for (int i = 0; i < sourceDirectories.size(); i++) {
			Element sourceDirectory = (Element) sourceDirectories.get(i);
			if ( sourceDirectory.getAttributeValue(XmlSerializationConstants.PATH_ATTR).equals(path) ) {
				return sourceDirectory.getAttributeValue(XmlSerializationConstants.TYPE_ATTR);
			}
		}
		return UNKNOWN_SRC_TYPE;
	}
}
