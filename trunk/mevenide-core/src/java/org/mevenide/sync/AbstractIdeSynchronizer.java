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
import org.mevenide.core.AbstractRunner;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractIdeSynchronizer implements ISynchronizer {
	private static Log log = LogFactory.getLog(AbstractIdeSynchronizer.class);
	
	/** the goal passed to the maven runner, f.i. 'eclipse:generate-classpath' */
	protected String synchronizationGoal;
    
    /** Maven Runner */
    private AbstractRunner runner;
    
    /** instantiate the maven runner */
    public AbstractIdeSynchronizer() {
        try {
            runner = AbstractRunner.getRunner();
    	} 
        catch (Exception e) {
    		log.debug("Unable to init AbstractIdeAynchronizer due to : " + e);
    	}
	}

    /**
     * template pattern
     * @see org.mevenide.core.sync.ISynchronizer#synchronize
     */
	public synchronized void synchronize()  {
		try {
			preSynchronization();
			process();
			postSynchronization();
		}
		catch ( Exception e ) {
			log.debug("Unable to synchronize project due to : " + e);
		}
	}

	/**
	 * 
	 * this method runs the maven-%IDE%-plugin synchronization goal. 
	 * It is provided as a default mean to synchronize IDE with POM. 
	 * however subclasses should override it to take advantage 
	 * of the various IDEs plugin API facilities.
	 *
	 */
	protected void process() {
		runner.run( new String[0], new String[] {synchronizationGoal} );
	}
	
    /**
     * @see org.mevenide.core.sync.ISynchronizer#canHandle
     */
	public boolean canHandle(int direction) {
		return direction == ISynchronizer.POM_TO_IDE;
	}
}