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




/**
 * @refactor GETRID get rid of the <code>getResolvedSourceType(String)</code>
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class BuildConstants {
	public static final String MAVEN_TEST = "test";
	
	public static final String MAVEN_SRC = "source";
	
	public static final String MAVEN_ASPECT = "aspects";

	
	public static String getResolvedSourceType(String type) throws InvalidSourceTypeException {
		if ( MAVEN_SRC.equals(type) ) {
			return "sourceDirectory";
		}
		if ( MAVEN_TEST.equals(type) ) {
			return "unitTestSourceDirectory";
		}
		if ( MAVEN_ASPECT.equals(type) ) {
			return "aspectSourceDirectory";
		}
		else {
			throw new InvalidSourceTypeException(type);
		}
	}

	
}
