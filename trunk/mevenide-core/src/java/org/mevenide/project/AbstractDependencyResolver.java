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

import org.apache.commons.discovery.tools.DiscoverClass;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractDependencyResolver implements IDependencyResolver {
	private static IDependencyResolver dependencyUtil;
	private static Object lock = new Object();
	
	protected AbstractDependencyResolver() {
	}
		
	public static IDependencyResolver getInstance() throws  Exception {
		if (dependencyUtil != null) {
			return dependencyUtil;
		}
		synchronized (lock) {
			if (dependencyUtil == null) {
				dependencyUtil = (IDependencyResolver) new  DiscoverClass().newInstance(IDependencyResolver.class);
			}
			return dependencyUtil;
		}
	}
	
}
