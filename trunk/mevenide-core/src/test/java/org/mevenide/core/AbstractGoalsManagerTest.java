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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;


/**
 * 
 * test file maven-goals.xml defines the following goals :
 * 
 *  + eclipse:generate-project
 *      => Generate Eclipse .project and .classpath project files
 *  + eclipse:add-maven-repo
 *      => Ensure that the classpath variable MAVEN_REPO is available
 *  + eclipse:get-goals
 *      => Get all the available goals
 *  + eclipse:external-tools
 *      => Generate an Eclipse external tool for each goal
 *  + eclipse:(default)
 *      => Generate Eclipse project files
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractGoalsManagerTest.java 8 mai 2003 13:21:0913:34:35 Exp gdodinet 
 * 
 */
public class AbstractGoalsManagerTest extends TestCase {

    IGoalsManager goalsManager;
    
    protected void setUp() throws Exception {
        System.setProperty("org.mevenide.core.AbstractGoalsGrabber", 
                           "org.mevenide.core.stub.AbstractGoalsGrabberStub");
        System.setProperty("org.mevenide.core.AbstractRunner", 
                           "org.mevenide.core.stub.AbstractRunnerStub");
		goalsManager = new AbstractGoalsManagerStub();
	}

	protected void tearDown() throws Exception {
		goalsManager = null;
	}

	public void testAddGoal() {
        goalsManager.addGoal("eclipse", "generate-project");
        
        try {
			goalsManager.addGoal("eclipse", "should fail");
            fail("expected GoalNotFoundException");
		} 
        catch (GoalNotFoundException e) { }
        
        try {
            goalsManager.addGoal("should fail", "any string");
            fail("expected GoalNotFoundException");
        } 
        catch (GoalNotFoundException e) { }
        
        goalsManager.addGoal("eclipse", null);
        
	}

	public void testRemoveGoal() {
	}

	public void testGetGoalsToRun() {
	}

	public void testGetGoals() {
        List expectedGoals = new ArrayList();
        
        goalsManager.addGoal("eclipse", "generate-project");
        expectedGoals.add("eclipse:generate-project");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoals("eclipse")));
        
        goalsManager.addGoal("eclipse", "generate-project");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoals("eclipse")));
        
        goalsManager.addGoal("eclipse", "(default)");
        expectedGoals.add(0, "eclipse:(default)");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoals("eclipse")));
	}


}