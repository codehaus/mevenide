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


import org.mevenide.core.AbstractRunner;

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public abstract class AbstractIdeSynchronizer implements ISynchronizer {
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
    		e.printStackTrace();
    	}
	}

    /**
     * template pattern
     * @see org.mevenide.core.sync.ISynchronizer#synchronize
     */
	public void synchronize()  {
		try {
			preSynchronization();
			runner.run( new String[] {synchronizationGoal} );
			postSynchronization();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
    /**
     * @see org.mevenide.core.sync.ISynchronizer#canHandle
     */
	public boolean canHandle(int direction) {
		return direction == ISynchronizer.POM_TO_IDE;
	}
}