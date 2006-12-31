/* ==========================================================================
 * Copyright 2003-2004 Mevenide Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * =========================================================================
 */
package org.mevenide.goals.grabber;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import org.mevenide.goals.AbstractTestCase;
import org.mevenide.goals.test.util.TestUtils;

/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: AbstractGoalsGrabberTestCase.java 5 sept. 2003 Exp gdodinet 
 * 
 */
public abstract class AbstractGoalsGrabberTestCase extends AbstractTestCase {
	protected IGoalsGrabber goalsGrabber;

	protected File goalsFile;
	
	protected void setUp() throws Exception {
		super.setUp();
		File src = new File(AbstractGoalsGrabberTestCase.class.getResource("/goals.cache").getFile());
		goalsFile = new File(pluginsLocal, "goals.cache") ; 
		TestUtils.copy(src.getAbsolutePath(), goalsFile.getAbsolutePath());

		goalsGrabber = getGoalsGrabber();
		goalsGrabber.refresh();
	}

	protected void tearDown() throws Exception {
        goalsGrabber = null;
        super.tearDown();
    }

	public void testGetPlugins() {
		Collection plugins = Arrays.asList(goalsGrabber.getPlugins());
		for (int i = 0; i < getGetPluginsResults().length; i++) {
			assertTrue(plugins.contains(getGetPluginsResults()[i]));    
        }
	}

	public void testGetGoals() {
		for (int i = 0; i < getGetGoalsParameters().length; i++) {
			String[] goals = goalsGrabber.getGoals(getGetGoalsParameters()[i]);
			Collection goalsCollection = Arrays.asList(goals);
			assertEquals(getGetGoalsResults()[i].length, goals.length);
			for (int j = 0; j < getGetGoalsResults()[i].length; j++) {
				assertTrue(goalsCollection.contains(getGetGoalsResults()[i][j]));
            }
        }
		goalsGrabber.getGoals(null);
	}

	public void testGetOrigin() {
		for (int i = 0; i < getGetOriginParameters().length; i++) {
			String origin = goalsGrabber.getOrigin(getGetOriginParameters()[i]);
			assertEquals(getGetOriginResults()[i], origin);
        }
	}
    
	public void testGetDescription() {
		for (int i = 0; i < getGetDescriptionParameters().length; i++) {
			assertEquals(getGetDescriptionResults()[i], goalsGrabber.getDescription(getGetDescriptionParameters()[i]));
        }
	}

	public void testGetPrereqs() {
		for (int i = 0; i < getGetPrereqsParameters().length; i++) {
			String[] prereqs = goalsGrabber.getPrereqs(getGetPrereqsParameters()[i]);
			Collection prereqsCollection = Arrays.asList(prereqs);
			assertEquals(getGetPrereqsResults()[i].length, prereqs.length);
			for (int j = 0; j < getGetPrereqsResults()[i].length; j++) {
                assertTrue(prereqsCollection.contains(getGetPrereqsResults()[i][j]));
            }
        }
	}
	
	
	protected abstract IGoalsGrabber getGoalsGrabber() throws Exception  ;

	protected abstract String[] getGetPluginsResults() ;

	protected abstract String[] getGetGoalsParameters() ;
	protected abstract String[][] getGetGoalsResults() ;

	protected abstract String[] getGetDescriptionParameters() ;
	protected abstract String[] getGetDescriptionResults() ;

	protected abstract String[] getGetPrereqsParameters() ;
	protected abstract String[][] getGetPrereqsResults() ;
    
    protected abstract String[] getGetOriginParameters();
    protected abstract String[] getGetOriginResults();
    
}
