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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.project.Dependency;
import org.apache.maven.project.Project;
import org.mevenide.Environment;
import org.mevenide.util.MevenideUtil;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DependencyUtil {
	private static Log log = LogFactory.getLog(DependencyUtil.class);
	
	private DependencyUtil() {}
	
	public static boolean areEquals(Dependency d1, Dependency d2) {
		return  (d1 == null && d2 == null) ||
				(   
				    d1 != null && d2 != null
				    && areEquals(d1.getArtifactId(), d2.getArtifactId())
					&& areEquals(d1.getGroupId(), d2.getGroupId())
					&& areEquals(d1.getVersion(), d2.getVersion())
				);
	}
	
	private static boolean areEquals(String s1, String s2) {
		return  (s1 == null && s2 == null) ||
				(
				    s1 != null && s1.equals(s2)
				);
	}
	
	public static boolean isValidGroupId(final String groupId) {
		File repo = new File(Environment.getMavenRepository());
		File[] children = repo.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().equals(groupId);
			}
		});
		return children != null && children.length != 0;
	}
	
	public static boolean isValid(Dependency d) {
		if ( d == null ) { 
			log.debug("null dependency found");
			return false;
		} 
		boolean valid = !MevenideUtil.isNull(d.getGroupId()) && !MevenideUtil.isNull(d.getArtifactId()) && !MevenideUtil.isNull(d.getVersion());
		log.debug("Dependency " + d.getArtifact() + " valid=" + valid + toString(d));
		return  valid;
	}

	public static String toString(Dependency d) {
		return " <groupId=" + d.getGroupId() + ">, <artifactId=" + d.getArtifactId() + ">, <version=" + d.getVersion() + ">" ;
	}

	/**
	 * modifies the list parameter passed, removing all non resolved dependencies
	 * 
	 * @param dependencies
	 * @return list of non resolved dependencies  
	 */
	public static List getNonResolvedDependencies(List dependencies) {
		List temp = new ArrayList(dependencies);
		List nonResolvedDependencies = new ArrayList();
		for (int i = 0; i < temp.size(); i++) {
			Dependency dependency = (Dependency)temp.get(i); 
			if ( !DependencyUtil.isValid(dependency) ) {
				dependencies.remove(dependency);
				nonResolvedDependencies.add(dependency);
			}
			
		} 
		return nonResolvedDependencies;
	}

	/**
	 * checks if a Dependency identified by its artifact path is present in the POM.
	 * 
	 * testing artifact doesnt seem to be a good solution since it is often omitted
	 * we rather have to test artifactId and version. 
	 * 
	 * i dont rely either on groupId cuz i dont think it is not relevant. 
	 * 
	 * @param project
	 * @param absoluteFileName
	 * @return
	 */
	public static boolean isDependencyPresent(Project project, Dependency dependency) {
		log.debug("searched dependency : " + DependencyUtil.toString(dependency));
		List dependencies = project.getDependencies();
		if ( dependencies == null ) {
			return false;
		}
		for (int i = 0; i < dependencies.size(); i++) {
			Dependency declaredDependency = (Dependency) dependencies.get(i);
	
			String version = declaredDependency.getVersion(); 
			String artifactId = declaredDependency.getArtifactId();
			
			log.debug("found dependency : " + DependencyUtil.toString(declaredDependency));
			
			if (  artifactId != null && artifactId.equals(dependency.getArtifactId()) 
				  && version != null && version.equals(dependency.getVersion())) {
				return true;
			}
		}
		return false;
	}
}
