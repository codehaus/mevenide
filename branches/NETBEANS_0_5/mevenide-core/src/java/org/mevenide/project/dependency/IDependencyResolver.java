/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
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
package org.mevenide.project.dependency;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public interface IDependencyResolver {
	void setFileName(String fileName);
	

	/**
	 * try to find the artifactId
	 * 
	 * @return guessed artifactId
	 */
	String guessArtifactId();
	
	/**
	 * try to find the version  of the artifact, if possible
	 * f.i. if fileName is rt.jar, version will be null
	 * 
	 * guessVersion("rt-1.4.1.jar") will return "1.4.1" 
	 * 
	 * @return guessed version
	 */
	String guessVersion();
	
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
	String guessExtension();
	
	/**
	 * try to guess the groupId if the file isnot present in the repository
	 * 
	 * @return guessed groupId
	 */
	String guessGroupId() ;
}