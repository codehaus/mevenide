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
package org.mevenide.core;



/**
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsBeanLoaderInterceptor.java 2 mai 2003 01:48:2413:34:35 Exp gdodinet 
 * 
 */
public aspect GoalsGrabberInterceptor {
    
   
    private pointcut loadInterception():
        call(static IGoalsGrabber AbstractGoalsGrabber.getGrabber(String))
        && cflow(within(junit.framework.Test+));
    
    IGoalsGrabber around(): loadInterception() {
        //System.out.println("DOH");
        return getMockGrabber();
        
    }

	private IGoalsGrabber getMockGrabber() {
		MockIGoalsGrabber grabber = new MockIGoalsGrabber();
        //@todo init grabber
        return grabber;
	}
    
    private String getGoalsFileName() {
        return GoalsGrabberInterceptor.class.getResource("/maven-goals.xml").getFile();
    }
   
}
