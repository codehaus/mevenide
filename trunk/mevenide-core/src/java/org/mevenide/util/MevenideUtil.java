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
package org.mevenide.util;

import java.io.File;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class MevenideUtil {
	
	public static boolean findFile(File rootDirectory, String fileName) {
		File[] f = rootDirectory.listFiles();
		for (int i = 0; i < f.length; i++) {
			if ( f[i].isDirectory() ) {
				if ( findFile(f[i], fileName) ) {
					return true;
				}
			}
			else {
				if ( f[i].getName().equals(fileName) ) {
					return true;
				}
			}
		}
		return false;
	}

	/** 
	 * @param sourceDirectory
	 * @return false if source is null or does only contain whitespaces
	 */
	public static boolean isNull(String string) {
		return string == null 
		 		|| string.trim().equals("");
	}
}
