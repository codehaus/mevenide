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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.Environment;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultDependencyResolver extends AbstractDependencyResolver {
	/**
	 * checks if a Dependency identified by its artifact path is present in the POM.
	 * 
	 * testing artifact doesnt seem to be a good solution since it is often omitted
	 * we rather have to test artifactId and version.
	 * 
	 * @param project
	 * @param absoluteFileName
	 * @return
	 */
	public boolean isDependencyPresent(Project project, Dependency dependency) {
		List dependencies = project.getDependencies();
		if ( dependencies == null ) {
			return false;
		}
		for (int i = 0; i < dependencies.size(); i++) {
			Dependency declaredDependency = (Dependency) dependencies.get(i);
	
			String version = declaredDependency.getVersion(); 
			String artifactId = declaredDependency.getArtifactId();
	
			if (  artifactId != null && artifactId.equals(dependency.getArtifactId()) 
				  && version != null && version.equals(dependency.getVersion())) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param fileName
	 * @return
	 */
	public String getGroupId(String fileName) {
		File mavenLocalRepo = new File(Environment.getMavenRepository());
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

	public String guessArtifactId(String fileName) {
		return split(fileName)[0];
	}

	public String guessVersion(String fileName) {
		if ( fileName.indexOf("SNAPSHOT") > 0 ) {
			return "SNAPSHOT";
		}
		return split(fileName)[1];
	}

	/**
	 * we assume that fileName follow that kind of pattern :
	 * 
	 * (.|(-\\D)+)-((\\d)+(.*))\\.(\\w+)
	 * 
	 * so we have $4 => version ; $7 => extension 
	 *  
	 * This assumes also that the file has not a multi-extension (e.g. tar.gz)
	 * 
	 * someone please provide with a more correct pattern ! 
	 * 
	 * 
	 * @param fileName
	 * @return {artifactId, version, extension}
	 */
	private String[] split(String fileName) {
	
		Pattern p = Pattern.compile("(.|(-\\D)+)-((\\d)+(.*))\\.(\\w+)");
		Matcher m = p.matcher(fileName);
	
		String[] allGroups = new String[m.groupCount() + 1];
	
		int i = 0;
		while ( i < m.groupCount() + 1 && m.find(i) ) {
			allGroups[i] = m.group(i);
			i++;
		}
	
		String[] consistentGroups = new String[3];
		consistentGroups[1] = allGroups[3];
		consistentGroups[2] = allGroups[6];
	
	
		if ( consistentGroups[1] != null ) {
			consistentGroups[0] = fileName.substring(0, fileName.indexOf(consistentGroups[1]) - 1);
		}
	
		return consistentGroups;
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
	public String guessGroupId(String absoluteFileName) throws InvalidDependencyException {
		File fileToCompute = new File(absoluteFileName);
		if ( fileToCompute.isDirectory() ){
			throw new InvalidDependencyException(absoluteFileName + " is a directory");
		}
		File firstLevelParent = fileToCompute.getParentFile();
		if ( firstLevelParent.getParentFile() != null ) {
			return firstLevelParent.getParentFile().getName();
		}
		else return null;
	}
	
	public String guessExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

}
