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

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class DefaultDependencyResolver extends AbstractDependencyResolver {
	private String artifact;
	
	private String fileName;
	
	private String artifactId;
	private String version;
	private String extension;
	private String groupId;
	
	/**
	 * 
	 * decomposition[0] = artifactId
	 * decomposition[1] = version
	 * decomposition[2] = extension
	 * 
	 */
	private String[] decomposition = new String[3];
	
	public void setFileName(String fName) {
		this.artifact = fName;
		this.fileName = new File(fName).getName();
		decomposition = new DependencySplitter(fileName).split();
		init();
	}

	private void init() {
		initArtifactId();
		initVersion();
		initExtension();
		initGroupId();
	}
	
	private void initArtifactId() {
		artifactId = decomposition[0];
		if ( artifactId == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			artifactId = fileName.substring(0, fileName.indexOf("SNAPSHOT") - 1);
		}
	}
	
	private void initVersion() {
		version = decomposition[1];
		if ( version == null && fileName.indexOf("SNAPSHOT") > 0 ) {
			version = "SNAPSHOT";
		}
	}
	
	private void initExtension() {
		extension = fileName.substring(fileName.lastIndexOf('.') + 1);
	}
	
	private void initGroupId() {
		File fileToCompute = new File(artifact);
		File firstLevelParent = fileToCompute.getParentFile();
		if ( firstLevelParent.getParentFile() != null ) {
			groupId = firstLevelParent.getParentFile().getName();
		}
		if ( !DependencyUtil.isValidGroupId(groupId) ) groupId = null;
	}

	public String guessArtifactId() {
		return artifactId;
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

	
	
	
	
	

}
