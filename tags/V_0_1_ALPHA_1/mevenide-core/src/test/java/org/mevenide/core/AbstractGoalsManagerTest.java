/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 Gilles Dodinet (rhill@wanadoo.fr).  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software licensed under 
 *        Apache Software License (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Mevenide" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact mevenide-general-dev@lists.sourceforge.net.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Mevenide", nor may "Apache" or "Mevenide" appear in their name, without
 *    prior written permission of the Mevenide Team and the ASF.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package org.mevenide.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mevenide.GoalNotFoundException;

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

    AbstractGoalsManager goalsManager;
    
    protected void setUp() throws Exception {
        System.setProperty("org.mevenide.core.AbstractGoalsGrabber", 
                           "org.mevenide.core.AbstractGoalsGrabberStub");
        System.setProperty("org.mevenide.core.AbstractRunner", 
                           "org.mevenide.core.AbstractRunnerStub");
		goalsManager = new AbstractGoalsManagerStub();
	}

	protected void tearDown() throws Exception {
		goalsManager = null;
	}

    public void testReset() {
        goalsManager.addGoal("eclipse", "generate-project");
        goalsManager.reset();
        assertEquals(0, goalsManager.getRunnableGoals().size());
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
        
        assertEquals(0, goalsManager.getGoals("noSuchPlugin").length);
	}

    public void testGetGoalsToRun() {
        List expectedGoals = new ArrayList();
        
        goalsManager.addGoal("eclipse", "generate-project");
        expectedGoals.add("eclipse:generate-project");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoalsToRun()));

        goalsManager.addGoal("eclipse", "generate-project");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoalsToRun()));
        
        goalsManager.addGoal("eclipse", "external-tools");
        expectedGoals.add(0, "eclipse:external-tools");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoalsToRun()));
       
        goalsManager.reset();
        expectedGoals.clear();
        
        goalsManager.addGoal("eclipse", null);
        expectedGoals.add("eclipse");
        assertEquals(expectedGoals, Arrays.asList(goalsManager.getGoalsToRun()));
        
        goalsManager.reset();
        expectedGoals.clear();
        
    }

    public void testRemoveGoal() {
        goalsManager.addGoal("eclipse", "generate-project");
        goalsManager.removeGoal("eclipse", "generate-project");
        assertEquals(0, goalsManager.getRunnableGoals().size());
        
        goalsManager.addGoal("eclipse", "generate-project");
        goalsManager.addGoal("eclipse", "external-tools");
        goalsManager.removeGoal("eclipse", "generate-project");
        assertEquals(1, goalsManager.getRunnableGoals().size());
        
        goalsManager.addGoal("eclipse", "generate-project");
        goalsManager.addGoal("eclipse", "external-tools");
        goalsManager.removeGoal("eclipse", null);
        assertEquals(0, goalsManager.getRunnableGoals().size());
    }
}
    