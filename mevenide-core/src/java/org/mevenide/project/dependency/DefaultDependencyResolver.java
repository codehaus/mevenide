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
package org.mevenide.project.dependency;

import java.io.File;

import org.mevenide.Environment;

/**
 * @still some refactoring to be done (init-phase)
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultDependencyResolver extends AbstractDependencyResolver {
	private String absoluteFileName;
	
	private String fileName;
	
	/**
	 * 
	 * decomposition[0] = artifactId
	 * decomposition[1] = version
	 * decomposition[2] = extension
	 * 
	 */
	private String[] decomposition = new String[3];
	
	public void setFileName(String fName) {
		this.absoluteFileName = fName;
		this.fileName = new File(fName).getName();
		decomposition = new DependencySplitter(fileName).split();
	}
	
	/**
	 *
	 * @param fileName
	 * @return
	 */
	public String getGroupId() {
		File mavenLocalRepo = new File(Environment.getMavenRepository());
		return getGroupId(mavenLocalRepo);
	}

	/**
	 * assume a standard repository layout, ergo a file is under one level under group Directory
	 * e.g. mevenide/jars/mevenide-core-0.1.jar 
	 * 
	 * @param fileName the short file Name (e.g. mevenide-core-0.1.jar) 
	 * @param rootDirectory
	 * @return
	 */
	private String getGroupId(File rootDirectory) {
		File[] files = rootDirectory.listFiles();
		File[] children = files == null ? new File[0] : files;
		for (int i = 0; i < children.length; i++) {
			if ( children[i].isDirectory() ) {
				String candidate = getGroupId(children[i]);
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

	/**
	 * @param fileName the short file name
	 */
	public String guessArtifactId() {
		
		String artifactId = decomposition[0];
		if ( artifactId == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			return fileName.substring(0, fileName.indexOf("SNAPSHOT") - 1);
		}
		return artifactId;
		
	}

	public String guessVersion() {
		/*if ( fileName.indexOf("SNAPSHOT") > 0 ) {
			return "SNAPSHOT";
		}*/
		String version = decomposition[1];
		if ( version == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			return "SNAPSHOT";
		}
		return version;
	}

	/**
	 * assume a layout similar to the one of the local repo 
	 * e.g. mevenide/jars/mevenide-core-0.1.jar 
	 * else it is quite impossible  to guess the groupÎd..
	 * 
	 * f.i. if the artefact is located under project_home/lib
	 * this method returns project_home and thats not quite consistent..
	 * 
	 * just wondering : is it so important to have the groupId ? we could just
	 * leave it as "Not Found" and warn the user about it 
	 * 
	 * @wonder get rid of that method ? 
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	public String guessGroupId()  {
		File fileToCompute = new File(absoluteFileName);
		
		File firstLevelParent = fileToCompute.getParentFile();
		if ( firstLevelParent.getParentFile() != null ) {
			return firstLevelParent.getParentFile().getName();
		}
		else return null;
//		return "Not Found";
	}
	
	public String guessExtension() {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}
	
	

}
