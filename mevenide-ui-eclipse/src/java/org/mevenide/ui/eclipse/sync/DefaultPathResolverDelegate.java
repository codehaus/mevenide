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
package org.mevenide.ui.eclipse.sync;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.mevenide.ProjectConstants;
import org.mevenide.project.InvalidSourceTypeException;
import org.mevenide.sync.NoSuchSourcePathException;
import org.mevenide.ui.eclipse.MavenPlugin;

/**
 * will be soon deprecated
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
	public String computePathToAdd(IClasspathEntry classpathEntry, IProject project) {
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
		            ? "${basedir}" : "${basedir}" + pathToAdd; 
	                
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

	/**
	 * @refactor JDOM (again >.<)
	 */
    public String getMavenSourceType(IClasspathEntry entry, IProject project) throws Exception {
		String path = "";
		if (entry.getPath().segmentCount() != 0) {
			path = entry.getPath().removeFirstSegments(1).toString();
			//path = path.substring(1, path.length());
		}
	
		String entryKind = "src";
		Document doc = new SAXBuilder().build(project.getFile(".classpath").getLocation().toFile().getAbsoluteFile());
		Element classpath = doc.getRootElement();
		List classpathEntries = classpath.getChildren("classpathentry");
	
		return searchSourceType(path, classpathEntries);
	}

	private String searchSourceType(String path, List classpathEntries) throws InvalidSourceTypeException, NoSuchSourcePathException {
		for (int i = 0; i < classpathEntries.size(); i++) {
			Element cpe = (Element) classpathEntries.get(i);
			String srcPath = cpe.getAttributeValue("path");
		    String mavenSourceType = cpe.getAttributeValue("mavenSrcType");
			path = path.replace('\\','/');
			srcPath = srcPath.replace('\\','/');
		    if (path.equals(srcPath)) {
				if ( mavenSourceType == null ) {
		            //@todo FUNCTIONAL open a dialog instead
					return ProjectConstants.MAVEN_SRC_DIRECTORY;
				}
				return mavenSourceType;
			}
		}
		throw new NoSuchSourcePathException(path);
	}
}
