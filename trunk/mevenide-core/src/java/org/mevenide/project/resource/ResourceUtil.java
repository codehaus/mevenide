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
package org.mevenide.project.resource;

import org.apache.maven.project.Resource;

/**
 * 
 * @author <a href="mailto:gdodinet@wanadoo.fr">Gilles Dodinet</a>
 * @version $Id$
 * 
 */
public class ResourceUtil {

	/**
	 * construct a Resource from a given path, including ALL children
	 * 
	 * @param path
	 * @return
	 */
	public static Resource newResource(String path) {
		Resource resource = new Resource();
		resource.setDirectory(path);
		resource.addInclude("**/*.*");
		
		return resource;
	}
}
