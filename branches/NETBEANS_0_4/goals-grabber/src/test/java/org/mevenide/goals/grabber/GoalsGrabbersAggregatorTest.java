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

import org.mevenide.environment.LocationFinderAggregator;



/**  
 * 
 * @author Gilles Dodinet (gdodinet@wanadoo.fr)
 * @version $Id: GoalsGrabbersAggregatorTest.java 6 sept. 2003 Exp gdodinet 
 * 
 */
public class GoalsGrabbersAggregatorTest extends AbstractGoalsGrabberTestCase {
	
	private static class ArrayUtils {

		static String[] concatenateArrays(String[] sourceArray1, String[] sourceArray2) {
	 		String[] resultArray = new String[sourceArray1.length + sourceArray2.length];
			System.arraycopy(sourceArray1, 0, resultArray, 0, sourceArray1.length);
			System.arraycopy(sourceArray2, 0, resultArray, sourceArray1.length, sourceArray2.length);
			return resultArray;
	 
		}
		
		static String[][] concatenateArrays(String[][] sourceArray1, String[][] sourceArray2) {
			String[][] resultArray = new String[sourceArray1.length + sourceArray2.length][];
			System.arraycopy(sourceArray1, 0, resultArray, 0, sourceArray1.length);
			System.arraycopy(sourceArray2, 0, resultArray, sourceArray1.length, sourceArray2.length);
			return resultArray;
		}
	
	}

	private GoalsGrabbersAggregator goalsGrabber;

	private IGoalsGrabber defaultGoalsGrabber;
	private IGoalsGrabber projectGoalsGrabber;
	
	private AbstractGoalsGrabberTestCase defaultGoalsGrabberTest = new DefaultGoalsGrabberTest();
	private AbstractGoalsGrabberTestCase projectGoalsGrabberTest = new ProjectGoalsGrabberTest();

	protected IGoalsGrabber getGoalsGrabber() throws Exception {
        defaultGoalsGrabber = new DefaultGoalsGrabber(new LocationFinderAggregator(context));
		projectGoalsGrabber = projectGoalsGrabberTest.getGoalsGrabber();
		
		goalsGrabber = new GoalsGrabbersAggregator();
		goalsGrabber.addGoalsGrabber(defaultGoalsGrabber);
		goalsGrabber.addGoalsGrabber(projectGoalsGrabber);
		goalsGrabber.refresh();

		return goalsGrabber;	
    }
	    
    protected String[] getGetDescriptionParameters() {
		String[] defaultGetGoalsGrabberDescriptionParameters = defaultGoalsGrabberTest.getGetDescriptionParameters();
		String[] projectGetGoalsGrabberDescriptionParameters = projectGoalsGrabberTest.getGetDescriptionParameters();
		return ArrayUtils.concatenateArrays(defaultGetGoalsGrabberDescriptionParameters, projectGetGoalsGrabberDescriptionParameters);
    }

    protected String[] getGetDescriptionResults() {
        String[] defaultGetGoalsGrabberDescriptionResults = defaultGoalsGrabberTest.getGetDescriptionResults();
		String[] projectGetGoalsGrabberDescriptionResults = projectGoalsGrabberTest.getGetDescriptionResults();
		return ArrayUtils.concatenateArrays(defaultGetGoalsGrabberDescriptionResults, projectGetGoalsGrabberDescriptionResults);
    }

    protected String[] getGetGoalsParameters() {
        String[] defaultGetGoalsParameters = defaultGoalsGrabberTest.getGetGoalsParameters();
		String[] projectGetGoalsParameters = projectGoalsGrabberTest.getGetGoalsParameters();
		return ArrayUtils.concatenateArrays(defaultGetGoalsParameters, projectGetGoalsParameters);
    }

    protected String[][] getGetGoalsResults() {
        String[][] defaultGetGoalsResults = defaultGoalsGrabberTest.getGetGoalsResults();
		String[][] projectGetGoalsResults = projectGoalsGrabberTest.getGetGoalsResults();
		return ArrayUtils.concatenateArrays(defaultGetGoalsResults, projectGetGoalsResults);
    }

    protected String[] getGetPluginsResults() {
        String[] defaultGetPluginsResults = defaultGoalsGrabberTest.getGetPluginsResults();
		String[] projectGetPluginsResults = projectGoalsGrabberTest.getGetPluginsResults();
		return ArrayUtils.concatenateArrays(defaultGetPluginsResults, projectGetPluginsResults);
    }

    protected String[] getGetPrereqsParameters() {
        String[] defaultGetPrereqsParameters = defaultGoalsGrabberTest.getGetPrereqsParameters();
		String[] projectGetPrereqsParameters = projectGoalsGrabberTest.getGetPrereqsParameters();
		return ArrayUtils.concatenateArrays(defaultGetPrereqsParameters, projectGetPrereqsParameters);
    }

    protected String[][] getGetPrereqsResults() {
        String[][] defaultGetPrereqsResults = defaultGoalsGrabberTest.getGetPrereqsResults();
		String[][] projectGetPrereqsResults = projectGoalsGrabberTest.getGetPrereqsResults();
		return ArrayUtils.concatenateArrays(defaultGetPrereqsResults, projectGetPrereqsResults);
    }

    protected String[] getGetOriginParameters()
    {
        String[] defaultGetOriginParameters = defaultGoalsGrabberTest.getGetOriginParameters();
		String[] projectGetOriginParameters = projectGoalsGrabberTest.getGetOriginParameters();
		return ArrayUtils.concatenateArrays(defaultGetOriginParameters, projectGetOriginParameters);
    }    
    
    protected String[] getGetOriginResults()
    {
        String[] defaultGetOriginResults = defaultGoalsGrabberTest.getGetOriginResults();
		String[] projectGetOriginResults = projectGoalsGrabberTest.getGetOriginResults();
		return ArrayUtils.concatenateArrays(defaultGetOriginResults, projectGetOriginResults);
    }    

}


