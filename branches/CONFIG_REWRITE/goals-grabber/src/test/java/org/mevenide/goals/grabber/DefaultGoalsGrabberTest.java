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
 * @version $Id: DefaultGoalsGrabberTest.java 4 sept. 2003 Exp gdodinet 
 * 
 */
public class DefaultGoalsGrabberTest extends AbstractGoalsGrabberTestCase {
	
	
   	protected IGoalsGrabber getGoalsGrabber() throws Exception {
		return new DefaultGoalsGrabber(new LocationFinderAggregator(context));
	}

	protected String[] getGetPluginsResults() {
		return new String[] { "java", "jar", "test", "war" };
	}
	
	protected String[] getGetGoalsParameters() {
		return new String[] { "java" };
	}
	
	protected String[][] getGetGoalsResults() {
		return new String[][] { 
			{"jar", "compile", "prepare-filesystem", "jar-resources"} 
		};
	}
	
	protected String[] getGetDescriptionParameters() {
		return new String[] { "java:compile", "java:jar"} ;
	}

	protected String[] getGetDescriptionResults() {
		return new String[] { "Compile the project", "Create the deliverable jar file." };  
	}

	protected String[] getGetPrereqsParameters() {
		return new String[] { "java:jar", "java:compile" };
	}
	
	protected String[][] getGetPrereqsResults() {
		return new String[][] { {"jar:jar"}, {"java:prepare-filesystem"} };
	}
	
    protected String[] getGetOriginParameters()
    {
       return new String[] { "java:compile", "java:jar", "jar" };        
    }    
 	 
    protected String[] getGetOriginResults()
    {
        return new String[] { IGoalsGrabber.ORIGIN_PLUGIN, IGoalsGrabber.ORIGIN_PLUGIN, IGoalsGrabber.ORIGIN_PLUGIN };
    }    

}
