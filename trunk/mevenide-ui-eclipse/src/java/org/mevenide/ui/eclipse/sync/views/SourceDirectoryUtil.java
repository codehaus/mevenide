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
package org.mevenide.ui.eclipse.sync.views;

import java.util.HashMap;
import java.util.Map;

import org.mevenide.ProjectConstants;

/**
 * @todo keep it in synch with org.mevenide.ProjectConstants
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public class SourceDirectoryUtil {

	private SourceDirectoryUtil() {
	}
	
	private static Map sourceIndexMap;

	final static String[] sourceTypes = {
		ProjectConstants.MAVEN_SRC_DIRECTORY,
		ProjectConstants.MAVEN_ASPECT_DIRECTORY,
		ProjectConstants.MAVEN_TEST_DIRECTORY,
		ProjectConstants.MAVEN_INTEGRATION_TEST_DIRECTORY,	
		"resources",
		"test resources",
		"integration test resources",		
	};
	
	static Integer getSourceTypeIndex(String sourceType) {
		if ( sourceIndexMap == null ) {
			sourceIndexMap = new HashMap();
			for (int i = 0; i < sourceTypes.length; i++) {
				sourceIndexMap.put(sourceTypes[i], new Integer(i));
			}
		}
		return (Integer) sourceIndexMap.get(sourceType); 
	}
}
