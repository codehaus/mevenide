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


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public interface IDependencyResolver {
	public void setFileName(String fileName);
	

	/**
	 * try to find the artifactId
	 * 
	 * @return guessed artifactId
	 */
	public abstract String guessArtifactId();
	
	/**
	 * try to find the version  of the artifact, if possible
	 * f.i. if fileName is rt.jar, version will be null
	 * 
	 * guessVersion("rt-1.4.1.jar") will return "1.4.1" 
	 * 
	 * @return guessed version
	 */
	public abstract String guessVersion();
	
	/**
	 * get the last extension of the file. specs are not clear yet..
	 * 
	 * absolutely if fileName = xxx.tar.gz it should return tar.gz
	 * altho is not gz enough already ? indeed it could become really 
	 * tricky to get that multi-extension. 
	 * 
	 * if someone has a clue, please share..
	 * 
	 * @return the last extension of the file
	 */
	public abstract String guessExtension();
	
	/**
	 * try to guess the groupId if the file isnot present in the repository
	 * 
	 * @return guessed groupId
	 */
	public abstract String guessGroupId() ;
}