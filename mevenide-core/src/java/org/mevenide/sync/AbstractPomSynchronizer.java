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
package org.mevenide.sync;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractPomSynchronizer implements ISynchronizer {
	private static Log log = LogFactory.getLog(AbstractPomSynchronizer.class);
    /**
     * template pattern
     * @see org.mevenide.core.sync.ISynchronizer#synchronize
     */
    public synchronized void synchronize()  {
		try {
			preSynchronization();
			mavenize();
			postSynchronization();
		}
		catch ( Exception e ) {
			log.debug("Unable to synchronize POM dur to : " + e );
		}
	}
    
    /**
     * @see org.mevenide.core.sync.ISynchronizer#canHandle
     */
	public boolean canHandle(int direction) {
		return direction == ISynchronizer.IDE_TO_POM;
	}
	
    /**
     * synchronize the POM with the current project properties
     */
	protected abstract void mavenize();
}