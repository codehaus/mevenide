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
package org.mevenide.core.sync;

import java.util.Enumeration;

import org.apache.commons.discovery.tools.Service;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class SynchronizerFactory  {
	
	/**
     * create a ISynchronizer instance that update either the IDE projects 
     * properties or the POM.
     * instanciation is under commons-discovery responsability.
     * 
	 * @param direction either ISynchronizer.POM_TO_IDE or ISynchronizer.IDE_TO_POM
	 * @return
	 */
	public static ISynchronizer getSynchronizer(int direction) {
		try {
			ISynchronizer synchronizer = null;
			Enumeration candidates = Service.providers(ISynchronizer.class);
			while ( candidates.hasMoreElements() ) {
				synchronizer = (ISynchronizer) candidates.nextElement();
				if ( synchronizer != null && synchronizer.canHandle(direction) ) {
					synchronizer.initialize();
					return synchronizer;
				}
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			//@todo FUNCTIONAL add correct exception handling
			return null;
		}
	}
}
