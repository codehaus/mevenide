/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.ui.eclipse;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ResourcesUtil.java 4 mai 2003 10:17:0413:34:35 Exp gdodinet 
 * 
 */
public class DefaultPathResolver implements IPathResolver {

	private static final String UNKNOWN_SRC_TYPE = "UNKNOWN";
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
		
		pathToAdd = (pathToAdd.equals("/") || pathToAdd.equals("")) 
		            ? "${basedir}" : pathToAdd.substring(1); 
		            
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
			throw new Exception("project should not be null");
		}
		Document doc = new SAXBuilder().build(Mevenide.getPlugin().getFile("sourceTypes.xml"));
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
