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


import org.apache.maven.project.Dependency;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
class DependencyUtil {
	
	/**
	 * should return the Dependency instance associated with a given path.
	 * however this seems hard if not impossible to achieve. indeed i cannot 
	 * imagine yet a way to extract the required information to build a coherent
	 * Dependency. 
	 * 
	 * so for now i'll stick with the jar overriding mechanism provided by maven  
	 * 
	 * in order to minimize the trouble, we will check if dependencies declared 
	 * in the project descriptor match some ide libraries, and use maven.jar.override
	 * if no match is found for the current path.
	 *  
	 * 
	 * @param absoluteFileName
	 * @return
	 */
	static Dependency getDependency(String absoluteFileName) {
		return null;
	}
	
}
