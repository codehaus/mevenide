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

/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 * 
 */
public interface ISynchronizer {
    /** update POM from IDE project setup */
    public static final int IDE_TO_POM = 1;
    
    /** update IDE project setup from POM data */
    public static final int POM_TO_IDE = 2;
    
    /** ISynchronizer initialization */
	public void initialize();
	
    /** presynchronization callback */
    public void preSynchronization();
    
    /** postsynchronization callback */
	public void postSynchronization();
    
    /** synchronization */
    public void synchronize() ; 
    
    /**
     * return wether this synchronizer can handle POM_TO_IDE 
     * or IDE_TO_POM synchronization
     * 
     * @param direction
     * @return
     */
    public boolean canHandle(int direction) ;  
    
	
}
