/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
package org.mevenide.goals.grabber;



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
        defaultGoalsGrabber = defaultGoalsGrabberTest.getGoalsGrabber();
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


