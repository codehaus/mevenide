/* 
 * Copyright (C) 2003  Gilles Dodinet (gdodinet@wanadoo.fr)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 */
package org.mevenide.ui.eclipse.sync.source;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * will be soon deprecated
 * 
 * @refactor crappy !!!  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: ResourcesUtil.java 4 mai 2003 10:17:0413:34:35 Exp gdodinet 
 * 
 */
public class DefaultPathResolverDelegate implements IPathResolverDelegate {

	/**
	 * extract the source path to add to the pom from the given classpathentry
	 * @todo BUGGYRISK what about projects whose root is not in workspace ? 
	 * 
	 * @param classpathEntry
	 * @return
	 */
	public String computePath(IClasspathEntry classpathEntry, IProject project) {
		IPath rootLocation = 
		        MavenPlugin.getWorkspace().getRoot().getLocation();
		
	    String workspaceRoot = 
	            getAbsolutePath(rootLocation.append(classpathEntry.getPath()));
		
	    String pathToAdd = 
	            workspaceRoot.substring(
	                getAbsolutePath(project.getLocation()).length(), 
	                workspaceRoot.length()
	            ); 
		
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
	    return path.toFile().getAbsoluteFile().toString();
	}

	public String getMavenSourceType(String sourceDirectoryPath, IProject project) throws Exception {
		if ( project == null ) {
			throw new Exception("project should not be null");
		}
		Document doc = new SAXBuilder().build(MavenPlugin.getPlugin().getFile("sourceTypes.xml"));
		Element root = doc.getRootElement();
		List sdGroupElements = root.getChildren("sourceDirectoryGroup");
		
		for (int i = 0; i < sdGroupElements.size(); i++) {
			Element group = (Element) sdGroupElements.get(i); 
			if ( project.getName().equals(group.getAttributeValue("projectName")) ) {
				return getType(sourceDirectoryPath, group);
			}
		}
		
		return "UNKNOWN";
	}

	private String getType(String path, Element group) {
		List sourceDirectories = group.getChildren("sourceDirectory");
		for (int i = 0; i < sourceDirectories.size(); i++) {
			Element sourceDirectory = (Element) sourceDirectories.get(i);
			if ( sourceDirectory.getAttributeValue("path").equals(path) ) {
				return sourceDirectory.getAttributeValue("type");
			}
		}
		return "UNKNOWN";
	}
}
