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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractDependencyResolver implements IDependencyResolver {

	private static Object lock = new Object();
	
	private static Map resolvers = new HashMap();
	
	protected AbstractDependencyResolver() {
	}
		
	public static IDependencyResolver newInstance(String absoluteFileName) throws  Exception {
		if (resolvers.containsKey(absoluteFileName)) {
			return (IDependencyResolver) resolvers.get(absoluteFileName);
		}
		synchronized (lock) {
			if (!resolvers.containsKey(absoluteFileName)) {
				IDependencyResolver resolver = (IDependencyResolver) new  DiscoverClass().newInstance(IDependencyResolver.class);
				resolver.setFileName(absoluteFileName);
				resolvers.put(absoluteFileName, resolver);
			}
			return (IDependencyResolver) resolvers.get(absoluteFileName);
		}
	}
	
}
