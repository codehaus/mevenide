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
package org.mevenide.ui.eclipse;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * 
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id$
 *
 * @refactor MOCKME this test is config-dependent ! o.O
 */
public class GoalsGrabberTest extends TestCase {

	GoalsGrabber goalsGrabber;
    public static void main(String[] args) {
    }

    protected void setUp() throws Exception {
        
       
        goalsGrabber = (GoalsGrabber) GoalsGrabber.getGrabber("INTERCEPTED");
        
        //goalsGrabber.load();
        
        
        
    }

    protected void tearDown() throws Exception {
        goalsGrabber = null;
    }
    
    /**
     * @todo TESTME 
     * @throws Exception
     */
    public void testMavenize() throws Exception {
	    
        assertNull(goalsGrabber.getGoals("java"));
        
        Collection expectedGoals = new ArrayList();
        expectedGoals.add("generate-project");
        expectedGoals.add("add-maven-repo");
        expectedGoals.add("get-goals");
        expectedGoals.add("external-tools");
        expectedGoals.add("(default)");

        assertEquals(expectedGoals, goalsGrabber.getGoals("java"));
        
       
        
	}
    
    
}
