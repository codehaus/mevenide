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
 */
package org.mevenide.project;


import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.project.Dependency;
import org.mevenide.Environment;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyFactory {
	
	private DependencyFactory() {
	}
	
	/** singleton related */
	private static DependencyFactory factory = null;
	private static Object lock = new Object();

	public static DependencyFactory getFactory() {
		if (factory != null) {
			return factory;
		}
		synchronized (lock) {
			if (factory == null) {
				factory = new DependencyFactory();
			}
			return factory;
		}
	}
		
	/**
	 * should return the Dependency instance associated with a given path.
	 * however this seems hard if not impossible to achieve. indeed i cannot 
	 * imagine yet a pertinent way to extract the required information to build a coherent
	 * Dependency. 
	 * 
	 * so for now i'll stick with the jar overriding mechanism provided by maven  
	 * 
	 * in order to minimize the burden, we will check if dependencies declared 
	 * in the project descriptor match some ide libraries, and use maven.jar.override
	 * if no match is found for the current path.
	 *  
	 * Also if a file is found in local repo that match the fileName passed as parameters, 
	 * we'll use parent.name as groupId. in either case we have to guess artifactId and version from the 
	 * fileName.
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	public Dependency getDependency(String absoluteFileName) {
		String fileName = new File(absoluteFileName).getName();
		String groupId = getGroupId(fileName);
		
		if ( groupId == null ) {
			groupId = guessGroupId(absoluteFileName); 
		}
		String artifactId = guessArtifactId(fileName);
		String version = guessVersion(fileName);
		
		Dependency dependency = new Dependency();
		
		dependency.setGroupId(groupId); //?
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);
		dependency.setArtifact(absoluteFileName);
		
		return dependency;
	}
	
	/**
	 * assume here that local repo is under maven_home...
	 * @todo add a mavenLocalRepo attribute to org.mevenide.Environment
	 * 
	 * @param fileName
	 * @return
	 */
	private String getGroupId(String fileName) {
		File mavenLocalRepo = new File(Environment.getMavenHome(), "repository");
		return getGroupId(fileName, mavenLocalRepo);
	}
	
	/**
	 * assume a standard repository layout, ergo a file is under one level under group Directory
	 * e.g. mevenide/jars/mevenide-core-0.1.jar 
	 * 
	 * @param fileName
	 * @param rootDirectory
	 * @return
	 */
	private String getGroupId(String fileName, File rootDirectory) {
		File[] files = rootDirectory.listFiles();
		File[] children = files == null ? new File[0] : files;
		for (int i = 0; i < children.length; i++) {
			if ( children[i].isDirectory() ) {
				String candidate = getGroupId(fileName, children[i]);
				if ( candidate != null ) {
					return candidate;
				}
			}
			else {
				if ( children[i].getName().equals(fileName) ) {
					return rootDirectory.getParentFile().getName();
				}
			}
		}
		return null;
	}
	
	private String guessArtifactId(String fileName) {
		return fileName.substring(0,1) + split(fileName)[1];
	}
	
	private String guessVersion(String fileName) {
		if ( fileName.indexOf("SNAPSHOT") > 0 ) {
			return "SNAPSHOT";
		}
		return split(fileName)[2];
	}
	
	/**
	 * we assume that fileName follow that kind of pattern :
	 * 
	 * (.*)-(\\d+(.*))*\\.(\\w*)
	 * 
	 * so we have $1 => artifactId ; $2 => version ; $4 => extension 
	 *  
	 * @bug for some artefacts such as jexl (jexl-1.0-beta-1.jar) this pattern doesnt work. 
	 * 
	 * someone please provide with a more correct pattern ! 
	 * 
	 * 
	 * @param fileName
	 * @return
	 */
	private String[] split(String fileName) {
		String[] groups ;
		
		Pattern p = Pattern.compile("(.*)-(\\d+(.*))*\\.(\\w*)");
		Matcher m = p.matcher(fileName);
		groups = new String[m.groupCount() + 1];
		int i = 0;
		while ( i < m.groupCount() + 1 && m.find(i) ) {
			groups[i] = m.group(i);
			i++;
		}
		return groups;
	}
	
	/**
	 * assume a layout similar to the one of the local repo 
	 * e.g. mevenide/jars/mevenide-core-0.1.jar 
	 * else it is quite impossible  to guess the groupÎd..
	 * 
	 * f.i. if the artefact is located under project_home/lib
	 * this method returns project_home and thats not quite consistent..
	 * 
	 * just wondering : is it so important to have the groupId ?
	 * 
	 * @wonder get rid of that method ? 
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	private String guessGroupId(String absoluteFileName) {
		return new File(absoluteFileName).getParentFile().getParentFile().getName();
	}
}
