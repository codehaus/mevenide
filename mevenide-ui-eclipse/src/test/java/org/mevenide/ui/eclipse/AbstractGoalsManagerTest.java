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

import org.mevenide.core.AbstractGoalsManager;

import junit.framework.TestCase;

/**
 * 
 * this TestCase should belong to org.mevenide.core package. however, since
 * the concrete instance used is of type org.mevenide.ui.eclipse.GoalsManager, 
 * i have moved it there to avoid cyclic dependencies.
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractGoalsManagerTest.java 3 mai 2003 21:52:4013:34:35 Exp gdodinet 
 * 
 */
public class AbstractGoalsManagerTest extends TestCase {
    AbstractGoalsManager manager ;
	
	protected void setUp() throws Exception {
		manager = new GoalsManager();
      //  manager.
	}

	protected void tearDown() throws Exception {
		manager = null;
	}

	public void testAddGoal() {
        manager.addGoal("plugin", "goal");
        manager.addGoal("plugin", "goal2");
        manager.addGoal("plugin2", "goal");
        manager.addGoal("plugin3", null);
        manager.addGoal(null, "goal");
	}

	public void testRemoveGoal() {
	}

	public void testGetSelectedGoals() {
	}

	public void testRunGoals() {
	}

}
