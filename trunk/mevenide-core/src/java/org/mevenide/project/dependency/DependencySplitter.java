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
 * 
 */
package org.mevenide.project.dependency;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class DependencySplitter {
	
	private String fileName;
	
	public DependencySplitter(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * we assume that fileName follows that kind of pattern that is pretty 
	 * general : "(.|(-\\D)*)-((\\d)+(.*))\\.(\\w+)"
	 * 
	 * so we have $4 => version ; $7 => extension 
	 * 
	 * This assumes also that the file has not a multi-extension (e.g. tar.gz)
	 * someone please provide with a more correct pattern$
	 * 
	 *  
	 * we should provide a mecanism to allow the user to specify more patterns that
	 * will be successfully applied still success considering, e.g.,
	 * that (artifactId, version and extension not null) implies success  
	 * 
	 * @param fileName
	 * @return {artifactId, version, extension}
	 */
	public String[] split() {
	
		Pattern p = Pattern.compile("(.|(-\\D)*)-((\\d)+(.*))\\.(\\w+)");
		
		String[] consistentGroups = applySplitStrategy(p, 3, 6);
	
		return consistentGroups;
	}

	private String[] applySplitStrategy(Pattern p, int expectedVersionIndex, int expectedExtensionIndex) {
		Matcher m = p.matcher(fileName);
		
		String[] allGroups = new String[m.groupCount() + 1];
		
		int i = 0;
		while ( i < m.groupCount() + 1 && m.find(i) ) {
			allGroups[i] = m.group(i);
			System.out.println(allGroups[i]);
			i++;
		}
		
		String[] consistentGroups = new String[3];
		consistentGroups[1] = allGroups[expectedVersionIndex];
		consistentGroups[2] = allGroups[expectedExtensionIndex];
		if ( consistentGroups[1] != null ) {
			consistentGroups[0] = fileName.substring(0, fileName.indexOf(consistentGroups[1]) - 1);
		}
		
		return consistentGroups;
	}
}
